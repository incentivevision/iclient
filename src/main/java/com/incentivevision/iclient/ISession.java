package com.incentivevision.iclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by ranatayyab on 11/28/17.
 */

public class ISession {
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String FROM_DATE = "from-date";
    private String TAG = ISession.class.getSimpleName();
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEdit;

    public ISession(Context ctx) {
        mPref = ctx.getSharedPreferences("IClient", 0);
        mEdit = mPref.edit();
    }

    public ISession initSession(String tokenType, String expiresIn, String accessToken, String refreshToken) {

        mEdit.putString(TOKEN_TYPE, tokenType);
        mEdit.putString(EXPIRES_IN, expiresIn);
        mEdit.putString(ACCESS_TOKEN, accessToken);
        mEdit.putString(REFRESH_TOKEN, refreshToken);
        mEdit.putString(FROM_DATE, IClient.formatDate(new Date()).toString());

        return this;
    }

    public void save() {

        mEdit.commit();
    }

    public ISession destroySession() {

        mEdit.clear();

        return this;
    }

    public String get(@Key String key) {
        return mPref.getString(key, null);
    }

    public Boolean verifyAccessToken() {

        int expiresIn = Integer.parseInt(get(EXPIRES_IN));

        Log.i(TAG, "Expires In: " + String.valueOf(expiresIn));

        Date fromDate = new Date(get(FROM_DATE));
        Log.i(TAG, "From Date: " + get(FROM_DATE));

        Date toDate = IClient.formatDate(new Date());
        Log.i(TAG, "To Date: " + toDate.toString());

        //in milliseconds
        long diff = toDate.getTime() - fromDate.getTime();

//        long diffSeconds = diff / 1000 % 60;
//        long diffMinutes = diff / (60 * 1000) % 60;
//        long diffHours = diff / (60 * 60 * 1000) % 24;

        long diffDays = diff / (24 * 60 * 60 * 1000);
        Log.i(TAG, "Calculated Days: " + diffDays + ", & " + ((int) diffDays) + ", & " + (expiresIn > (int) diffDays));

        return expiresIn > (int) diffDays;

    }

    // Declare the @StringDef for these constants
    @StringDef({
            //SessionKey Keys
            TOKEN_TYPE, EXPIRES_IN, ACCESS_TOKEN, REFRESH_TOKEN, FROM_DATE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Key {
    }


}
