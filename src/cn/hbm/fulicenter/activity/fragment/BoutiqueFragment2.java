package cn.hbm.fulicenter.activity.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.adapter.BoutiqueAdapter;
import cn.hbm.fulicenter.activity.bean.BoutiqueBean;
import cn.hbm.fulicenter.hxim.data.OkHttpUtils2;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.Utils;

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
                if (mListTheBottom == mBoutiqueAdapter.getItemCount() - 1 && isNoData) {
                    initData();
                    isNoData = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mListTheBottom = mLinearManager.findLastVisibleItemPosition();
                Log.i("main", "mListTheBottom=" + mListTheBottom);
                if (dy > 0.1 || dy < 0) {
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


        mTvHint = (TextView) mView.findViewById(R.id.tvBoutiqueHint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return mView = inflater.inflate(R.layout.boutique_fragment2, container, false);
    }

}
