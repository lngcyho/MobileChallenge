package com.cyho.lng.currencyconversionapp.main;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * Created by LNG on 3/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {
    //No data - get Data
    //Has data and < 30min - Cache data
    //Has data and > 30min - get Data
    @Mock
    MainActivity activity;

    @Mock
    SharedPreferences sharedPref;

    @Mock
    SharedPreferences.Editor editor;

    @Mock
    FileInputStream fileInputStream;

    @Mock
    FileOutputStream fileOutputStream;

    private MainPresenter mainPresenter;

    @Before
    public void setUp() throws Exception {
        mainPresenter = new MainPresenter(activity);
    }

    @Test
    public void invalidInputs() {
        final String amount = "";
        final String currency = "";

        when(activity.getActivity()).thenReturn(activity);
        when(activity.getActivity().getPreferences(Context.MODE_PRIVATE)).thenReturn(sharedPref);

        assertFalse(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        mainPresenter.requestCurrencyConversion(amount, currency);

        assertFalse(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));
    }

    @Test
    public void fetchFromServerDueToNoCache() throws IOException {
        final String amount = "123.45";
        final Float floatAmount = Float.parseFloat(amount);
        final String currency = "CAD";

        final String currencyListPath = "currency/currencyList.json";

        when(activity.getActivity()).thenReturn(activity);
        when(activity.getActivity().getPreferences(Context.MODE_PRIVATE)).thenReturn(sharedPref);
        when(sharedPref.getLong(currency, 0)).thenReturn(0L);
        when(sharedPref.edit()).thenReturn(editor);
        when(activity.openFileOutput(currency, Context.MODE_PRIVATE))
            .thenReturn(new FileOutputStream("./src/test/resources/" +
                currencyListPath));

        assertFalse(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        Gson gson = new Gson();
        URL url = ClassLoader.getSystemResource(currencyListPath);
        List<CurrencyConversion> listOfCurrencyConversion = gson.fromJson(new InputStreamReader(url.openStream()),
            new TypeToken<List<CurrencyConversion>>() {}.getType());

        mainPresenter.updateCurrencyFields(currency, floatAmount, listOfCurrencyConversion);

        when(sharedPref.getLong(currency, 0)).thenReturn(System.currentTimeMillis());

        assertTrue(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        when(activity.openFileInput(currency)).thenReturn(new FileInputStream("./src/test/resources/" +
            currencyListPath));

        List<CurrencyConversion> listOfCurrencyConversionFromInternalStorage = mainPresenter.getRatesFromInternalStorage
            (currency);

        //Check size
        assertEquals(listOfCurrencyConversion.size(), listOfCurrencyConversionFromInternalStorage.size());

        //Check if values are the same
        for (int index = 0; index < listOfCurrencyConversion.size(); index++) {
            CurrencyConversion fromFile = listOfCurrencyConversion.get(index);
            CurrencyConversion internalStorage = listOfCurrencyConversionFromInternalStorage.get(index);
            assertEquals(fromFile.getCountryCode(), internalStorage.getCountryCode());
            assertEquals(fromFile.getCountryRate(), internalStorage.getCountryRate());
        }
    }

    @Test
    public void fetchFromServerBecauseCacheInvalidates() throws IOException {
        final String amount = "123.45";
        final Float floatAmount = Float.parseFloat(amount);
        final String currency = "CAD";
        final String currencyListPath = "currency/currencyList.json";

        when(activity.getActivity()).thenReturn(activity);
        when(activity.getActivity().getPreferences(Context.MODE_PRIVATE)).thenReturn(sharedPref);
        when(sharedPref.getLong(currency, 0)).thenReturn(1L);
        when(sharedPref.edit()).thenReturn(editor);
        when(activity.openFileOutput(currency, Context.MODE_PRIVATE))
            .thenReturn(new FileOutputStream("./src/test/resources/" +
                currencyListPath));

        assertTrue(mainPresenter.hasCacheCopy(currency));
        assertTrue(mainPresenter.hasCacheCopyExpired(currency));

        Gson gson = new Gson();
        URL url = ClassLoader.getSystemResource(currencyListPath);
        List<CurrencyConversion> listOfCurrencyConversion = gson.fromJson(new InputStreamReader(url.openStream()),
            new TypeToken<List<CurrencyConversion>>() {}.getType());

        mainPresenter.updateCurrencyFields(currency, floatAmount, listOfCurrencyConversion);

        when(sharedPref.getLong(currency, 0)).thenReturn(System.currentTimeMillis());

        assertTrue(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        when(activity.openFileInput(currency)).thenReturn(new FileInputStream("./src/test/resources/" +
            currencyListPath));

        List<CurrencyConversion> listOfCurrencyConversionFromInternalStorage = mainPresenter.getRatesFromInternalStorage
            (currency);

        //Check size
        assertEquals(listOfCurrencyConversion.size(), listOfCurrencyConversionFromInternalStorage.size());

        //Check if values are the same
        for (int index = 0; index < listOfCurrencyConversion.size(); index++) {
            CurrencyConversion fromFile = listOfCurrencyConversion.get(index);
            CurrencyConversion internalStorage = listOfCurrencyConversionFromInternalStorage.get(index);
            assertEquals(fromFile.getCountryCode(), internalStorage.getCountryCode());
            assertEquals(fromFile.getCountryRate(), internalStorage.getCountryRate());
        }
    }

    @Test
    public void fetchCacheCopy() throws IOException {
        final String amount = "123.45";
        final String currency = "CAD";
        final String currencyListPath = "currency/currencyList.json";

        when(activity.getActivity()).thenReturn(activity);
        when(activity.getActivity().getPreferences(Context.MODE_PRIVATE)).thenReturn(sharedPref);
        when(sharedPref.getLong(currency, 0)).thenReturn(System.currentTimeMillis());

        when(activity.openFileInput(currency)).thenReturn(new FileInputStream("./src/test/resources/" +
            currencyListPath));

        assertTrue(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        mainPresenter.requestCurrencyConversion(amount, currency);

        assertTrue(mainPresenter.hasCacheCopy(currency));
        assertFalse(mainPresenter.hasCacheCopyExpired(currency));

        Gson gson = new Gson();
        URL url = ClassLoader.getSystemResource(currencyListPath);
        List<CurrencyConversion> listOfCurrencyConversion = gson.fromJson(new InputStreamReader(url.openStream()),
            new TypeToken<List<CurrencyConversion>>() {}.getType());

        when(activity.openFileInput(currency)).thenReturn(new FileInputStream("./src/test/resources/" +
            currencyListPath));

        List<CurrencyConversion> listOfCurrencyConversionFromInternalStorage = mainPresenter
            .getRatesFromInternalStorage(currency);

        //Check size
        assertEquals(listOfCurrencyConversion.size(), listOfCurrencyConversionFromInternalStorage.size());

        //Check if values are the same
        for (int index = 0; index < listOfCurrencyConversion.size(); index++) {
            CurrencyConversion fromFile = listOfCurrencyConversion.get(index);
            CurrencyConversion internalStorage = listOfCurrencyConversionFromInternalStorage.get(index);
            assertEquals(fromFile.getCountryCode(), internalStorage.getCountryCode());
            assertEquals(fromFile.getCountryRate(), internalStorage.getCountryRate());
        }
    }

    @Test
    public void properConversionAmountShouldBeDisplayedOnScreenIfAmountAndRatesAreProperlyFetched() throws IOException {
        final String amount = "123.45";
        final Float floatAmount = Float.parseFloat(amount);
        final String currencyListPath = "currency/currencyList.json";

        Gson gson = new Gson();
        URL url = ClassLoader.getSystemResource(currencyListPath);
        List<CurrencyConversion> listOfCurrencyConversion = gson.fromJson(new InputStreamReader(url.openStream()),
            new TypeToken<List<CurrencyConversion>>() {}.getType());
        List<CurrencyConversion> listOfCurrencyAmount = mainPresenter.getListOfCurrencyConversionAmount(floatAmount,
            listOfCurrencyConversion);

        //Check size
        assertEquals(listOfCurrencyAmount.size(), listOfCurrencyConversion.size());

        //Check if values are the same
        for (int index = 0; index < listOfCurrencyAmount.size(); index++) {
            CurrencyConversion currencyAmount = listOfCurrencyAmount.get(index);
            CurrencyConversion currencyRate = listOfCurrencyConversion.get(index);
            assertEquals(currencyRate.getCountryCode(), currencyAmount.getCountryCode());

            Float value = currencyRate.getCountryRate() * floatAmount;
            assertEquals(value, currencyAmount.getCountryRate());
        }
    }
}