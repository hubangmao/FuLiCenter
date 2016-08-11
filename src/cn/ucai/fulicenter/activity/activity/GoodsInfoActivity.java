package cn.ucai.fulicenter.activity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.day3.FlowIndicator;
import cn.ucai.fulicenter.activity.activity.day3.SlideAutoLoopView;
import cn.ucai.fulicenter.activity.bean.AlbumBean;
import cn.ucai.fulicenter.activity.bean.GoodDetailsBean;
import cn.ucai.fulicenter.activity.bean.MessageBean;
import cn.ucai.fulicenter.activity.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.super_activity.BaseActivity;
import cn.ucai.fulicenter.super_activity.LoginActivity;
import cn.ucai.fulicenter.task.DowCollectTask;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;

public class GoodsInfoActivity extends BaseActivity implements View.OnClickListener {
    String TAG = GoodsInfoActivity.class.getSimpleName();
    GoodsInfoActivity mContext;
    int mGoodId;
    //        购物车   收藏       分享
    ImageView mIvCatr, mSelected, mShare;
    //                                    标价      卖价
    TextView mGoodsName, mGoodsEnglish, mGoodsPrice1, mGoodsPrice2, mtvCartHint;

    //图片轮播
    SlideAutoLoopView mSlide;
    //绘制的圆
    FlowIndicator mFlow;
    RelativeLayout mBackRelative, mGoodsInfoRelative;
    WebView mVewView;
    GoodDetailsBean mDetail;
    GestureDetector mDetector;
    boolean isSelected;
    NewGoodBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_info_layout);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra("Good_Id", 0);
        bean = (NewGoodBean) getIntent().getSerializableExtra("bean");
        if (mGoodId == 0) {
            Utils.toast(this, "网络开小差去了");
            return;
        } else {
            dowGoodsInfo();
        }

    }

    private void dowGoodsInfo() {
        String url = F.SERVIEW_URL + F.REQUEST_FIND_GOOD_DETAILS + "&" + D.GoodDetails.KEY_GOODS_ID + "=" + mGoodId;
        OkHttpUtils2<GoodDetailsBean> http2 = new OkHttpUtils2<GoodDetailsBean>();
        http2.url(url)
                .targetClass(GoodDetailsBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                    @Override
                    public void onSuccess(GoodDetailsBean result) {
                        Log.i("main", TAG + result);
                        mDetail = result;
                        setGoodsInfo();
                    }

                    @Override
                    public void onError(String error) {
                        Utils.toast(mContext, "网络错误");
                    }
                });
    }

    private void setGoodsInfo() {
        mGoodsName.setText(mDetail.getGoodsName());
        mGoodsEnglish.setText(mDetail.getGoodsEnglishName());
        mGoodsPrice1.setText(mDetail.getShopPrice());
        mGoodsPrice2.setText(mDetail.getCurrencyPrice());
        mSlide.startPlayLoop(mFlow, getGoodsImageUrlInfo(), getImageSize());
        mVewView.loadDataWithBaseURL(null, mDetail.getGoodsBrief(), D.TEXT_HTML, D.UTF_8, null);

    }

    public String[] getGoodsImageUrlInfo() {
        if (mDetail.getProperties() != null && mDetail.getProperties().length > 0) {
            AlbumBean[] albums = mDetail.getProperties()[0].getAlbums();
            String[] strings = new String[albums.length];
            for (int i = 0; i < strings.length; i++) {
                strings[i] = albums[i].getImgUrl();
            }
            return strings;
        }
        return null;
    }

    public int getImageSize() {
        if (mDetail.getProperties() != null && mDetail.getProperties().length > 0) {
            return mDetail.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void initView() {
        Utils.initBack(this);
        registerCartBroCast();
        mBackRelative = (RelativeLayout) findViewById(R.id.backRelative);
        mIvCatr = (ImageView) findViewById(R.id.iv_goods_info_cart);
        mSelected = (ImageView) findViewById(R.id.iv_goods_info_selected);
        mShare = (ImageView) findViewById(R.id.iv_goods_info_share);

        mGoodsName = (TextView) findViewById(R.id.tv_Good_Name);
        mGoodsEnglish = (TextView) findViewById(R.id.tv_English_Name);
        mGoodsPrice1 = (TextView) findViewById(R.id.tv_good_price1);
        mGoodsPrice2 = (TextView) findViewById(R.id.tv_good_price2);
        mtvCartHint = (TextView) findViewById(R.id.tv_goods_info_carts);

        mVewView = (WebView) findViewById(R.id.goodsInfo_WebView);
        mSlide = (SlideAutoLoopView) findViewById(R.id.salv);
        //绘制的圆
        mFlow = (FlowIndicator) findViewById(R.id.indicator);
        mGoodsInfoRelative = (RelativeLayout) findViewById(R.id.RelativeGoodsInfo);
        mDetector = new GestureDetector(this, new Listener(this, mVewView, mGoodsInfoRelative));
    }

    private void setListener() {
        mBackRelative.setVisibility(View.VISIBLE);
        //购物车
        mIvCatr.setOnClickListener(this);
        //收藏
        mSelected.setOnClickListener(this);
        //分享
        mShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //购物车
            case R.id.iv_goods_info_cart:
                new UpdateCartTask().addCartTask(mContext, bean.getGoodsId());
                break;
            //收藏
            case R.id.iv_goods_info_selected:
                goodSelected();
                break;
            //分享
            case R.id.iv_goods_info_share:
                showShare();
                break;
        }
    }

    private void goodSelected() {
        if (isLogin()) {
            //true取消收藏
            if (isSelected) {
                deleteSelected();
            } else {
                addSelected();
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    //添加收藏
    private void addSelected() {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
        utils2.setRequestUrl(F.REQUEST_ADD_COLLECT)
                .addParam(F.Collect.GOODS_ID, String.valueOf(mGoodId))
                .addParam(F.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(F.Collect.GOODS_NAME, bean.getGoodsName())
                .addParam(F.Collect.GOODS_ENGLISH_NAME, bean.getGoodsEnglishName())
                .addParam(F.Collect.GOODS_THUMB, bean.getGoodsThumb())
                .addParam(F.Collect.GOODS_IMG, bean.getGoodsImg())
                .addParam(F.Collect.ADD_TIME, String.valueOf(bean.getAddTime()))
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if (result.isSuccess()) {
                            Utils.toast(mContext, result.getMsg());
                            isSelected = true;
                            new DowCollectTask().dowCollectInfo(mContext);
                            updateHint();
                        } else {
                            Utils.toast(mContext, result.getMsg());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Utils.toast(mContext, "收藏网络错误");

                    }
                });

    }

    //取消收藏
    private void deleteSelected() {
        OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
        utils2.setRequestUrl(F.REQUEST_DELETE_COLLECT)
                .addParam(F.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(F.Collect.GOODS_ID, String.valueOf(mGoodId))
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        Log.i("main", TAG + "result=" + result);
                        if (result.isSuccess()) {
                            isSelected = false;
                            updateHint();
                            Utils.toast(mContext, result.getMsg());
                            new DowCollectTask().dowCollectInfo(mContext);
                        } else {
                            Utils.toast(mContext, result.getMsg());
                        }

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        isSelected();
        mGoodId = getIntent().getIntExtra("Good_Id", 0);
        bean = (NewGoodBean) getIntent().getSerializableExtra("bean");
    }

    //查询是否存在该收藏商品
    private void isSelected() {
        if (isLogin()) {
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(F.REQUEST_IS_COLLECT)
                    .addParam(F.Cart.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                    .addParam(F.Collect.GOODS_ID, String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            if (result.isSuccess()) {
                                isSelected = true;
                                updateHint();
                            } else {
                                updateHint();
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
        }

    }

    public void updateHint() {
        if (isSelected) {
            mSelected.setImageResource(R.drawable.bg_collect_out);
        } else {
            mSelected.setImageResource(R.drawable.bg_collect_in);
        }
    }

    //验证是否登录
    public boolean isLogin() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            return true;
        } else {
            return false;
        }
    }

    class updateCartBroCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isLogin()) {
                return;
            }
            int size = Utils.getCartNumber();
            if (size > 0 && isLogin()) {
                mtvCartHint.setVisibility(View.VISIBLE);
                mtvCartHint.setText(String.valueOf(size));
            } else {
                mtvCartHint.setVisibility(View.GONE);

            }
        }
    }

    updateCartBroCast mUpdate;

    private void registerCartBroCast() {
        mUpdate = new updateCartBroCast();
        IntentFilter intentFilter = new IntentFilter("update_cart");
        registerReceiver(mUpdate, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdate != null) {
            unregisterReceiver(mUpdate);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
}

class Listener extends GestureDetector.SimpleOnGestureListener {
    WebView mWewView;
    RelativeLayout mGoodsInfoRelative;
    GoodsInfoActivity mActivity;

    public Listener(GoodsInfoActivity mActivity, WebView mWewView, RelativeLayout mGoodsInfoRelative) {
        this.mWewView = mWewView;
        this.mGoodsInfoRelative = mGoodsInfoRelative;
        this.mActivity = mActivity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("main", "e1=" + e1.getX() + "e2=" + e2.getY() + "velocityX速度=" + velocityX + "velocityX=" + velocityY);
        if (e1.getX() < (e2.getX() - 200) || velocityX > 1000) {
            mActivity.finish();
        }
        return false;
    }


}

