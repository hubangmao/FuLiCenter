package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowAllFirendListTask {
    private final String TAG = DowAllFirendListTask.class.getSimpleName();
    Context mContext;
    String userName =SuperWeChatApplication.getInstance().getUser().getMUserName();



    public DowAllFirendListTask(Context mContext) {
        this.mContext = mContext;
    }

    public void dowAllFirendLsit() {
        OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        String strAllUrl = I.SERVER_URL + "?request=download_contact_all_list&m_contact_user_name=" + userName;
        utils.url(strAllUrl)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s == null) {
                            return;
                        }
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                        if (list.size() > 0 && list != null) {
                            SuperWeChatApplication.getInstance().setUserList(list);
                            Map<String, UserAvatar> map = SuperWeChatApplication.getInstance().getMap();
                            mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                            for (UserAvatar l : list) {
                                Log.i("main", "用户昵称=" + l.getMUserNick());
                                map.put(l.getMUserName(), l);
                            }
                            SuperWeChatApplication.getInstance().setMap(map);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }


}

