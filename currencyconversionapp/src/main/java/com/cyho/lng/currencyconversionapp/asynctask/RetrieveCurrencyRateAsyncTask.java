package com.cyho.lng.currencyconversionapp.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cyho.lng.currencyconversionapp.R;
import com.cyho.lng.currencyconversionapp.model.CurrencyConversion;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LNG on 3/6/2017.
 */

public class RetrieveCurrencyRateAsyncTask
    extends AsyncTask<String, String, RetrieveCurrencyRateAsyncTask.CurrencyConversionResponse> {

    //Interface
    public interface OnCurrencyRateRetrieveCallback {
        void onCurrencyRateRetrieve(List<CurrencyConversion> listOfCurrencyConversion);
    }

    //Response Model
    public class CurrencyConversionResponse {
        @SerializedName("base")
        public String base;
        @SerializedName("date")
        public String date;
        @SerializedName("rates")
        public Map<String, Float> rates;
    }

    private final Context context;
    private final OnCurrencyRateRetrieveCallback onCurrencyRateRetrieveCallback;
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private final String CONTENT_TYPE = "Content-Type";
    private final String CONTENT_TYPE_VALUE = "application/json";

    public RetrieveCurrencyRateAsyncTask(@NonNull final WeakReference<Context> context,
        @Nullable final OnCurrencyRateRetrieveCallback onCurrencyRateRetrieveCallback) {
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }
        this.context = context.get();
        this.onCurrencyRateRetrieveCallback = onCurrencyRateRetrieveCallback;
    }

    @Override
    protected CurrencyConversionResponse doInBackground(String... params) {
        String currency = (params.length > 0) ? params[0] : "";

        try {
            Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                .url(context != null ? context.getResources().getString(R.string.foreign_exchange_url, currency) : "")
                .build();
            Log.e(getClass().getName(), "Request: \n" + "Header: " + request.headers().toString() + "\nURL: " +
                request.url().toString());
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            Log.e(getClass().getName(), "Response: " + responseString);

            CurrencyConversionResponse currencyConversionResponse = gson.fromJson(responseString.replaceAll("\\s+", ""),
                CurrencyConversionResponse.class);
            return currencyConversionResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<CurrencyConversion> convertToCurrencyConversion(
        CurrencyConversionResponse currencyConversionResponse) {
        List<CurrencyConversion> listOfCurrencyConversion = new ArrayList<>();
        if (currencyConversionResponse != null) {
            Map<String, Float> mapOfRates = currencyConversionResponse.rates;
            for (Map.Entry<String, Float> pair : mapOfRates.entrySet()) {
                String key = pair.getKey();
                Float value = pair.getValue();
                listOfCurrencyConversion.add(new CurrencyConversion(key, value));
            }
        }
        return listOfCurrencyConversion;
    }

    @Override
    protected void onPostExecute(CurrencyConversionResponse currencyConversions) {
        super.onPostExecute(currencyConversions);
        if (onCurrencyRateRetrieveCallback != null) {
            onCurrencyRateRetrieveCallback.onCurrencyRateRetrieve(convertToCurrencyConversion(currencyConversions));
        }
    }
}
