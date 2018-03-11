package com.sssz.netapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;


public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;
    String url = "http://192.168.0.190/";
    String httpsUrl = "https://192.168.0.190/";
    String uploadUrl = "http://192.168.0.190/upload";
    String downloadUrl = "http://192.168.0.190/download";

    String crt = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDfzCCAmegAwIBAgIJAMoHuF8eDt0SMA0GCSqGSIb3DQEBCwUAMFUxCzAJBgNV\n" +
            "BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX\n" +
            "aWRnaXRzIFB0eSBMdGQxDjAMBgNVBAMMBWhlbGxvMCAXDTE4MDMxMTAzNTYxNFoY\n" +
            "DzIxMTgwMjE1MDM1NjE0WjBVMQswCQYDVQQGEwJBVTETMBEGA1UECAwKU29tZS1T\n" +
            "dGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMQ4wDAYDVQQD\n" +
            "DAVoZWxsbzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAPXJQ//kk5Bd\n" +
            "w/jHmEay+GZ3Bb1gCoC3Bh4m/oAFcbdYhayyFdz2iUoilnjBavINKfG6wbF91XWN\n" +
            "NE+bUe07Ea+MmByqM2ydNcoB9LIW/r/4CXlLLTesOuaaNaztsPk62Z2CZV+79g0S\n" +
            "QOUxNL61AoKl39O9HbBDci23tPaOrI6pJpXw8ZZgWlAbS+doTfFcJp7+X44CXqwi\n" +
            "5I0krtfhDRWiOOtNpRKSx2NwO4FitYGSHLzM0nhKDzB/2e6Y4w7gdXliXxKEktCP\n" +
            "NzVVxWC7q0tFtq2y9nr4V/qbQpBV/O2JlaaBB73uP5WuiDPsOHE6NDuAR8r8GQ+A\n" +
            "YJTlia7+pucCAwEAAaNQME4wHQYDVR0OBBYEFNcydXd/HQMKXOK+ejyA7kCBNXJa\n" +
            "MB8GA1UdIwQYMBaAFNcydXd/HQMKXOK+ejyA7kCBNXJaMAwGA1UdEwQFMAMBAf8w\n" +
            "DQYJKoZIhvcNAQELBQADggEBAIVHa+VjR0bSHGgt1ZSbemT+mIXtc8aMWd6AfJc4\n" +
            "we4yXG/PJth2Ckz/diT2k60xeM11FAlHFz4OXDBVG2Fir5nR3HqG+rwwTm5iDRPn\n" +
            "DcjgyABnl3BuvOxPJ74dqd4mn+6b3mzrJTLVI48+35BP2iZVEZsrp2M7x5tPrEnx\n" +
            "Zln2/rDIFphIUd2XFkfU0rMxDGRGQxRsQ05Ewo4h71WfuUbrMq2aZc9VfSPfL2lR\n" +
            "4RhiXKI4d3VvIoErvsLsjL9jWO5+662OqlTNp49elDSPQjdb9aNjg/xKAzp7Qy1A\n" +
            "0Fu5spPxo284I3V75KlqFBMcWk0OLpiHfdtxbeyHsL14TWs=\n" +
            "-----END CERTIFICATE-----\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getHttp();
        getHttps();

        postHttp();
        postHttps();

        uploadFile();
        downloadFile();

    }

    public void getHttp(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            NetClient.getInstance().getRequest(url, jsonObject, new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "getHttp:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "getHttp:" + ((model) response).getName());
                    }
                }
            }, model.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public void getHttps(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            NetClient.getInstance().initHttpsClient(crt);

            NetClient.getInstance().getHttpsRequest(httpsUrl, jsonObject, new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "getHttps:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "getHttps:" + ((model) response).getName());
                    }
                }
            }, model.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void postHttp(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            NetClient.getInstance().postRequest(url, jsonObject, new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "postHttp:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "postHttp:" + ((model) response).getName());
                    }
                }
            }, model.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void postHttps(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            NetClient.getInstance().initHttpsClient(crt);
            NetClient.getInstance().postHttpsRequest(httpsUrl, jsonObject, new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "postHttps:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "postHttps:" + ((model) response).getName());
                    }
                }
            }, model.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void uploadFile(){
        try {
            NetClient.getInstance().uploadFile(uploadUrl, "/storage/sdcard0/DCIM/Camera/IMG20160122201128.jpg", "pic", new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "uploadFile:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "uploadFile:" + ((model) response).getCode());
                    }
                }
            }, model.class);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public void downloadFile(){
        try {
            NetClient.getInstance().downloadFile(downloadUrl, null,"/storage/sdcard0/hello.jpg", new NetClient.NetCallBack() {
                @Override
                public void onFail(Exception e) {
                    Log.d("netSdk", "downloadFile:fail");
                    e.printStackTrace();
                }

                @Override
                public void onProcess(float process) {
                }

                @Override
                public void onSuccess(Object response) {
                    if(response instanceof model) {
                        Log.d("netSdk", "downloadFile" + ((model) response).getCode());
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
