/**
 * @Author 范承祥
 * @CreateTime 2020/7/9
 * @UpdateTime 2020/7/11
 */
package com.sosotaxi.driver.common;

import android.Manifest;

import okhttp3.MediaType;

/**
 * 常量类
 */
public class Constant {

    /**
     * 区号请求
     */
    public static final int SELECT_AREA_CODE_REQUEST=0;

    public static final int PERMISSION_SEND_SMS_REQUEST=1;

    public static final int PERMISSION_CALL_PHONE_REQUEST=2;

    public static final int PERMISSION_NAVIGATION_REQUEST=3;

    /**
     * 区号EXTRA
     */
    public static final String EXTRA_AREA_CODE ="com.sosotaxi.driver.ui.login.EXTRA_AREA_CODE";

    /**
     * 手机号EXTRA
     */
    public static final String EXTRA_PHONE="com.sosotaxi.driver.ui.login.EXTRA_PHONE";

    public static final String EXTRA_PASSWORD="com.sosotaxi.driver.ui.login.EXTRA_PASSWORD";

    public static final String EXTRA_TOKEN="com.sosotaxi.driver.ui.login.EXTRA_TOKEN";

    public static final String EXTRA_IS_REGISTERED="com.sosotaxi.driver.ui.login.EXTRA_IS_REGISTERED";

    public static final String EXTRA_IS_AUTHORIZED="com.sosotaxi.driver.ui.login.EXTRA_IS_AUTHORIZED";

    public static final String EXTRA_IS_SUCCESSFUL="com.sosotaxi.driver.ui.login.EXTRA_IS_SUCCESSFUL";

    public static final String EXTRA_RESPONSE_MESSAGE="com.sosotaxi.driver.ui.login.EXTRA_RESPONSE_MESSAGE";

    public static final String EXTRA_TOTAL="com.sosotaxi.driver.ui.driverorder.EXTRA_TOTAL";

    public static final String IS_REGISTERED_URL="http://122.51.162.119:8001/user/isRegistered";

    public static final String EXTRA_ERROR="com.sosotaxi.driver.ui.login.EXTRA_ERROR";

    public static final String REGISTER_URL="http://122.51.162.119:8001/user/registry";

    public static final String LOGIN_URL="http://122.51.162.119:8001/auth/login";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static final String SHARE_PREFERENCE_LOGIN="loginUser";

    public static final String USERNAME="com.sosotaxi.username";

    public static final String PASSWORD="com.sosotaxi.password";

    public static final String APP_FOLDER_NAME="SoSoTaxiDriver";

    public static final String[] AUTH_ARRAY_NAVIGATION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static final String TTS_APP_ID="21383548";

}
