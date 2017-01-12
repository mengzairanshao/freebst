package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.message.wei_xin_jing_xuan_message;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;
import com.person.han.freebst.utils.UITools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class weiXinJingXuan extends AppCompatActivity {
    private String TAG = "weiXinJingXuan";
    private Context mContext;
    private static List<wei_xin_jing_xuan_message> weiXinJingXuanMessages;
    private ListView listView;
    private static Handler handler;
    private static weiXinListAdapter wxAda;
    private int pno=1;
    private static Resources resources;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wei_xin_jing_xuan);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("微信精选");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        resources=getResources();
        weiXinJingXuanMessages=new ArrayList<>();
        listView= (ListView) findViewById(R.id.wei_xin_list);
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        new getBitmap().execute(weiXinJingXuanMessages.get(0).getFirstImg(),0);
                        if (wxAda==null){
                            wxAda=new weiXinListAdapter(mContext);
                            listView.setAdapter(wxAda);
                        }else {
                            wxAda.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        Intent intent=new Intent(mContext,weiXinJingXuanDetail.class);
                        intent.putExtra("url",weiXinJingXuanMessages.get(msg.arg1).getUrl());
                        startActivity(intent);
                    default:
                        break;
                }
                return true;
            }
        });

        getData(pno++);
        UITools.scrollListener(listView, UITools.setFooter(mContext), new UITools.Run() {
            @Override
            public void run(Object o) {
                getData(pno++);
            }
        },mContext);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wxAda=null;
        weiXinJingXuanMessages=null;
        listView=null;
        handler=null;
        mContext=null;
        resources=null;
    }

    public void getData(int pno){
        new NetConnection(Config.WEIXIN_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("error_code").equals("0")) {
                        JSONArray list = jsonObject.getJSONObject("result").getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            String firstImg = list.getJSONObject(i).getString("firstImg");
                            String id = list.getJSONObject(i).getString("id");
                            String source = list.getJSONObject(i).getString("source");
                            String title = list.getJSONObject(i).getString("title");
                            String url = list.getJSONObject(i).getString("url");
                            String mark = list.getJSONObject(i).getString("mark");
                            weiXinJingXuanMessages.add(new wei_xin_jing_xuan_message(mark, firstImg, id, source, title, url));
                        }
                        Message message=new Message();
                        message.what=1;
                        handler.sendMessage(message);
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("reason"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, "key", Config.WEIXIN_KEY,"pno",String.valueOf(pno));
    }

    private static class weiXinListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        private weiXinListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return weiXinJingXuanMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return weiXinJingXuanMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_wei_xin_jing_xuan_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final wei_xin_jing_xuan_message jokeItem = (wei_xin_jing_xuan_message) getItem(position);
            viewHolder.imageView.setImageBitmap(jokeItem.getBitmap());
            viewHolder.text.setText(jokeItem.getTitle());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message=new Message();
                    message.what=2;
                    message.arg1=position;
                    handler.sendMessage(message);
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView text;
            private ImageView imageView;

            private ViewHolder(View view) {
                this.text = (TextView) view.findViewById(R.id.wei_xin_jing_xuan_title);
                this.imageView = (ImageView) view.findViewById(R.id.wei_xin_jing_xuan_img);
            }
        }
    }

    /**
     * 获取图片的异步任务
     */
    private static class getBitmap extends AsyncTask<Object, Void, Object> {
        private int position;

        @Override
        protected Object doInBackground(Object... params) {
            position=(int)params[1];
            return NetConnection.getImageBitmap((String) params[0]);
        }

        @Override
        protected void onPostExecute(Object object) {
            if (weiXinJingXuanMessages != null && position <= weiXinJingXuanMessages.size()) {
                if (object==null){
                    weiXinJingXuanMessages.get(position).setBitmap(BitmapFactory.decodeResource(resources,R.drawable.ask));
                }else {
                    weiXinJingXuanMessages.get(position).setBitmap((Bitmap) object);
                }
                weiXinJingXuanMessages.get(position).setGetBitmap(true);
                if (wxAda!=null)wxAda.notifyDataSetChanged();
                for (int i=0;i<weiXinJingXuanMessages.size();i++){
                    if (!weiXinJingXuanMessages.get(i).getGetBitmap()){
                        new getBitmap().execute(weiXinJingXuanMessages.get(i).getFirstImg(),i);
                        break;
                    }
                }
            }
         }
    }
}
