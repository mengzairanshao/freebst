package com.person.han.freebst.netConnc;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by h on 2016/6/28.
 *
 */
public class NetConnection {
    private final String TAG = "NetConnection";

    public NetConnection(final String url, final HttpMethod httpMethod, final SuccessCallback successCallback, final FailCallback failCallback, final String... kvs) {
        getOrPost(url,httpMethod,successCallback,failCallback,kvs);
    }

    public NetConnection(final String url, final HttpMethod httpMethod, final SuccessCallback successCallback, final FailCallback failCallback, Handler handler, final String... kvs) {
        getOrPost(url,httpMethod,successCallback,failCallback,kvs);
    }

    private void setParams(RequestParams params,String...kvs){
        for (int i = 0; i < kvs.length; i += 2) {
            params.put(kvs[i],kvs[i + 1]);

        }
    }

    private void getOrPost(final String url, final HttpMethod httpMethod, final SuccessCallback successCallback, final FailCallback failCallback, final String... kvs){
        if (httpMethod.equals(HttpMethod.POST)) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            setParams(params,kvs);
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String json = new String(responseBody);
                    Log.e(TAG, "POST成功");
                    if (successCallback != null) successCallback.onSuccess(json);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "POST失败");
                    if (failCallback != null) failCallback.onFail();
                }
            });
        }
        if (httpMethod.equals(HttpMethod.GET)) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            setParams(params,kvs);

            client.get(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        String json = new String(responseBody);
                        if (successCallback != null) successCallback.onSuccess(json);
                    } else {
                        Log.e(TAG, "statusCode==" + statusCode);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.e(TAG, "statusCode==" + statusCode + "  GET连接失败");
                    if (failCallback != null) failCallback.onFail();
                }
            });
        }
    }
    public interface SuccessCallback {
        void onSuccess(String result);
    }

    public interface FailCallback {
        void onFail();
    }

    public static boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping1次
            // 读取ping的内容，可不加。
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.i("TTT", "result content : " + stringBuffer.toString());
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Log.i("TTT", "result = " + result);
        }
        return false;
    }

    /**
     * 从指定URL获取图片
     *
     * @param url
     * @return
     */
    public static Object getImageBitmap(String url) {
        if (url.equals(""))return null;
        URL imgUrl;
        Bitmap bitmap = null;
        byte[] gifByte=null;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            if (url.contains(".gif")){
                byte[] buffer=new byte[1024];
                int len=0;
                while((len=is.read(buffer))>0){
                    out.write(buffer,0,len);
                }
                gifByte=out.toByteArray();
            } else{
                bitmap = BitmapFactory.decodeStream(is);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url.contains(".gif")?gifByte:bitmap;
    }

}

