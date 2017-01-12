package com.person.han.freebst.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.person.han.freebst.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";
    private Context mContext;
    private GridView gridView;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    private long mExitTime;
    private int[] icon = {
            R.mipmap.phone,
            R.mipmap.history,
            R.mipmap.stock,
            R.mipmap.joke,
            R.mipmap.weather,
            R.mipmap.credit_card,
            R.mipmap.ip,
            R.mipmap.basketball,
            R.mipmap.wannianli,
            R.mipmap.huilv,
            R.mipmap.book,
            R.mipmap.movie,
            R.mipmap.robot,
            R.mipmap.football,
            R.mipmap.gold,
            R.mipmap.television,
            R.mipmap.voice,
            R.mipmap.weixin,
            R.mipmap.wifi,
            R.mipmap.xingzuo,
            R.mipmap.zhougong,
            R.mipmap.bus,
            R.mipmap.dictionary,
            R.mipmap.huangli,
            R.mipmap.jiazhaokaoti,
            R.mipmap.youbian
    };
    private String[] icon_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mExitTime = System.currentTimeMillis();
        mContext = this;
        File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/FreeBst");
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
                Toast.makeText(mContext, "成功创建文件夹", Toast.LENGTH_SHORT).show();
            }
        }
        icon_text = getResources().getStringArray(R.array.main_text);
        gridView = (GridView) findViewById(R.id.main_grid_view);
        //新建List
        data_list = new ArrayList<>();
        //获取数据
        data_list = getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.main_grid_item, from, to);
        //配置适配器
        gridView.setAdapter(sim_adapter);

        gridView.setOnItemClickListener(new ItemClickListener());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (System.currentTimeMillis()-mExitTime>2000){
                Toast.makeText(mContext,"再按一次退出",Toast.LENGTH_SHORT).show();
                mExitTime=System.currentTimeMillis();
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);
            switch (item.get("text").toString()) {
                case "电话归属地":
                    startActivity(new Intent(mContext, phoneNumberAttribution.class));
                    break;
                case "历史上的今天":
                    startActivity(new Intent(mContext,historyToday.class));
                    break;
                case "股票":
                    startActivity(new Intent(mContext,stock.class));
                    break;
                case "笑话":
                    startActivity(new Intent(mContext,joke.class));
                    break;
                case "天气":
                    startActivity(new Intent(mContext,weather.class));
                    break;
                case "身份证查询":
                    startActivity(new Intent(mContext,identityCard.class));
                    break;
                case "ip查询":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "NBA":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "万年历":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "汇率":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "书籍查询":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "最新电影":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "机器人对话":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "足球":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "黄金":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "电视节目表":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "语音识别":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "微信精选":
                    startActivity(new Intent(mContext,weiXinJingXuan.class));
                    break;
                case "全国WiFi":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "星座运势":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "周公解梦":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "公交车查询":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "词典":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "老黄历":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "驾照考库":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                case "邮编查询":
                    Toast.makeText(mContext, R.string.is_build_ing, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    }

    public List<Map<String, Object>> getData() {
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", icon[i]);
            map.put("text", icon_text[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
