package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowAllMemberListTask {
    private final String TAG = DowAllMemberListTask.class.getSimpleName();
    Context mContext;
    String hxId;


    public DowAllMemberListTask(Context mContext, String hxId) {
        this.mContext = mContext;
        this.hxId = hxId;
    }

    public void dowAllMemberLsit() {
        SuperWeChatApplication.getInstance().getMemberMap().clear();
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        String strAllUrl = I.SERVER_URL + "?request=download_group_members_by_hxid&m_member_group_hxid=" + hxId;
        utils.url(strAllUrl)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main", TAG + "群成员下载信息=" + s.toString());
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        if (result.getRetData() == null) {
                            return;
                        }
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                        if (list.size() > 0) {
                            Map<String, HashMap<String, MemberUserAvatar>> memberMap = SuperWeChatApplication.getInstance().getMemberMap();
                            if (!memberMap.containsKey(hxId)) {
                                memberMap.put(hxId, new HashMap<String, MemberUserAvatar>());

                            }
                            HashMap<String, MemberUserAvatar> memberMaps = memberMap.get(hxId);
                            for (MemberUserAvatar u : list) {
                                memberMaps.put(u.getMUserName(), u);
                            }
                            mContext.sendStickyBroadcast(new Intent("update_member_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main", TAG + "群成员信息下载错误");
                    }
                });
    }


}


