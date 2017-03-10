package com.cyho.lng.currencyconversionapp.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyho.lng.currencyconversionapp.asynctask.RetrieveCurrencyRateAsyncTask;
import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LNG on 3/6/2017.
 */

public class MainPresenter {

    private final MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
    }

    public void requestCurrencyConversion(final String amount, final String currency) {
        if (amount == null || amount.isEmpty()
            || currency == null || currency.isEmpty()) {
            return;
        }

        //HTTP or Local Cache?
        final Float currencyValue = Float.parseFloat(amount);

        if (hasCacheCopy(currency) && !hasCacheCopyExpired(currency)) {
            List<CurrencyConversion> listOfCurrencyConversionRate = getRatesFromInternalStorage(currency);
            view.updateCurrencyAdapter(getListOfCurrencyConversionAmount(currencyValue, listOfCurrencyConversionRate));
            return;
        }

        //Retrieve from Server
        RetrieveCurrencyRateAsyncTask retrieveCurrencyRateAsyncTask = new RetrieveCurrencyRateAsyncTask(new
            WeakReference<Context>(view.getActivity()), new RetrieveCurrencyRateAsyncTask
            .OnCurrencyRateRetrieveCallback() {
            @Override
            public void onCurrencyRateRetrieve(final List<CurrencyConversion> listOfCurrencyConversionRate) {
                updateCurrencyFields(currency, currencyValue, listOfCurrencyConversionRate);
            }
        });
        retrieveCurrencyRateAsyncTask.execute(currency);
    }

    protected void updateCurrencyFields(String currency, Float currencyValue, List<CurrencyConversion>
        listOfCurrencyConversionRate) {
        view.updateCurrencyAdapter(
            getListOfCurrencyConversionAmount(currencyValue, listOfCurrencyConversionRate));
        if (listOfCurrencyConversionRate.size() != 0) {
            saveTimeStampToSharedPreference(currency);
            saveRatesToInternalStorage(currency, listOfCurrencyConversionRate);
        }
    }

    protected List<CurrencyConversion> getListOfCurrencyConversionAmount(float amount,
        List<CurrencyConversion> listOfCurrencyConversionRate) {
        List<CurrencyConversion> listOfCurrencyConversionAmount = new ArrayList<>();
        for (CurrencyConversion currencyConversion : listOfCurrencyConversionRate) {
            float currencyAmount = currencyConversion.getCountryRate() * amount;
            listOfCurrencyConversionAmount
                .add(new CurrencyConversion(currencyConversion.getCountryCode(), currencyAmount));
        }
        return listOfCurrencyConversionAmount;
    }

    protected List<CurrencyConversion> getRatesFromInternalStorage(final String currency) {
        try {
            Gson gson = new Gson();
            FileInputStream fileInputStream = view.getActivity().openFileInput(currency);
            List<CurrencyConversion> listOfCurrencyConversion = gson.fromJson(new InputStreamReader(fileInputStream),
                new TypeToken<List<CurrencyConversion>>() {}.getType());
            fileInputStream.close();
            return listOfCurrencyConversion;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveRatesToInternalStorage(final String currency, final List<CurrencyConversion>
        listOfCurrencyConversion) {
        try {
            Gson gson = new Gson();
            FileOutputStream fileOutputStream = view.getActivity().openFileOutput(currency, Context.MODE_PRIVATE);
            fileOutputStream.write(gson.toJson(listOfCurrencyConversion).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean hasCacheCopy(final String currency) {
        return getTimeStamp(currency) != 0;
    }

    protected boolean hasCacheCopyExpired(String currency) {
        return hasCacheCopy(currency) && (System.currentTimeMillis() - getTimeStamp(currency) > (1000 * 60 * 30));
        //30 minutes
    }

    private long getTimeStamp(String currency) {
        SharedPreferences sharedPref = view.getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getLong(currency, 0);
    }

    private void saveTimeStampToSharedPreference(final String currency) {
        SharedPreferences.Editor editor = view.getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putLong(currency, System.currentTimeMillis());
        editor.apply();
    }
}
