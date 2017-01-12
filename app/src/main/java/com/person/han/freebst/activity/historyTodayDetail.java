package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class historyTodayDetail extends AppCompatActivity {
    private Context mContext;
    private String TAG = "historyTodayDetail";
    private TextView textView;
    private TextView title;
    private Button next;
    private Button last;
    private ScrollView scrollView;
    private Animation btnAniTrans;
    private int ScrollY;
    private int position;
    private String[] e_id_arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_today_detail);
        mContext = this;
        final Intent i = this.getIntent();
        position = i.getIntExtra("position", 0);
        e_id_arr = i.getExtras().getStringArray("e_id_arr");
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("详细");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        textView = (TextView) findViewById(R.id.content);
        title = (TextView) findViewById(R.id.wei_xin_jing_xuan_title);
        next = (Button) findViewById(R.id.next);
        last = (Button) findViewById(R.id.last);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        btnVis();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                getData(e_id_arr[position]);
                btnVis();
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                getData(e_id_arr[position]);
                btnVis();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ScrollY = scrollView.getScrollY();
                        break;
                    case MotionEvent.ACTION_UP:
                        /**
                         * 小于0说明是,内容在向上滚动
                         * 其他情况是内容向下滚动
                         */
                        if (ScrollY - scrollView.getScrollY() < 0) {
                            if (next.getVisibility() == View.VISIBLE || last.getVisibility() == View.VISIBLE) {
                                btnAniTrans = new TranslateAnimation(0.0f, 0.0f, 0.0f, 100.0f);
                                btnAniTrans.setDuration(500);
                                btnVis(View.INVISIBLE,btnAniTrans);
                            }
                        } else {
                            if (next.getVisibility() == View.INVISIBLE && last.getVisibility() == View.INVISIBLE) {
                                btnAniTrans = new TranslateAnimation(0.0f, 0.0f, 100.0f, 0.0f);
                                btnAniTrans.setDuration(500);
                                btnVis(View.VISIBLE,btnAniTrans);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        getData(e_id_arr[position]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void btnVis() {
        if (position == e_id_arr.length-1) next.setVisibility(View.INVISIBLE);
        else next.setVisibility(View.VISIBLE);
        if (position == 0) last.setVisibility(View.INVISIBLE);
        else last.setVisibility(View.VISIBLE);
    }

    public void btnVis(int vis,Animation btnAniTrans){
        if (position < e_id_arr.length-1) {
            next.startAnimation(btnAniTrans);
            next.setVisibility(vis);
        }
        if (position > 0) {
            last.startAnimation(btnAniTrans);
            last.setVisibility(vis);
        }
    }

    public void getData(String e_id) {
        new NetConnection(Config.HISTORY_TODAY_BY_ID_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("reason").equals("success")) {
                        textView.setText(jsonObject.getJSONArray("result").getJSONObject(0).getString("content"));
                        title.setText(jsonObject.getJSONArray("result").getJSONObject(0).getString("title"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, "key", Config.HISTORY_TODAY_KEY, "e_id", e_id);
    }
}
