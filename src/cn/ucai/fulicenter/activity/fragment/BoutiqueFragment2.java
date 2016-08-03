package cn.ucai.fulicenter.activity.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.GoodsInfoActivity;
import cn.ucai.fulicenter.activity.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.activity.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.fulibean.BoutiqueBean;
import cn.ucai.fulicenter.bean.fulibean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.Utils;

/**
 * 精选界面
 */
public class BoutiqueFragment2 extends Fragment {
    View mView;
    Context mContext;
    SwipeRefreshLayout mSwipe;
    RecyclerView mRecycler;
    LinearLayoutManager mLinearManager;
    BoutiqueAdapter mBoutiqueAdapter;
    TextView mTvHint;
    ArrayList<BoutiqueBean> mList;
    int mListTheBottom;
    boolean isNoData = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        //下拉刷新
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                Utils.toast(mContext, "刷新成功");
            }
        });
        //上拉加载
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                initData();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mListTheBottom = mLinearManager.findLastVisibleItemPosition();
                Log.i("main", "mListTheBottom=" + mListTheBottom);

                if (dx > 0.03 || dy > 0.3) {
                    mTvHint.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData() {
        final OkHttpUtils2<BoutiqueBean[]> utils = new OkHttpUtils2<BoutiqueBean[]>();
        utils.setRequestUrl(F.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
                    @Override
                    public void onSuccess(BoutiqueBean[] result) {
                        ArrayList<BoutiqueBean> bean = utils.array2List(result);
                        if (bean.size() - 1 == mListTheBottom) {
                            mTvHint.setVisibility(View.VISIBLE);
                            mTvHint.setText("已经没有更多加载...");
                        }
                        mBoutiqueAdapter.updateAdapterData(bean, mSwipe);
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
        mSwipe = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_Boutique);
        mSwipe.setColorSchemeColors(R.color.good_detail_bg, R.color.good_detail_currency_price, R.color.google_green);
        mRecycler = (RecyclerView) mView.findViewById(R.id.receiver_Boutique);
        mLinearManager = new LinearLayoutManager(mContext);
        mRecycler.setLayoutManager(mLinearManager);
        mList = new ArrayList<BoutiqueBean>();
        mBoutiqueAdapter = new BoutiqueAdapter(mContext, mList);
        mRecycler.setAdapter(mBoutiqueAdapter);

        mBoutiqueAdapter.setOnActionItemClickListener(new BoutiqueAdapter.OnActionItemClickListener() {
            @Override
            public void onItemClickListener(View v, int pos, BoutiqueBean ben) {
            }
        });

        mTvHint = (TextView) mView.findViewById(R.id.tvBoutiqueHint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.boutique_fragment2, container, false);
    }


}
