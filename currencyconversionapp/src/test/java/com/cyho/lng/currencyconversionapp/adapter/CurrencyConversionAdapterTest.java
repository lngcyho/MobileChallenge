package com.cyho.lng.currencyconversionapp.adapter;

import android.content.Context;

import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

/**
 * Created by LNG on 3/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CurrencyConversionAdapterTest {
    @Mock
    private Context context;

    private CurrencyConversionAdapter adapter;
    private final List<CurrencyConversion> listOfCurrencyConversion = Arrays
        .asList(new CurrencyConversion("AUD", 1.0f), new CurrencyConversion("CAD", 2.0f),
            new CurrencyConversion("USD", 3.0f));

    @Test
    public void checkListHasBeenPopulatedCorrectly() {
        adapter = new CurrencyConversionAdapter(context, listOfCurrencyConversion);

        //Assertion Checks
        assertEquals(adapter.getItemCount(), listOfCurrencyConversion.size());
    }

    @Test(expected=NullPointerException.class)
    public void shouldReturnNpeIfContextIsNull() {
        adapter = new CurrencyConversionAdapter(null, listOfCurrencyConversion);

        //Assertion Checks
        fail("Should have thrown an exception!");
    }

    @Test(expected=NullPointerException.class)
    public void shouldReturnNpeIfListIsNull() {
        adapter = new CurrencyConversionAdapter(context, null);

        //Assertion Checks
        fail("Should have thrown an exception!");
    }
}