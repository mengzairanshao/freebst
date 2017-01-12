package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.message.joke_message;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;
import com.person.han.freebst.utils.UITools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class joke extends AppCompatActivity {
    private static String TAG = "joke";
    private Context mContext;
    private static jokeListAdapter jokeImgListAda;
    private jokeListAdapter jokeTextListAda;
    private PagerAdapter pagerAdapter;
    private static MyHandler jokeHandler;
    private List<View> listViews;
    private List<String> titleViews;
    private ListView jokeTextListView;
    private ListView jokeImgListView;
    private ViewPager viewPager;
    private static List<joke_message> msgs_text;
    private static List<joke_message> msgs_img;

    private Boolean isImgSetAda = false;
    private Boolean isTextSetAda = false;
    private static Boolean isScale = false;
    private static Bitmap newBitmap;
    private static int width;
    private File destDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        mContext = this;
        /**
         * 创建joke文件夹
         * 只会创建一次,担当joke文件夹被删除时,会再次创建
         */
        destDir = new File(Config.APP_PATH + "/joke");
        if (!destDir.exists()) {
            if (destDir.mkdirs()) {
                Toast.makeText(mContext, "成功创建文件夹", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 创建ActionBar
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("笑话");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /**
         * width为屏幕宽度
         */
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;


        viewPager = (ViewPager) findViewById(R.id.jokeViewPage);
        LayoutInflater inflater = getLayoutInflater();
        View jokeTextView = inflater.inflate(R.layout.joke_list, null);
        View jokeImgView = inflater.inflate(R.layout.joke_list, null);
        jokeTextListView = (ListView) jokeTextView.findViewById(R.id.jokeListView);
        jokeImgListView = (ListView) jokeImgView.findViewById(R.id.jokeListView);

        /**
         * joke_message List
         */
        msgs_text = new ArrayList<>();
        msgs_img = new ArrayList<>();

        /**
         * ViewPager 包含的View
         */
        listViews = new ArrayList<>();
        listViews.add(jokeImgView);
        listViews.add(jokeTextView);

        /**
         * ViewPager的title
         */
        titleViews = new ArrayList<>();
        titleViews.add("图片");
        titleViews.add("文字");

        /**
         * 为ListView设置divider
         * 顺序不可颠倒
         */
        jokeImgListView.setDivider(new ColorDrawable(Color.BLUE));
        jokeImgListView.setDividerHeight(1);
        jokeTextListView.setDivider(new ColorDrawable(Color.BLUE));
        jokeTextListView.setDividerHeight(1);


        /**
         * 静态的Handler
         */
        jokeHandler = new MyHandler(joke.this);

        /**
         * ViewPager的Adapter
         */
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(listViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(listViews.get(position));
                return listViews.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleViews.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);

        /**
         * activity启动后只加载一次
         * 而后上拉才能加载
         */
        getJoke("");
        getJoke("pic");

        /**
         * 设置ListView的滚动监听函数
         */
        UITools.scrollListener(jokeImgListView, UITools.setFooter(mContext), new UITools.Run() {
            @Override
            public void run(Object o) {
                getJoke("pic");
            }
        },mContext);
        UITools.scrollListener(jokeTextListView, UITools.setFooter(mContext), new UITools.Run() {
            @Override
            public void run(Object o) {
                getJoke("");
            }
        },mContext);
    }

    /**
     * 在此要释放所有资源
     * 否则可能会造成内存泄漏(activity不能被回收)
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        jokeHandler.removeCallbacksAndMessages(null);
        for (int i = 0; i < msgs_img.size(); i++) {
            msgs_img.get(i).setJokeImg(null);
        }
        jokeTextListAda = null;
        jokeImgListView = null;
        jokeHandler = null;
        listViews.clear();
        listViews = null;
        titleViews.clear();
        titleViews = null;
        pagerAdapter = null;
        viewPager = null;
        setContentView(R.layout.activity_null);
        msgs_text.clear();
        msgs_text = null;
        msgs_img.clear();
        msgs_img = null;
    }

    /**
     * 目录上的项目点击监听
     * @param item 目录的子项目
     * @return 是否已经处理
     */
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

    /**
     * 点击监听函数
     * @param view 被点击的组件
     * @param position 组件所在的位置
     */
    private static void clickListener(final View view, final int position) {

        /**
         *图片点击监听
         */
        if (view instanceof GifImageView)
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView= (GifImageView) view;
                Matrix matrix = new Matrix();
                Bitmap bitmap;
                /**
                 * 如果点击的图片是Bitmap
                 * 则第一次点击进行放大操作
                 * 第二次点击则恢复原来大小
                 */
                if (!isScale && msgs_img.get(position).getJokeImg() instanceof Bitmap) {
                    bitmap = (Bitmap) msgs_img.get(position).getJokeImg();
                    float scale = (float) width / bitmap.getWidth();
                    matrix.postScale(scale, scale);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    imageView.setImageBitmap(newBitmap);
                    isScale = true;
                } else if (isScale && msgs_img.get(position).getJokeImg() instanceof Bitmap) {
                    bitmap = (Bitmap) msgs_img.get(position).getJokeImg();
                    matrix.postScale(1.0f, 1.0f);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    imageView.setImageBitmap(newBitmap);
                    isScale = false;
                }
            }
        });

        /**
         *长按的话,则进行分享操作
         * 通过向Handler发送信息
         */
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Message message = new Message();
                message.what = 3;
                message.arg1 = position;
                if (view instanceof GifImageView)message.arg2=1;
                else message.arg2=2;
                jokeHandler.sendMessage(message);
                return true;
            }
        });
    }

    /**
     * 获取笑话
     * @param type 获取的笑话类型
     */
    private void getJoke(final String type) {
        new NetConnection(Config.JOKE_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("error_code").equals("0")) {
                        Message message = new Message();
                        if (type.equals("")) {
                            for (int i = 0; i < jsonObject.getJSONArray("result").length(); i++) {
                                msgs_text.add(new joke_message(jsonObject.getJSONArray("result").getJSONObject(i).getString("content")));
                            }
                            message.what = 1;
                        } else if (type.equals("pic")) {
                            for (int j = 0; j < jsonObject.getJSONArray("result").length(); j++) {
                                msgs_img.add(new joke_message(jsonObject.getJSONArray("result").getJSONObject(j).getString("content"), jsonObject.getJSONArray("result").getJSONObject(j).getString("url")));
                            }
                            message.what = 2;
                        }
                        jokeHandler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, "key", Config.JOKE_KEY, "type", type);
    }

    /**
     * 静态Handler(静态内部类可防止内存泄漏)
     */
    private static class MyHandler extends Handler {
        WeakReference mActivity;
        joke activity;

        private MyHandler(joke activity) {
            mActivity = new WeakReference(activity);
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            joke theActivity = (joke) mActivity.get();
            if (theActivity != null) {
                switch (msg.what) {

                    /**
                     *更新jokeTextListView
                     */
                    case 1: {
                        if (!theActivity.isTextSetAda) {
                            theActivity.jokeTextListAda = new jokeListAdapter(theActivity, "");
                            theActivity.jokeTextListView.setAdapter(theActivity.jokeTextListAda);
                            theActivity.isTextSetAda = true;
                        } else {
                            theActivity.jokeTextListAda.notifyDataSetChanged();
                        }
                        break;
                    }

                    /**
                     *更新jokeImgListView
                     */
                    case 2: {
                        if (!theActivity.isImgSetAda) {
                            jokeImgListAda = new jokeListAdapter(theActivity, "pic");
                            theActivity.jokeImgListView.setAdapter(jokeImgListAda);
                            theActivity.isImgSetAda = true;
                        } else {
                            jokeImgListAda.notifyDataSetChanged();
                        }
                        //TODO 这里可以改为依次获取图片,而不是同时获取图片
//                        for (int i = 0; i < msgs_img.size(); i++)
                            new getBitmap().execute(msgs_img.get(0).getJokeImgUrl(), 0);
                        break;
                    }

                    /**
                     *分享图片/文字
                     */
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        /**
                         * arg2==1分享图片加文字
                         * arg2==2分享文字
                         */
                        if (msg.arg2==1){
                            String filename;
                            FileOutputStream outputStream;
                            File file;
                            if (msgs_img.get(msg.arg1).getJokeImg() instanceof GifDrawable)
                                filename = "share.gif";
                            else filename = "share.jpeg";
                            /**
                             * 将文件保存到本地,然后进行分享
                             */
                            file = new File(theActivity.destDir, filename);
                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                //如果设置为true,则会设置为append(追加模式)
                                outputStream = new FileOutputStream(file, false);
                                if (msgs_img.get(msg.arg1).getJokeImg() instanceof GifDrawable) {
                                    outputStream.write(msgs_img.get(msg.arg1).getJokeImgByte());
                                } else {
                                    Bitmap bitmap = (Bitmap) msgs_img.get(msg.arg1).getJokeImg();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    outputStream.write(baos.toByteArray());
                                }
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Uri u = Uri.fromFile(file);
                            intent.setType("image/jpeg");
                            intent.putExtra(Intent.EXTRA_STREAM, u);
                            intent.putExtra(Intent.EXTRA_TEXT, msgs_img.get(msg.arg1).getJokeImgText());
                        } else if (msg.arg2==2){
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, msgs_text.get(msg.arg1).getJokeText());
                        }
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        theActivity.startActivity(Intent.createChooser(intent, theActivity.getTitle()));
                    default:
                        break;
                }
            }
        }
    }

    /**
     * jokeListAdapter,包括jokeImgListView和jokeTextListView
     */
    private static class jokeListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private String type;

        /**
         * 构造函数
         * @param context 上下文
         * @param type Adapter 类型
         */
        private jokeListAdapter(Context context, String type) {
            this.type = type;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return type.equals("pic") ? msgs_img.size() : msgs_text.size();
        }

        @Override
        public Object getItem(int position) {
            return type.equals("pic") ? msgs_img.get(position) : msgs_text.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.joke_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            joke_message jokeItem = (joke_message) getItem(position);
            if (type.equals("pic")) {
                if (jokeItem.getJokeImg() instanceof GifDrawable) {
                    viewHolder.img.setImageDrawable((GifDrawable) jokeItem.getJokeImg());
                } else {
                    viewHolder.img.setImageBitmap((Bitmap) jokeItem.getJokeImg());
                }
                // 分享Img的监听函数
                clickListener(viewHolder.img, position);
                viewHolder.text.setText(jokeItem.getJokeImgText());
            } else if (type.equals("")) {
                viewHolder.text.setText(jokeItem.getJokeText());

                //分享 Text的监听函数
                clickListener(viewHolder.text,position);
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView text;
            private GifImageView img; //声明为GifImageView,是为了显示Gif

            private ViewHolder(View view) {
                this.text = (TextView) view.findViewById(R.id.joke_text_item);
                this.img = (GifImageView) view.findViewById(R.id.joke_img_item);
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
            position = (int) params[1];
            return NetConnection.getImageBitmap((String) params[0]);
        }

        @Override
        protected void onPostExecute(Object object) {
            if (msgs_img != null && position <= msgs_img.size()) {
                if (object instanceof byte[]) {
                    try {
                        //此处将GifDrawable的byte[]存储,是为了在将gif保存到本地时更加的方便
                        msgs_img.get(position).setJokeImgByte((byte[]) object);
                        object = new GifDrawable((byte[]) object);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                msgs_img.get(position).setJokeImg(object);
                msgs_img.get(position).setIsGetImg(1);
                jokeImgListAda.notifyDataSetChanged();
                for (int i=0;i<msgs_img.size();i++){
                    if (msgs_img.get(i).getIsGetImg()==0){
                        new getBitmap().execute(msgs_img.get(i).getJokeImgUrl(), i);
                        break;
                    }
                }
            }
        }
    }

}
