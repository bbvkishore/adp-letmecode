package com.adp.interview.monetary.type;

public enum CoinsType {

    QUARTERS(25),
    DIMES(10),
    NICKELS(5),
    PENNIES(1);

    public final Integer value;

    CoinsType(Integer coinValue){
        this.value = coinValue;
    }
}
