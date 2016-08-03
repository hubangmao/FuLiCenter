package cn.ucai.fulicenter.activity.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BaseActivity;
import cn.ucai.fulicenter.activity.activity.day3.FlowIndicator;
import cn.ucai.fulicenter.activity.activity.day3.SlideAutoLoopView;

public class GoodsInfoActivity extends BaseActivity implements View.OnClickListener {
    String TAG = GoodsInfoActivity.class.getSimpleName();
    int mGoodId;
    //        购物车   收藏       分享
    ImageView mBack, mIvCatr, mSelected, mShare;
    //                                    标价      卖价
    TextView mGoodsName, mGoodsEnglish, mGoodsPrice1, mGoodsPrice2;

    //图片轮播
    SlideAutoLoopView mSlide;
    //绘制的圆
    FlowIndicator mFlow;

    WebView mVewView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_info_layout);
        initView();
        initData();
        setListener();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra("Good_Id", 0);
        Log.i("main", "GoodId=" + mGoodId);

    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.ivBack);
        mIvCatr = (ImageView) findViewById(R.id.iv_goods_info_cart);
        mSelected = (ImageView) findViewById(R.id.iv_goods_info_selected);
        mShare = (ImageView) findViewById(R.id.iv_goods_info_share);

        mGoodsName = (TextView) findViewById(R.id.tv_Good_Name);
        mGoodsEnglish = (TextView) findViewById(R.id.tv_English_Name);
        mGoodsPrice1 = (TextView) findViewById(R.id.tv_good_price1);
        mGoodsPrice2 = (TextView) findViewById(R.id.tv_good_price2);

        mVewView = (WebView) findViewById(R.id.goodsInfo_WebView);


    }

    private void setListener() {
        //关闭
        mBack.setOnClickListener(this);
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
            //关闭
            case R.id.ivBack:
                finish();
                break;
            //购物车
            case R.id.iv_goods_info_cart:

                break;
            //收藏
            case R.id.iv_goods_info_selected:

                break;
            //分享
            case R.id.iv_goods_info_share:

                break;
        }
    }


}
