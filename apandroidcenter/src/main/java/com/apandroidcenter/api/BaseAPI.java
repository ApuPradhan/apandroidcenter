package com.apandroidcenter.api;

import static com.apandroidcenter.api.BaseAPI.JsonRequest;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.misc.AsyncTask;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.apandroidcenter.BaseUtils;
import com.apandroidcenter.BaseConstants;
import com.apandroidcenter.type.CaseType;
import com.apandroidcenter.type.DataAccessType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

enum RequestType {
    JsonRequest, MultiPartRequest
}

enum MultipartRequestType {
    SimpleMultipartRequest, OkHttpClient
}

public abstract class BaseAPI implements Request.Method, BaseAPIHelper {

    protected static DataAccessType _dataAccessType = BaseConstants.dataAccessType;
    protected static HashMap<String, String> _headers = new HashMap<String, String>();
    protected static CaseType reqMultipartBodyNamingType = CaseType.CAMEL_CASE;
    protected static FieldNamingPolicy reqBodyNamingType = FieldNamingPolicy.IDENTITY;
    protected static FieldNamingPolicy resBodyNamingType = FieldNamingPolicy.IDENTITY;


    /**
     * asyncProcess would be use when Developer wants to run request process in Background.
     * *** Important: Developer will use it when app are visible. It's not like a foreground process.
     * **** Instructions: Don't use this when app are not visible.
     */
    protected static boolean _asyncProcess = false;
    protected static MultipartRequestType _multipartRequestType = MultipartRequestType.OkHttpClient;


