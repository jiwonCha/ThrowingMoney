package com.throwing.money.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DistributedMoneyInfo {
    private Integer money;
    private Integer moneyOwner;
};