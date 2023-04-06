package com.apandroidcenter.api;

import android.content.Context;

import java.util.Map;

public class RequestParam<T, U> {
    public Context context;
    public int method;
    public String url;
    public Map<String, String> headers;
    public T requestModel;
    public JsonCallBack<U> jsonCallBack;
    public MultipartCallBack<U> multipartCallBack;
    private RequestType requestType;


    public RequestParam() {
    }

    public RequestParam(Context context) {
        this.context = context;
    }

    public RequestParam(Context context, int method, String url, Map<String, String> headers, T requestModel, JsonCallBack<U> jsonCallBack) {
        this.context = context;
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.requestModel = requestModel;
        this.jsonCallBack = jsonCallBack;
    }

    public RequestParam(Context context, int method, String url, Map<String, String> headers, JsonCallBack<U> jsonCallBack) {
        this.context = context;
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.jsonCallBack = jsonCallBack;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
