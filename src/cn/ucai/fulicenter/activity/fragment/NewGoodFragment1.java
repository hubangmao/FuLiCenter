package cn.ucai.fulicenter.activity.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.fulibean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;

/**
 * 新品Fragment
 */
public class NewGoodFragment1 extends Fragment {
    View mView;
    Context mContext;
    SwipeRefreshLayout mSwipe;
    RecyclerView mRecycler;
    GridLayoutManager mGrid;
    NewGoodsAdapter mGoodsAdapter;
    TextView mTvHint;
    ArrayList<NewGoodBean> mList;
    public static int PAGE_ID = 1;
    final public static int DOWN_PULL = 1;
    final public static int UP_PULL = 2;
    boolean isNoData = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        setListener();
        initData(DOWN_PULL);
    }

    private void setListener() {
        //下拉刷新
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isNoData = true;
                PAGE_ID = 1;
                initData(DOWN_PULL);
            }
        });
        //上拉加载

        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int itemMax;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemMax == mGoodsAdapter.getItemCount() - 1 && isNoData) {
                    Log.i("main", "上拉加载=" + recyclerView.SCROLL_STATE_IDLE + "\\ =" + RecyclerView.SCROLL_STATE_IDLE + "//" + itemMax + "=" + (mGoodsAdapter.getItemCount() - 1));
                    PAGE_ID++;
                    mTvHint.setVisibility(View.VISIBLE);
                    mTvHint.setText("加载更多...");
                    initData(UP_PULL);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                itemMax = mGrid.findLastVisibleItemPosition();//获取当前屏幕显示的最后一项下标
                if (dx > 0.3 || dy > 0.3) {
                    mTvHint.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData(final int where) {
        String goodsRrl = F.SERVIEW_URL + "find_new_boutique_goods&cat_id=" +
                F.CAT_ID + "&page_id=" + PAGE_ID +
                "&page_size=" + F.PAGE_SIZE_DEFAULT;
        final OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.url(goodsRrl)
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
                        ArrayList<NewGoodBean> bean = utils.array2List(result);
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
        mSwipe = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_goods);
        mSwipe.setColorSchemeColors(R.color.good_detail_bg, R.color.good_detail_currency_price, R.color.google_green);
        mRecycler = (RecyclerView) mView.findViewById(R.id.receiver_goods);
        mGrid = new GridLayoutManager(mContext, 2);
        mRecycler.setLayoutManager(mGrid);
        mList = new ArrayList<NewGoodBean>();
        mGoodsAdapter = new NewGoodsAdapter(mContext, mList);
        mRecycler.setAdapter(mGoodsAdapter);
        mTvHint = (TextView) mView.findViewById(R.id.tvGoodHint);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.new_good_fragment, container, false);
    }

}
