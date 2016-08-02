package cn.ucai.fulicenter.activity.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.adapter.NewGoodsAdapter;
import cn.ucai.fulicenter.bean.fulibean.NewGoodBean;

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
    ArrayList<NewGoodBean> mList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.new_good_fragment, container, false);
    }

}
