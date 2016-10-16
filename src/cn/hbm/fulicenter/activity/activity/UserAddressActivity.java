package cn.hbm.fulicenter.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.bean.UserAddress;
import cn.hbm.fulicenter.hxim.super_activity.BaseActivity;
import cn.hbm.fulicenter.utils.Utils;


public class UserAddressActivity extends BaseActivity implements PaymentHandler {
    private String mSumPrice;
    private UserAddress mUser;
    private EditText mEtName, phoneNumber;
    private Spinner mAdd1, mAdd2;
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
            Utils.toast(this, "用户名呢");
            mEtName.requestFocus();
            return;
        }
        String number = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(number) || number.length() < 11) {
            Utils.toast(this, "电话号码呢");
            phoneNumber.requestFocus();
            return;
        }
        String add1 = mAdd1.getSelectedItem().toString();

        if (TextUtils.isEmpty(add1)) {
            Utils.toast(this, "地址呢");
            return;
        }
        String add2 = mAdd2.getSelectedItem().toString();

        if (TextUtils.isEmpty(add2)) {
            Utils.toast(this, "详细地址呢");
            return;
        }
        mUser.setUserName(userName);
        mUser.setPhoneNumber(number);
        mUser.setAddress1(add1);
        mUser.setAddress2(add1);
        String str = "\n姓名:" + userName + "\n电话号码:" + number + "\n地址:" + add1 + "\n详细地址:" + add2;
        Utils.toast(this, str);
        ((TextView) findViewById(R.id.tvAll)).setText("总额:" + mSumPrice + str);
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
