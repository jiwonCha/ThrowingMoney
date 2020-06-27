package com.throwing.money.type;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ServiceInfo {
    private String createdTime;
    private int originalMoney;
    private int distributedMoney;
    ArrayList<DistributedMoneyInfo> distributedMoneyInfo;
}

