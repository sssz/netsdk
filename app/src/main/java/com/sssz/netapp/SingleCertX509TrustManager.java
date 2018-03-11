package com.sssz.netapp;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sssz on 2018/3/10.
 */

public class SingleCertX509TrustManager implements X509TrustManager {

    private X509TrustManager standardTrustManager = null;

    public SingleCertX509TrustManager(TrustManager trustManager) throws Exception {
        super();
        if(trustManager == null) {
            throw new Exception("trustManager not found");
        }
        this.standardTrustManager = (X509TrustManager) trustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        standardTrustManager.checkClientTrusted(x509Certificates, s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        try {
            standardTrustManager.checkServerTrusted(x509Certificates, s);
        } catch (Exception e){
            throw  new CertificateException("Certificate not valid or trusted.");
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
