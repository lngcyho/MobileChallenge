package com.cyho.lng.currencyconversionapp.asynctask;

import android.content.Context;

import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

/**
 * Created by LNG on 3/9/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class RetrieveCurrencyRateAsyncTaskTest {
    @Mock
    Context context;

    @Mock
    RetrieveCurrencyRateAsyncTask.OnCurrencyRateRetrieveCallback callback;

    final private String currencyResponsePath = "currency/currencyResponse.json";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void shouldReturnNpeIfContextIsNull() {
        new RetrieveCurrencyRateAsyncTask(null, null);

        //Assertion Checks
        fail("Should have thrown an exception!");
    }

    @Test
    public void shouldReturnListOfCurrencyConversionIfProperResponseIsReturned() {
        RetrieveCurrencyRateAsyncTask retrieveCurrencyRateAsyncTask = new RetrieveCurrencyRateAsyncTask(new
            WeakReference<>(context), callback);
        RetrieveCurrencyRateAsyncTask.CurrencyConversionResponse currencyConversionResponse = null;

        //get Json from file
        try {
            URL url = ClassLoader.getSystemResource(currencyResponsePath);
            Gson gson = new Gson();
            currencyConversionResponse = gson.fromJson(new InputStreamReader(url.openStream()),
                RetrieveCurrencyRateAsyncTask.CurrencyConversionResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to retrieve JSON");
        }

        retrieveCurrencyRateAsyncTask.onPostExecute(currencyConversionResponse);
        verify(callback).onCurrencyRateRetrieve((List<CurrencyConversion>)any(Object.class));
    }

    @Test
    public void shouldReturnListOfCurrencyConversionIfMaleResponseIsReturned() {
        RetrieveCurrencyRateAsyncTask retrieveCurrencyRateAsyncTask = new RetrieveCurrencyRateAsyncTask(new
            WeakReference<>(context), callback);

        retrieveCurrencyRateAsyncTask.onPostExecute(null);
        verify(callback).onCurrencyRateRetrieve((List<CurrencyConversion>)any(Object.class));
    }
}