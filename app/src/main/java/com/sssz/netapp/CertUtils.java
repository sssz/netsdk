package com.sssz.netapp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okio.Buffer;

/**
 * Created by sssz on 2018/3/11.
 */

public class CertUtils {
    private SingleCertX509TrustManager trustManager;
    private X509TrustManager x509TrustManager;

    private SSLContext sslContext;
    private KeyStore keyStore;

    private String cartificateString;
    private Certificate certificate;
    private HostnameVerifier hostnameVerifier;


    public void init(String cartificateString) throws Exception {
        this.cartificateString = cartificateString;
        this.certificate = createCertificateByString();
        this.keyStore = createKeyStore();
        this.sslContext = createSslContext();
        this.trustManager = createTrustManager();
        this.hostnameVerifier = createHostnameVerifier();
    }
    private Certificate createCertificateByString() throws CertificateException {
        InputStream buffer = new Buffer()
                .writeUtf8(this.cartificateString)
                .inputStream();

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return certificateFactory.generateCertificate(buffer);
    }
    private KeyStore createKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry("0", certificate);
        return keyStore;
    }
    private SSLContext createSslContext() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        if (trustManagerFactory.getTrustManagers().length == 0)
        {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
        x509TrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        //keyManager[], TrustManager[], SecureRandom
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
    private SingleCertX509TrustManager createTrustManager() throws Exception {
        return new SingleCertX509TrustManager(x509TrustManager);
    }
    private HostnameVerifier createHostnameVerifier(){
        return new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                Log.d("netSdk", "verify: " + s);
                return true;
            }
        };
    }

    public SingleCertX509TrustManager getTrustManager() {
        return trustManager;
    }
    public SSLSocketFactory getSSLSocketFactory(){
        return sslContext.getSocketFactory();
    }
    public HostnameVerifier getHostNameVerifier(){
        return hostnameVerifier;
    }

}
