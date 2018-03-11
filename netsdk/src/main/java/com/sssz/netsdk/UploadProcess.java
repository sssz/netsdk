package com.sssz.netsdk;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by sssz on 2018/3/10.
 */

public class UploadProcess extends RequestBody {

    private MediaType mediaType;
    private String filePath;
    private NetClient.NetCallBack netCallBack;
    private File file;

    public UploadProcess(MediaType mediaType, String filePath, NetClient.NetCallBack netCallBack) {
        this.mediaType = mediaType;
        this.filePath = filePath;
        this.netCallBack = netCallBack;
        file = new File(filePath);
    }
    @Override
    public long contentLength() throws IOException {
        if(NetClient.checkFile(filePath)){
            return file.length();
        }
        return -1L;
    }
    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        Source source = null;
        Buffer buf = null;
        try {
            source = Okio.source(file);
            buf = new Buffer();
            long total = contentLength();
            long current = 0;
            for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                bufferedSink.write(buf, readCount);
                current += readCount;
                if(netCallBack != null){
                    netCallBack.onProcess(Float.valueOf(current)/Float.valueOf(total));
                }
            }
            bufferedSink.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(source != null){
                source.close();
            }
            if(buf != null) {
                buf.close();
            }
        }
    }
}
