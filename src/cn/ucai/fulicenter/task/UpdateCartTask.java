package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.activity.bean.CartBean;
import cn.ucai.fulicenter.activity.bean.GoodDetailsBean;
import cn.ucai.fulicenter.activity.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;


/**
 * 更新购物车商品件数
 */
public class UpdateCartTask {
    private final String TAG = UpdateCartTask.class.getSimpleName();
    Context mContext;

    public void updateCartTask(final Context mContext, CartBean bean) {
        this.mContext = mContext;
        if (bean == null || bean.getGoods().getAddTime() < 0) {
            return;
        }

        final OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
        utils.setRequestUrl(F.REQUEST_UPDATE_CART)
                .addParam(F.Cart.ID, bean.getId() + "")
                .addParam(F.Cart.COUNT, bean.getCount() + "")
                .addParam(F.Cart.IS_CHECKED, bean.isChecked() + "")
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result.isSuccess()) {
                            new DowCartTask().dowCartTask(mContext);
                            Utils.toast(mContext, result.getMsg());
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    public void deleteCartTask(final Context mContext, int cartId) {
        this.mContext = mContext;
        if (cartId < 0) {
            return;
        }
        final OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
        utils.setRequestUrl(F.REQUEST_DELETE_CART)
                .addParam(F.Cart.ID, cartId + "")
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result.isSuccess()) {
                            FuLiCenterApplication.getInstance().getCartBeen().clear();
                            new DowCartTask().dowCartTask(mContext);
                            mContext.sendStickyBroadcast(new Intent("update_cart"));
                            Utils.toast(mContext, result.getMsg());
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }


}


