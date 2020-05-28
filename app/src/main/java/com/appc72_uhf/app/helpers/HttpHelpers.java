package com.appc72_uhf.app.helpers;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpHelpers {
    private RequestQueue request;
    private StringRequest jsonRequest;
    private String urlBase;
    private Map<String, String> headers;


    public HttpHelpers(Context context, String url, String port) {
        this.request = Volley.newRequestQueue(context);
        this.urlBase = url;// + ":" + port;
        headers = new HashMap<String, String>();
        addHeader("Content-Type", "application/json; charset=utf-8");
    }

    public void client(int method, String url, final String BodyContentType, final JSONObject jsonBody, Response.Listener listener, @Nullable Response.ErrorListener errorListener) {

        jsonRequest = new StringRequest(method, this.urlBase + url, listener, errorListener) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                    return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType()
            {
                return BodyContentType;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonRequest.setRetryPolicy(policy);
        request.add(jsonRequest);

        request.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> requestObject) {
                request.getCache().clear();
            }
        });
    }
    public void clientArray(int method, String url, final String BodyContentType, final  Map<String, String> params, Response.Listener listener, @Nullable Response.ErrorListener errorListener) {

        jsonRequest = new StringRequest(method, this.urlBase + url, listener, errorListener) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                if(params != null && params.size()>0){
                    return HttpHelpers.this.encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            @Override
            public String getBodyContentType()
            {
                //return BodyContentType;
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/x-www-form-urlencoded");
                return pars;
                //return headers;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonRequest.setRetryPolicy(policy);
        request.add(jsonRequest);

        request.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> requestObject) {
                request.getCache().clear();
            }
        });
    }

    public void clientProductDetail(int method, String url, final  Map<String, String> params, Response.Listener listener, @Nullable Response.ErrorListener errorListener) {

        jsonRequest = new StringRequest(method, this.urlBase + url, listener, errorListener) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                if(params != null && params.size()>0){
                    return HttpHelpers.this.encodeParameters(params, getParamsEncoding());
                }
                return null;
            }

            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //Map<String, String> pars = new HashMap<String, String>();
                //pars.put("Content-Type", "application/json; charset=UTF-8");
                //return pars;
                return headers;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonRequest.setRetryPolicy(policy);
        request.add(jsonRequest);


        request.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> requestObject) {
                request.getCache().clear();
            }
        });
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void AjaxCancel() {
        request.cancelAll(jsonRequest);
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}
