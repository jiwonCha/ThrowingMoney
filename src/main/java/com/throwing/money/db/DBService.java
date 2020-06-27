package com.throwing.money.db;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBService {
    Logger logger = LoggerFactory.getLogger(DBService.class);

    @Autowired
    private ServiceRepository serviceRepository;

    public void setService(Service newService) {
        logger.info("setService : {}", newService);
        newService.setCreatedTime(LocalDateTime.now().toString());
        serviceRepository.save(newService);
    }

    public Service updateService(Service newService) {
        serviceRepository.save(newService);
        return newService;

    }

    public Optional<Service> getSerivce(String token) {
        if (serviceRepository == null) {
            return Optional.empty();
        }
        return serviceRepository.findById(token);
    }

    // // Atomic 해야 함
    // // 위치를 Token 쪽으로 옮겨야 될 것 같음.
    // public int updateMoneyInfo(String token, int userID) {
    //     Optional<Service> svc = getSerivce(token);
    //     Service svcElem = svc.get();

    //     int curMoneyIdx = svcElem.getDistributedMoneyIdx();

    //     svcElem.setDistributedMoneyIdx(curMoneyIdx + 1);
    //     svcElem.setDistributedMoney(
    //             svcElem.getDistributedMoney() + svcElem.getDistributeMoneyInfo().get(curMoneyIdx).getMoney());
    //     svcElem.getDistributeMoneyInfo().get(curMoneyIdx).setMoneyOwner(userID);
    //     ArrayList<Integer> ownerList = svcElem.getMoneyOwnerList();
    //     if (ownerList == null) {
    //         ownerList = new ArrayList<Integer>();
    //     }
    //     ownerList.add(userID);
    //     svcElem.setMoneyOwnerList(ownerList);


    //     updateService(svcElem);

    //     return svcElem.getDistributeMoneyInfo().get(curMoneyIdx).getMoney();
    // }
}