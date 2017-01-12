package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.message.history_today_message;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class historyToday extends AppCompatActivity {
    private String TAG = "historyToday";
    private Context mContext;
    private Handler mainHandler;
    private Spinner day;
    private ListView listView;
    private int monthNum;
    private int dayNum;
    private static List<history_today_message> msgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_today);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle("历史上的今天");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id.history_today_listview);
        listView.setDivider(new ColorDrawable(Color.BLUE));
        listView.setDividerHeight(1);
        Button getByDate = (Button) findViewById(R.id.history_today_get);
        Spinner month = (Spinner) findViewById(R.id.history_today_month);
        day = (Spinner) findViewById(R.id.history_today_day);
        Calendar c = Calendar.getInstance();
        monthNum = c.get(Calendar.MONTH);
        dayNum = c.get(Calendar.DAY_OF_MONTH);
        month.setSelection(monthNum, true);
        getDate(monthNum);
        day.setSelection(dayNum - 1, true);

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1: {
                        history_today_list_adapter list_adapter = new history_today_list_adapter(mContext,
                                history_today_list_adapter.getData(),
                                R.layout.activity_history_today_listview_item,
                                history_today_list_adapter.from,
                                history_today_list_adapter.to);
                        listView.setAdapter(list_adapter);
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        getData();

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthNum = position;
                getDate(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dayNum = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, historyTodayDetail.class);
                String[] e_id = new String[msgs.size()];
                for (int i = 0; i < msgs.size(); i++) {
                    e_id[i] = msgs.get(i).getId();
                }
                Bundle b = new Bundle();
                b.putStringArray("e_id_arr", e_id);
                intent.putExtra("position", position);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static class history_today_list_adapter extends SimpleAdapter {
        private static int[] to = {R.id.history_today_listview_content};
        private static String[] from = {"text"};

        public history_today_list_adapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public static List<Map<String, Object>> getData() {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> map ;
            for (int i = 0; i < msgs.size(); i++) {
                map = new HashMap<>();
                map.put("text", msgs.get(i).getDate() + msgs.get(i).getTitle());
                list.add(map);
            }
            return list;
        }
    }

    public void getDate(int position) {
        String[] mItems = getResources().getStringArray(R.array.day);
        switch (position + 1) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12: {
                ArrayAdapter dayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, mItems);
                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                day.setAdapter(dayAdapter);
                break;
            }
            case 4:
            case 6:
            case 9:
            case 11: {
                String[] mItem30 = new String[30];
                System.arraycopy(mItems, 0, mItem30, 0, 30);
                ArrayAdapter dayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, mItem30);
                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                day.setAdapter(dayAdapter);
                break;
            }
            case 2: {
                String[] mItem30 = new String[29];
                System.arraycopy(mItems, 0, mItem30, 0, 29);
                ArrayAdapter dayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, mItem30);
                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                day.setAdapter(dayAdapter);
                break;
            }
            default:
                break;
        }
    }

    public void getData() {
        new NetConnection(Config.HISTORY_TODAY_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("error_code").equals("0")) {
                        msgs.clear();
                        for (int i = 0; i < jsonObject.getJSONArray("result").length(); i++) {
                            msgs.add(new history_today_message(jsonObject.getJSONArray("result").getJSONObject(i).getString("date"),
                                    jsonObject.getJSONArray("result").getJSONObject(i).getString("title"),
                                    jsonObject.getJSONArray("result").getJSONObject(i).getString("e_id")));
                        }
                        Message message = new Message();
                        message.what = 1;
                        mainHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, mainHandler, "key", Config.HISTORY_TODAY_KEY, "date", (monthNum + 1) + "/" + dayNum);
    }

}
