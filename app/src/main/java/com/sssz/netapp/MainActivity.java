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
            "MIIDhzCCAm+gAwIBAgIJAKbpUP9RA1ahMA0GCSqGSIb3DQEBCwUAMFkxCzAJBgNV\n" +
            "BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX\n" +
            "aWRnaXRzIFB0eSBMdGQxEjAQBgNVBAMMCWZsYXNrdGVzdDAgFw0xODAzMTAwODE0\n" +
            "MjNaGA8yMTE4MDIxNDA4MTQyM1owWTELMAkGA1UEBhMCQVUxEzARBgNVBAgMClNv\n" +
            "bWUtU3RhdGUxITAfBgNVBAoMGEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDESMBAG\n" +
            "A1UEAwwJZmxhc2t0ZXN0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n" +
            "7rHjhpVnIRhf7AVHP7xvdr49cgnRJGw5t8loqPlKdGXGu4hTaIjzhQ7Y1h1m3L81\n" +
            "iOEXPLfkjaSVk4zYEonYtqFNpwWofM/eXyBvGlo9febY2pZJFuLUmz+6A1zSbocs\n" +
            "PJO0eiV7qVSGmUzfsD63q7VDLdJVUdvclAHAtql1GJIpKYZ2DWHC29/UGM2cTzkG\n" +
            "ATH9DUKEe3xR+J4YvdBmrgCLPNWlmd/50EEo14E5P5LXqaLAWHbAXrRzA6TCY6qn\n" +
            "E4lIP4s8HB5J89ujUgdxjRc97fUPHNV6BUxZPFf291UVuKz2l8pAFjLXTcJK0Xg5\n" +
            "uNpWfGBhHs7lAXgLBEfXswIDAQABo1AwTjAdBgNVHQ4EFgQU1Z1djsbu5GTpiYW0\n" +
            "z4ipZCQXPGgwHwYDVR0jBBgwFoAU1Z1djsbu5GTpiYW0z4ipZCQXPGgwDAYDVR0T\n" +
            "BAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAQEAg+G2AsrtO/ANqkYt4bQudeysf6kZ\n" +
            "v4Z32AF0dOIu3fnRVxFyBND7Mifre9+XMjVh/eQABbd6nglfwvFz/N+kKSVS6BLP\n" +
            "oXtvmOep9YW4BwCJLWTwN8HL78HdmdE3Zt5QivUCugqEoREM7hUZ7a+Co7blbhIy\n" +
            "JO2pWHwEbQT+Tfr7f7VFUvK3Nt/OhabuBovdsvxLcnHup4arNTJAHMBkV9vthYFe\n" +
            "TRtuJ/goYXRa/xGPpK1eReXOAuGATwnPaggiq6A3rqmSbhKI32Nx176xkxlxTlSV\n" +
            "1Xycya8cBMQkbs27qKmHZMsPVnL0xv/DgU60c+osxC0RNgrj5Z7Q9JHTtA==\n" +
            "-----END CERTIFICATE-----\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


  //      getHttp();
        getHttps();

//        postHttp();
 //       postHttps();

//        uploadFile();
//        downloadFile();

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
                    Log.d("netSdk", "uploadFile:onProcess" + process);
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
                    Log.d("netSdk", "downloadFile:onProcess" + process);
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
