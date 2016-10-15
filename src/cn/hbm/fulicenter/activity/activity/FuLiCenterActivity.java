package cn.hbm.fulicenter.activity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cn.hbm.fulicenter.DemoHXSDKHelper;
import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.fragment.BoutiqueFragment2;
import cn.hbm.fulicenter.activity.fragment.CartFragment4;
import cn.hbm.fulicenter.activity.fragment.CategoryFragment3;
import cn.hbm.fulicenter.activity.fragment.FriendsFragment5;
import cn.hbm.fulicenter.activity.fragment.NewGoodFragment1;
import cn.hbm.fulicenter.hxim.super_activity.BaseActivity;
import cn.hbm.fulicenter.hxim.super_activity.LoginActivity;
import cn.hbm.fulicenter.utils.Utils;

public class FuLiCenterActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = FuLiCenterApplication.class.getSimpleName();
    private TextView mTvNew_goods1, mTvBoutique2, mTvCategory3, mTvCart4, mTvFragment5, mtvCartHint;
    private ViewPager mViewPager;
    private Fragment[] mFragments;
    private ViewPageAdapter mAdapter;
    private final int LOG_RETURN = 100;
    private static final int LOG_CATE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuli_main);
        initView();
        setListener();

    }

    private void setListener() {
        mViewPager.setCurrentItem(0);
        setAllItem();
        setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
        mTvNew_goods1.setOnClickListener(this);
        mTvBoutique2.setOnClickListener(this);
        mTvCategory3.setOnClickListener(this);
        mTvCart4.setOnClickListener(this);
        mTvFragment5.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int item, float v, int i1) {

            }

            @Override
            public void onPageSelected(int item) {
                setAllItem();
                switch (item) {
                    case 0:
                        setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
                        break;
                    case 1:
                        setItemImageAndText(mTvBoutique2, R.drawable.boutique_selected, getResources().getColor(R.color.ebpay_red));
                        break;
                    case 2:
                        setItemImageAndText(mTvCategory3, R.drawable.menu_item_category_selected, getResources().getColor(R.color.ebpay_red));
                        break;
                    case 3:
                        setItemImageAndText(mTvCart4, R.drawable.menu_item_cart_selected, getResources().getColor(R.color.ebpay_red));
                        if (!isLogin()) {
                            startActivityForResult(new Intent(FuLiCenterActivity.this, LoginActivity.class), LOG_CATE);
                        }
                        break;
                    case 4:
                        setItemImageAndText(mTvFragment5, R.drawable.menu_item_personal_center_selected, getResources().getColor(R.color.ebpay_red));
                        if (!isLogin()) {
                            startActivityForResult(new Intent(FuLiCenterActivity.this, LoginActivity.class), LOG_RETURN);
                        }
                        break;
                }
            }

            @Override

            public void onPageScrollStateChanged(int item) {

            }
        });


    }

    private void initView() {


        registerCartBroCast();
        mTvNew_goods1 = (TextView) findViewById(R.id.tvNew_Goods1);
        mTvBoutique2 = (TextView) findViewById(R.id.tvBoutique2);
        mTvCategory3 = (TextView) findViewById(R.id.tvCategory3);
        mTvCart4 = (TextView) findViewById(R.id.tvCater4);
        mTvFragment5 = (TextView) findViewById(R.id.tvFriends5);
        mtvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mViewPager = (ViewPager) findViewById(R.id.main_viewPage);

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

    @Override
    public void onClick(View view) {
        setAllItem();
        switch (view.getId()) {
            case R.id.tvNew_Goods1:
                mViewPager.setCurrentItem(0);
                setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
                break;
            case R.id.tvBoutique2:
                mViewPager.setCurrentItem(1);
                setItemImageAndText(mTvBoutique2, R.drawable.boutique_selected, getResources().getColor(R.color.ebpay_red));
                break;
            case R.id.tvCategory3:
                setItemImageAndText(mTvCategory3, R.drawable.menu_item_category_selected, getResources().getColor(R.color.ebpay_red));
                mViewPager.setCurrentItem(2);
                break;
            case R.id.tvCater4:
                setItemImageAndText(mTvCart4, R.drawable.menu_item_cart_selected, getResources().getColor(R.color.ebpay_red));
                if (isLogin()) {
                    mViewPager.setCurrentItem(3);
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), LOG_CATE);
                }
                break;
            case R.id.tvFriends5:
                setItemImageAndText(mTvFragment5, R.drawable.menu_item_personal_center_selected, getResources().getColor(R.color.ebpay_red));
                if (isLogin()) {
                    mViewPager.setCurrentItem(4);
                } else {
                    startActivityForResult(new Intent(this, LoginActivity.class), LOG_RETURN);
                }
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
    protected void onResume() {
        Log.i("main", TAG + "购物车返回键监听");
        super.onResume();
        if (!FuLiCenterApplication.isLogin) {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setAllItem();
        if (resultCode == -1) {
            mViewPager.setCurrentItem(0);
            setItemImageAndText(mTvNew_goods1, R.drawable.menu_item_new_good_selected, getResources().getColor(R.color.ebpay_red));
            return;
        }
        if (requestCode == LOG_RETURN) {
            mViewPager.setCurrentItem(4);
            setItemImageAndText(mTvFragment5, R.drawable.menu_item_personal_center_selected, getResources().getColor(R.color.ebpay_red));
            return;
        }
        if (requestCode == LOG_CATE) {
            setItemImageAndText(mTvCart4, R.drawable.menu_item_cart_selected, getResources().getColor(R.color.ebpay_red));
            mViewPager.setCurrentItem(3);
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

    class ViewPageAdapter extends FragmentStatePagerAdapter {
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

    class updateCartBroCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int size = Utils.getCartNumber();
            if (!isLogin()) {
                mtvCartHint.setVisibility(View.GONE);
                return;
            }
            if (size > 0) {
                mtvCartHint.setVisibility(View.VISIBLE);
                mtvCartHint.setText(String.valueOf(size));
            } else {
                mtvCartHint.setVisibility(View.GONE);
            }
        }
    }

    updateCartBroCast mUpdate;

    private void registerCartBroCast() {
        mUpdate = new updateCartBroCast();
        IntentFilter intentFilter = new IntentFilter("update_cart");
        registerReceiver(mUpdate, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdate != null) {
            unregisterReceiver(mUpdate);
        }

    }
}