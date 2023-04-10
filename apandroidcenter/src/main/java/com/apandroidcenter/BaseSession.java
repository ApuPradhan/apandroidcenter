package com.apandroidcenter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;


public class BaseSession {

    protected final SharedPreferences pref;
    protected final Context _ctx;

    public BaseSession(Context ctx) {
        this._ctx = ctx;
        //pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        pref = ctx.getSharedPreferences(BaseUtils.getAppName(ctx), Context.MODE_PRIVATE);
    }

    /* imp: TModel means Temporary Model. */
    /* msg: when we will use below set or get TModel function we don't need to pass static key. */
    /* msg: We can set Multiple TModel but when we will getting any one TModel we only get that once time. */
    public <U> void setModel(U obj) {
        pref.edit().putString(BaseUtils.getClassName(obj), BaseUtils.SerializeObject(obj)).apply();
    }

    public <U> void setModel(U obj, String key) {
        pref.edit().putString(key, BaseUtils.SerializeObject(obj)).apply();
    }


    /*msg: This parameter: Class.class*/
    protected <U> U getTObject(Class<U> type) {
        String user = pref.getString(type.getSimpleName(), null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(type.getSimpleName());
        editor.apply();
        return BaseUtils.DeserializeObject(user, type);
    }

    protected <U> U getTObject(Class<U> type, String key) {
        String user = pref.getString(key, null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
        return BaseUtils.DeserializeObject(user, type);
    }

    protected <U> List<U> getTObjectList(Class<U[]> type) {
        String user = pref.getString(BaseUtils.getClassName(type), null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(type.getSimpleName());
        editor.apply();
        return BaseUtils.DeserializeList(user, type);
    }

    protected <U> List<U> getTObjectList(Class<U[]> type, String key) {
        String user = pref.getString(key, null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
        return BaseUtils.DeserializeList(user, type);
    }


    protected <U> U getObject(Class<U> type) {
        String user = pref.getString(type.getSimpleName(), null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        return BaseUtils.DeserializeObject(user, type);
    }

    protected <U> U getObject(Class<U> type, String key) {
        String user = pref.getString(key, null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        return BaseUtils.DeserializeObject(user, type);
    }

    protected <U> List<U> getObjectList(Class<U[]> type) {
        String user = pref.getString(BaseUtils.getClassName(type), null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        return BaseUtils.DeserializeList(user, type);
    }

    protected <U> List<U> getObjectList(Class<U[]> type, String key) {
        String user = pref.getString(key, null);
        if (BaseUtils.isEmptyOrNull(user)) {
            return null;
        }
        return BaseUtils.DeserializeList(user, type);
    }


    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    protected <U> void clearModel(Class<U> type) {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(type.getSimpleName());
        editor.apply();
    }

    protected <U> void clearModel(Class<U> type, String key) {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.apply();
    }
}
