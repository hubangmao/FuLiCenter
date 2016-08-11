package cn.ucai.fulicenter.activity.fragment;


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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.adapter.CartAdapter;
import cn.ucai.fulicenter.activity.bean.CartBean;
import cn.ucai.fulicenter.task.DowCartTask;
import cn.ucai.fulicenter.utils.Utils;


public class CartFragment4 extends Fragment {
    View mView;
    Context mContext;
    SwipeRefreshLayout mSwipe;
    RecyclerView mRecycler;
    LinearLayoutManager mGrid;
    CartAdapter mCartAdapter;
    ArrayList<CartBean> mList;
    public static int PAGE_ID = 1;
    final public static int DOWN_PULL = 1;
    final public static int UP_PULL = 2;
    boolean isNoData = true;
    Button mButBuy;
    TextView mTvAll, mTvSave;

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
                mSwipe.setRefreshing(false);
                Utils.toast(mContext, "刷新成功");

            }
        });
        //上拉加载

        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int itemMax;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && itemMax == mCartAdapter.getItemCount() - 1 && isNoData) {
                    Log.i("main", "上拉加载=" + RecyclerView.SCROLL_STATE_IDLE + "\\ =" + RecyclerView.SCROLL_STATE_IDLE + "//" + itemMax + "=" + (mCartAdapter.getItemCount() - 1));
                    PAGE_ID++;
                    initData(UP_PULL);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                itemMax = mGrid.findLastVisibleItemPosition();//获取当前屏幕显示的最后一项下标
            }
        });
    }

    private void initData(int where) {
        switch (where) {
            case DOWN_PULL:
                mList = FuLiCenterApplication.getInstance().getCartBeen();
                if (mList.size() == 0) {
                    new DowCartTask().dowCartTask(mContext);
                }
                mList = FuLiCenterApplication.getInstance().getCartBeen();
                mCartAdapter.updateAdapterData(mList, mSwipe);
                break;
            case UP_PULL:

                break;

        }
    }

    private void initView() {
        mButBuy = (Button) mView.findViewById(R.id.butBuy);
        mTvAll = (TextView) mView.findViewById(R.id.tvAll);
        mTvSave = (TextView) mView.findViewById(R.id.tvSave);

        mSwipe = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_goods);
        mSwipe.setColorSchemeColors(R.color.good_detail_bg, R.color.good_detail_currency_price, R.color.google_green);
        mRecycler = (RecyclerView) mView.findViewById(R.id.receiver_goods);
        mGrid = new LinearLayoutManager(mContext);
        mRecycler.setLayoutManager(mGrid);
        mList = new ArrayList<CartBean>();
        mCartAdapter = new CartAdapter(mContext, mList, mButBuy, mTvAll, mTvSave);
        mCartAdapter.mList.clear();
        mRecycler.setAdapter(mCartAdapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.cart_fragment4, container, false);
    }

}
