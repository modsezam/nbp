package com.sda.nbp;

import java.util.List;

public class Currency {

    private String effectiveDate;
    private List<Rates> rates;

    public List<Rates> getRates() {
        return rates;

    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
}

class Rates {

    private String currency;
    private String code;
    private double mid;
    private double bid;
    private double ask;

    public String getCode() {
        return code;
    }

    public double getMid() {
        return mid;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }
}
