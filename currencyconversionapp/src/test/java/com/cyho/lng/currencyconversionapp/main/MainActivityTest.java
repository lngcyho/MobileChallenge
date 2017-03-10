package com.cyho.lng.currencyconversionapp.main;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.cyho.lng.currencyconversionapp.R;
import com.cyho.lng.currencyconversionapp.runner.CustomRobolectricTestRunner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by LNG on 3/9/2017.
 */
@RunWith(CustomRobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class MainActivityTest {

    private EditText editText;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        editText = (EditText)activity.findViewById(R.id.currency_amount);
        spinner = (Spinner)activity.findViewById(R.id.currency_dropdown);
        recyclerView = (RecyclerView)activity.findViewById(R.id.currency_list);
    }

    @Test
    public void checkInitializedState() {
        assertNull(recyclerView.getAdapter());
        assertEquals("", editText.getText().toString());
        assertEquals(activity.getResources().getStringArray(R.array.currency_arrays).length,
            spinner.getAdapter().getCount());
    }

    @Test
    public void onSelectNothingShouldDisplayNothingOnRecyclerView() {
        final String currencyValue = "123.45";
        spinner.setSelection(-1);
        editText.setText(currencyValue);

        //Assert Check
        assertEquals(currencyValue, editText.getText().toString());
        assertEquals(activity.getResources().getStringArray(R.array.currency_arrays).length,
            spinner.getAdapter().getCount());
        assertNull(spinner.getSelectedItem());
        assertNull(recyclerView.getAdapter());
    }

    @Test
    public void onSelectItemWithNoCurrencyValueShouldDisplayNothingOnRecyclerView() {
        final String currencyValue = "";
        final String currency = "CAD";
        editText.setText(currencyValue);

        ArrayAdapter arrayAdapter = (ArrayAdapter)spinner.getAdapter();
        spinner.setSelection(arrayAdapter.getPosition(currency));

        //Assert Check
        assertEquals(currencyValue, editText.getText().toString());
        assertEquals(activity.getResources().getStringArray(R.array.currency_arrays).length,
            spinner.getAdapter().getCount());
        assertEquals(currency, spinner.getSelectedItem());
        assertNull(recyclerView.getAdapter());
    }

    @Ignore("This is a End to End test. Should probably be moved to Instrumental?")
    @Test
    public void onSelectItemWithCurrencyValueShouldDisplayListOfCurrencyConversionOnRecyclerView() {
        final String currencyValue = "123.45";
        final String currency = "CAD";
        editText.setText(currency);

        ArrayAdapter arrayAdapter = (ArrayAdapter)spinner.getAdapter();
        spinner.setSelection(arrayAdapter.getPosition(currency));

        //Assert Check
        assertEquals(currencyValue, editText.getText().toString());
        assertEquals(activity.getResources().getStringArray(R.array.currency_arrays).length,
            spinner.getAdapter().getCount());
        assertEquals(currency, spinner.getSelectedItem());
        assertNotNull(recyclerView.getAdapter());
        assertEquals(recyclerView.getAdapter().getItemCount(),
            activity.getResources().getStringArray(R.array.currency_arrays));
    }
}