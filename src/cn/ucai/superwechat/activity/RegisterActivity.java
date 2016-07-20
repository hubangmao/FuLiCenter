/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.I;

/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity {
    private EditText userNameEditText;
    private EditText userNickEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    public EditText mHead;
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
        //添加头像
        findViewById(R.id.relativeAvatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("main", "点击测试");
                mSetAvatar = new OnSetAvatarListener(RegisterActivity.this,
                        R.id.linearRegister, userNameEditText.getText().toString().trim(), I.AVATAR_TYPE_USER_PATH);
                mHead.setCursorVisible(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//得到新Activity 关闭后返回的数据
        Log.i("main", "requestCode=" + requestCode + "]resultCode=" + resultCode);
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
        mHead = (EditText) findViewById(R.id.head);
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
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(userNick)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            userNickEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {

            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
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
        File file = new File(OnSetAvatarListener.getAvatarPath(RegisterActivity.this, I.AVATAR_TYPE_USER_PATH)
                , username + I.AVATAR_SUFFIX_JPG);
        OkHttpUtils2<Result> utils = new OkHttpUtils2<Result>();
        String strUrl = "http://10.0.2.2:8080/SuperWeChatServer/Server?request=register&m_user_name=" + username + "&m_user_nick=" + username + "&m_user_password=" + pwd;
        Log.i("main", "RegisterActivity" + strUrl);
        utils.url(strUrl)
                .targetClass(Result.class)
                .addFile(file)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            pd.dismiss();
                            registerEMService(pd);
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.mes_102), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();

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
                            SuperWeChatApplication.getInstance().setUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
