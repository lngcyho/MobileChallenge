package com.cyho.lng.currencyconversionapp.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.cyho.lng.currencyconversionapp.R;
import com.cyho.lng.currencyconversionapp.adapter.CurrencyConversionAdapter;
import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;

public class MainActivity extends Activity implements MainView {

    @BindView(R.id.currency_amount)
    EditText currencyAmount;

    @BindView(R.id.currency_dropdown)
    Spinner currentDropDown;

    @BindView(R.id.currency_list)
    RecyclerView recyclerView;

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainPresenter = new MainPresenter(this);

        init();
    }

    private void init() {
        final String[] currencyArray = getResources().getStringArray(R.array.currency_arrays);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
            android.R.layout.simple_spinner_item, currencyArray);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentDropDown.setAdapter(adapter);
        currentDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String amount = currencyAmount.getText().toString();
                if (position >= 0 && position < currencyArray.length
                    && !amount.isEmpty()) {
                    String currency = currencyArray[position];
                    mainPresenter.requestCurrencyConversion(amount, currency);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing
                recyclerView.setAdapter(null);
            }
        });
        currencyAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int position = currentDropDown.getSelectedItemPosition();
                String amount = editable.toString();
                if (amount.isEmpty()) {
                    recyclerView.setAdapter(null);
                }

                if (position >= 0 && position < currencyArray.length
                    && !amount.isEmpty()) {
                    String currency = currencyArray[position];
                    mainPresenter.requestCurrencyConversion(amount, currency);
                }
            }
        });
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void updateCurrencyAdapter(List<CurrencyConversion> listOfCurrencyConversion) {
        CurrencyConversionAdapter currencyConversionAdapter = new CurrencyConversionAdapter
            (this, listOfCurrencyConversion);
        recyclerView.setAdapter(currencyConversionAdapter);
    }
}
