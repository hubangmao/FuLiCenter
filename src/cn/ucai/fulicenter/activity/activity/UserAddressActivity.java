package cn.ucai.fulicenter.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.bean.UserAddress;
import cn.ucai.fulicenter.hxim.super_activity.BaseActivity;
import cn.ucai.fulicenter.utils.Utils;

public class UserAddressActivity extends BaseActivity implements PaymentHandler {
    String mSumPrice;
    UserAddress mUser;
    EditText mEtName, phoneNumber;
    Spinner mAdd1, mAdd2;
    private static String URL = "http://218.244.151.190/demo/charge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_layout);
        mSumPrice = getIntent().getStringExtra("sumPrice");
        initView();
    }

    private void initView() {
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});
        Utils.initBack(this);
        mUser = new UserAddress();
        mEtName = (EditText) findViewById(R.id.etUser);
        phoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        mAdd1 = (Spinner) findViewById(R.id.spAddress1);
        mAdd2 = (Spinner) findViewById(R.id.spAddress2);
        ((TextView) findViewById(R.id.user_back_headHint)).setText("填写收货地址");
        ((TextView) findViewById(R.id.tvAll)).setText("总额:" + mSumPrice);


    }

    public void onPay(View v) {
        String userName = mEtName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(UserAddressActivity.this, "用户名呢", Toast.LENGTH_SHORT).show();
            mEtName.requestFocus();
            return;
        }
        String number = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(number) || number.length() < 11) {
            Toast.makeText(UserAddressActivity.this, "电话号码呢", Toast.LENGTH_SHORT).show();
            phoneNumber.requestFocus();
            return;
        }
        String add1 = mAdd1.getSelectedItem().toString();

        if (TextUtils.isEmpty(add1)) {
            Toast.makeText(UserAddressActivity.this, "地址呢", Toast.LENGTH_SHORT).show();
            return;
        }
        String add2 = mAdd2.getSelectedItem().toString();

        if (TextUtils.isEmpty(add2)) {
            Toast.makeText(UserAddressActivity.this, "详细地址呢", Toast.LENGTH_SHORT).show();
            return;
        }
        mUser.setUserName(userName);
        mUser.setPhoneNumber(number);
        mUser.setAddress1(add1);
        mUser.setAddress2(add1);
        Toast.makeText(UserAddressActivity.this, "姓名:" + userName + "\n电话号码:" + number + "\n地址:" + add1 + "\n详细地址:" + add2, Toast.LENGTH_SHORT).show();
        goSendMoney();
    }

    private void goSendMoney() {
// 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date());

        // 计算总金额（以分为单位）

        // 构建账单json对象
        JSONObject bill = new JSONObject();

        // 自定义的额外信息 选填
        JSONObject extras = new JSONObject();
        try {
            extras.put("extra1", "extra1");
            extras.put("extra2", "extra2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bill.put("order_no", orderNo);
            bill.put("amount", mSumPrice);
            bill.put("extras", extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //壹收款: 创建支付通道的对话框
        PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);
    }

    @Override
    public void handlePaymentResult(Intent intent) {

    }
}
