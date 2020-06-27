package com.throwing.money;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import com.throwing.money.controllers.ServiceController;
import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;
import com.throwing.money.type.DistributedMoneyInfo;
import com.throwing.money.type.RequestInfo;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import reactor.test.StepVerifier;

@SpringBootTest
public class ServiceControllerTests {
    @Autowired
    ValidationService validataionSvc;

    @Autowired
    MoneyService moneySvc;

    @MockBean
    DBService dbService;

    @Autowired
    ServiceController serviceController;

    @Test
    void createServiceTest() {
        StepVerifier.create(serviceController.createService(1, "MockRoomID", new RequestInfo(10000, 3)))
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void getMoneyTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        mockSvc.setDistributedMoneyIdx(0);

        ArrayList<DistributedMoneyInfo> distributedMoneyInfos = new ArrayList<DistributedMoneyInfo>();
        distributedMoneyInfos.add(new DistributedMoneyInfo(1000, null));
        mockSvc.setDistributeMoneyInfo(distributedMoneyInfos);
        Optional<Service> retMockSvc = Optional.of(mockSvc);

        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        
        StepVerifier.create(serviceController.getMoney(2, "MockRoomID", "tmp")).expectNextCount(1).verifyComplete();
    }

    @Test
    void getServiceTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        Optional<Service> retMockSvc = Optional.of(mockSvc);

        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        StepVerifier.create(serviceController.getSerivce(1, "MockRoomID", "tmp")).expectNextCount(1).verifyComplete();
    }
}