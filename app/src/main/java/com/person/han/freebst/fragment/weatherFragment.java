package com.person.han.freebst.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link weatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link weatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class weatherFragment extends Fragment implements View.OnClickListener {

    private String TAG = "weatherFragment";
    private ImageView imageView;
    private TextView weatherWind;
    private TextView weatherTemp;
    private TextView update;
    private TextView airQuality;
    private TextView releaseTime;
    private TextView airQualityImgText;
    private TextView airIndex;
    private TextView pm25;
    private TextView pm10;
    private TextView airDes;
    private LinearLayout weatherPreLin;
    private LinearLayout weatherLifeLin;
    private LinearLayout weather24Lin;
    private List<View> weatherPreViewList = new ArrayList<>();
    private List<View> weatherLifeList = new ArrayList<>();
    private List<View> weather24List = new ArrayList<>();
    private String[] resStr = {"chuanyi", "ganmao", "kongtiao", "xiche", "yundong", "ziwaixian"};
    private String[] resStrTitle = {"穿衣", "感冒", "空调", "洗车", "运动", "紫外线"};
    private final LinearLayout.LayoutParams pm25lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private Context mContext;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "city_name";
    private static final String ARG_PARAM2 = "resultStr";

    private String cityName;
    private String resultStr;

    private OnFragmentInteractionListener mListener;

    /**
     * Construction
     */
    public weatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment weatherFragment.
     */
    public static weatherFragment newInstance(String param1, String param2) {
        weatherFragment fragment = new weatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 初始化参数
     *
     * @param savedInstanceState 保存的状态
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityName = getArguments().getString(ARG_PARAM1);
            resultStr = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * 绘制View
     *
     * @param inflater           解析器
     * @param container          viewGroup
     * @param savedInstanceState 保存的状态
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        mContext = getActivity();
        init(view);
        getData(pm25lp, cityName);
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * 与Activity通信
     *
     * @param cityName    城市名称
     * @param action_code 操作码
     */
    public void communicateWithActivity(String cityName, int action_code) {
        if (mListener != null) {
            mListener.onFragmentInteraction(cityName, action_code);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * View点击Listener
     *
     * @param v 被点击的View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_page:
                communicateWithActivity(cityName, 1);
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String cityName, int action_code);
    }

    /**
     * 初始化控件
     *
     * @param view 解析出的RootView
     */
    public void init(View view) {
        weatherPreLin = (LinearLayout) view.findViewById(R.id.weather_pre_list);
        weatherLifeLin = (LinearLayout) view.findViewById(R.id.weather_life_list);
        weather24Lin = (LinearLayout) view.findViewById(R.id.weather_24);
        int weatherPreListItemNum = 5;
        setListView(weatherPreViewList, R.layout.weather_pre_list_item, weatherPreListItemNum);
        int weatherLifeListItemNum = 6;
        setListView(weatherLifeList, R.layout.weather_life_list_item, weatherLifeListItemNum);
        int weather24ListItemNum = 9;
        setListView(weather24List, R.layout.weather_24_list_item, weather24ListItemNum);
        imageView = (ImageView) view.findViewById(R.id.weather_img);
        weatherWind = (TextView) view.findViewById(R.id.weather_wind);
        weatherTemp = (TextView) view.findViewById(R.id.weather_temp);
        update = (TextView) view.findViewById(R.id.update);
        airQuality = (TextView) view.findViewById(R.id.air_quality);
        releaseTime = (TextView) view.findViewById(R.id.release_time);
        airQualityImgText = (TextView) view.findViewById(R.id.air_quality_img_text);
        airIndex = (TextView) view.findViewById(R.id.air_index);
        pm25 = (TextView) view.findViewById(R.id.pm25);
        pm10 = (TextView) view.findViewById(R.id.pm10);
        airDes = (TextView) view.findViewById(R.id.air_des);
        ImageButton deletePage = (ImageButton) view.findViewById(R.id.delete_page);
        deletePage.setOnClickListener(this);
    }

    /**
     * 获取天气字符串,并更新UI
     * 其中共有两种方式
     * 1.从本机缓存中获取后,再从网络获取,而后比较决定是否需要更新UI(比较部分在setUI中实现)
     * 2.从网络获取(刚添加的城市)
     *
     * @param lp       PM25的滑动条的LayoutParams
     * @param cityName 城市名称
     */
    public void getData(final LinearLayout.LayoutParams lp, final String cityName) {
        if (Config.isCachedDATA(mContext, cityName)) {
            setUI(Config.getCachedDATA(mContext, cityName), lp, cityName, false);
        }
        if (!resultStr.equals("")) {
            setUI(resultStr, lp, cityName, true);
        } else
            new NetConnection(Config.WEATHER_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, result);
                    setUI(result, lp, cityName, true);
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail() {

                }
            }, "cityname", cityName, "key", Config.WEATHER_KEY);
    }

    /**
     * 更新UI,
     * 1.新添加的城市一定会更新UI,
     * 2.result 从本机缓存中获取,再从网络获取,而后比较决定是否需要更新UI
     *
     * @param result           获取的天气字符串
     * @param lp               PM25的滑动条的LayoutParams
     * @param cityName         城市名称
     * @param isGetFromNetwork result是否是从网络获取
     */
    public void setUI(String result, LinearLayout.LayoutParams lp, String cityName, Boolean isGetFromNetwork) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            //判断result是否出错,如果出错则弹出reason
            if (jsonObject.getString("error_code").equals("0")) {
                JSONObject jResult = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime");
                //获取标准的城市名称,并将其存储起来
                cityName = jResult.getString("city_name");
                //是否更新UI
                Boolean isUpdate = true;
                //判断缓存是否和网络获取的result相同,如果相同则不更新UI,否则更新UI
                if (Config.isCachedDATA(mContext, cityName) && isGetFromNetwork) {
                    JSONObject isEqObject = new JSONObject(Config.getCachedDATA(mContext, cityName));
                    isUpdate = !jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime").getString("dataUptime").equals(
                            isEqObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime").getString("dataUptime"));
                }
                //更新UI
                if (isUpdate) {
                    //以城市名称为Key,result为data,将其存储起来
                    Config.cacheDATA(mContext, result, cityName);
                    //将城市名称添加到city_list里
                    Config.cacheDATA(mContext, cityName, Config.CITY_LIST, true, true);
                    JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONArray("weather");
                    //对5天预报更新UI
                    weatherPreListSetUI(jsonArray);
                    JSONObject tmp = jResult.getJSONObject("wind");
                    weatherWind.setText(tmp.getString("direct") + "    " + tmp.getString("power"));
                    tmp = jResult.getJSONObject("weather");
                    //判断是白天还是黑夜,采用不同的图片
                    if (Integer.parseInt(jResult.getString("time").substring(0, 2)) >= 18 || Integer.parseInt(jResult.getString("time").substring(0, 2)) <= 6) {
                        imageView.setImageResource(getResourceByReflect("n" + tmp.getString("img")));
                    } else {
                        imageView.setImageResource(getResourceByReflect("d" + tmp.getString("img")));
                    }
                    weatherTemp.setText(tmp.getString("info") + "    " + tmp.getString("temperature") + getResources().getString(R.string.degrees) + "C");
                    update.setText("发布时间:  " + jResult.getString("date").substring(5, 10) + "  " + jResult.getString("time").substring(0, 5) + "  " + jResult.getString("city_name"));
                    tmp = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("life").getJSONObject("info");
                    //对生活指数更新UI
                    weatherLifeListSetUI(tmp);
                    tmp = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("f3h");
                    //对24小时预报更新UI
                    weather24ListSetUI(tmp);
                    //部分地区没有pm2.5的数据,在此进行判断
                    if (!(jsonObject.getJSONObject("result").getJSONObject("data").get("pm25") instanceof JSONArray)) {
                        tmp = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("pm25");
                        releaseTime.setText(tmp.getString("cityName") + "  " + tmp.getString("dateTime").substring(11, 13) + ":00发布");
                        airQuality.setText(tmp.getJSONObject("pm25").getString("quality") + "  " + tmp.getJSONObject("pm25").getString("curPm"));
                        setMargin(lp, airQualityImgText, Integer.parseInt(tmp.getJSONObject("pm25").getString("curPm")));
                        airQualityImgText.setText(tmp.getJSONObject("pm25").getString("quality"));
                        airIndex.setText(tmp.getJSONObject("pm25").getString("curPm"));
                        pm25.setText(tmp.getJSONObject("pm25").getString("pm25"));
                        pm10.setText(tmp.getJSONObject("pm25").getString("pm10"));
                        airDes.setText(tmp.getJSONObject("pm25").getString("des"));
                    } else {
                        airQuality.setText(R.string.pm25_no_values);
                    }
                    //24小时预报划线
                    drawLine();
                }
            } else {
                Toast.makeText(mContext, cityName + ":  " + jsonObject.getString("reason"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成View
     *
     * @param viewList 待填充的List
     * @param res      要填充到List的View对应的资源id
     * @param num      要填充到List的View对应的数目
     */
    public void setListView(List<View> viewList, int res, int num) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        viewList.clear();
        for (int i = 0; i < num; i++) {
            viewList.add(layoutInflater.inflate(res, null));
        }
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName 图片资源名称
     * @return 图片资源id
     */
    public int getResourceByReflect(String imageName) {
        Class drawable = R.drawable.class;
        Field field;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
            r_id = R.drawable.ask;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }

    /**
     * 设置pm2.5的滑动条的margin属性
     * @param lp pm2.5的滑动条的LayoutParams
     * @param view pm2.5的滑动条对应的View
     * @param values pm25的值
     */
    public void setMargin(LinearLayout.LayoutParams lp, View view, int values) {
        int margin = 0;
        int width;
        int imgWidth = getResources().getDimensionPixelSize(R.dimen.pm25_background_width) / 2 + 5;
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = (metric.widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
        if (values >= 0 && values <= 200) {
            margin = values * width / 300;
        } else if (values > 200 && values <= 300) {
            margin = width * 2 / 3 + (values - 200) * width / 600;
        } else if (values > 300 && values <= 500) {
            margin = width * 5 / 6 + (values - 300) * width / 1200;
        }
        if (margin > width - imgWidth) {
            margin = width - imgWidth;
        } else if (margin < 0) {
            margin = imgWidth;
        }
        lp.setMargins(margin - imgWidth, 0, 0, getResources().getDimensionPixelSize(R.dimen.weather_margin_5));
        view.setLayoutParams(lp);
    }

    public void weatherPreListSetUI(JSONArray jsonArray) {
        weatherPreLin.removeAllViews();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("info");
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.which_day)).setText(jsonArray.getJSONObject(i).getString("date").substring(5, 10));
                if (i == 0) {
                    ((ImageView) weatherPreViewList.get(i).findViewById(R.id.dawn_img)).setImageResource(R.drawable.d0x);
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_weather)).setText("");
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_temp)).setText("" + getResources().getString(R.string.degrees) + "C");
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_wind)).setText("");
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_wind_power)).setText("");
                } else {
                    ((ImageView) weatherPreViewList.get(i).findViewById(R.id.dawn_img)).setImageResource(getResourceByReflect("d" + jsonObject.getJSONArray("dawn").get(0) + "x"));
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_weather)).setText(jsonObject.getJSONArray("dawn").getString(1));
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_temp)).setText(jsonObject.getJSONArray("dawn").getString(2) + getResources().getString(R.string.degrees) + "C");
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_wind)).setText(jsonObject.getJSONArray("dawn").getString(3));
                    ((TextView) weatherPreViewList.get(i).findViewById(R.id.dawn_wind_power)).setText(jsonObject.getJSONArray("dawn").getString(4));
                }
                ((ImageView) weatherPreViewList.get(i).findViewById(R.id.day_img)).setImageResource(getResourceByReflect("d" + jsonObject.getJSONArray("day").get(0) + "x"));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.day_weather)).setText(jsonObject.getJSONArray("day").getString(1));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.day_temp)).setText(jsonObject.getJSONArray("day").getString(2) + getResources().getString(R.string.degrees) + "C");
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.day_wind)).setText(jsonObject.getJSONArray("day").getString(3));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.day_wind_power)).setText(jsonObject.getJSONArray("day").getString(4));
                ((ImageView) weatherPreViewList.get(i).findViewById(R.id.night_img)).setImageResource(getResourceByReflect("d" + jsonObject.getJSONArray("night").get(0) + "x"));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.night_weather)).setText(jsonObject.getJSONArray("night").getString(1));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.night_temp)).setText(jsonObject.getJSONArray("night").getString(2) + getResources().getString(R.string.degrees) + "C");
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.night_wind)).setText(jsonObject.getJSONArray("night").getString(3));
                ((TextView) weatherPreViewList.get(i).findViewById(R.id.night_wind_power)).setText(jsonObject.getJSONArray("night").getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            weatherPreLin.addView(weatherPreViewList.get(i));
        }
    }

    public void weatherLifeListSetUI(JSONObject jsonObject) {
        try {
            weatherLifeLin.removeAllViews();
            for (int i = 0; i < jsonObject.length(); i++) {
                ((TextView) weatherLifeList.get(i).findViewById(R.id.item_title)).setText(resStrTitle[i]);
                ((TextView) weatherLifeList.get(i).findViewById(R.id.item_text)).setText(jsonObject.getJSONArray(resStr[i]).getString(0) + "\n" + jsonObject.getJSONArray(resStr[i]).getString(1));
                weatherLifeLin.addView(weatherLifeList.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void weather24ListSetUI(JSONObject jsonObject) {
        try {
            weather24Lin.removeAllViews();
            JSONArray jsonArrayTemp = jsonObject.getJSONArray("temperature");
            JSONArray jsonArrayPrec = jsonObject.getJSONArray("precipitation");
            for (int i = 0; i < jsonArrayTemp.length(); i++) {
                LinearLayout.LayoutParams weather24Templp =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
                weather24Templp.setMargins(0, 50 - Integer.parseInt(jsonArrayTemp.getJSONObject(i).getString("jb")), 0, Integer.parseInt(jsonArrayTemp.getJSONObject(i).getString("jb")));
                (weather24List.get(i).findViewById(R.id.weather_24_item_thuan_lin)).setLayoutParams(weather24Templp);
                ((TextView) weather24List.get(i).findViewById(R.id.weather_24_item_temp)).setText(jsonArrayTemp.getJSONObject(i).getString("jb"));
                ((TextView) weather24List.get(i).findViewById(R.id.weather_24_item_rain)).setText(jsonArrayPrec.getJSONObject(i).getString("jf"));
                ((TextView) weather24List.get(i).findViewById(R.id.weather_24_item_time)).setText(
                        jsonArrayPrec.getJSONObject(i).getString("jg").substring(8, 10) + "" + getResources().getString(R.string.clock));
                LinearLayout.LayoutParams weather24Preclp =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
                weather24Preclp.setMargins(0, 50 - 2 * Math.round(Float.parseFloat(
                        jsonArrayPrec.getJSONObject(i).getString("jf"))), 0, 2 * Math.round(Float.parseFloat(jsonArrayPrec.getJSONObject(i).getString("jf"))));
                (weather24List.get(i).findViewById(R.id.weather_24_item_rhuan_lin)).setLayoutParams(weather24Preclp);
                weather24Lin.addView(weather24List.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听器,如果布局绘制完成,或者改变将会调用该函数
     * 所以执行完逻辑后,将Listener移除
     */
    public void drawLine() {

        ((View) (weather24List.get(weather24List.size() - 1).findViewById(R.id.weather_24_item_thuan).getParent()).getParent().getParent()).getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int picSize = Math.round(getResources().getDimension(R.dimen.weather_24_list_circle_size));
                        int linLength = Math.round(getResources().getDimension(R.dimen.weather_24_list_item_width));
                        //新建一张和weather24Lin一样大小的Bitmap
                        Bitmap bitmap = Bitmap.createBitmap(weather24Lin.getWidth(), weather24Lin.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        //设置抗锯齿
                        paint.setAntiAlias(true);
                        paint.setColor(Color.GRAY);                    //设置画笔颜色
                        int bitmapAlpha = 100;
                        canvas.drawColor(Color.alpha(bitmapAlpha));                  //设置背景颜色
                        double bitmapPaintSize = 3.0;
                        paint.setStrokeWidth((float) bitmapPaintSize);              //设置线宽
                        for (int i = 0; i < weather24List.size() - 1; i++) {
                            int startX = linLength / 2 + picSize / 2 + i * linLength;
                            int startY = ((weather24List.get(i).findViewById(R.id.weather_24_item_rhuan)).getTop() + (weather24List.get(i + 1).findViewById(R.id.weather_24_item_rhuan)).getBottom()) / 2 +
                                    ((View) (weather24List.get(i).findViewById(R.id.weather_24_item_rhuan).getParent())).getTop() +
                                    ((View) (weather24List.get(i).findViewById(R.id.weather_24_item_rhuan).getParent()).getParent().getParent()).getTop();
                            int stopX = startX + linLength - picSize;
                            int stopY = ((weather24List.get(i + 1).findViewById(R.id.weather_24_item_rhuan)).getTop() + (weather24List.get(i + 1).findViewById(R.id.weather_24_item_rhuan)).getBottom()) / 2 +
                                    ((View) (weather24List.get(i + 1).findViewById(R.id.weather_24_item_rhuan).getParent())).getTop() +
                                    ((View) (weather24List.get(i + 1).findViewById(R.id.weather_24_item_rhuan).getParent()).getParent().getParent()).getTop();
                            canvas.drawLine(startX, startY, stopX, stopY, paint);       //绘制直线

                            startY = ((weather24List.get(i).findViewById(R.id.weather_24_item_thuan)).getTop() + (weather24List.get(i + 1).findViewById(R.id.weather_24_item_thuan)).getBottom()) / 2 +
                                    ((View) (weather24List.get(i).findViewById(R.id.weather_24_item_thuan).getParent())).getTop() +
                                    ((View) (weather24List.get(i).findViewById(R.id.weather_24_item_thuan).getParent()).getParent().getParent()).getTop();
                            stopY = ((weather24List.get(i + 1).findViewById(R.id.weather_24_item_thuan)).getTop() + (weather24List.get(i + 1).findViewById(R.id.weather_24_item_thuan)).getBottom()) / 2 +
                                    ((View) (weather24List.get(i + 1).findViewById(R.id.weather_24_item_thuan).getParent())).getTop() +
                                    ((View) (weather24List.get(i + 1).findViewById(R.id.weather_24_item_thuan).getParent()).getParent().getParent()).getTop();
                            canvas.drawLine(startX, startY, stopX, stopY, paint);       //绘制直线

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            weather24Lin.setBackground(new BitmapDrawable(bitmap));
                        } else {
                            weather24Lin.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        }
                        ((View) (weather24List.get(weather24List.size() - 1).findViewById(R.id.weather_24_item_thuan).getParent()).getParent().getParent()).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });


    }

}
