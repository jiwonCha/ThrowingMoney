package com.throwing.money;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;
import com.throwing.money.type.DistributedMoneyInfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class MoneyServiceTests {
    @MockBean
    DBService dbService;

    @Autowired
    MoneyService moneyService;

    @Test
    void setMoneyListTest() {
        moneyService.setMoneyList(10000, 3);
        assertEquals(3, moneyService.getDistributedMoneyInfos().size());
    }

    @Test
    void updateMoneyInfoTest() {
        Service mockSvc = new Service();
        
        mockSvc.setToken("tmp");
        mockSvc.setTokenOwner(1);
        mockSvc.setRoomID("MockRoomID");
        mockSvc.setDistributedMoneyIdx(0);
        mockSvc.setDistributedMoney(0);

        ArrayList<DistributedMoneyInfo> distributedMoneyInfos = new ArrayList<DistributedMoneyInfo>();
        distributedMoneyInfos.add(new DistributedMoneyInfo(1000, null));
        mockSvc.setDistributeMoneyInfo(distributedMoneyInfos);

        Optional<Service> retMockSvc = Optional.of(mockSvc);
        when(dbService.getSerivce("tmp")).thenReturn(retMockSvc);
        when(dbService.updateService(mockSvc)).thenReturn(mockSvc);

        assertEquals(1000, moneyService.updateMoneyInfo("tmp", 1 ));
    }

}