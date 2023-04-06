package com.apandroidcenter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.error.VolleyError;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseUtils {

    public static final int PhoneNumberLength = 10;

    public static String getAppName(Context ctx) {
        return (String) ctx.getPackageManager().getApplicationLabel(ctx.getApplicationInfo());
    }

    public static Calendar GetCalenderFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Calendar GetDate0AM(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, 0);
        return calendar;
    }

    public static Calendar GetDate0AM(Date date) {
        Calendar calendar = GetDate0AM(GetCalenderFromDate(date));
        return calendar;
    }

    public static Calendar GetDate23PM(Calendar date) {
        Calendar calendar = GetDate0AM(date);
        calendar.add(Calendar.HOUR, 24);
        calendar.add(Calendar.SECOND, -1);
        return calendar;
    }

    public static Calendar GetDate23PM(Date date) {
        Calendar calendar = GetDate23PM(GetDate0AM(date));
        return calendar;
    }

    public static Calendar GetNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar;
    }

    public static Calendar GetCalenderFromYearMonthDay(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        return calendar;
    }

    public static Calendar GetCalenderFromHHmmAmPm(int hours, int minute, int second) {
        Calendar calendar = new GregorianCalendar(0, 0, 0, hours, minute, second);
        return calendar;
    }

    public static Calendar GetCalenderFromString(String dateData, DateFormat format) {
        try {
            if (!dateData.isEmpty()) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat spf = new SimpleDateFormat(String.valueOf(format));
                calendar.setTime(spf.parse(dateData));

                Log.d("date", "GetCalenderFromString: " + spf.format(calendar.getTime()));
                return calendar;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public static String GetFloatWithOutPrecesion(double d) {
        return String.format("%d", (long) d);
    }


    public static String GetDateString(Calendar value, DateFormat format) {
        return GetDateString(value.getTime(), format);
    }

    public static String GetDateString(Date value, DateFormat format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(String.valueOf(format));
        return dateFormat.format(value);
    }


    //method to get the file path from uri
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                String storageDefinition;


                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                } else {

                    if (Environment.isExternalStorageRemovable()) {
                        storageDefinition = "EXTERNAL_STORAGE";

                    } else {
                        storageDefinition = "SECONDARY_STORAGE";
                    }

                    return System.getenv(storageDefinition) + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {// DownloadsProvider

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)

            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isEmptyOrNull(String data) {
        if (data == null) {
            return true;
        }

        if (data.isEmpty() || data.trim().isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String CheckAndGetValidPhoneNumber(String mobileNumber) {
        if (BaseUtils.isEmptyOrNull(mobileNumber) || BaseUtils.GetCleanNumber(mobileNumber).length() != PhoneNumberLength) {
            return null;
        }

        return mobileNumber;
    }

    public static boolean IsValidPhoneNumber(String mobileNumber) {
        if (BaseUtils.isEmptyOrNull(mobileNumber) || BaseUtils.GetCleanNumber(mobileNumber).length() != PhoneNumberLength) {
            return false;
        }
        return true;
    }

    public static String GetCleanNumber(String number) {
        String cleanNo = number.replaceAll("[\\D]", "");
        return GetLast10Character(cleanNo);
    }

    public static String GetLast10Character(String str) {
        if (str.length() == 10) {
            return str;
        } else if (str.length() > 10) {
            return str.substring(str.length() - 10);
        }
        return str;
    }

    public static String getErrorMessageFromError(VolleyError error) {
        if (error == null) {
            return "";
        }

        if (!isEmptyOrNull(error.getMessage())) {
            error.getMessage();
        }

        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }

        return "";
    }

    public static String getEmptyIfNull(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    public static long GetTimeDifference(Date startDate, Date endDate, DurationType Duration) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long elapsednMilisecond = different;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;


        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        switch (Duration) {
            case MiliSecond:
                return elapsednMilisecond;
            case Second:
                return elapsedSeconds;
            case Minute:
                return elapsedMinutes;
            case Hour:
                return elapsedHours;
            case Day:
                return elapsedDays;
            default:
                return 0;
        }
    }

    public static String GetApplicationName(Context mContext) {
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : mContext.getString(stringId);
    }

    public static Calendar MergeCalender(Calendar dateCalender, Calendar timeCalender) {
        dateCalender.set(Calendar.HOUR_OF_DAY, timeCalender.get(Calendar.HOUR_OF_DAY));
        dateCalender.set(Calendar.MINUTE, timeCalender.get(Calendar.MINUTE));
        dateCalender.set(Calendar.SECOND, timeCalender.get(Calendar.SECOND));
        return dateCalender;
    }

    public static void ShowRedToast(Context mContext, String message, int duration, int backGroundColor) {
        Toast toast = Toast.makeText(mContext, message, duration);
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(backGroundColor, PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();
    }

    public static void SetLabelForSMSCount(TextView lable, int messageLength, int MaxCount) {

        int smsSet = (messageLength - 1) / MaxCount;

        lable.setText("" + messageLength + "/" + smsSet);
    }

    public static boolean RunAppIfInstalled(Context context, String AppNamespace) {
        boolean isAppInstalled = IsAppInstalled(context, AppNamespace);
        if (isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(AppNamespace);
            context.startActivity(LaunchIntent);
            return true;
        }

        return false;
    }

    public static boolean IsAppInstalled(Context context, String AppNamespace) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(AppNamespace, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static String GetCurrentVersion(Context mContext) {
        String version = "";
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception e) {

        }
        return version;
    }

    public static boolean isSerializeObjectList(String serializedObject) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(serializedObject);
        return jsonElement.isJsonArray();
    }

    public static <U> String SerializeObject(U myObject) {
        // serialize the object
        try {
            Gson gson = new Gson();
            String json = gson.toJson(myObject);
            return json;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static <U> U DeserializeObject(String serializedObject, Class<U> type) {
        try {
            if (serializedObject == null || serializedObject.isEmpty()) {
                return null;
            }
            Gson gson = new Gson();
            return gson.fromJson(serializedObject, type);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /*public static <U> List<U> DeserializeList(String serializedObject, Class<U> type) {
        try {
            if (serializedObject == null || serializedObject.isEmpty()) {
                return null;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<U>>() {
            }.getType();
            List<U> list = gson.fromJson(serializedObject, listType);
            if (list == null) {
                list = new ArrayList<>();
            } else if (list instanceof LinkedList) {
                list = new ArrayList<>(list);
            }
            return list;
        } catch (JsonSyntaxException e) {
            System.out.println(e);
            return null;
        }
    }*/

    public static <U> List<U> DeserializeList(String serializedObject, Class<U[]> clazz) {
        try {
            if (serializedObject == null || serializedObject.isEmpty()) {
                return null;
            }
            Gson gson = new Gson();
            //Type listType = new TypeToken<List<U>>() {}.getType();
            U[] array = gson.fromJson(serializedObject, clazz);
            if (array == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(array));
        } catch (JsonSyntaxException e) {
            System.out.println(e);
            return null;
        }
    }

    public static String SubstringFromLast(final String str, int charToRemove) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (str.length() < charToRemove) {
            charToRemove = str.length();
        }

        return str.substring(0, (str.length() - charToRemove));
    }

    public static String StringRepeat(final String str, int RepeatCount) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RepeatCount; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static JSONObject BunndleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                // json.put(key, bundle.get(key)); see edit below
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch (JSONException e) {
                //Handle exception here
            }
        }

        return json;
    }

    public static String GetPointData(double d) {
        //return   Constants.strRupeesSymbol + KSUtility.GetFloatWithOutPrecesion(d);
        return BaseUtils.GetFloatWithOutPrecesion(d);
    }

    public static Bundle setExtraParameter(Bundle mBundle, Object obj, String Key) {
        String value = BaseUtils.SerializeObject(obj);
        if (mBundle == null) {
            mBundle = new Bundle();
        }
        mBundle.putString(Key, value);
        return mBundle;
    }

    public static Intent setExtraParameter(Intent mIntent, Object obj, String Key) {
        Bundle mBundle = mIntent.getExtras();
        mBundle = setExtraParameter(mBundle, obj, Key);
        mIntent.putExtras(mBundle);
        return mIntent;
    }

    public static <U> U getExtraParameter(Bundle mBundle, String Key, Class<U> type) {

        if (mBundle == null) {
            return null;
        }

        String value = mBundle.getString(Key, null);
        if (value == null) {
            return null;
        }

        return BaseUtils.DeserializeObject(value, type);
    }

    /*This will return the string value*/
    public static String getExtraParameter(Intent mIntent, String key) {
        Bundle extras = mIntent.getExtras();
        if (extras != null) {
            return extras.getString(key);
        }
        return null;
    }

    public static <U> U getExtraParameter(Intent mIntent, String Key, Class<U> type) {
        if (mIntent == null) {
            return null;
        }
        return getExtraParameter(mIntent.getExtras(), Key, type);
    }

    public static <U> U getExtraParameter(Intent mIntent, Class<U> type) {
        if (mIntent == null) {
            return null;
        }
        return getExtraParameter(mIntent.getExtras(), type.getSimpleName(), type);
    }

    @SuppressLint("MissingPermission")
    public static List<SubscriptionInfo> GetSimData(Context context) {
        List<SubscriptionInfo> subscriptionInfos = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return subscriptionInfos;
            }
            subscriptionInfos = SubscriptionManager.from(context.getApplicationContext()).getActiveSubscriptionInfoList();
            if (subscriptionInfos != null) {
                return subscriptionInfos;
            }
        }
        return subscriptionInfos;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetDateFromCon(String d) {
        if (d != null) {
            try {
                java.text.DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date date = f.parse(d);
                java.text.DateFormat newDate = new SimpleDateFormat("EEE, d MMM");
                String convertedDate = newDate.format(date);
                return convertedDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetPostDate(String d) {
        if (d != null) {
            try {
                java.text.DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = f.parse(d);
                java.text.DateFormat newDate = new SimpleDateFormat("EEE, d MMM");
                String convertedDate = newDate.format(date);
                return convertedDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetPostDateFromDate(Date d) {
        if (d != null) {
            /*java.text.DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = f.parse(d);*/
            java.text.DateFormat newDate = new SimpleDateFormat("EEE, d MMM");
            String convertedDate = newDate.format(d);
            return convertedDate;
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetMonthFromDate(String d) {
        if (d != null) {
            try {
                java.text.DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = f.parse(d);
                java.text.DateFormat newDate = new SimpleDateFormat("MMMM yyyy");
                String convertedDate = newDate.format(date);
                return convertedDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String GetTime(String d) {
        if (d != null) {
            try {
                java.text.DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = f.parse(d);
                java.text.DateFormat time = new SimpleDateFormat("hh:mm:ss a");
                return time.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String GetStringLastOne(String str) {
        if (str == null) {
            return null;
        }
        return str.substring(str.lastIndexOf("/") + 1);
    }

    public static int CalculateImageSize(long imgLength) {
        int imgCompress = 0;
        int maxLength = 2048;
        if (imgLength != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int kb = Math.toIntExact(imgLength / 1024);
                if (kb > maxLength) {
                    int maximumSize = Math.toIntExact(kb - maxLength);
                    int maxPercent = (int) (maximumSize * 100 / kb);
                    int percent = Math.toIntExact(110 - maxPercent);
                    //int finalPercent = Math.toIntExact(Percent - 5);
                    return percent;
                }
                return 100;
            } else {
                return 0;
            }
        }
        return imgCompress;
    }

    public static String RandomImageName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int random = ThreadLocalRandom.current().nextInt(10, 9999999);
            return String.valueOf(random);
        }
        return null;
    }

    public static int GetRandomNumberForActivityRequest() {
        return new Random().nextInt(10 - 99);
    }

    /*public static GetOverLayPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }*/

    public static String takeLast(String value, int count) {
        if (value == null || value.trim().length() == 0) return "";
        if (count < 1) return "";

        if (value.length() > count) {
            return value.substring(value.length() - count);
        } else {
            return value;
        }
    }

    public static String createTransactionID() throws Exception {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    //Youtube Video Thumbnail URL [YOUTUBE]
    public static String getYoutubeThumbnailUrlFromVideoUrl(String videoUrl) {
        return "http://img.youtube.com/vi/" + getYoutubeVideoIdFromUrl(videoUrl) + "/0.jpg";
    }

    //Youtube Video ID [YOUTUBE]
    public static String getYoutubeVideoIdFromUrl(String inUrl) {
        inUrl = inUrl.replace("&feature=youtu.be", "");
        if (inUrl.toLowerCase().contains("youtu.be")) {
            return inUrl.substring(inUrl.lastIndexOf("/") + 1);
        }
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(inUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    //Watch Youtube video using ID [YOUTUBE]
    public static void watchYoutubeVideo(Activity ctx, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            ctx.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            ctx.startActivity(webIntent);
        }
    }

    public static String getRealPathFromURI(Context ctx, Uri contentURI) {
        String result;
        Cursor cursor = ctx.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Uri getImageUri(Context ctx, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }

    public static String getImagePath_from_Bitmap(Context ctx, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return path;
    }

    public static <T> JSONObject OldToJson(T model) {
        Field[] fields = model.getClass().getFields();
        JSONObject obj = new JSONObject();
        for (Field data : fields) {
            data.setAccessible(true);
            try {
                obj.put(PascalCase(data.getName()), data.get(model));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static String PascalCase(String value) {
        String data = value.substring(0, 1);
        return value.replaceFirst(data, data.toUpperCase());
    }

    /*Start Copy model*/
    public static <M, T> T CopyModel(M model, Class<T> type) {
        T data = null;
        try {
            String stringModel = ClassToString(model);
            data = StringToClass(stringModel, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String ClassToString(Object myObject) {
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        return json;
    }

    public static <U> U StringToClass(String serializedObject, Class<U> type) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        U data = gson.fromJson(serializedObject, type);
        return data;
    }

    public static <T> JSONObject ToJson(T model, boolean IsPascalCase) {
        Field[] fields = model.getClass().getFields();
        JSONObject obj = new JSONObject();
        for (Field data : fields) {
            data.setAccessible(true);
            try {
                if (IsPascalCase) {
                    obj.put(PascalCase(data.getName()), data.get(model));
                } else {
                    obj.put(data.getName(), data.get(model));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static <U> U ToObject(String json, Class<U> type) {
        U data = null;
        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).registerTypeAdapter(Date.class, new DateDeserializer()).create();
            data = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static int RandomNumber() {
        return new Random().nextInt(99999);
    }

    public static String getClassName(Object obj) {
        if (obj instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) obj;
            Iterator<?> iterator = iterable.iterator();
            Class<?> elementType = iterator.next().getClass();
            return elementType.getSimpleName();
        }
        return obj.getClass().getSimpleName();
    }

    public static <U> String getClassName(Class<U[]> type) {
        Class<?> componentType = type.getComponentType();
        String cName;
        if (componentType != null) {
            cName = componentType.getSimpleName();
        } else {
            String name = type.getName();
            int idx = name.lastIndexOf('[') + 1;
            if (idx < name.length() && name.charAt(idx) == 'L') {
                // object array, extract component type name
                int endIdx = name.indexOf(';', idx);
                if (endIdx < 0) {
                    endIdx = name.length();
                }
                cName = name.substring(idx + 1, endIdx);
            } else {
                // primitive array, use component type name directly
                cName = name.substring(idx);
            }
        }
        return cName;
    }

    public enum DurationType {
        MiliSecond, Second, Minute, Hour, Day
    }


    /*public static <U> U ToObject(String json, Class<U> type) {
        U data=null;
        try {
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            data = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }*/
    /*End Copy model*/

    public enum DateFormat {
        dd_MM_yyyy("dd-MM-yyyy"), yyyy_MM_dd("yyyy-MM-dd"), hh_mm_am_pm("hh:mm a"), dd_MM_yyyy_hh_mm_am_pm("dd-MM-yyyy hh:mm a");
        private final String name;

        private DateFormat(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    public static JSONObject readDummyResponse(Context ctx, String assetsFileName) {
        InputStream inputStream = null;
        try {
            inputStream = ctx.getAssets().open(assetsFileName);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JSONObject jsonObject = null;
        if (inputStream != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String statement;
            try {
                while ((statement = reader.readLine()) != null) {
                    if (statement.trim().length() > 0) {
                        buffer.append(statement);
                    }
                }
            } catch (IOException e1) { // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (buffer.length() > 0) {
                try {
                    jsonObject = new JSONObject(buffer.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return (JSONObject) jsonObject;
    }

    public static String convertJSONObjectToURLParams(JSONObject jsonObject) throws UnsupportedEncodingException, JSONException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = jsonObject.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            Object value = jsonObject.get(key);
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return result.toString();
    }

    public static String convertJSONObjectToURLParams(String url, JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = jsonObject.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            Object value = jsonObject.get(key);
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }

        return url + "?" + result.toString();
    }

    public static boolean isJSONObjectNull(JSONObject jsonObject) {
        return jsonObject == null;
    }


    public static String getCamelCaseWithoutSpecialCase(String str) {
        //String login = "/usercontroller/login.html";
        str = str.replaceAll("[^a-zA-Z0-9]", ""); // remove special characters
        String[] parts = str.split("(?<=.)(?=\\p{Upper})"); // split into camel case parts
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static String getStringWithoutSpecialCharacterAndNumeric(String str) {
        //String login = "http://10.248.1.68:8080/hrmis2/login.html";
        str = str.replaceAll("[^a-zA-Z]", ""); // remove non-alphabetic characters
        return str.toLowerCase(); // convert to lowercase
    }

    public static String getExtension(String filename) {
        String extension = "";
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            extension = filename.substring(dotIndex + 1);
        }
        return extension;
    }

    public static <T> T copyNonNullFieldValues(Object source, Class<T> destinationClass) throws IllegalAccessException, InstantiationException {
        // Create a new instance of the destination class
        T destination = destinationClass.newInstance();

        // Get the list of fields for the source and destination objects
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] destinationFields = destinationClass.getDeclaredFields();

        // Loop through the source fields
        for (Field sourceField : sourceFields) {
            // Check if the source field is not null
            sourceField.setAccessible(true);
            Object sourceValue = sourceField.get(source);
            if (sourceValue != null) {
                // If the source field is an object, recursively copy its non-null fields
                if (sourceValue.getClass().getDeclaredFields().length > 0) {
                    Object nestedDestination = copyNonNullFieldValues(sourceValue, sourceValue.getClass());
                    sourceValue = nestedDestination;
                }
                // Loop through the destination fields to find the corresponding field
                for (Field destinationField : destinationFields) {
                    if (destinationField.getName().equals(sourceField.getName())) {
                        // Set the value of the destination field to the value of the source field
                        destinationField.setAccessible(true);
                        destinationField.set(destination, sourceValue);
                        break;
                    }
                }
            }
        }

        return destination;
    }

    /*public static void copyNonNullFieldValues(Object source, Object destination) throws IllegalAccessException, InstantiationException {
        // Get the list of fields for the source and destination objects
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] destinationFields = destination.getClass().getDeclaredFields();

        // Loop through the source fields
        for (Field sourceField : sourceFields) {
            // Check if the source field is not null
            sourceField.setAccessible(true);
            Object sourceValue = sourceField.get(source);
            if (sourceValue != null) {
                // If the source field is an object, recursively copy its non-null fields
                if (sourceValue.getClass().getDeclaredFields().length > 0) {
                    Object nestedDestination = null;
                    for (Field destinationField : destinationFields) {
                        if (destinationField.getName().equals(sourceField.getName())) {
                            destinationField.setAccessible(true);
                            nestedDestination = destinationField.get(destination);
                            break;
                        }
                    }
                    if (nestedDestination == null) {
                        // If the nested destination object is null, create a new instance of the same class
                        nestedDestination = sourceValue.getClass().newInstance();
                    }
                    copyNonNullFieldValues(sourceValue, nestedDestination);
                    sourceValue = nestedDestination;
                }
                // Loop through the destination fields to find the corresponding field
                for (Field destinationField : destinationFields) {
                    if (destinationField.getName().equals(sourceField.getName())) {
                        // Set the value of the destination field to the value of the source field
                        destinationField.setAccessible(true);
                        destinationField.set(destination, sourceValue);
                        break;
                    }
                }
            }
        }
    }*/

    public static void copyNonNullFieldValues(Object source, Object destination) throws IllegalAccessException {
        // Get the list of fields for the source and destination objects
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] destinationFields = destination.getClass().getDeclaredFields();

        // Loop through the source fields
        for (Field sourceField : sourceFields) {
            // Check if the source field is not null
            sourceField.setAccessible(true);
            Object sourceValue = sourceField.get(source);
            if (sourceValue != null) {
                // Loop through the destination fields to find the corresponding field
                for (Field destinationField : destinationFields) {
                    if (destinationField.getName().equals(sourceField.getName())) {
                        // Set the value of the destination field to the value of the source field
                        destinationField.setAccessible(true);
                        destinationField.set(destination, sourceValue);
                        break;
                    }
                }
            }
        }
    }

    public static int getProgressPercentage(long transferredBytes, long totalSize) {
        return (int) ((transferredBytes / ((float) totalSize)) * 100);
    }

    public static byte[] getBytesFromContentUri(Context context, Uri contentUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(contentUri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}
