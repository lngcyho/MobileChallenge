package com.cyho.lng.currencyconversionapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyho.lng.currencyconversionapp.R;
import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.List;

/**
 * Created by LNG on 3/6/2017.
 */
public class CurrencyConversionAdapter extends RecyclerView.Adapter<CurrencyConversionAdapter
    .CurrencyConversionViewHolder> {

    private final Context context;
    private final List<CurrencyConversion> listOfCurrencyConversion;

    public CurrencyConversionAdapter(@NonNull final Context context, @NonNull final List<CurrencyConversion>
        listOfCurrencyConversion) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        if (listOfCurrencyConversion == null) {
            throw new NullPointerException("listOfCurrencyConversion cannot be null");
        }
        this.context = context;
        this.listOfCurrencyConversion = listOfCurrencyConversion;
    }

    @Override
    public CurrencyConversionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_currency_conversion, parent, false);
        CurrencyConversionViewHolder currencyConversionViewHolder = new CurrencyConversionViewHolder(view);
        return currencyConversionViewHolder;
    }

    @Override
    public void onBindViewHolder(CurrencyConversionViewHolder holder, int position) {
        CurrencyConversion currencyConversionItem = listOfCurrencyConversion.get(position);

        holder.currencyCodeView.setText(currencyConversionItem.getCountryCode());
        holder.currencyRateView.setText(currencyConversionItem.getCountryRate().toString());
    }

    @Override
    public int getItemCount() {
        return listOfCurrencyConversion.size();
    }

    protected class CurrencyConversionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.country_code)
        protected TextView currencyCodeView;

        @BindView(R.id.currency_rate)
        protected TextView currencyRateView;

        public CurrencyConversionViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
