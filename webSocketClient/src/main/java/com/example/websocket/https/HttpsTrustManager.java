package com.example.websocket.https;

import android.util.Log;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * https信任所有证书实现
 */
public class HttpsTrustManager implements X509TrustManager {
    private static final String TAG = "HttpsTrustManager";
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

    @Override
    public void checkClientTrusted(
            X509Certificate[] chain, String authType)
            throws java.security.cert.CertificateException {
        Log.d(TAG, "checkClientTrusted() called with: chain = [" + chain + "], authType = [" + authType + "]");
    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] chain, String authType)
            throws java.security.cert.CertificateException {
        Log.d(TAG, "checkServerTrusted() called with: chain = [" + chain + "], authType = [" + authType + "]");
    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        Log.i(TAG, "isClientTrusted() called with: chain = [" + chain + "]");
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        Log.d(TAG, "isServerTrusted() called with: chain = [" + chain + "]");
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        Log.d(TAG, "getAcceptedIssuers() called");
        return _AcceptedIssuers;
    }

}

