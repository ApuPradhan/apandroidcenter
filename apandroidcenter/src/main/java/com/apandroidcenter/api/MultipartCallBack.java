package com.apandroidcenter.api;

public interface MultipartCallBack<T> {
    void onResponse(T response);

    void onError(String msg, Exception e);

    void onProgress(long transferredBytes, long totalSize);
}