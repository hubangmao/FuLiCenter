package cn.hbm.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.hbm.fulicenter.D;
import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.activity.bean.CartBean;
import cn.hbm.fulicenter.activity.bean.GoodDetailsBean;
import cn.hbm.fulicenter.hxim.data.OkHttpUtils2;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.Utils;


/**
 * Created by Administrator on 2016/7/20.
 */
public class DowCartTask {
    private final String TAG = DowCartTask.class.getSimpleName();
    Context mContext;
    List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartBeen();

    public void dowCartTask(final Context mContext) {
        this.mContext = mContext;
        String userName = FuLiCenterApplication.getInstance().getUserName();
        final OkHttpUtils2<CartBean[]> utils = new OkHttpUtils2<CartBean[]>();
        utils.setRequestUrl(F.REQUEST_FIND_CARTS)
                .addParam(F.Cart.USER_NAME, userName)
                .addParam(F.PAGE_ID, String.valueOf(F.PAGE_ID_DEFAULT))
                .addParam(F.PAGE_SIZE, F.PAGE_SIZE_DEFAULT + "")
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] s) {
                        if (s == null || s.length == 0) {
                            return;
                        }
                        Log.i("main", "购物车数据下载=" + s[0]);
                        ArrayList<CartBean> list = utils.array2List(s);

                        for (final CartBean bean : list) {
                            if (!cartList.contains(bean)) {//不存在就添加
                                String url = F.SERVIEW_URL + F.REQUEST_FIND_GOOD_DETAILS + "&" + D.GoodDetails.KEY_GOODS_ID + "=" + bean.getGoodsId();
                                OkHttpUtils2<GoodDetailsBean> http2 = new OkHttpUtils2<GoodDetailsBean>();
                                http2.url(url)
                                        .targetClass(GoodDetailsBean.class)
                                        .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                                            @Override
                                            public void onSuccess(GoodDetailsBean result) {
                                                Log.i("main", TAG + result);
                                                bean.setGoods(result);
                                            }

                                            @Override
                                            public void onError(String error) {
                                                Utils.toast(mContext, "网络错误");
                                            }
                                        });

                                cartList.add(bean);
                            } else {
                                cartList.get(cartList.indexOf(bean)).setChecked(bean.isChecked());
                                cartList.get(cartList.indexOf(bean)).setCount(bean.getCount());

                            }
                        }
                        mContext.sendStickyBroadcast(new Intent("update_cart"));

                    }


                    @Override
                    public void onError(String error) {

                    }
                });
    }


}


