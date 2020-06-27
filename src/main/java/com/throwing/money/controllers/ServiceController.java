package com.throwing.money.controllers;

import java.util.Optional;

import com.throwing.money.MoneyService;
import com.throwing.money.type.RequestInfo;
import com.throwing.money.type.ServiceInfo;
import com.throwing.money.ValidationService;
import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "v1/api")
public class ServiceController {
    Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    DBService dbService;

    @Autowired
    ValidationService validationSvc;

    @Autowired
    MoneyService moneySvc;

    @RequestMapping(value = "/service", method = RequestMethod.POST)
    public Mono<String> createService(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @RequestBody RequestInfo requestInfo) {
        logger.info("createService");

        String token = validationSvc.newToken();

        moneySvc.setMoneyList(requestInfo.getTargetMoney(), requestInfo.getHeadCount());

        Service service = new Service();
        service.setToken(token);
        service.setTokenOwner(userID);
        service.setRoomID(roomID);
        service.setOriginalMoney(requestInfo.getTargetMoney());
        service.setDistributeMoneyInfo(moneySvc.getDistributedMoneyInfos());
        service.setDistributedMoneyIdx(0);

        logger.info("Service : {}", service);

        dbService.setService(service);

        return Mono.just(token);
    }

    @GetMapping(value = "/money/{token}")
    public Mono<Integer> getMoney(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @PathVariable("token") String token) {
        if (!validationSvc.isValidToken(token, roomID, false)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Admission Error : Token is invalid");
        }

        if (validationSvc.isTokenOwner(token, userID)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Admission Error : Token Owner");
        }

        if (validationSvc.hasAlreadyGetMoney(token, userID)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Duplicate User Request");
        }

        if (validationSvc.isDistributedOver(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Service is Over");
        }

        return Mono.just(Integer.valueOf(moneySvc.updateMoneyInfo(token, userID)));
    }

    @GetMapping(value = "/service/{token}")
    public Mono<ServiceInfo> getSerivce(@RequestHeader(value = "X-USER-ID", required = true) int userID,
            @RequestHeader(value = "X-ROOM-ID", required = true) String roomID, @PathVariable("token") String token) {

        if (!validationSvc.isTokenOwner(token, userID)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Permission Error");
        }

        if (!validationSvc.isValidToken(token, roomID, true)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Admission Error : Token is invalid");
        }

        Optional<Service> svc = dbService.getSerivce(token);
        Service svcElem = svc.get();
        logger.info("servic Info : {}", svcElem);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setCreatedTime(svcElem.getCreatedTime());
        serviceInfo.setOriginalMoney(svcElem.getOriginalMoney());
        serviceInfo.setDistributedMoney(svcElem.getDistributedMoney());
        serviceInfo.setDistributedMoneyInfo(svcElem.getDistributeMoneyInfo());

        return Mono.just(serviceInfo);
    }
}