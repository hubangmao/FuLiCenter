package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowAllGroupListTask {
    private final String TAG = DowAllGroupListTask.class.getSimpleName();
    Context mContext;
    String groupId;


    public DowAllGroupListTask(Context mContext, String userName) {
        this.mContext = mContext;
        this.groupId = userName;
    }

    public void dowAllGroupLsitTask() {
        OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        String strAllUrl = I.SERVER_URL + "?request=find_group_by_user_name&m_user_name=" + groupId;
        Log.i("main", "下载所有群信息Url=" + strAllUrl);
        utils.url(strAllUrl)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Result result = Utils.getListResultFromJson(s, GroupAvatar.class);
                        List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                        if (list.size() > 0) {
                            SuperWeChatApplication.getInstance().setGroupAvatarList(list);
                            mContext.sendStickyBroadcast(new Intent("update_group_list"));
                            for (GroupAvatar l : list) {
                                Log.i("main", "所该用户的群信息=" + l.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main", "所有群成员信息=" + error.toString());

                    }
                });
    }


}


