package com.sssz.netsdk;


import com.alibaba.fastjson.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sssz on 2018/3/7.
 */

public class NetClient {
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";


    public interface NetCallBack {
        void onFail(Exception e);
        void onProcess(float process);
        void onSuccess(Object response);
    }

    private volatile static NetClient netClient;

    private OkHttpClient client, httpsClient;
    private MediaType mediaType;
    private CertUtils certUtils;

    public NetClient() {
        client = new OkHttpClient();
        httpsClient = null;
        mediaType = MediaType.parse("multipart/form-data");
    }

    public void initHttpsClient(String cert){
        try {
            certUtils = new CertUtils();
            certUtils.init(cert);
            httpsClient = new OkHttpClient.Builder()
                    .hostnameVerifier(certUtils.getHostNameVerifier())
                    // SSLSocketFactory,  X509TrustManager
                    .sslSocketFactory(certUtils.getSSLSocketFactory(), certUtils.getTrustManager())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static NetClient getInstance() {
        if (netClient == null) {
            synchronized (NetClient.class) {
                if (netClient == null) {
                    netClient = new NetClient();
                }
            }
        }
        return netClient;
    }

    public void setMediaType(MediaType mediaType) {
        if (mediaType != null) {
            this.mediaType = mediaType;
        }
    }

    private boolean checkParamUrl(String url) {
        if (url == null || url.length() == 0) {
            return false;
        }
        if (!url.startsWith(HTTP_PREFIX) && !url.startsWith(HTTPS_PREFIX)) {
            return false;
        }
        return true;
    }

    public static boolean checkString(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return true;
    }

    public static boolean checkFile(String path) {
        if (!checkString(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    private void checkHttpsInit() throws Exception{
        if(httpsClient == null){
            throw new Exception("https not init");
        }
    }
    private String buildUrl(String url, JSONObject jsonObject) throws JSONException {
        StringBuilder resultUrl = new StringBuilder(url);
        resultUrl.append("?");
        Iterator iterator = jsonObject.keys();
        Object key, value;
        while (iterator.hasNext()) {
            key = iterator.next();
            if (key instanceof String) {
                value = jsonObject.get((String) key);
                if (value instanceof String || value instanceof Integer || value instanceof Float) {
                    resultUrl.append(key + "=" + String.valueOf(value));
                    if (iterator.hasNext()) {
                        resultUrl.append("&");
                    }
                }
            }
        }
        return resultUrl.toString();
    }

    private FormBody buildBody(JSONObject jsonObject) throws JSONException {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator iterator = jsonObject.keys();
        Object key, value;
        while (iterator.hasNext()) {
            key = iterator.next();
            if (key instanceof String) {
                value = jsonObject.get((String) key);
                if (value instanceof String || value instanceof Integer || value instanceof Float) {
                    builder.add(String.valueOf(key), String.valueOf(value));
                }
            }
        }
        return builder.build();
    }

    private Request createGetRequest(String url, JSONObject jsonObject) throws Exception {
        if (!checkParamUrl(url)) {
            throw new Exception("Param Illegal Exception");
        }
        String newUrl = url;
        if (jsonObject != null && jsonObject.length() != 0) {
            newUrl = buildUrl(url, jsonObject);
        }
        return new Request.Builder().url(newUrl).get().build();
    }
    private Request createPostRequest(String url, JSONObject jsonObject) throws Exception{
        if (!checkParamUrl(url)) {
            throw new Exception("Param Illegal Exception");
        }
        FormBody body = null;
        if (jsonObject != null && jsonObject.length() != 0) {
            body = buildBody(jsonObject);
        }
        return new Request.Builder().url(url).post(body).build();
    }

    /*
    * 方法说明：
    * getRequest: get请求
    * postRequest: post请求
    * uploadFile: 以Post方式上传文件
    * downloadFile: 下载文件
    *
    * 参数说明：
    * url: 服务器域名或IP, 以http://或https://开头的URL
    * jsonObject: get请求的参数，将拼接到url后，可为null
    *   key: String
    *   value: String， Integer, Float
    * netCallBack: 回调接口
    *   onFail： 失败
    *   onProcess: 上传或下载进度
    *   onSuccess: 成功
    * filePath: 上传文件路径，下载文件保存路径（带文件名）
    * name: 上传文件对应字段名
    * tClass: 接受的返回值类型变量
     */
    public <T> void getRequest(String url, JSONObject jsonObject, final NetCallBack netCallBack, final Class<T> tClass) throws Exception {

        Request request = createGetRequest(url, jsonObject);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (netCallBack != null) {
                    netCallBack.onFail(e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String re = response.body().string();
                    Object m = JSON.parseObject(re, tClass);
                    netCallBack.onSuccess(m);
                }
            }
        });
    }

    public <T> void postRequest(String url, JSONObject jsonObject, final NetCallBack netCallBack, final Class<T> tClass) throws Exception {
        Request request = createPostRequest(url, jsonObject);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (netCallBack != null) {
                    netCallBack.onFail(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String re = response.body().string();
                    Object m = JSON.parseObject(re, tClass);
                    netCallBack.onSuccess(m);
                }
            }
        });
    }

    public <T> void uploadFile(String url, String filePath, String name, final NetCallBack netCallBack, final Class<T> tClass) throws Exception {
        if (!checkParamUrl(url) || !checkFile(filePath) || !checkString(name)) {
            throw new Exception("Param Illegal Exception");
        }
        String filenName = filePath;
        if (filePath.lastIndexOf("/") != -1) {
            filenName = filePath.substring(filePath.lastIndexOf("/")+1);
        }
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, filenName, new UploadProcess(mediaType, filePath, netCallBack))
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCallBack.onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String re = response.body().string();
                    Object m = JSON.parseObject(re, tClass);
                    netCallBack.onSuccess(m);
                }
            }
        });

    }

    public <T> void downloadFile(String url, JSONObject jsonObject, final String filePath, final NetCallBack netCallBack) throws Exception {
        if (!checkParamUrl(url)) {
            throw new Exception("Param Illegal Exception");
        }
        FormBody body = null;
        if (jsonObject != null && jsonObject.length() != 0) {
            body = buildBody(jsonObject);
        } else {
            body = new FormBody.Builder()
                    .add("hello", "netSdk")
                    .build();
        }
        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (netCallBack != null) {
                    netCallBack.onFail(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                long total = 0, current = 0;
                byte[] buff = null;
                try {
                    inputStream = response.body().byteStream();
                    total = response.body().contentLength();
                    if (response.isSuccessful() && total > 0) {
                        outputStream = new FileOutputStream(filePath);
                        buff = new byte[2048];
                        for (int len; (len = inputStream.read(buff)) != -1; ) {
                            outputStream.write(buff, 0, len);
                            current += len;
                            if(netCallBack != null) {
                                netCallBack.onProcess(Float.valueOf(current)/Float.valueOf(total));
                            }
                        }
                        outputStream.flush();
                    }
                    if(netCallBack != null) {
                        netCallBack.onSuccess(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        });
    }

    public <T> void getHttpsRequest(String url, JSONObject jsonObject, final NetCallBack netCallBack, final Class<T> tClass) throws Exception {
        checkHttpsInit();
        Request request = createGetRequest(url, jsonObject);
        httpsClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (netCallBack != null) {
                    netCallBack.onFail(e);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String re = response.body().string();
                    Object m = JSON.parseObject(re, tClass);
                    netCallBack.onSuccess(m);
                }
            }
        });
    }

    public <T> void postHttpsRequest(String url, JSONObject jsonObject, final NetCallBack netCallBack, final Class<T> tClass) throws Exception {
        checkHttpsInit();
        Request request = createPostRequest(url, jsonObject);
        httpsClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (netCallBack != null) {
                    netCallBack.onFail(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String re = response.body().string();
                    Object m = JSON.parseObject(re, tClass);
                    netCallBack.onSuccess(m);
                }
            }
        });

    }


}
