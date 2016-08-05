package cn.ucai.fulicenter.activity.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.day3.CatChildFilterButton;
import cn.ucai.fulicenter.activity.adapter.NewGoodsOrBoutiqueAdapter;
import cn.ucai.fulicenter.activity.bean.CategoryGroupBean;
import cn.ucai.fulicenter.bean.fulibean.CategoryChildBean;
import cn.ucai.fulicenter.bean.fulibean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.super_activity.BaseActivity;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;

//分类二级页面
public class CategoryInfoActivity extends BaseActivity implements View.OnClickListener {
    CategoryInfoActivity mContext;
    SwipeRefreshLayout mSwipe;
    RecyclerView mRecycler;
    GridLayoutManager mGrid;
    NewGoodsOrBoutiqueAdapter mGoodsAdapter;
    TextView mTvHint, mHeadHint;
    ArrayList<NewGoodBean> mList;
    RelativeLayout mBackRelative;
    LinearLayout mLinearSort;
    Button mButProce, mButTime;
    public static int PAGE_ID = 1;
    final public static int DOWN_PULL = 1;
    final public static int UP_PULL = 2;
    boolean isNoData = true;
    int Id;
    ArrayList<CategoryChildBean> mChildList;
    CatChildFilterButton mPopouWidows;
    String GroupName;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_info_fragment);
        mContext = this;
        GroupName = getIntent().getStringExtra("GroupName");
        Id = getIntent().getIntExtra("id", 0);
        mChildList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        Log.i("main", "GroupName=" + GroupName + "mChildList=" + mChildList.get(1));
        initView();
        setListener();
        if (Id == 0) {
            return;
        }
        initData(DOWN_PULL);

    }

    private void setListener() {
        mButTime.setOnClickListener(this);
        mButProce.setOnClickListener(this);
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemMax == mGoodsAdapter.getItemCount() - 1 && isNoData) {
                    Log.i("main", "上拉加载=" + RecyclerView.SCROLL_STATE_IDLE + "\\ =" + RecyclerView.SCROLL_STATE_IDLE + "//" + itemMax + "=" + (mGoodsAdapter.getItemCount() - 1));
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

    private void initData(final int where) {
        final OkHttpUtils2<NewGoodBean[]> util = new OkHttpUtils2<NewGoodBean[]>();
        util.setRequestUrl(F.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(F.NewAndBoutiqueGood.CAT_ID, Id + "")
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
                        Log.i("main", "result=" + result[0]);
                        ArrayList<NewGoodBean> bean = util.array2List(result);
                        switch (where) {
                            //下拉刷新
                            case DOWN_PULL:
                                mGoodsAdapter.updateAdapterData(bean, mSwipe);
                                break;
                            //上拉加载
                            case UP_PULL:
                                mGoodsAdapter.upAdapterData(bean, mSwipe);
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
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_goods);
        mSwipe.setColorSchemeColors(R.color.good_detail_bg, R.color.good_detail_currency_price, R.color.google_green);
        mRecycler = (RecyclerView) findViewById(R.id.receiver_goods);
        mGrid = new GridLayoutManager(this, 2);
        mRecycler.setLayoutManager(mGrid);
        mList = new ArrayList<NewGoodBean>();
        mGoodsAdapter = new NewGoodsOrBoutiqueAdapter(this, mList);
        mGoodsAdapter.mList.clear();
        mRecycler.setAdapter(mGoodsAdapter);
        mTvHint = (TextView) findViewById(R.id.tvGoodHint);
        mBackRelative = (RelativeLayout) findViewById(R.id.backRelative);
        mLinearSort = (LinearLayout) findViewById(R.id.linearSort);
        mLinearSort.setVisibility(View.VISIBLE);
        mHeadHint = (TextView) findViewById(R.id.back_headHint);
        mBackRelative.setVisibility(View.VISIBLE);
        mHeadHint.setTextColor(getResources().getColor(R.color.google_green));
        mHeadHint.setGravity(Gravity.CENTER_HORIZONTAL);
        Utils.initBackTitle(mHeadHint, "分类");

        mButProce = (Button) findViewById(R.id.butPrice);
        mButTime = (Button) findViewById(R.id.butTime);
        mPopouWidows = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        mPopouWidows.setVisibility(View.VISIBLE);

        mPopouWidows.setText(GroupName);

        mPopouWidows.setOnCatFilterClickListener(GroupName, mChildList);

    }

    boolean isWhat1 = true;
    boolean isWhat2 = true;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.butPrice:
                Log.i("main", "价格排序=");
                if (isWhat1) {
                    isWhat1 = false;
                    mGoodsAdapter.setGoodsSort(1);
                    setSortImage(mButProce, R.drawable.one);
                } else {
                    setSortImage(mButProce, R.drawable.tow);
                    mGoodsAdapter.setGoodsSort(2);
                    isWhat1 = true;
                }
                break;
            case R.id.butTime:
                Log.i("main", "时间排序排序=");
                if (isWhat2) {
                    isWhat2 = false;
                    setSortImage(mButTime, R.drawable.one);
                    mGoodsAdapter.setGoodsSort(3);
                } else {
                    setSortImage(mButTime, R.drawable.tow);
                    mGoodsAdapter.setGoodsSort(4);
                    isWhat2 = true;
                }
                break;
        }
    }

    private void setSortImage(Button butId, int drawableId) {
        //图片id
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        //设置尺寸
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        //设置图片
        butId.setCompoundDrawables(null, null, drawable, null);
    }
}
