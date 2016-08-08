package cn.ucai.fulicenter.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ucai.fulicenter.R;


//购物车Fragment
public class FriendsFragment5 extends Fragment implements View.OnClickListener {
    View mLayout;
    ImageView mIvIcon, mIvMessage, mIv2;
    TextView mTvName, mTvSet;

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
    }

    private void initView() {
        mTvSet = (TextView) mLayout.findViewById(R.id.tvSet);
        mIvMessage = (ImageView) mLayout.findViewById(R.id.ivMessage);

        mIvIcon = (ImageView) mLayout.findViewById(R.id.ivIcon);
        mTvName = (TextView) mLayout.findViewById(R.id.tvUserName);
        mIv2 = (ImageView) mLayout.findViewById(R.id.iv2);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
}
