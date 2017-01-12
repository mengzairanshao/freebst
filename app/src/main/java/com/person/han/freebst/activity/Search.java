package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.utils.AssetsDatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity {
    private SearchView searchView;
    private String TAG = "Search";
    private ListView searchResult;
    private int[] to={R.id.search_listview_item};
    private String[] from={"result_text"};
    private Context mContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getIntent().getStringExtra("title"));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        searchResult= (ListView) findViewById(R.id.search_result_list);

        //获取DBManger
        AssetsDatabaseManager.initManager(getApplication());
        AssetsDatabaseManager assetsDatabaseManager = AssetsDatabaseManager.getManager();
        String database = "ChinaCity.db";
        final SQLiteDatabase chinaCity = assetsDatabaseManager.getDatabase(database);
        searchView = (SearchView) findViewById(R.id.search_city);
        //新建Adapter
        searchResultAdapter searchResultAda=new searchResultAdapter(mContext,getHistoryData(),R.layout.search_listview_item,from,to);
        searchResult.setAdapter(searchResultAda);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("输入城市名称");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //提交时的动作
            @Override
            public boolean onQueryTextSubmit(String query) {
                //此处暂时不存储cityName,而是将cityName传回上级(weather),进行处理后,再进行添加
                Intent intent = new Intent();
                intent.putExtra("cityName", query);
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }

            //输入文字改变时,查询数据库,并将结果显示
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    searchResultAdapter searchResultAda=new searchResultAdapter(mContext,getHistoryData(),R.layout.search_listview_item,from,to);
                    searchResult.setAdapter(searchResultAda);
                }else {
                    //数据库查询语句,查询包含newText的条目
                    String sql = "select * from china_city_code where city or county like " + "'%" + newText + "%'";
                    findFromDatabase(chinaCity, sql);
                    searchResultAdapter searchResultAda=new searchResultAdapter(mContext,findFromDatabase(chinaCity, sql),R.layout.search_listview_item,from,to);
                    searchResult.setAdapter(searchResultAda);
                }
                return false;
            }
        });

        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //此处暂时不存储cityName,而是将cityName传回上级(weather),进行处理后,再进行添加
                String str=((String) ((TextView)view.findViewById(R.id.search_listview_item)).getText());
                String query;
                if(str.contains("-")){
                    //将字符串进行拆解
                    String[] strArr= str.split("-");
                    query=strArr[strArr.length-1];
                }else {
                    query=str;
                }
                searchView.setQuery(query,true);
            }
        });
    }

    /**
     *searchResult 的Adapter
     */
    class searchResultAdapter extends SimpleAdapter{
        searchResultAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
    }

    @Override
    protected void onDestroy() {
        AssetsDatabaseManager.closeAllDatabase();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 数据库查询执行函数
     * @param db SQLiteDatabase
     * @param sql sql查询语句
     * @return 数据list,供Adapter使用
     */
    public List<Map<String, Object>> findFromDatabase(SQLiteDatabase db, String sql) {
        Cursor result = db.rawQuery(sql, null);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map ;
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            map=new HashMap<>();
            String str = "";
            str += result.getString(1)+"-";
            str += result.getString(2)+"-";
            str += result.getString(3);
            map.put("result_text",str);
            list.add(map);
        }
        result.close();
        return list;
    }

    /**
     * 历史搜索数据
     * @return 数据list,供Adapter使用
     */
    public List<Map<String, Object>> getHistoryData(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map ;
        if(!Config.isCachedDATA(mContext,Config.CITY_CHECKED)) return list;
        String str=Config.getCachedDATA(mContext,Config.CITY_CHECKED);
        String[] strArr=str.split(Config.regularEx);
        for (String aStrArr : strArr) {
            map = new HashMap<>();
            map.put("result_text", aStrArr);
            list.add(map);
        }
        return list;
    }
}
