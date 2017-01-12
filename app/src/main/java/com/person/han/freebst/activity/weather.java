package com.person.han.freebst.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.person.han.freebst.Config;
import com.person.han.freebst.R;
import com.person.han.freebst.fragment.weatherFragment;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class weather extends AppCompatActivity implements weatherFragment.OnFragmentInteractionListener, LocationListener {
    private String TAG = "weather";
    private Context mContext;
    private List<Fragment> fragmentList = new ArrayList<>();
    public static final int REQUEST = 1;
    private fragmentAdapter fragmentAda;
    private LocationManager locationManager;
    private Location currentLocation;
    private static Boolean isAdd=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("天气");
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.weather_viewpager);
        Config.cacheDATA(mContext, "成都", Config.CITY_LIST, true, true);
        String[] cityList = Config.getCachedDATA(mContext, Config.CITY_LIST).split(Config.regularEx);
        Log.e(TAG, Config.getCachedDATA(mContext, Config.CITY_LIST));
        for (String aCityList : cityList) {
            fragmentList.add(weatherFragment.newInstance(aCityList, ""));
        }
        fragmentAda = new fragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAda);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        currentLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onFragmentInteraction(String cityName, int action_code) {
        if (action_code == 1) {
            int location = Config.removeCachedData(mContext, Config.CITY_LIST, cityName, true);
            Config.removeCachedData(mContext, cityName);
            if (location != -1) {
                fragmentList.remove(location);
                fragmentAda.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!isAdd)
        new NetConnection(Config.TEST_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    result=result.replace("renderReverse&&renderReverse(","");
                    JSONObject jsonObject=new JSONObject(result);
                    if (!jsonObject.getString("status").equals("0"))return;
                    String cityName=jsonObject.getJSONObject("result").getJSONObject("addressComponent").getString("city");
                    if (!Config.isCachedDATA(mContext,Config.CITY_LIST)){
                        fragmentList.add(weatherFragment.newInstance(cityName, ""));
                        fragmentAda.notifyDataSetChanged();
                    }
                    isAdd=true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        },"location",location.getLatitude()+","+location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * FragmentStatePagerAdapter 此处必须使用FragmentStatePagerAdapter
     * The ViewPager doesn't remove your fragments with the code above because it loads several views (or fragments in your case) into memory.
     * In addition to the visible view, it also loads the view to either side of the visible one.
     * This provides the smooth scrolling from view to view that makes the ViewPager so cool.
     * To achieve the effect you want, you need to do a couple of things.
     * Change the FragmentPagerAdapter to a FragmentStatePagerAdapter.
     * The reason for this is that the FragmentPagerAdapter will keep all the views that it loads into memory forever.
     * Where the FragmentStatePagerAdapter disposes of views that fall outside the current and traversable views.
     * <p>
     * Override the adapter method getItemPosition (shown below).
     * When we call mAdapter.notifyDataSetChanged();
     * the ViewPager interrogates the adapter to determine what has changed in terms of positioning.
     * We use this method to say that everything has changed so reprocess all your view positioning。
     */
    class fragmentAdapter extends FragmentStatePagerAdapter {

        fragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST && resultCode == RESULT_OK) {
            //获取从Search页面传回的cityName
            String cityName = data.getStringExtra("cityName");
            new NetConnection(Config.WEATHER_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        //获取返回的json中的cityName,作为标准的cityName
                        String cityName_copy = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime").getString("city_name");
                        if (jsonObject.getString("error_code").equals("0")) {
                            if (!Config.isCachedDATA(mContext, Config.CITY_LIST, cityName_copy, true)) {
                                fragmentList.add(weatherFragment.newInstance(cityName_copy, result));
                                fragmentAda.notifyDataSetChanged();
                            } else {
                                Toast.makeText(mContext, "已经添加过" + cityName_copy, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, jsonObject.getString("reason"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail() {

                }
            }, "cityname", cityName, "key", Config.WEATHER_KEY);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.addCity:
                Intent intent = new Intent(mContext, Search.class);
                intent.putExtra("title", "增加城市");
                startActivityForResult(intent, REQUEST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
    }
}
