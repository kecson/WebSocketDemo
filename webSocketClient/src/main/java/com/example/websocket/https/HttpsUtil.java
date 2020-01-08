package com.example.websocket.https;

import android.content.Context;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * https工具
 */
public class HttpsUtil {
    private static final String TAG = "HttpsUtil";
    private static TrustManager[] trustManagers;

    /**
     * 忽略所有证书验证
     */
    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                Log.d(TAG, "verify() called with: hostname = [" + hostname + "], session = [" + session + "]");
                return true;
            }

        });

        SSLContext sslContext = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager()};
        }

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }

    }

    public static SSLSocketFactory newSslSocketFactory(Context context) {
        try {
            //https证书
//            InputStream in = context.getApplicationContext().getResources().openRawResource(R.raw.crt_192_168_128_10_client);
//
//            //初始化公钥:keyStore
//            String keyType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyType);
//            try {
//                keyStore.load(in, null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);

            //https证书,暂时不添加证书，
            /***
             * {@link HttpsTrustManager#allowAllSSL()} 设置允许所有Https
             */
            tmf.init((KeyStore) null);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
            sslContext.init(null, trustManagers, null);
            SSLSocketFactory sf = sslContext.getSocketFactory();
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        List<TrustManager> list = new ArrayList<>(Arrays.asList(trustManagers));
        list.add(0, new HttpsTrustManager());
        return list.toArray(new TrustManager[0]);
    }
}
