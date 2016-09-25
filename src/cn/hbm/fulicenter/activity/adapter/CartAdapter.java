package cn.hbm.fulicenter.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.activity.GoodsInfoActivity;
import cn.hbm.fulicenter.activity.activity.UserAddressActivity;
import cn.hbm.fulicenter.activity.bean.CartBean;
import cn.hbm.fulicenter.activity.bean.GoodDetailsBean;
import cn.hbm.fulicenter.task.UpdateCartTask;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.UserUtils;
import cn.hbm.fulicenter.utils.Utils;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.NewGoodsHolder> {
    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context mContext;
    public ArrayList<CartBean> mList;
    Button mButBuy;
    TextView mTvAll, mTvSave;
    boolean isCheck;
    /**
     * 总价格
     */
    int sumPrice = 0;
    /**
     * 打折价格
     */
    int rankPrice = 0;


    //当前商品件数
    int mNumber;

    public CartAdapter(Context mContext, ArrayList<CartBean> list, Button butBuy, TextView mTvAll, TextView tvSave) {
        this.mContext = mContext;
        this.mList = list;
        this.mButBuy = butBuy;
        this.mTvAll = mTvAll;
        this.mTvSave = tvSave;
    }

    //下拉刷新
    public void updateAdapterData(ArrayList<CartBean> mList, SwipeRefreshLayout swipe) {
        this.mList.clear();
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        notifyDataSetChanged();
    }

    //上拉加载
    public void upAdapterData(ArrayList<CartBean> mList, SwipeRefreshLayout swipe) {
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        notifyDataSetChanged();
    }

    @Override
    public NewGoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewGoodsHolder(View.inflate(mContext, R.layout.cart_adapter, null));
    }


    @Override
    public void onBindViewHolder(final NewGoodsHolder holder, final int position) {
        final CartBean bean = mList.get(position);
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_NEW_GOOD + F.FILE_NAME + bean.getGoods().getGoodsThumb();
        UserUtils.setImage(mContext, holder.mIvIcon, imageUrl);
        mNumber = bean.getCount();
        holder.mTvGoodName.setText(bean.getGoods().getGoodsName());
        holder.mTvGoodNumber.setText("(" + String.valueOf(bean.getCount()) + ")");
        holder.mTvPrice.setText(String.valueOf("￥" + getSubStr(bean.getGoods().getCurrencyPrice()) * bean.getCount()));
        holder.mChIsCheck.setChecked(bean.isChecked());
        setPrice();
        //选择购买
        isCheck = bean.isChecked();
        mButBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCheck) {
                    mContext.startActivity(new Intent(mContext, UserAddressActivity.class).putExtra("sumPrice", String.valueOf(sumPrice)));//传递总价
                } else {
                    Utils.toast(mContext, "亲你还没有选中商品呢");
                }
            }
        });

        //选择单个商品
        holder.mChIsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck = b;
                bean.setChecked(b);
                bean.setCount(mNumber);
                //更新服务端商品数量数据
                new UpdateCartTask().updateCartTask(mContext, bean);
            }
        });

        //添加商品数量
        holder.mIvCartAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberStr = holder.mTvGoodNumber.getText().toString();
                int number = Integer.parseInt(numberStr.substring(1, numberStr.lastIndexOf(")")));
                number++;
                //设置选中的商品价钱
                mNumber = number;
                holder.mTvGoodNumber.setText("(" + mNumber + ")");
                new UpdateCartTask().addCartTask(mContext, bean.getGoodsId());
                setPrice();


            }
        });
        //减掉商品数量
        holder.mIvCartDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberStr = holder.mTvGoodNumber.getText().toString();
                int number = Integer.parseInt(numberStr.substring(1, numberStr.lastIndexOf(")")));
                number--;
                mNumber = number;
                holder.mTvGoodNumber.setText("(" + mNumber + ")");
                //删除商品
                if (number == 0) {
                    new UpdateCartTask().deleteCartTask(mContext, bean.getId());
                    mList.remove(bean);
                    setPrice();
                    notifyDataSetChanged();
                    if (getItemCount() == 0) {
                        isCheck = false;
                    }
                    return;
                }
            }
        });
        //商品图片
        holder.mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("Good_Id", bean.getGoodsId()));
            }
        });
    }


    //得到总价钱及优惠价
    private void setPrice() {
        if (mList != null && mList.size() > 0) {
            for (CartBean cb : mList) {
                GoodDetailsBean goods = cb.getGoods();
                if (goods != null) {
                    sumPrice += cb.getCount() * getSubStr(goods.getCurrencyPrice());
                    rankPrice = getSubStr(goods.getRankPrice());
                }
            }
            mTvAll.setText("合计:￥" + sumPrice + ".00");
            mTvSave.setText("节省:￥" + (sumPrice - rankPrice) + ".00");
        } else {
            mTvAll.setText("合计:￥0.00");
            mTvSave.setText("合计:￥0.00");

        }
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    public int getSubStr(String shopPrice) {
        return (Integer.parseInt(shopPrice.substring(1, shopPrice.length())));
    }

    class NewGoodsHolder extends RecyclerView.ViewHolder {
        ImageView mIvIcon, mIvCartAdd, mIvCartDel;
        TextView mTvGoodName, mTvGoodNumber, mTvPrice;
        CheckBox mChIsCheck;

        public NewGoodsHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            mChIsCheck = (CheckBox) itemView.findViewById(R.id.ivIsCheck);
            mIvCartAdd = (ImageView) itemView.findViewById(R.id.ivCartAdd);
            mIvCartDel = (ImageView) itemView.findViewById(R.id.ivDelCart);

            mTvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            mTvGoodNumber = (TextView) itemView.findViewById(R.id.tvNumber);
            mTvPrice = (TextView) itemView.findViewById(R.id.tvCartPrice);

        }
    }


}
