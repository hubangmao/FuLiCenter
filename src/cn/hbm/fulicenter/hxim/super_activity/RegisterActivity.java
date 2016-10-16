/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package cn.hbm.fulicenter.hxim.super_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.hxim.bean.Result;
import cn.hbm.fulicenter.hxim.data.OkHttpUtils2;
import cn.hbm.fulicenter.hxim.listener.OnSetAvatarListener;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.I;
import cn.hbm.fulicenter.utils.Utils;

import static cn.hbm.fulicenter.R.id.relativeAvatar;

/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText userNameEditText;
    private EditText userNickEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    public ImageView mIv_Head;
    public OnSetAvatarListener mSetAvatar;

    String username;
    String userNick;
    String pwd;
    String confirm_pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.butRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        findViewById(R.id.iv_head).setOnClickListener(this);
        findViewById(R.id.head).setOnClickListener(this);
        findViewById(R.id.relativeAvatar).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        mSetAvatar.setAvatar(requestCode, data, mIv_Head);//得到数据后传入setAvatar()方法
    }

    private void initView() {
        userNameEditText = (EditText) findViewById(R.id.username);
        userNickEditText = (EditText) findViewById(R.id.nick);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
        mIv_Head = (ImageView) findViewById(R.id.iv_head);
    }

    /**
     * 注册
     */
    public void register() {
        username = userNameEditText.getText().toString().trim();
        userNick = userNickEditText.getText().toString().trim();
        pwd = passwordEditText.getText().toString().trim();
        confirm_pwd = confirmPwdEditText.getText().toString().trim();

        if (!username.matches("[\\w][\\w\\d_]+")) {
            Utils.toast(this, getResources().getString(R.string.User_name_cannot_be_empty));
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(userNick)) {
            Utils.toast(this, getResources().getString(R.string.toast_nick_not_isnull));
            userNickEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Utils.toast(this, getResources().getString(R.string.Password_cannot_be_empty));
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Utils.toast(this, getResources().getString(R.string.Confirm_password_cannot_be_empty));
            confirmPwdEditText.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Utils.toast(this, getResources().getString(R.string.Two_input_password));
            return;
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();
            registerMyService(pd);
        }
    }

    private void registerMyService(final ProgressDialog pd) {
        File file = new File(OnSetAvatarListener.getAvatarPath
                (RegisterActivity.this, I.AVATAR_TYPE_USER_PATH), username + I.AVATAR_SUFFIX_JPG);
        OkHttpUtils2<Result> utils = new OkHttpUtils2<Result>();
        utils.setRequestUrl(F.REQUEST_REGISTER)
                .addParam(F.USER_NAME, username)
                .addParam(F.USER_PASS, pwd)
                .addParam(F.USER_NICK, userNick)
                .targetClass(Result.class)
                .addFile(file)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            registerEMService(pd);
                            pd.dismiss();
                        } else {
                            pd.dismiss();
                            Utils.toast(RegisterActivity.this, getResources().getString(R.string.mes_102));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pd.dismiss();
                        Utils.toast(RegisterActivity.this, "请检查网络");
                    }
                });
    }

    private void registerEMService(final ProgressDialog pd) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(username, pwd);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // 保存用户名
                            FuLiCenterApplication.getInstance().setUserName(username);
                            Utils.toast(RegisterActivity.this, getResources().getString(R.string.Registered_successfully));
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NONETWORK_ERROR) {
                                Utils.toast(RegisterActivity.this, getResources().getString(R.string.network_anomalies));
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                Utils.toast(RegisterActivity.this, getResources().getString(R.string.User_already_exists));
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                Utils.toast(RegisterActivity.this, getResources().getString(R.string.registration_failed_without_permission));
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                Utils.toast(RegisterActivity.this, getResources().getString(R.string.illegal_user_name));
                            } else {
                                Utils.toast(RegisterActivity.this, getResources().getString(R.string.Registration_failed));
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }

    //注册界面切换至登陆界面
    public void login(View view) {
        finish();
    }

    //添加头像
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
            case R.id.head:
            case relativeAvatar:
                mSetAvatar = new OnSetAvatarListener(RegisterActivity.this,
                        R.id.linearRegister, userNameEditText.getText().toString().trim(), I.AVATAR_TYPE_USER_PATH);
                break;

        }
    }
}
