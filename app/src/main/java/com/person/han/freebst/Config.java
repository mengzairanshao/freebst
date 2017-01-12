package com.person.han.freebst;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Created by han on 2016/9/25.
 */

public class Config {
    private static String APP_ID = "com.person.han.freebst";
    public static String APP_PATH = Environment.getExternalStorageDirectory().getPath() + "/FreeBst";
    public static String regularEx = "#";
    public static String CITY_LIST="city";
    public static String CITY_CHECKED="city-checked";
    public static String LOGIN_URL = "http://prod.tjut.cc/api/login";
    public static String CHECK_URL = "http://prod.tjut.cc/api/user";
    public static String ENCODE = "UTF-8";
    public static String UID = "uid";
    public static String TOKEN = "token";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String APP_AD_ID = "9118c000bd03f593";
    public static String APP_SECRET = "5395466703c8e345";
    public static String JOKE_URL = "http://v.juhe.cn/joke/randJoke.php";
    public static String JOKE_KEY = "03fff1d6390f4c26aad3d0e1646e3032";
    public static String WEIXIN_URL = "http://v.juhe.cn/weixin/query";
    public static String WEIXIN_KEY = "79bf8c4f8da652caffde3f49f861e191";
    public static String WEATHER_URL = "http://op.juhe.cn/onebox/weather/query";
    public static String WEATHER_KEY = "3dcc21fd1cf8087cb644ed83af1a4f17";
    public static String PHONE_ATTRIBUTION_URL = "http://apis.juhe.cn/mobile/get";
    public static String PHONE_ATTRIBUTION_KEY = "31b2c5e4c7a20a2832eea3bb7505b222";
    public static String HISTORY_TODAY_URL = "http://v.juhe.cn/todayOnhistory/queryEvent.php";
    public static String HISTORY_TODAY_BY_ID_URL = "http://v.juhe.cn/todayOnhistory/queryDetail.php";
    public static String HISTORY_TODAY_KEY = "3ee3e7d60843c179fa0d2a13ed4da6df";
    public static String IDENTITY_CARD_KEY = "38451117fa1dd51da9413e10244e657e";
    public static String IDENTITY_CARD_CHECK_URL = "http://apis.juhe.cn/idcard/index";
    public static String IDENTITY_CARD_LEAK_URL = "http://apis.juhe.cn/idcard/leak";
    public static String IDENTITY_CARD_LOSS_URL = "http://apis.juhe.cn/idcard/loss";
    public static String BAIDU_GEOCODING_URL = "http://api.map.baidu.com/geocoder/v2/";
    public static String BAIDU_GEOCODING_KEY = "RBrBx0GrkxE7RR9DcBnj6PhsGSpOYuT7";
    public static String BAIDU_GEOCODING_MCODE = "D3:0D:19:99:C6:1D:5E:FC:D4:B1:5B:3E:87:CB:FC:79:41:DA:FA:37;com.person.han.freebst";
    public static String TEST_URL="http://api.map.baidu.com/geocoder/v2/?ak="+BAIDU_GEOCODING_KEY+"&callback=renderReverse&output=json&pois=0&mcode="+BAIDU_GEOCODING_MCODE;

    public static String getCachedDATA(Context context, String key) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(key, null);
    }

    public static void cacheDATA(Context context, String data, String key, Boolean isArray, Boolean isNotRepeat) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();

        if (isArray) {
            String str = "";
            if (isCachedDATA(context, key)) {
                if (isNotRepeat) {
                    String a=getCachedDATA(context, key);
                    Boolean b=a.contains(data);
                    if (getCachedDATA(context, key).contains(data)) {
                        str=getCachedDATA(context, key);
                    } else {
                        str = getCachedDATA(context, key) + data;
                        str += regularEx;
                    }
                } else {
                    str = getCachedDATA(context, key) + data;
                    str += regularEx;
                }
            } else {
                str += data;
                str += regularEx;
            }
            e.putString(key, str);
        } else {
            e.putString(key, data);
        }
        e.apply();
    }

    public static void cacheDATA(Context context, String data, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();

        e.putString(key, data);
        e.apply();
    }

    public static Boolean isCachedDATA(Context context, String key) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).contains(key);
    }

    public static Boolean isCachedDATA(Context context, String key, String data,Boolean isArray) {
        Boolean isCachedDATA=false;
        if (isArray){
            String[] str=getCachedDATA(context,key).split(regularEx);
            for (String aStr : str) {
                if (data.equals(aStr))
                    isCachedDATA = true;
            }
        }
        return isCachedDATA;
    }

    public static void removeCachedData(Context context, String key) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        e.remove(key);
        e.apply();
    }

    public static int removeCachedData(Context context, String key, String data,Boolean isArray) {
        SharedPreferences.Editor e = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        int location=-1;
        if (isArray){
            String resultStr="";
            String[] str=getCachedDATA(context,key).split(regularEx);
            for (int i=0;i<str.length;i++) {
                if (!data.equals(str[i]))
                    resultStr += str[i] + regularEx;
                else
                    location=i;
            }
            e.putString(key,resultStr);
        }
        e.apply();
        return location;
    }

}

