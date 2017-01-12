package com.person.han.freebst.activity;

import android.content.Context;
import android.content.Intent;
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
import com.person.han.freebst.R;
import com.person.han.freebst.netConnc.HttpMethod;
import com.person.han.freebst.netConnc.NetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class identityCard extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "identityCard";
    private Context mContext;
    private Button check;
    private Button loss;
    private Button leak;
    private EditText idNmu;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_card);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("身份证查询");
        }
        check = (Button) findViewById(R.id.id_card_check);
        leak = (Button) findViewById(R.id.id_card_leak);
        loss = (Button) findViewById(R.id.id_card_loss);
        idNmu = (EditText) findViewById(R.id.identity_card_num);
        resultView = (TextView) findViewById(R.id.result_view);
        check.setOnClickListener(this);
        loss.setOnClickListener(this);
        leak.setOnClickListener(this);
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
     * 进行网络连接,获取json
     * @param url 不同的查询接口
     * @param cardNum 身份证号码
     */
    public void getData(final String url, String cardNum) {
        new NetConnection(url, HttpMethod.GET, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (!json.getString("error_code").equals("0")) {
                        Toast.makeText(mContext, json.getString("reason"), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (url.equals(Config.IDENTITY_CARD_CHECK_URL)) {
                        resultView.setText(json.getJSONObject("result").getString("area") + "\n"
                                + json.getJSONObject("result").getString("sex") + "\n"
                                + json.getJSONObject("result").getString("birthday"));
                    } else if (url.equals(Config.IDENTITY_CARD_LOSS_URL) || url.equals(Config.IDENTITY_CARD_LEAK_URL)) {
                        resultView.setText(json.getJSONObject("result").getString("cardno") + "\n"
                                + json.getJSONObject("result").getString("tips"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail() {

            }
        }, "key", Config.IDENTITY_CARD_KEY, "cardno", cardNum, "dtype", "json");
    }

    /**
     * click 监视函数
     * @param v 被点击的view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //查询身份证信息
            case R.id.id_card_check:
                if (!idNmu.getText().toString().equals("")) {
                    if ((idNmu.getText().toString()).trim().length() == 18)
                        getData(Config.IDENTITY_CARD_CHECK_URL, idNmu.getText().toString());
                    else
                        Toast.makeText(mContext, R.string.please_input_correct_identity_num, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, R.string.please_input_identity_num, Toast.LENGTH_LONG).show();
                }
                break;
            //查询是否泄漏
            case R.id.id_card_leak:
                if (!idNmu.getText().toString().equals("")) {
                    if ((idNmu.getText().toString()).trim().length() == 18)
                        getData(Config.IDENTITY_CARD_LEAK_URL, idNmu.getText().toString());
                    else
                        Toast.makeText(mContext, R.string.please_input_correct_identity_num, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, R.string.please_input_identity_num, Toast.LENGTH_LONG).show();
                }
                break;
            //查询是否挂失
            case R.id.id_card_loss:
                if (!idNmu.getText().toString().equals("")) {
                    if ((idNmu.getText().toString()).trim().length() == 18)
                        getData(Config.IDENTITY_CARD_LOSS_URL, idNmu.getText().toString());
                    else
                        Toast.makeText(mContext, R.string.please_input_correct_identity_num, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, R.string.please_input_identity_num, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
