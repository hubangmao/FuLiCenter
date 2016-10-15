/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hbm.fulicenter.hxim.super_activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hbm.fulicenter.Constant;
import cn.hbm.fulicenter.DemoHXSDKHelper;
import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.hxim.applib.controller.HXSDKHelper;
import cn.hbm.fulicenter.hxim.bean.UserAvatar;
import cn.hbm.fulicenter.hxim.data.OkHttpUtils2;
import cn.hbm.fulicenter.hxim.db.UserDao;
import cn.hbm.fulicenter.task.DowCollectTask;
import cn.hbm.fulicenter.utils.CommonUtils;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.Utils;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ImageView mBack;
    private boolean progressShow;
    private boolean autoLogin = false;

    private String currentUsername;
    private String currentPassword;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* // 如果用户名密码都有，直接进入主页面
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }*/
        setContentView(R.layout.fuli_login);
        initView();

        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (FuLiCenterApplication.getInstance().getUserName() != null) {
            usernameEditText.setText(FuLiCenterApplication.getInstance().getUserName());
        }
    }

    int exit = -1;

    private void initView() {
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        mBack = (ImageView) findViewById(R.id.ivBack);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(exit);
                finish();
            }
        });
    }


    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = usernameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        final long start = System.currentTimeMillis();

//        //验证我们的 服务器
//        MySuperVerify();
        //福利社服务器
        FuLiCenterLogin();

    }

    //request=login&userName=&password=
    private void FuLiCenterLogin() {
        final String logUrl = F.SERVIEW_URL + "login&userName=" + currentUsername + "&password=" + currentPassword;
        OkHttpUtils2<UserAvatar> utils = new OkHttpUtils2<UserAvatar>();
        utils.url(logUrl)
                .targetClass(UserAvatar.class)
                .execute(new OkHttpUtils2.OnCompleteListener<UserAvatar>() {
                    @Override
                    public void onSuccess(UserAvatar result) {
                        if (result == null) {
                            Utils.toast(LoginActivity.this, "登陆失败网络错误");
                            return;
                        }
                        Log.i("main", "result=" + result);
                        FuLiCenterApplication.isLogin = false;
                        Utils.toast(LoginActivity.this, "FuLiCenter登陆验证成功");
                        Utils.toast(LoginActivity.this, "正在验证环信服务器");
                        //环信服务器验证
                        HXServiceVerify();
                        //保存用户信息至数据库
                        addSuperDBData(result);
                        //保存用户信息到内存
                        userInfoAddRAM(result);
                        //下载商品收藏数量
                        new DowCollectTask().dowCollectInfo(LoginActivity.this);
                        //下载所有好友信存到集合 ->内存
//                        new DowAllFirendListTask(LoginActivity.this).dowAllFirendLsit();
                        Log.i("main", "值" + logUrl + result.toString());

                    }

                    @Override
                    public void onError(String error) {
                        Utils.toast(LoginActivity.this, "登陆失败网络错误");
                    }
                });


    }

    //保存用户信息至内存中
    private void userInfoAddRAM(UserAvatar userInfo) {
        Log.i("main", "查看" + userInfo.getMAvatarId());
        FuLiCenterApplication.getInstance().getUser().setMUserName(userInfo.getMUserName());
        FuLiCenterApplication.getInstance().getUser().setMUserNick(userInfo.getMUserNick());
    }

    private void addSuperDBData(UserAvatar userInfo) {
        if (userInfo == null) {
            return;
        }
        new UserDao(LoginActivity.this).saveSuperData(userInfo);
    }

    private void HXServiceVerify() {
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
                // 登陆成功，保存用户名密码
                FuLiCenterApplication.getInstance().setUserName(currentUsername);
                FuLiCenterApplication.getInstance().setPassword(currentPassword);
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    // 处理好友和群组
                    initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            DemoHXSDKHelper.getInstance().logout(true, null);
                            Utils.toastResources(LoginActivity.this, R.string.login_failure_failed);
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        FuLiCenterApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Utils.toastResources(LoginActivity.this, R.string.Login_failed);
                    }
                });
            }
        });
    }

    private void initializeContacts() {
        Map<String, cn.hbm.fulicenter.hxim.domain.User> userlist = new HashMap<String, cn.hbm.fulicenter.hxim.domain.User>();
        // 添加user"申请与通知"
        cn.hbm.fulicenter.hxim.domain.User newFriends = new cn.hbm.fulicenter.hxim.domain.User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        cn.hbm.fulicenter.hxim.domain.User groupUser = new cn.hbm.fulicenter.hxim.domain.User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        cn.hbm.fulicenter.hxim.domain.User robotUser = new cn.hbm.fulicenter.hxim.domain.User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<cn.hbm.fulicenter.hxim.domain.User> users = new ArrayList<cn.hbm.fulicenter.hxim.domain.User>(userlist.values());
        dao.saveContactList(users);
    }

    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }


}
