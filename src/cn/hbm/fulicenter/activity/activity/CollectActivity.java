package cn.hbm.fulicenter.activity.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.adapter.CollectAdapter;
import cn.hbm.fulicenter.activity.adapter.NewGoodsOrBoutiqueAdapter;
import cn.hbm.fulicenter.activity.bean.BoutiqueBean;
import cn.hbm.fulicenter.activity.bean.NewGoodBean;
import cn.hbm.fulicenter.data.OkHttpUtils2;
import cn.hbm.fulicenter.super_activity.BaseActivity;
import cn.hbm.fulicenter.super_activity.SplashActivity;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.Utils;

//精选
public class CollectActivity extends BaseActivity {
    CollectActivity mContext;
    SwipeRefreshLayout mSwipe;
    RecyclerView mRecycler;
    GridLayoutManager mGrid;
    CollectAdapter mCollectAdapter;
    TextView mTvHint, mHeadHint;
    ArrayList<NewGoodBean> mList;
    RelativeLayout mBackRelative;
    public static int PAGE_ID = 0;
    final public static int DOWN_PULL = 1;
    final public static int UP_PULL = 2;
    boolean isNoData = true;
    BoutiqueBean mBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_layout2);
        mContext = this;
        mBean = (BoutiqueBean) getIntent().getSerializableExtra("BoutiqueBean");
        initView();
        initData(DOWN_PULL);
        setListener();

    }

    private void setListener() {
        //下拉刷新
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isNoData = true;
                PAGE_ID = 1;
                initData(DOWN_PULL);
                Utils.toast(mContext, "刷新成功");
                mTvHint.setVisibility(View.GONE);
            }
        });
        //上拉加载
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int itemMax;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemMax == mCollectAdapter.getItemCount() - 1 && isNoData) {
                    Log.i("main", "上拉加载=" + RecyclerView.SCROLL_STATE_IDLE + "\\ =" + RecyclerView.SCROLL_STATE_IDLE + "//" + itemMax + "=" + (mCollectAdapter.getItemCount() - 1));
                    PAGE_ID++;
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText("加载更多...");
                    initData(UP_PULL);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0.1 || dy < 0) {
                    mTvHint.setVisibility(View.GONE);
                }
                super.onScrolled(recyclerView, dx, dy);
                itemMax = mGrid.findLastVisibleItemPosition();//获取当前屏幕显示的最后一项下标
            }
        });
    }

    //http://localhost:8080/FuLiCenterServer/Server?request=find_collects&userName=aa&page_id=1&page_size=3
    private void initData(final int where) {
        final OkHttpUtils2<NewGoodBean[]> util = new OkHttpUtils2<NewGoodBean[]>();
        util.setRequestUrl(F.REQUEST_FIND_COLLECTS)
                .addParam(F.Cart.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(F.PAGE_ID, PAGE_ID + "")
                .addParam(F.PAGE_SIZE, F.PAGE_SIZE_DEFAULT + "")
                .targetClass(NewGoodBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                    @Override
                    public void onSuccess(NewGoodBean[] result) {
                        if (result == null || result.length == 0) {
                            isNoData = false;
                            mTvHint.setVisibility(View.VISIBLE);
                            mTvHint.setText("已经没有更多加载...");
                            mSwipe.setRefreshing(false);
                            return;
                        }
                        Log.i("main", "收藏的商品=" + result[0]);
                        ArrayList<NewGoodBean> bean = util.array2List(result);
                        switch (where) {
                            //下拉刷新
                            case DOWN_PULL:
                                mCollectAdapter.updateAdapterData(bean, mSwipe);
                                break;
                            //上拉加载
                            case UP_PULL:
                                mCollectAdapter.upAdapterData(bean, mSwipe);
                                break;
                        }
                    }

                    @Override
                    public void onError(String error) {
                        mTvHint.setVisibility(View.VISIBLE);
                        mTvHint.setText("网络错误");
                        mSwipe.setRefreshing(false);
                    }
                });
    }

    private void initView() {
        Utils.initBack(mContext);
        mHeadHint = (TextView) findViewById(R.id.user_back_headHint);
        mHeadHint.setText("收藏的宝贝");
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_Boutique);
        mSwipe.setColorSchemeColors(R.color.good_detail_bg, R.color.good_detail_currency_price, R.color.google_green);
        mRecycler = (RecyclerView) findViewById(R.id.receiver_Boutique);
        mGrid = new GridLayoutManager(this, 2);
        mRecycler.setLayoutManager(mGrid);
        mList = new ArrayList<NewGoodBean>();
        mCollectAdapter = new CollectAdapter(this, mList);
        mCollectAdapter.mList.clear();
        mRecycler.setAdapter(mCollectAdapter);
        mTvHint = (TextView) findViewById(R.id.tvBoutiqueHint);
        mBackRelative = (RelativeLayout) findViewById(R.id.backRelative);

    }
}
