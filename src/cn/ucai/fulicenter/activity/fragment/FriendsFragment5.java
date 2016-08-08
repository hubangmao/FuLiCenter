package cn.ucai.fulicenter.activity.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.SettingsActivity;
import cn.ucai.fulicenter.super_activity.LoginActivity;


//购物车Fragment
public class FriendsFragment5 extends Fragment implements View.OnClickListener {
    View mLayout;
    ImageView mIvIcon, mIvMessage, mIv2;
    TextView mTvName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.my_center_layout, container, false);
        initView();
        setListener();
        return mLayout;
    }

    private void setListener() {
        //收藏
        mLayout.findViewById(R.id.relative1).setOnClickListener(this);
        mLayout.findViewById(R.id.relative2).setOnClickListener(this);
        mLayout.findViewById(R.id.relative3).setOnClickListener(this);
        mLayout.findViewById(R.id.tvSet).setOnClickListener(this);
    }

    private void initView() {
        mIvMessage = (ImageView) mLayout.findViewById(R.id.ivMessage);

        mIvIcon = (ImageView) mLayout.findViewById(R.id.ivIcon);
        mTvName = (TextView) mLayout.findViewById(R.id.tvUserName);
        mIv2 = (ImageView) mLayout.findViewById(R.id.iv2);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //设置
            case R.id.tvSet:
                if (isLogin()) {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            //收藏宝贝
            case R.id.relative1:

                break;
            //收藏店铺
            case R.id.relative2:

                break;
            //足迹
            case R.id.relative3:

                break;

        }
    }

    public boolean isLogin() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            return true;
        } else {
            return false;
        }
    }
}
