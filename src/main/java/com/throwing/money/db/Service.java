package com.throwing.money.db;

import java.util.ArrayList;

import com.throwing.money.type.DistributedMoneyInfo;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@RedisHash("serivces")
@Data
public class Service {
    @Id
    private String token;
    private int tokenOwner;

    private String roomID;

    private String createdTime;
    
    private int originalMoney;
    
    private int distributedMoney;
    private int distributedMoneyIdx;
    
    private ArrayList<DistributedMoneyInfo> distributeMoneyInfo;
    private ArrayList<Integer> moneyOwnerList;
}