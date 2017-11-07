package com.qingwing.safebox.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.utils.ToastTool;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommReq {
    private static final String TAG = "qingWing-Req";
    /**
     * 请求类的TAG
     */
    private String tag = "";
    /**
     * 请求的URL
     */
    private String baseUrl;
    private RequestQueue requestQueue;
    private ObjectRequest request;
    /**
     * 请求类型
     */
    private int requestType = Request.Method.POST;
    public static final int Request_Method_BYTE = 787;

    /**
     * POST请求时附带的参数
     */
    private Map<String, String> postParams = new HashMap<String, String>();
    private RetryPolicy retryPolicy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private RetryPolicy retryPolicy1 = new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    // ///////////////////
    // 抽象函数
    // /////////////////
    abstract public String generUrl();

    abstract public Class<BaseResponse> getResClass();

    abstract public BaseResponse getResBean();

    /**
     * 请求结果回调接口
     */
    private NetCallBack netCallback = null;
    private Context context;
    /**
     * hasDecode: 返回的数据是否需要解密， 默认是不要解
     */
    private boolean hasDecode = false;

    public BaseCommReq() {
        requestQueue = QWApplication.getInstance().getRequestQueue();
        context = QWApplication.getInstance().getApplicationContext();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setIsneedDecode(boolean hasDecode) {
        this.hasDecode = hasDecode;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public void setPostParam(Map<String, String> params) {
        this.postParams = params;
    }

    public void setNetCallback(NetCallBack netCallback) {
        this.netCallback = netCallback;
    }

    private void produceRequst() {
        baseUrl = generUrl();
        if (TextUtils.isEmpty(baseUrl)) {
            // handleErrorResponse(null);
            ToastTool.showShortBigToast(context, "url is empty");
            return;
        }
        FakeX509TrustManager.allowAllSSL();
        switch (requestType) {
            case Request.Method.POST:
                // 参数以String上传
                StringRequest stringRequest = new StringRequest(Request.Method.POST, baseUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                handleOnRequest(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleErrorResponse(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return postParams;
                    }
                };
                stringRequest.setTag(tag);
                if (tag.equals("ApkUpdateReq")) {
                    stringRequest.setRetryPolicy(retryPolicy1);
                    Log.d(TAG, "setRetryPolicy1超时时间：----------:" + retryPolicy1.getCurrentTimeout());

                } else {
                    stringRequest.setRetryPolicy(retryPolicy);
                    Log.d(TAG, "setRetryPolicy超时时间：----------:" + retryPolicy.getCurrentTimeout());

                }
                requestQueue.add(stringRequest);
                break;
            case Request_Method_BYTE:

                break;

            case Request.Method.GET:
            default:
                if (hasDecode) {//加密的数据先解密
                } else {
                    request = new ObjectRequest(Request.Method.GET, baseUrl, null,
                            new Response.Listener<Object>() {
                                @Override
                                public void onResponse(Object obj) {
                                    handleOnRequest(obj);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleErrorResponse(error);
                        }

                    });
                    request.setTag(tag);
                    if (tag.equals("ApkUpdateReq")) {
                        request.setRetryPolicy(retryPolicy1);
                        Log.d(TAG, "setRetryPolicy超时时间：----------:" + retryPolicy1.getCurrentTimeout());
                    } else {
                        request.setRetryPolicy(retryPolicy);
                        Log.d(TAG, "setRetryPolicy超时时间：----------:" + retryPolicy.getCurrentTimeout());

                    }
                    requestQueue.add(request);
                }
        }
    }

    public void addRequest() {
        produceRequst();
        Log.d(TAG, tag + "  qingwing BaseCommReq addRequest" + "  url=" + baseUrl);
        if (!postParams.isEmpty()) {
            Log.d(TAG, tag + "qingwing BaseCommReq addRequest" + "  postParams=" + postParams.toString());
        }
    }

    private void handleOnRequest(Object obj) {
        if (netCallback != null) {
            try {
                BaseResponse baseRes = getResBean();
                baseRes = HttpJsonAdapter.getInstance().get(obj.toString(), getResClass());
                netCallback.onNetResponse(baseRes);
                Log.d(TAG, "qingwing onResponse----------:" + obj.toString().replace("\n", ""));
            } catch (BizException e) {
                e.printStackTrace();
                ToastTool.showShortBigToast(context, "hand data exception");
            }
        }
    }

    private void handleErrorResponse(VolleyError error) {
        if (error != null) {
            Log.d(TAG, "qingwing onErrorResponse----------:" + error.toString());
        }
        if (netCallback != null && !TextUtils.isEmpty(tag)) {
            netCallback.onNetErrorResponse(tag, error);
        }
    }

}