package com.throwing.money;

import java.util.Optional;
import java.util.Random;

import com.throwing.money.db.DBService;
import com.throwing.money.db.Service;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationService {
    static Logger logger = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    DBService dbService;

    public String newToken() {
        String token = "";
        final Random rnd = new Random();

        while (true) {
            for (int i = 0; i < 3; i++) {
                final int rIndex = rnd.nextInt(3);
                switch (rIndex) {
                    case 0:
                        // a-z
                        token = token.concat(String.valueOf((char) ((int) (rnd.nextInt(26)) + 97)));
                        break;
                    case 1:
                        // A-Z
                        token = token.concat(String.valueOf((char) ((int) (rnd.nextInt(26)) + 65)));
                        break;
                    case 2:
                        // 0-9
                        token = token.concat(String.valueOf((rnd.nextInt(10))));
                        break;
                }
            }

            if (dbService.getSerivce(token).isEmpty()) {
                logger.info("Token : {}", token);
                break;
            }
        }

        return token;
    }

    public boolean isValidToken(String token, String roomID, Boolean forRetreive) {
        // 7일 후에 삭제하는 건 DB에서 관리해야 되나..?
        Optional<Service> svc = dbService.getSerivce(token);
        if (svc.isEmpty()) {
            return false;
        }

        final Service svcElem = svc.get();
        if (false == roomID.equals(svcElem.getRoomID())) {
            return false;
        }

        if (true == forRetreive) {
            if (LocalDateTime.parse(svcElem.getCreatedTime()).plusDays(7).isBefore(LocalDateTime.now())) {
                return false;
            }
        } else {
            if (LocalDateTime.parse(svcElem.getCreatedTime()).plusMinutes(10).isBefore(LocalDateTime.now())) {
                return false;
            }
        }

        return true;
    }

    public boolean isTokenOwner(String token, int userID) {
        Optional<Service> svc = dbService.getSerivce(token);
        if (svc.isEmpty()) {
            return false;
        }

        final Service svcElem = svc.get();
        if (userID != svcElem.getTokenOwner()) {
            return false;
        }

        return true;
    }

    public boolean hasAlreadyGetMoney(String token, int userID) {
        if (dbService.getSerivce(token).get().getMoneyOwnerList() == null) {
            return false;
        }
        return dbService.getSerivce(token).get().getMoneyOwnerList().contains(userID);
    }

    public boolean isDistributedOver(String token) {
        Service svc = dbService.getSerivce(token).get();
        int distributedIdx = svc.getDistributedMoneyIdx();
        if (distributedIdx == svc.getDistributeMoneyInfo().size()) {
            return true;
        }

        return false;
    }
}