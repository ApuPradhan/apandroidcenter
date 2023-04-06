package com.apandroidcenter.api;

public interface JsonCallBack<T> {
    void onResponse(T response);

    void onError(String msg, Exception e);
}