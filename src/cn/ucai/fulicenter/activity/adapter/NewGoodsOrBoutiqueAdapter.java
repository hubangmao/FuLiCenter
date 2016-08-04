package cn.ucai.fulicenter.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.GoodsInfoActivity;
import cn.ucai.fulicenter.bean.fulibean.NewGoodBean;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.UserUtils;


public class NewGoodsOrBoutiqueAdapter extends RecyclerView.Adapter<NewGoodsOrBoutiqueAdapter.NewGoodsHolder> {
    private Context mContext;
    public ArrayList<NewGoodBean> mList;

    public NewGoodsOrBoutiqueAdapter(Context mContext, ArrayList<NewGoodBean> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    //下拉刷新
    public void updateAdapterData(ArrayList<NewGoodBean> mList, SwipeRefreshLayout swipe) {
        Log.i("main", "NewBoutiqueAdapterList=" + mList.get(1));
        this.mList.clear();
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        soryTime();
        notifyDataSetChanged();
    }

    //上拉加载
    public void upAdapterData(ArrayList<NewGoodBean> mList, SwipeRefreshLayout swipe) {
        soryTime();
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        soryTime();
        notifyDataSetChanged();
    }

    @Override
    public NewGoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewGoodsHolder(View.inflate(mContext, R.layout.goods_adapter, null));
    }

    @Override
    public void onBindViewHolder(NewGoodsHolder holder, int position) {
        final NewGoodBean bean = mList.get(position);
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_NEW_GOOD + F.FILE_NAME + bean.getGoodsImg();
        UserUtils.setImage(mContext, holder.mIvGood, imageUrl);
        Log.i("main", "bean=name" + bean.getGoodsName());
        holder.mTvPrice.setText(bean.getShopPrice());
        holder.mTvGoodName.setText(bean.getGoodsName());
        holder.mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("Good_Id", bean.getGoodsId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    class NewGoodsHolder extends RecyclerView.ViewHolder {
        ImageView mIvGood;
        TextView mTvGoodName;
        TextView mTvPrice;
        RelativeLayout mRelative;

        public NewGoodsHolder(View itemView) {
            super(itemView);
            mIvGood = (ImageView) itemView.findViewById(R.id.iv_goods);
            mTvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            mTvPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.layout);

        }
    }

    private void soryTime() {
        Collections.sort(mList, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean goodLeft, NewGoodBean goodRight) {
                return (int) (Long.valueOf(goodLeft.getAddTime()) - (Long.valueOf(goodRight.getAddTime())));
            }
        });
    }

}
