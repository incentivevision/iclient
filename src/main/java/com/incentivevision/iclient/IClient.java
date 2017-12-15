package com.incentivevision.iclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ranatayyab on 11/28/17.
 */

public class IClient extends Application {

    // Request methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String PATCH = "PATCH";
    public static final String DELETE = "DELETE";
    OnResponseListener listener = null;
    OkHttpClient client = new OkHttpClient();
    HttpUrl.Builder httpUrlBuilder;
    Request.Builder requestBuilder;
    Request request;
    RequestBody requestBody = null;
    // Session with shared preferences
    ISession iSession;
    private Toast mToast;
    // List of possible urls on which request will be sent.
    private HashMap<String, String> urls;

    // Init with current activity
    private Activity activity;

    /**
     * @param date
     * @return
     */
    public static Date formatDate(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        return new Date(format.format(date));
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // HttpUrlBuilder INIT
        httpUrlBuilder = new HttpUrl.Builder();
        requestBuilder = new Request.Builder();

    }

    /**
     * @param msg
     */
    public void showToast(Object msg) {
        String message;

        if (msg instanceof String)
            message = (String) msg;
        else message = getString((Integer) msg);

        if (mToast != null && mToast.getView().isShown())
            mToast.cancel(); // Close the toast if it is already open
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    public IClient setHeader(String name, String value) {
        requestBuilder.addHeader(name, value);
        return this;
    }

    /**
     * @param scheme
     */
    public IClient setScheme(String scheme) {
        httpUrlBuilder.scheme(scheme);
        return this;
    }

    /**
     * @param host
     */
    public IClient setHost(String host) {
        httpUrlBuilder.host(host);
        return this;
    }

    /**
     * @param prefixs
     */
    public IClient setPrefixs(String prefixs) {
        for (String prefix : prefixs.split("/")) {
            httpUrlBuilder.addPathSegment(prefix);
        }
        return this;
    }

    public IClient setResponseListener(OnResponseListener listener) {
        this.listener = listener;
        return this;
    }

    public IClient setUrlPathSegments(String pathSegments) {
        // Format url
        for (String segment : pathSegments.split("/")) {
            httpUrlBuilder.addPathSegment(segment);
        }
        return this;
    }

    public IClient setUrlQueryParams(HashMap<String, String> queryParams) {
        // Add url query params if not equal to null
        for (String key : queryParams.keySet()) {
            httpUrlBuilder.setQueryParameter(key, queryParams.get(key));
        }
        return this;
    }

    public IClient setPostBodyParams(String postParams) {
        // Add post body params if not equal to null
        requestBody = RequestBody.create(MediaType.parse("application/json"), postParams);
        return this;
    }

    /**
     * @param urls
     */
    protected void setUrls(HashMap<String, String> urls) {
        this.urls = urls;
    }

    /**
     * @param key
     * @return
     * @throws Exception
     */
    public String getUrl(String key) {
        if (urls.containsKey(key))
            return urls.get(key);
        else
            throw new RuntimeException("Url '" + key + "' key not exist.");
    }

    public void setActivity(Activity act) {
        this.activity = act;
    }

    /**
     * @param e
     * @return
     */
    public boolean isValidEmail(String e) {
        return Patterns.EMAIL_ADDRESS.matcher(e).matches();
    }

    /**
     * Send api request to server and get response
     *
     * @param method
     * @param authenticationRequired
     */
    public void send(String method,
                     boolean authenticationRequired) {

        if (listener == null) {
            throw new RuntimeException(activity.getBaseContext().toString()
                    + " must implement OnResponseListener");
        }

        // Setup url
        requestBuilder.url(httpUrlBuilder.build())
                .method(method, requestBody);


        // Add Authorization header when user already login
        // Header:
        // Authorization:Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImY3MDY1OGNjNGIxYWU3OTY5
        //              MzgxMjJlZjIzMjQyZDZlNGVkNjE3NjMzNDEwNDNhMzlhOWJmM2QxOWMzOWZlMDFiMzAy
        //              MWZlM2QwNTcwNTM3In0.eyJhdWQiOiIyIiwianRpIjoiZjcwNjU4Y2M0YjFhZTc5Njkz
        //              ODEyMmVmMjMyNDJkNmU0ZWQ2MTc2MzM0MTA0M2EzOWE5YmYzZDE5YzM5ZmUwMWIzMDIxZm
        //              UzZDA1NzA1MzciLCJpYXQiOjE1MDkwMTU2NTUsIm5iZiI6MTUwOTAxNTY1NSwiZXhwIjox
        //              NTQwNTUxNjU1LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.Ekyw64axh5GbDE32sLCpN5....
        // We will save token_type, expires_in, access_token, refresh_token on login in sharedPref
        // and will get access_token and send with each request except login & register
        if (authenticationRequired) {
            requestBuilder.addHeader("Authorization", iSession.get(ISession.TOKEN_TYPE) + " " +
                    iSession.get(ISession.ACCESS_TOKEN));
        }

        // Build request from above data
        request = requestBuilder.build();

        // send request using okhttp
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            responseBodyWithCode(response.body().string(), response.code());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    /**
     * @param body
     * @param code
     */
    public void responseBodyWithCode(String body, int code) {
        // Determine response code
        switch (code) {
            case 200:
                break;
            case 201:
                break;
            case 400:
                break;
            case 401:
                break;
            case 404:
                break;
            case 500:
                break;
            // ...

        }
    }

    /**
     * Server Response Listener
     */
    public interface OnResponseListener {
        void onSuccess(String responseBody, String requestUrl);
    }

}
