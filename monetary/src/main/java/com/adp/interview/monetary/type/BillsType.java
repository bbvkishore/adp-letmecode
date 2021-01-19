package com.adp.interview.monetary.type;

public enum BillsType {
    ONE_DOLLAR(1),
    TWO_DOLLARS(2),
    FIVE_DOLLARS(5),
    TEN_DOLLARS(10),
    TWENTY_DOLLARS(20),
    FIFTY_DOLLARS(50),
    HUNDRED_DOLLARS(100);

    public final Integer value;

    BillsType(Integer value){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }
}
