package com.person.han.freebst.activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.person.han.freebst.Config;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;
import com.person.han.freebst.R;

import org.json.JSONException;
import org.json.JSONObject;

public class phoneNumberAttribution extends AppCompatActivity {
    private String TAG = "phoneNumberAttribution";
    private Context mContext;
    private TextView result_text;
    private EditText phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_attribution);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("电话归属地");
        }
        mContext = this;
        Button checkBtn = (Button) findViewById(R.id.check_button);
        result_text = (TextView) findViewById(R.id.result);
        phoneNum = (EditText) findViewById(R.id.phone_num);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNum.getText().toString().equals("")) {
                    Toast.makeText(mContext, "请输入号码", Toast.LENGTH_LONG).show();
                } else {
                    new NetConnection(Config.PHONE_ATTRIBUTION_URL, HttpMethod.GET, new NetConnection.SuccessCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getString("error_code").equals("0")) {
                                    JSONObject resultObject = jsonObject.getJSONObject("result");
                                    result_text.setText(resultObject.getString("province") + resultObject.getString("city") + "," + resultObject.getString("company") + "," + resultObject.getString("card"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new NetConnection.FailCallback() {
                        @Override
                        public void onFail() {
                            Log.e(TAG, "fail");
                        }
                    }, "phone", phoneNum.getText().toString(), "key", Config.PHONE_ATTRIBUTION_KEY);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
