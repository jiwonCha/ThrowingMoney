package com.throwing.money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;
import com.throwing.money.type.DistributedMoneyInfo;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class ValidationServiceTests {

    @MockBean
    DBService dbService;

    @Autowired
    ValidationService validationService;

    @Test
    void newTokenTest() {
        String token = validationService.newToken();
        assertEquals(token.length(), 3, "token creation success");
    }

    @Test
    void isValidsTokenTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        Optional<Service> retMockSvc = Optional.of(mockSvc);

        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        assertEquals(true, validationService.isValidToken("tmp", "MockRoomID", false),
                "token validation success for get money");

        mockSvc.setCreatedTime(LocalDateTime.now().plusMinutes(20).toString());
        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        assertEquals(true, validationService.isValidToken("tmp", "MockRoomID", true),
                "token validation success for get service");
    }

    @Test
    void isTokenOwnerTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        Optional<Service> retMockSvc = Optional.of(mockSvc);

        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        assertEquals(true, validationService.isTokenOwner("tmp", 1), "token owner validation success");
    }

    @Test
    void hasAlreadyGetMoneyTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        mockSvc.setDistributedMoneyIdx(0);

        // @TODO 초기화가 왜 바로 안되는 건지 확인
        ArrayList<Integer> moneyOwnerList = new ArrayList<Integer>();
        moneyOwnerList.add(2);
        mockSvc.setMoneyOwnerList(moneyOwnerList);

        Optional<Service> retMockSvc = Optional.of(mockSvc);
        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);

        assertEquals(true, validationService.hasAlreadyGetMoney("tmp", 2), "in case of already get money test success");
    }

    @Test
    void isDistributedOverTest() {
        Service mockSvc = new Service();
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setCreatedTime(LocalDateTime.now().toString());
        mockSvc.setDistributedMoneyIdx(0);
        mockSvc.setDistributedMoneyIdx(1);

        ArrayList<DistributedMoneyInfo> distributedMoneyInfos = new ArrayList<DistributedMoneyInfo>();
        distributedMoneyInfos.add(new DistributedMoneyInfo(1000, 2));
        mockSvc.setDistributeMoneyInfo(distributedMoneyInfos);
        Optional<Service> retMockSvc = Optional.of(mockSvc);

        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);

        assertEquals(true, validationService.isDistributedOver("tmp"), "in case of distributed over test success");
    }
}