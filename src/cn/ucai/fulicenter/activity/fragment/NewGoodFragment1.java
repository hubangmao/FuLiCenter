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

import com.google.gson.Gson;

import java.util.ArrayList;

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
    ArrayList<NewGoodBean> mList;
    public static int PAGE_ID = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        setListener();
        initData();
    }

    private void setListener() {
        //下拉刷新
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                mSwipe.setRefreshing(false);
            }
        });
        //上拉加载
        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initData() {
        String goodsRrl = F.SERVIEW_URL + "request=find_new_boutique_goods&cat_id=" +
                F.CAT_ID + "&page_id=" + PAGE_ID +
                "&page_size=" + F.PAGE_SIZE_DEFAULT;
        Log.i("main", "bean" + goodsRrl);

        final OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.url(goodsRrl)
                .targetClass(NewGoodBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                    @Override
                    public void onSuccess(NewGoodBean[] result) {
                        if (result == null) {
                            Utils.toast(mContext, "网络错误");
                            return;
                        }
                        Log.i("main", "result=" + result);
                        ArrayList<NewGoodBean> bean = utils.array2List(result);
                        mGoodsAdapter.upAdapterData(bean);
                        mSwipe.setRefreshing(false);
                    }

                    @Override
                    public void onError(String error) {
                        Utils.toast(mContext, "网络错误");
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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.new_good_fragment, container, false);
    }

}
