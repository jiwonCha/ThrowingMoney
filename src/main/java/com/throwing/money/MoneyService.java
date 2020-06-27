package com.throwing.money;

import java.util.ArrayList;
import java.util.Random;

import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;
import com.throwing.money.type.DistributedMoneyInfo;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoneyService {
    @Getter
    private ArrayList<DistributedMoneyInfo> distributedMoneyInfos;
    static Logger logger = LoggerFactory.getLogger(MoneyService.class);

    @Autowired
    DBService dbService;

    public void setMoneyList(int targetMoney, int headCount) {
        logger.info("setMoneyList");
        Random rnd = new Random();
        distributedMoneyInfos = new ArrayList<DistributedMoneyInfo>(headCount);

        for (int i = 0; i < headCount; i++) {
            // 1-99까지의 seed값
            float seed = (rnd.nextInt(99) + 1);

            Integer rndMoney = Integer.valueOf((int) Math.floor(targetMoney * seed) / 100);

            DistributedMoneyInfo distributedMoneyInfo = new DistributedMoneyInfo();
            distributedMoneyInfo.setMoney(rndMoney);

            distributedMoneyInfos.add(distributedMoneyInfo);
            logger.info("Money : {}", distributedMoneyInfos.get(i));

            targetMoney -= rndMoney;
        }
    }

    public int updateMoneyInfo(String token, int userID) {
        Service svcElem = dbService.getSerivce(token).get();
        int curMoneyIdx = svcElem.getDistributedMoneyIdx();

        svcElem.setDistributedMoneyIdx(curMoneyIdx + 1);
        svcElem.setDistributedMoney(
                svcElem.getDistributedMoney() + svcElem.getDistributeMoneyInfo().get(curMoneyIdx).getMoney());
        svcElem.getDistributeMoneyInfo().get(curMoneyIdx).setMoneyOwner(userID);
        ArrayList<Integer> ownerList = svcElem.getMoneyOwnerList();
        if (ownerList == null) {
            ownerList = new ArrayList<Integer>();
        }
        ownerList.add(userID);
        svcElem.setMoneyOwnerList(ownerList);


        dbService.updateService(svcElem);

        return svcElem.getDistributeMoneyInfo().get(curMoneyIdx).getMoney();
    }

}