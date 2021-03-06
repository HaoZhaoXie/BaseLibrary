package com.caijia.download;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cai.jia on 2018/6/26.
 */
public class FileResponse {

    private InputStream byteStream;
    private Map<String, List<String>> headers;
    private String realDownloadUrl;
    private int httpCode;

    public InputStream getByteStream() {
        return byteStream;
    }

    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getRealDownloadUrl() {
        return realDownloadUrl;
    }

    public void setRealDownloadUrl(String realDownloadUrl) {
        this.realDownloadUrl = realDownloadUrl;
    }

    public boolean isSuccessful() {
        return httpCode >= 200 && httpCode < 300;
    }

    public long getContentLength() {
        String contentLength = Utils.getHeader("Content-Length", headers);
        long contentLengthLong = -1;
        try {
            contentLengthLong = Long.parseLong(contentLength);
        } catch (Exception e) {
        }
        return contentLengthLong;
    }

    public String getFileName() {
        String disposition = Utils.getHeader("Content-Disposition", headers);

        if (!Utils.isEmpty(disposition)) {
            Matcher m = Pattern.compile(".*filename=.*").matcher(disposition.toLowerCase());
            if (m.find()) {
                String name = disposition.substring(disposition.indexOf("=") + 1, disposition.length());
                try {
                    return URLDecoder.decode(name.replaceAll("\"", ""), "utf-8");
                } catch (Exception e) {
                    return name.replaceAll("\"", "");
                }
            } else {
                String end = realDownloadUrl.substring(realDownloadUrl.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault());
                if (end.contains("jpg") || end.contains("gif") || end.contains("png") || end.contains("jpeg") || end.contains("bmp")) {
                    return Utils.getMD5(realDownloadUrl) +"."+ end;
                }
            }
        } else {
            int pathIndex = realDownloadUrl.lastIndexOf("\\");
            if (pathIndex == -1) {
                pathIndex = realDownloadUrl.lastIndexOf("/");
            }
            int whatIndex = realDownloadUrl.indexOf("?", pathIndex);
            return realDownloadUrl.substring(pathIndex + 1,
                    whatIndex == -1 ? realDownloadUrl.length() : whatIndex);
        }
        return Utils.getMD5(realDownloadUrl) + ".temp";//????????????????????????;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
