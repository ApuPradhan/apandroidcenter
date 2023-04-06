package com.apandroidcenter.service;

import android.content.Context;

import com.apandroidcenter.api.BaseAPI;
import com.apandroidcenter.api.JsonCallBack;

public class APIService extends BaseAPI {

    /*public interface Urls {
        String Login = "login.html";
    }*/

    public static <T> void getUrls(final Context ctx, final JsonCallBack<T> callBack) {
        JsonRequest(ctx,
                GET,
                "https://script.google.com/macros/s/AKfycbz4XBr4lGfBn_f5dMRl76RSUFB_ix4tN0oS9lcK7YXEuxwnSzzwHNMSM_j1zf9HSHS8/exec?title=systemInfo",
                _headers,
                callBack);
    }
}