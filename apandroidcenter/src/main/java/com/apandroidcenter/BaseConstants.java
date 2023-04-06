package com.apandroidcenter;

import com.apandroidcenter.type.DataAccessType;

public class BaseConstants {

    public static final String TAG = "ApuCoreLibrary";
    public static String strPermissionRequired = "Required permission disabled";
    public static String strRequiredPermissionPath = "Please enable the permission in Settings>Apps>Permission";
    public static String strUnHandledError = "Unexpected issue found";
    public static DataAccessType dataAccessType = DataAccessType.API;
    protected static final int reservekey = 100;


    //RQST - Request
    //PERM - Permission

    int PERM_Accessibility_RQST = reservekey - 50;
    public static final int PERM_STORAGE_RQST = reservekey - 51;
    public static final int MULTIPLE_PERMISSIONS_RQST = reservekey - 52;
    public static final int REQUEST_LOCATION_PERMISSION = reservekey - 53;
}
