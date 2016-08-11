package cn.ucai.fulicenter.activity.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.bean.UserAddress;

public class UserAddressActivity extends Activity {
    String mSumPrice;
    UserAddress mUser;
    EditText mEtName, phoneNumber;
    Spinner mAdd1, mAdd2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_layout);
        mSumPrice = getIntent().getStringExtra("mSumPrice");
        initView();
    }

    private void initView() {
        mUser = new UserAddress();
        mEtName = (EditText) findViewById(R.id.etUser);
        phoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        mAdd1 = (Spinner) findViewById(R.id.spAddress1);
        mAdd2 = (Spinner) findViewById(R.id.spAddress2);
        ((TextView) findViewById(R.id.tvAll)).setText("总额:" + mSumPrice);

        ((TextView) findViewById(R.id.user_back_headHint)).setText("填写收货地址");


    }

    public void onPay(View v) {
        String userName = mEtName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(UserAddressActivity.this, "用户名呢", Toast.LENGTH_SHORT).show();
            return;
        }
        String number = phoneNumber.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(UserAddressActivity.this, "电话号码呢", Toast.LENGTH_SHORT).show();
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
    }

}
