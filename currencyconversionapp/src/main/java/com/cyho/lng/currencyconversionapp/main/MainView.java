package com.cyho.lng.currencyconversionapp.main;

import android.app.Activity;

import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;

import java.util.List;

/**
 * Created by LNG on 3/6/2017.
 */

public interface MainView {
    Activity getActivity();
    void updateCurrencyAdapter(List<CurrencyConversion> listOfCurrencyConversion);
}
