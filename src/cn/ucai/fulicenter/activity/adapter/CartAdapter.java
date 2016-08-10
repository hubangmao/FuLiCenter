package cn.ucai.fulicenter.activity.adapter;

import android.content.Context;
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

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.bean.CartBean;
import cn.ucai.fulicenter.activity.bean.GoodDetailsBean;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.UserUtils;


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

    //之前选中的价钱
    int mBefore;
    //之前选中的优惠价钱
    int mSave;

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

        holder.mTvGoodName.setText(bean.getGoods().getGoodsName());
        holder.mTvGoodNumber.setText("(" + String.valueOf(bean.getCount()) + ")");
        holder.mTvPrice.setText(String.valueOf("￥" + getSubStr(bean.getGoods().getCurrencyPrice()) * bean.getCount()));
        //选择购买
        mButBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //选择单个商品
        holder.mChIsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //总价
                    int sumStr = getSubStr(holder.mTvPrice.getText().toString());
                    mBefore = sumStr;
                    sumPrice += sumStr;
                    //优惠价
                    mSave = getSubStr(bean.getGoods().getRankPrice());
                    rankPrice += getSubStr(bean.getGoods().getRankPrice());
                    mTvAll.setText("合计:￥" + sumPrice + ".00");
                    mTvSave.setText("节省:￥" + (sumPrice - rankPrice) + ".00");
                } else {
                    int subStr = getSubStr1(mTvAll.getText().toString());
                    int subSave = getSubStr1(mTvSave.getText().toString());
                    mSave = subSave;
                    mTvAll.setText("合计:￥0.00");
                    mTvSave.setText("节省:￥0.00");
                    if (subStr > 0) {
                        mTvAll.setText("合计:￥" + (subStr - mBefore) + ".00");
                        mTvSave.setText("节省:￥" + (subSave - mSave) + ".00");
                        mBefore = 0;
                        mSave = 0;
                    }
                    sumPrice = 0;
                    rankPrice = 0;
                }

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
                holder.mTvGoodNumber.setText("(" + number + ")");
                holder.mTvPrice.setText(String.valueOf("￥" + getSubStr(bean.getGoods().getCurrencyPrice()) * number));
                //拿到商品的总价
                int curren = getSubStr(holder.mTvPrice.getText().toString());
                if (holder.mChIsCheck.isChecked()) {
                    //设置总价
                    int subStr = getSubStr(holder.mTvPrice.getText().toString()) + curren;
                    rankPrice += getSubStr(bean.getGoods().getRankPrice());
                    mTvAll.setText("合计:￥" + subStr + ".00");
                    mTvSave.setText("节省:￥" + (subStr - rankPrice) + ".00");
                }
            }
        });
        //减掉商品数量
        holder.mIvCartDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberStr = holder.mTvGoodNumber.getText().toString();
                int number = Integer.parseInt(numberStr.substring(1, numberStr.lastIndexOf(")")));
                number--;
                if (number == 0) {
                    return;
                }
                //设置选中的商品价钱
                holder.mTvGoodNumber.setText("(" + number + ")");
                holder.mTvPrice.setText(String.valueOf("￥" + getSubStr(bean.getGoods().getCurrencyPrice()) * number));
                //拿到商品的总价
                int curren = getSubStr(holder.mTvPrice.getText().toString());
                if (holder.mChIsCheck.isChecked()) {
                    //设置总价
                    int subStr = getSubStr(holder.mTvPrice.getText().toString()) + curren;
                    rankPrice += getSubStr(bean.getGoods().getRankPrice());
                    mTvAll.setText("合计:￥" + subStr + ".00");
                    mTvSave.setText("节省:￥" + (subStr - rankPrice) + ".00");
                }
            }
        });
        //商品图片
        holder.mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private int getSubStr1(String s) {
        return Integer.parseInt(s.substring(s.lastIndexOf("￥") + 1, s.length() - 3));

    }

    //得到总价钱
    private void setPrice() {
        if (mList != null && mList.size() > 0) {
            for (CartBean cb : mList) {
                GoodDetailsBean goods = cb.getGoods();
                if (goods != null) {
                    sumPrice += cb.getCount() * getSubStr(goods.getCurrencyPrice());
                    rankPrice += getSubStr(goods.getRankPrice());
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
