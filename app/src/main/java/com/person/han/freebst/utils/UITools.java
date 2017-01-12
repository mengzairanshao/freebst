package com.person.han.freebst.utils;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by han on 2016/11/7.
 */

public class UITools{
    private static String TAG="UITools";
    private static Boolean isAll=false;//是否完全出现

    /**
     * 设置ListView页脚,显示上拉加载
     */
    public static LinearLayout setFooter(Context mContext) {
        /**
         * 设置布局显示属性
         */
        LinearLayout.LayoutParams WWLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        /**
         * 设置布局显示目标最大化属性
         */
        LinearLayout.LayoutParams MMLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // 线性布局
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        ProgressBar progressBar = new ProgressBar(mContext);
        layout.addView(progressBar, WWLayoutParams);
        TextView textView = new TextView(mContext);
        textView.setTag("textView");
        textView.setText("松手后加载");
        textView.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(textView, MMLayoutParams);
        LinearLayout loadingLayout;
        loadingLayout = new LinearLayout(mContext);
        loadingLayout.addView(layout, WWLayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        return loadingLayout;

    }

    /**
     * ListView 滚动监听
     * @param listView 要监听的ListView
     */
    public static void scrollListener(final ListView listView, final LinearLayout loadingLayout, final Run run,Context mContext) {
        final GestureDetector detector=new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e(TAG,"Scroll "+distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e(TAG,"onFiling "+velocityY);

                return false;
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            TextView footerText;
            private Boolean isEnd = false; //是否滚动到最后一个
            private Boolean isStop = false;//是否停止滑动
            private Boolean isOnTouch = false;//手指是否正在滑动
            private Boolean isFlying = false;//是否正在作惯性滑动

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                /**
                 * 共分两种情况
                 * 1.是手指滑动后停止 scrollState: 1->0
                 * 2.是手指滑动后惯性滑动而后停止 scrollState: 1->2->0
                 */
                if (scrollState == 1) {
                    if (isEnd) {
                        footerText = (TextView) loadingLayout.findViewWithTag("textView");
                        footerText.setText("松手后刷新");
                        listView.addFooterView(loadingLayout);
                    } else {
                        listView.removeFooterView(loadingLayout);
                    }
                    isOnTouch = true;
                    isStop = isFlying = false;
                } else if (scrollState == 2) {
                    isFlying = true;
                    isStop = isOnTouch =false;
                    listView.removeFooterView(loadingLayout);
                } else if (scrollState == 0) {
                    if (isOnTouch && !isFlying && isEnd) {
                        footerText = (TextView) loadingLayout.findViewWithTag("textView");
                        footerText.setText("加载中......");
                        listView.addFooterView(loadingLayout);
                        Log.e(TAG,isAll+"");
                        if (run!=null&&isAll){
                            run.run(null);
                        }
                    } else {
                        listView.removeFooterView(loadingLayout);
                    }
                    isStop = isOnTouch = isFlying = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isEnd = firstVisibleItem + visibleItemCount == totalItemCount;
                if (loadingLayout.getParent()!=null)isAll=Math.abs(loadingLayout.getBottom()-((View)loadingLayout.getParent()).getBottom())==((View)loadingLayout.getParent().getParent()).getPaddingBottom();
            }
        });
    }

    public interface Run{
        void run(Object o);
    }
}