    /**
     * Creates a new request.
     *
     * @param context      A {@link Context} to use for creating the cache dir.
     * @param method       the HTTP method to use
     * @param url          URL to fetch the JSON from
     * @param headers      Request Headers to be added
     * @param requestModel A {@link T} to post with the request. Null is allowed and
     *                     indicates no parameters will be posted along with request.
     * @param jsonCallBack {@link JsonCallBack} for Response, Error
     */
    protected static <U, T> void JsonRequest(final Context context, final int method, String url, Map<String, String> headers, T requestModel, @NonNull final JsonCallBack<U> jsonCallBack) {

        if (_dataAccessType != DataAccessType.API) {
            switch (_dataAccessType) {
                case DummyJson:
                    DummyJsonRequest(context, url, jsonCallBack);
                    break;
            }
            return;
        }

        if (_asyncProcess) {
            _asyncProcess = false;
            RequestParam<T, U> param = new RequestParam<T, U>(context, method, url, headers, requestModel, jsonCallBack);
            param.setRequestType(RequestType.JsonRequest);

            AsyncRequest task = new AsyncRequest();
            task.execute(param);
            return;
        }

        // Obtain the actual type arguments of the CallBack parameter
        Type[] actualTypeArguments = ((ParameterizedType) jsonCallBack.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        // Obtain the class object of T by casting the first element of the actualTypeArguments array to Class
        Class<U> type = (Class<U>) actualTypeArguments[0];

        JSONObject reqBody = null;
        if (requestModel != null) {
            try {
                reqBody = BaseAPIHelper.toJSONObject(requestModel, reqBodyNamingType);
            } catch (JSONException e) {
                e.printStackTrace();
                jsonCallBack.onError(e.getMessage(), e);
            }
        }

        switch (method) {
            case DELETE:
                if (!BaseUtils.isJSONObjectNull(reqBody)) {
                    String params = null;
                    try {
                        params = BaseUtils.convertJSONObjectToURLParams(reqBody);
                        url += "?" + params;
                    } catch (UnsupportedEncodingException | JSONException e) {
                        jsonCallBack.onError(e.getMessage(), e);
                    }
                }
                break;
            case PUT:
                break;
            case GET:
                if (!BaseUtils.isJSONObjectNull(reqBody)) {
                    String params = null;
                    try {
                        params = BaseUtils.convertJSONObjectToURLParams(reqBody);
                        url += "?" + params;
                    } catch (UnsupportedEncodingException | JSONException e) {
                        jsonCallBack.onError(e.getMessage(), e);
                    }
                }
                break;

        }

        Log.i(BaseConstants.TAG, "Headers: " + method + " " + url + "\n" + "Body: " + reqBody);

        Response.Listener<JSONObject> resListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(BaseConstants.TAG, "Response: " + response);
                try {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(resBodyNamingType).create();
                    U data = gson.fromJson(response.toString(), type);
                    jsonCallBack.onResponse(data);
                } catch (Exception e) {
                    //RepoertErrorInLog("onResponseError: ", e);
                    jsonCallBack.onError(e.getMessage(), e);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(BaseConstants.TAG, "Error: " + error);
                jsonCallBack.onError(error.getMessage() != null ? error.getMessage() : "We have a little problem, our developer are working on it...", error);
                //VolleyErrorHandler(context, error);
            }
        };

        JsonObjectRequest jObj = new JsonObjectRequest(method, url, reqBody, resListener, errorListener);
        jObj.setHeaders(headers);
        jObj.setShouldCache(false);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jObj);
        /*requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
            }
        });*/
    }

    /**
     * Creates a new request.
     *
     * @param context      A {@link Context} to use for creating the cache dir.
     * @param method       the HTTP method to use
     * @param url          URL to fetch the JSON from
     * @param headers      Request Headers to be added
     * @param jsonCallBack {@link JsonCallBack} for Response, Error
     */
    protected static <U, T> void JsonRequest(final Context context, int method, final String url, Map<String, String> headers, @NonNull final JsonCallBack<U> jsonCallBack) {

        if (_dataAccessType != DataAccessType.API) {
            switch (_dataAccessType) {
                case DummyJson:
                    DummyJsonRequest(context, url, jsonCallBack);
                    break;
            }
            return;
        }

        if (_asyncProcess) {
            _asyncProcess = false;
            RequestParam<T, U> param = new RequestParam<T, U>(context, method, url, headers, jsonCallBack);
            param.setRequestType(RequestType.JsonRequest);

            AsyncRequest task = new AsyncRequest();
            task.execute(param);
            return;
        }

        // Obtain the actual type arguments of the CallBack parameter
        Type[] actualTypeArguments = ((ParameterizedType) jsonCallBack.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        // Obtain the class object of T by casting the first element of the actualTypeArguments array to Class
        Class<U> type = (Class<U>) actualTypeArguments[0];

        Log.i(BaseConstants.TAG, "Headers: " + method + " " + url);

        Response.Listener<JSONObject> resListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(BaseConstants.TAG, "Response: " + response);
                try {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(resBodyNamingType).create();
                    Log.d(BaseConstants.TAG, response.toString());
                    U data = gson.fromJson(response.toString(), type);
                    jsonCallBack.onResponse(data);
                } catch (Exception e) {
                    //RepoertErrorInLog("onResponseError: ", e);
                    jsonCallBack.onError(e.getMessage(), e);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(BaseConstants.TAG, "Error: " + error);
                jsonCallBack.onError(error.getMessage() != null ? error.getMessage() : "We have a little problem, our developer are working on it...", error);
                //VolleyErrorHandler(context, error);
            }
        };

        JsonObjectRequest jObj = new JsonObjectRequest(method, url, null, resListener, errorListener);
        jObj.setHeaders(headers);
        jObj.setShouldCache(false);
        //jObj.setPriority(Request.Priority.HIGH);
        jObj.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jObj);
        /*requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
            }
        });*/
    }

    //Todo: MultiPartRequest is not completely done
    protected static <U, T> void MultiPartRequest(final Context context, final String url, Map<String, String> headers, T requestModel, final MultipartCallBack<U> multipartCallBack) {

        switch (_multipartRequestType) {
            case OkHttpClient:
                /*RequestParam<T, U> param = new RequestParam<T, U>();
                param.context = context;
                param.url = url;
                param.headers = headers;
                param.requestModel = requestModel;
                param.multipartCallBack = multipartCallBack;

                OkHttpClientMultipartRequest task = new OkHttpClientMultipartRequest();
                task.execute(param);*/

                OkHttpClientMultipartRequest(context, url, headers, requestModel, multipartCallBack);
                break;
            case SimpleMultipartRequest:
                SimpleMultiPartRequest(context, url, headers, requestModel, multipartCallBack);
                break;
        }
    }

    protected static <U, T> void OkHttpClientMultipartRequest(final Context context,
                                                              final String url,
                                                              Map<String, String> headers,
                                                              T requestModel,
                                                              final MultipartCallBack<U> multipartCallBack) {

        // Obtain the actual type arguments of the CallBack parameter
        Type[] actualTypeArguments = ((ParameterizedType) multipartCallBack.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        // Obtain the class object of T by casting the first element of the actualTypeArguments array to Class
        Class<U> type = (Class<U>) actualTypeArguments[0];

        Log.i(BaseConstants.TAG, "Headers: " + " " + url);


        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        try {
            builder = BaseAPIHelper.toMultiPartBodyBuilder(context, requestModel, reqMultipartBodyNamingType, builder);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        okhttp3.Request.Builder reqBuilder = new okhttp3.Request.Builder();
        reqBuilder.url(url);
        reqBuilder.post(builder.build());

        //***
        headers.forEach(reqBuilder::addHeader);

        okhttp3.Request request = reqBuilder.build();

        List<ConnectionSpec> connectionSpecs = new ArrayList<>();
        connectionSpecs.add(ConnectionSpec.COMPATIBLE_TLS);

        OkHttpClient client = new OkHttpClient();
        client.connectionSpecs();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                multipartCallBack.onError(e != null ? e.getMessage() : "", e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String strResponse = response.body().string();
                Log.d(BaseConstants.TAG, "Response: " + strResponse);
                try {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(resBodyNamingType).create();
                    U data = gson.fromJson(strResponse, type);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            multipartCallBack.onResponse(data);
                        }
                    });
                    //Log.i(TAG, String.valueOf(data));
                } catch (Exception e) {
                    Log.e(BaseConstants.TAG, "Error: " + e);
                    multipartCallBack.onError(e.getMessage(), e);
                }
            }
        });
    }

    protected static <U, T> void SimpleMultiPartRequest(final Context context, final String url, Map<String, String> headers, T requestModel, final MultipartCallBack<U> multipartCallBack) {

        // Obtain the actual type arguments of the CallBack parameter
        Type[] actualTypeArguments = ((ParameterizedType) multipartCallBack.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
        // Obtain the class object of T by casting the first element of the actualTypeArguments array to Class
        Class<U> type = (Class<U>) actualTypeArguments[0];


        Log.i(BaseConstants.TAG, "Headers: " + " " + url);

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(BaseConstants.TAG, "Response: " + response);
                try {
                    Gson gson = new GsonBuilder().setFieldNamingPolicy(resBodyNamingType).create();
                    Log.d(BaseConstants.TAG, response.toString());
                    U data = gson.fromJson(response.toString(), type);
                    multipartCallBack.onResponse(data);
                } catch (Exception e) {
                    //RepoertErrorInLog("onResponseError: ", e);
                    multipartCallBack.onError(e.getMessage(), e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                multipartCallBack.onError(error.getMessage(), error);
            }
        });

        smr.setOnProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                multipartCallBack.onProgress(transferredBytes, totalSize);
                //multipartCallBack.onProgress((int) ((transferredBytes / ((float) totalSize)) * 100));
            }
        });

        SimpleMultiPartRequest sm = null;
        try {
            sm = BaseAPIHelper.ToSimpleMultiPartRequest(requestModel, reqMultipartBodyNamingType, smr);
            sm.setHeaders(headers);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.i(BaseConstants.TAG, "dataSend: " + sm.getMultipartParams());

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(sm);
        mRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<String>() {
            @Override
            public void onRequestFinished(Request<String> request) {
                //CloseProgressDialog();
            }
        });
    }

    protected static <U> void DummyJsonRequest(final Context context, final String url, final JsonCallBack<U> jsonCallBack) {
        String jsonFileName = BaseUtils.getStringWithoutSpecialCharacterAndNumeric(url);
        Log.i(BaseConstants.TAG, "DummyJson: " + jsonFileName);

        AssetManager assetManager = context.getAssets(); // get a reference to the AssetManager
        boolean isFileOK = true;
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(jsonFileName + ".json"); // replace with the actual filename
        } catch (IOException e) {
            isFileOK = false;
            jsonCallBack.onError(e.getMessage(), e);
        }

        if (!isFileOK) {
            return;
        }

        JSONObject response = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String statement;
        try {
            while ((statement = reader.readLine()) != null) {
                if (statement.trim().length() > 0) {
                    buffer.append(statement);
                }
            }
            inputStream.close(); // close the input stream after use
        } catch (IOException e1) { // TODO Auto-generated catch block
            //e1.printStackTrace();
            jsonCallBack.onError(e1.getMessage(), e1);
        }
        if (buffer.length() > 0) {
            try {
                response = new JSONObject(buffer.toString());
            } catch (JSONException e) {
                //e.printStackTrace();
                jsonCallBack.onError(e.getMessage(), e);
            }
        }

        if (response != null) {
            // Obtain the actual type arguments of the CallBack parameter
            Type[] actualTypeArguments = ((ParameterizedType) jsonCallBack.getClass().getGenericInterfaces()[0]).getActualTypeArguments();
            // Obtain the class object of T by casting the first element of the actualTypeArguments array to Class
            Class<U> type = (Class<U>) actualTypeArguments[0];

            Log.i(BaseConstants.TAG, "DummyResponse: " + response);
            try {
                Gson gson = new GsonBuilder().setFieldNamingPolicy(resBodyNamingType).create();
                U data = gson.fromJson(response.toString(), type);
                jsonCallBack.onResponse(data);
            } catch (Exception e) {
                //RepoertErrorInLog("onResponseError: ", e);
                jsonCallBack.onError(e.getMessage(), e);
            }
        }

    }
}

class AsyncRequest extends AsyncTask<RequestParam, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(RequestParam... params) {
        RequestParam requestParam = params[0];
        switch (requestParam.getRequestType()) {
            case JsonRequest:
                if (requestParam.requestModel == null) {
                    //ex: GET request
                    JsonRequest(requestParam.context, requestParam.method, requestParam.url, requestParam.headers, requestParam.jsonCallBack);
                } else {
                    JsonRequest(requestParam.context, requestParam.method, requestParam.url, requestParam.headers, requestParam.requestModel, requestParam.jsonCallBack);
                }
                break;
            case MultiPartRequest:
                break;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}

/*ProgressDialog progress = new ProgressDialog(context);
        progress.setIndeterminate(false);
        progress.setMax(100);
        progress.setMessage("Uploading...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();


progress.dismiss();


int percentage = (int) ((transferredBytes / ((float) totalSize)) * 100);
                if (progress != null) {
                    progress.setProgress(percentage);
                }
                if (percentage == 100) {
                    progress.dismiss();
                }
*/