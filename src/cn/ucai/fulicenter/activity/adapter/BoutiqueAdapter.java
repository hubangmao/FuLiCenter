package cn.ucai.fulicenter.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.activity.BoutiqueInfoActivity;
import cn.ucai.fulicenter.activity.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.F;
import cn.ucai.fulicenter.utils.UserUtils;


public class BoutiqueAdapter extends RecyclerView.Adapter<BoutiqueAdapter.BoutiqueHolder> {
    private Context mContext;
    private ArrayList<BoutiqueBean> mList;

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    //下拉刷新
    public void updateAdapterData(ArrayList<BoutiqueBean> mList, SwipeRefreshLayout swipe) {
        this.mList.clear();
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        notifyDataSetChanged();
    }


    @Override
    public BoutiqueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BoutiqueHolder(View.inflate(mContext, R.layout.boutique_adapter_item, null));
    }

    @Override
    public void onBindViewHolder(final BoutiqueHolder holder, int position) {
        final BoutiqueBean bean = mList.get(position);
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_NEW_GOOD + F.FILE_NAME + bean.getImageurl();
        UserUtils.setImage(mContext, holder.mIvBoutique, imageUrl);
        holder.mName.setText(bean.getName());
        holder.mTitle.setText(bean.getTitle());
        holder.mTvAbout.setText(bean.getDescription());



        holder.mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, BoutiqueInfoActivity.class).putExtra("BoutiqueBean", bean));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    class BoutiqueHolder extends RecyclerView.ViewHolder {
        ImageView mIvBoutique;
        TextView mName, mTitle, mTvAbout;
        RelativeLayout mRelative;

        public BoutiqueHolder(View itemView) {
            super(itemView);
            mIvBoutique = (ImageView) itemView.findViewById(R.id.iv_boutique);
            mTitle = (TextView) itemView.findViewById(R.id.tv_boutique_title);
            mName = (TextView) itemView.findViewById(R.id.tv_boutique_name);
            mTvAbout = (TextView) itemView.findViewById(R.id.tv_boutique_about);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.boutique_relative);
        }
    }




}
