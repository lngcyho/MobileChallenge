package com.cyho.lng.currencyconversionapp.model;

/**
 * Created by LNG on 3/6/2017.
 */
public class CurrencyConversion {
    private final String country;
    private final Float rate;

    public CurrencyConversion(final String country, final Float rate) {
        this.country = country;
        this.rate = rate;
    }

    public String getCountryCode () {
        return country;
    }

    public Float getCountryRate() {
        return rate;
    }
}
