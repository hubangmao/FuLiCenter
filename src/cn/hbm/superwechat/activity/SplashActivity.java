package cn.hbm.superwechat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import cn.hbm.superwechat.DemoHXSDKHelper;
import cn.hbm.superwechat.R;
import cn.hbm.superwechat.SuperWeChatApplication;
import cn.hbm.superwechat.bean.Result;
import cn.hbm.superwechat.bean.UserAvatar;
import cn.hbm.superwechat.data.OkHttpUtils2;
import cn.hbm.superwechat.db.DemoDBManager;
import cn.hbm.superwechat.db.UserDao;
import cn.hbm.superwechat.task.DowAllFirendListTask;
import cn.hbm.superwechat.task.DowAllGroupListTask;
import cn.hbm.superwechat.utils.I;
import cn.hbm.superwechat.utils.Utils;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {
    static final String TAG = SplashActivity.class.getSimpleName();
    private RelativeLayout rootLayout;
    private TextView versionText;

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        Log.i("main", "SplashActivity.onCreate()");
        setContentView(R.layout.activity_splash);
        super.onCreate(arg0);

        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        versionText = (TextView) findViewById(R.id.tv_version);

        versionText.setText(getVersion());
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    //免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();

                    //先查看全局变量有没有 用户信息
                    String userName = SuperWeChatApplication.getInstance().getUserName();

                    UserDao userDao = new UserDao(SplashActivity.this);
                    UserAvatar userAvatar = userDao.getDBUserInfo(userName);
                    //数据库没拿到数据的话在下载一次数据保存到数据库
                    if (userAvatar == null) {
                        if (addSuperDBUserInfo(userName, SuperWeChatApplication.getInstance().getPassword())) {
                            UserDao userDao1 = new UserDao(SplashActivity.this);
                            UserAvatar userAvatar1 = userDao1.getDBUserInfo(userName);
                            //全局变量 UserAvatar 保存信息
                            SuperWeChatApplication.getInstance().setUser(userAvatar1);
                        }
                    }

                    //全局变量 UserAvatar 保存信息
                    SuperWeChatApplication.getInstance().setUser(userAvatar);
                    //群组所有好友信息储存
                    new DowAllGroupListTask(SplashActivity.this, userName).dowAllGroupLsitTask();
                    //得到用户信息后下载 好友列表保存到全局变量
                    new DowAllFirendListTask(SplashActivity.this).dowAllFirendLsit();
                    Log.i("main", "得到用户信息后下载 好友列表保存到全局变量");
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();

    }

    //本地数据库没有数据在下载一次
    boolean isOk;

    private boolean addSuperDBUserInfo(String userName, String psw) {
        String strUrl = I.SERVER_URL + "?request=login&m_user_name=" + userName + "&m_user_password=" + psw;
        Log.i("main", "登陆url" + strUrl);
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.url(strUrl)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Result user = Utils.getResultFromJson(result, UserAvatar.class);
                        if (user.isRetMsg() && result != null) {
                            //保存用户信息至数据库
                            UserAvatar ua = (UserAvatar) user.getRetData();
                            DemoDBManager.getInstance().saveSuperData(ua);
                            isOk = true;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        SuperWeChatApplication.mMyUtils.toast(SplashActivity.this, "网络错误");
                        isOk = false;
                    }
                });
        return isOk;
    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        String st = getResources().getString(R.string.Version_number_is_wrong);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }
}
