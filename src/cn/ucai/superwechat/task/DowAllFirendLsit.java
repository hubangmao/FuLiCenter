package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowAllFirendLsit {
    private final String TAG = DowAllFirendLsit.class.getSimpleName();
    Context mContext;
    String userName = SuperWeChatApplication.getInstance().getUser().getMUserName();

    public DowAllFirendLsit(Context mContext) {
        this.mContext = mContext;
    }

    //DowAllFirendLsit
    //http://localhost:8080/SuperWeChatServer/Server?request=download_contact_all_list&m_contact_user_name=
    public void dowAllFirendLsit() {
        OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        String strAllUrl = I.SERVER_URL + "?request=download_contact_all_list&m_contact_user_name=" + userName;
        utils.url(strAllUrl)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main", TAG + "所有好友下载 = " + s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                        if (list.size() > 0 && list != null) {
                            SuperWeChatApplication.getInstance().setUserList(list);
                            mContext.sendStickyBroadcast(new Intent("dowAllFriend"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}


