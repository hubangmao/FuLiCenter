package cn.ucai.fulicenter.activity.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.super_activity.BaseActivity;
import cn.ucai.fulicenter.activity.fragment.BoutiqueFragment2;
import cn.ucai.fulicenter.activity.fragment.CartFragment4;
import cn.ucai.fulicenter.activity.fragment.CategoryFragment3;
import cn.ucai.fulicenter.activity.fragment.FriendsFragment5;
import cn.ucai.fulicenter.activity.fragment.NewGoodFragment1;
import cn.ucai.fulicenter.super_activity.LoginActivity;
import cn.ucai.fulicenter.utils.Utils;

public class FuLiCenterActivity extends BaseActivity implements View.OnClickListener {
    TextView mTvNew_goods1, mTvBoutique2, mTvCategory3, mTvCart4, mTvFragment5, mSetBackListener;
    ViewPager mViewPager;
    Fragment[] mFragments;
    ViewPageAdapter mAdapter;
    static final int LOG_RETURN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuli_main);
        initView();
        setListener();
    }

    private void setListener() {
        mViewPager.setCurrentItem(0);
        setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
        mTvNew_goods1.setOnClickListener(this);
        mTvBoutique2.setOnClickListener(this);
        mTvCategory3.setOnClickListener(this);
        mTvCart4.setOnClickListener(this);
        mTvFragment5.setOnClickListener(this);

    }

    private void initView() {
        mTvNew_goods1 = (TextView) findViewById(R.id.tvNew_Goods1);
        mTvBoutique2 = (TextView) findViewById(R.id.tvBoutique2);
        mTvCategory3 = (TextView) findViewById(R.id.tvCategory3);
        mTvCart4 = (TextView) findViewById(R.id.tvCater4);
        mTvFragment5 = (TextView) findViewById(R.id.tvFriends5);

        mViewPager = (ViewPager) findViewById(R.id.main_viewPage);
        mSetBackListener = (TextView) findViewById(R.id.backHint);

        //初始化Fragment
        mFragments = new Fragment[5];
        mFragments[0] = new NewGoodFragment1();
        mFragments[1] = new BoutiqueFragment2();
        mFragments[2] = new CategoryFragment3();
        mFragments[3] = new CartFragment4();
        mFragments[4] = new FriendsFragment5();
        mAdapter = new ViewPageAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
    }

    int item;

    @Override
    public void onClick(View view) {
        setAllItem();
        switch (view.getId()) {
            case R.id.tvNew_Goods1:
                item = 0;
                mViewPager.setCurrentItem(0);
                setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
                break;
            case R.id.tvBoutique2:
                item = 1;
                mViewPager.setCurrentItem(1);
                setItemImageAndText(mTvBoutique2, R.drawable.boutique_selected, getResources().getColor(R.color.ebpay_red));
                break;
            case R.id.tvCategory3:
                item = 2;
                setItemImageAndText(mTvCategory3, R.drawable.menu_item_category_selected, getResources().getColor(R.color.ebpay_red));
                mViewPager.setCurrentItem(2);
                break;
            case R.id.tvCater4:
                item = 3;
                setItemImageAndText(mTvCart4, R.drawable.menu_item_cart_selected, getResources().getColor(R.color.ebpay_red));
                if (isLogin()) {
                    mViewPager.setCurrentItem(3);
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), LOG_RETURN);
                }
                break;
            case R.id.tvFriends5:
                item = 4;
                if (isLogin()) {
                    mViewPager.setCurrentItem(4);
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), LOG_RETURN);
                }
                setItemImageAndText(mTvFragment5, R.drawable.menu_item_personal_center_selected, getResources().getColor(R.color.ebpay_red));
                break;
        }
    }

    //验证是否登录
    public boolean isLogin() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOG_RETURN) {
            mViewPager.setCurrentItem(item);
        } else {
            mViewPager.setCurrentItem(item);
            item = -1;
        }

    }

    //初始化全部
    private void setAllItem() {
        setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_normal, getResources().getColor(R.color.myTextColor));
        setItemImageAndText(mTvBoutique2, R.drawable.boutique_normal, getResources().getColor(R.color.myTextColor));
        setItemImageAndText(mTvCategory3, R.drawable.menu_item_category_normal, getResources().getColor(R.color.myTextColor));
        setItemImageAndText(mTvCart4, R.drawable.menu_item_cart_normal, getResources().getColor(R.color.myTextColor));
        setItemImageAndText(mTvFragment5, R.drawable.menu_item_personal_center_normal, getResources().getColor(R.color.myTextColor));
    }

    //设置选项的图片
    private void setItemImageAndText(TextView textView, int drawableId, int color) {
        //设置字体颜色
        textView.setTextColor(color);
        //图片id
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        //设置尺寸
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        //设置图片
        textView.setCompoundDrawables(null, drawable, null, null);
    }

    class ViewPageAdapter extends FragmentPagerAdapter {
        Fragment[] mFragments;

        public ViewPageAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments[i];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

    @Override
    public void onBackPressed() {
        if (Utils.setBackListener(this, mSetBackListener)) {
            return;
        }
    }
}