package com.apandroidcenter.api;

import android.content.Context;
import android.net.Uri;

import com.android.volley.request.SimpleMultiPartRequest;
import com.apandroidcenter.BaseUtils;
import com.apandroidcenter.CaseConverter;
import com.apandroidcenter.type.CaseType;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface BaseAPIHelper {

    static JSONObject toJSONObject(Object obj, FieldNamingPolicy fNP) throws JSONException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        if (fNP != null) {
            gsonBuilder.setFieldNamingPolicy(fNP);
        }
        Gson gson = gsonBuilder.create();
        return new JSONObject(gson.toJson(obj));
    }

    static <T> SimpleMultiPartRequest ToSimpleMultiPartRequest(T model, CaseType caseType, SimpleMultiPartRequest sMultiPartRequest) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null) {
                    sMultiPartRequest.addFile(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            } else {
                sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
            }
        }

        return sMultiPartRequest;
    }

    /*static <T> MultipartBody.Builder toMultiPartBodyBuilder(T model, CaseType caseType, MultipartBody.Builder builderFormData) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null){
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), bytes);
                    builder.addFormDataPart("leaveAttachment", "dummy_leave_application.pdf", requestBody);

                    sMultiPartRequest.addFile(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            } else {
                builder.addFormDataPart("leaveToDate", leaveRequest.leaveToDate);
                sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
            }
        }

        return sMultiPartRequest;
    }*/

    static <T> MultipartBody.Builder toMultiPartBodyBuilder(Context context, T model, CaseType caseType, MultipartBody.Builder builderData) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        int i = 0;
        while (i<fields.length){
            if (fields[i].isAnnotationPresent(MultipartFileType.class)) {
                if (fields[i].get(model) != null) {
                    String fileType = Objects.requireNonNull(fields[i].getAnnotation(MultipartFileType.class)).type();
                    String fileName = Objects.requireNonNull(fields[i].getAnnotation(MultipartFileType.class)).fileName();
                    if (fields[i].get(model) instanceof Uri) {
                        byte[] bytes = new byte[0];

                        try {
                            bytes = BaseUtils.getBytesFromContentUri(context, (Uri) fields[i].get(model));
                        } catch (IOException e) {
                            // handle the exception
                        }

                        RequestBody requestBody = RequestBody.create(MediaType.parse(fileType), bytes);
                        builderData.addFormDataPart(CaseConverter.stringToCase(fields[i].getName(), caseType), fileName, requestBody);
                    }
                }
            } else {
                builderData.addFormDataPart(CaseConverter.stringToCase(fields[i].getName(), caseType), fields[i].get(model) != null ? fields[i].get(model).toString() : "");
            }

            i++;
        }


        /*for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null) {
                    String fileType = Objects.requireNonNull(field.getAnnotation(MultipartFileType.class)).type();
                    String fileName = Objects.requireNonNull(field.getAnnotation(MultipartFileType.class)).fileName();
                    if (field.get(model) instanceof byte[]) {
                        byte[] bytes = new byte[0];

                        try {
                            bytes = BaseUtils.getBytesFromContentUri(context, (Uri) fields[i].get(model));
                        } catch (IOException e) {
                            // handle the exception
                        }

                        RequestBody requestBody = RequestBody.create(MediaType.parse(fileType), (byte[]) field.get(model));
                        builderData.addFormDataPart(CaseConverter.stringToCase(field.getName(), caseType), fileName, requestBody);
                    }
                }
            } else {
                builderData.addFormDataPart(CaseConverter.stringToCase(field.getName(), caseType), field.get(model) != null ? field.get(model).toString() : "");
            }
        }*/
        return builderData;
    }

    /*static <T> MultipartBody.Builder toMultiPartBodyBuilder(T model, CaseType caseType, MultipartBody.Builder builderData) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null){
                    byte[] bytes = new byte[0];
                    String fileType = field.getAnnotation(MultipartFileType.class).value(); // get value of MultipartFileType annotation
                    RequestBody requestBody = RequestBody.create(MediaType.parse(fileType), bytes);
                    builderData.addFormDataPart(CaseConverter.stringToCase(field.getName(), caseType), "xyz.pdf", requestBody);
                }
            } else {
                builderData.addFormDataPart(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
            }
        }

        return builderData;
    }*/

    /*static <T> SimpleMultiPartRequest ToSimpleMultiPartRequest(T model, CaseType caseType, SimpleMultiPartRequest sMultiPartRequest) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null){
                    sMultiPartRequest.addFile(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            } else {
                if (field.getType().isEnum() && field.isAnnotationPresent(SerializedName.class)) {
                    Enum<?> enumValue = (Enum<?>) field.get(model);
                    String enumName = enumValue.name();
                    SerializedName annotation = field.getAnnotation(SerializedName.class);
                    sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), annotation.value());
                } else {
                    sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            }
        }

        return sMultiPartRequest;
    }*/


    /*static <T> SimpleMultiPartRequest ToSimpleMultiPartRequest(T model, CaseType caseType, SimpleMultiPartRequest sMultiPartRequest) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null){
                    sMultiPartRequest.addFile(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            } else {
                if (field.getType().isEnum() && field.isAnnotationPresent(SerializedName.class)) {
                    Enum<?> enumValue = (Enum<?>) field.get(model);
                    SerializedName annotation = field.getAnnotation(SerializedName.class);
                    sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), annotation.value());
                } else {
                    sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            }
        }

        return sMultiPartRequest;
    }*/

    /*static <T> SimpleMultiPartRequest ToSimpleMultiPartRequest(T model, CaseType caseType, SimpleMultiPartRequest sMultiPartRequest) throws IllegalAccessException {
        Class<?> cls = model.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MultipartFileType.class)) {
                if (field.get(model) != null){
                    sMultiPartRequest.addFile(CaseConverter.stringToCase(field.getName(), caseType), field.get(model).toString());
                }
            } else {
                Object fieldValue = field.get(model);
                if (fieldValue instanceof LeaveType) {
                    LeaveType leaveType = (LeaveType) fieldValue;
                    SerializedName annotation = leaveType.getClass().getField(leaveType.name()).getAnnotation(SerializedName.class);
                    if (annotation != null) {
                        sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), annotation.value());
                    } else {
                        sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), leaveType.name());
                    }
                } else {
                    sMultiPartRequest.addStringParam(CaseConverter.stringToCase(field.getName(), caseType), fieldValue.toString());
                }
            }
        }

        return sMultiPartRequest;
    }*/


}
