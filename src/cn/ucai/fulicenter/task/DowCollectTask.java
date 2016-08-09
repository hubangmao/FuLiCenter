package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;


import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowCollectTask {
    private final String TAG = DowCollectTask.class.getSimpleName();
    Context mContext;


    public void dowCollectInfo(final Context mContext) {
        this.mContext = mContext;
        String userName = FuLiCenterApplication.getInstance().getUserName();
        OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
        String strAllUrl = F.SERVIEW_URL + F.REQUEST_FIND_COLLECT_COUNT + "&userName=" + userName;
        utils.url(strAllUrl)
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean s) {
                        if (s == null) {
                            return;
                        }
                        if (s.isSuccess()) {
                            FuLiCenterApplication.getInstance().setCollocation(String.valueOf(s.getMsg()));
                            mContext.sendStickyBroadcast(new Intent("update_collect"));
                        } else {
                            FuLiCenterApplication.getInstance().setCollocation("0");
                            mContext.sendStickyBroadcast(new Intent("update_collect"));

                        }

                    }


                    @Override
                    public void onError(String error) {

                    }
                });
    }


}


