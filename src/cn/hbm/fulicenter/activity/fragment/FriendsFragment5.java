package cn.hbm.fulicenter.activity.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.hbm.fulicenter.DemoHXSDKHelper;
import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.activity.CollectActivity;
import cn.hbm.fulicenter.activity.activity.SettingsActivity;
import cn.hbm.fulicenter.hxim.super_activity.MainActivity;
import cn.hbm.fulicenter.task.DowCollectTask;
import cn.hbm.fulicenter.utils.UserUtils;


//购物车Fragment
public class FriendsFragment5 extends Fragment implements View.OnClickListener {
    private  Context mContext;
    private  View mLayout;
    private  ImageView mIvIcon, mIv2;
    private  TextView mTvName, mTvCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mLayout = inflater.inflate(R.layout.my_center_layout, container, false);
        initView();
        initData();
        setListener();
        return mLayout;
    }

    private void initData() {
        new DowCollectTask().dowCollectInfo(mContext);
        UserUtils.setFuLiAvatar(mContext, FuLiCenterApplication.getInstance().getUserName(), mIvIcon);
    }

    private void setListener() {
        //收藏
        mLayout.findViewById(R.id.relative1).setOnClickListener(this);
        mLayout.findViewById(R.id.relative2).setOnClickListener(this);
        mLayout.findViewById(R.id.relative3).setOnClickListener(this);
        mLayout.findViewById(R.id.tvSet).setOnClickListener(this);
        mLayout.findViewById(R.id.ivMessage).setOnClickListener(this);

    }

    private void initView() {
        registerBroad();
        mIvIcon = (ImageView) mLayout.findViewById(R.id.ivIcon);
        mTvName = (TextView) mLayout.findViewById(R.id.tvUserName);
        mIv2 = (ImageView) mLayout.findViewById(R.id.iv2);
        mTvCollection = (TextView) mLayout.findViewById(R.id.tvCollection);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //设置
            case R.id.tvSet:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            //消息列表
            case R.id.ivMessage:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
            //收藏宝贝
            case R.id.relative1:
               startActivity(new Intent(mContext, CollectActivity.class));
                break;
            //收藏店铺
            case R.id.relative2:

                break;
            //足迹
            case R.id.relative3:

                break;

        }
    }

    //验证是否登录成功
    public boolean isLogin() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            return true;
        } else {
            return false;
        }
    }

    class UpdateCollection extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mTvCollection.setText(FuLiCenterApplication.getInstance().getCollocation());
        }
    }

    UpdateCollection mUpdate;

    public void registerBroad() {
        mUpdate = new UpdateCollection();
        IntentFilter intentFilter = new IntentFilter("update_collect");
        mContext.registerReceiver(mUpdate, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdate != null) {
            mContext.unregisterReceiver(mUpdate);
        }
    }
}
