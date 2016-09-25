package cn.hbm.fulicenter.activity.adapter;

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

import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.activity.GoodsInfoActivity;
import cn.hbm.fulicenter.activity.bean.MessageBean;
import cn.hbm.fulicenter.activity.bean.NewGoodBean;
import cn.hbm.fulicenter.data.OkHttpUtils2;
import cn.hbm.fulicenter.task.DowCollectTask;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.UserUtils;
import cn.hbm.fulicenter.utils.Utils;


public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.NewGoodsHolder> {
    private static final String TAG = CollectAdapter.class.getSimpleName();
    private Context mContext;
    public ArrayList<NewGoodBean> mList;

    public CollectAdapter(Context mContext, ArrayList<NewGoodBean> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    //下拉刷新
    public void updateAdapterData(ArrayList<NewGoodBean> mList, SwipeRefreshLayout swipe) {
        this.mList.clear();
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        notifyDataSetChanged();
    }

    //上拉加载
    public void upAdapterData(ArrayList<NewGoodBean> mList, SwipeRefreshLayout swipe) {
        this.mList.addAll(mList);
        swipe.setRefreshing(false);
        notifyDataSetChanged();
    }

    @Override
    public NewGoodsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewGoodsHolder(View.inflate(mContext, R.layout.collect_adapter, null));
    }

    @Override
    public void onBindViewHolder(NewGoodsHolder holder, int position) {
        final NewGoodBean bean = mList.get(position);
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_NEW_GOOD + F.FILE_NAME + bean.getGoodsImg();
        UserUtils.setImage(mContext, holder.mIvGood, imageUrl);
        holder.mTvGoodName.setText(bean.getGoodsName());
        holder.mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, GoodsInfoActivity.class).putExtra("Good_Id", bean.getGoodsId()));
            }
        });
        //取消收藏
        holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpUtils2<MessageBean> utils2 = new OkHttpUtils2<MessageBean>();
                utils2.setRequestUrl(F.REQUEST_DELETE_COLLECT)
                        .addParam(F.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(F.Collect.GOODS_ID, String.valueOf(bean.getGoodsId()))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                if (result.isSuccess()) {
                                    new DowCollectTask().dowCollectInfo(mContext);
                                    Utils.toast(mContext, result.getMsg() + item);
                                    mList.remove(bean);
                                    notifyDataSetChanged();
                                }
                                notifyDataSetChanged();

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 0 : mList.size();
    }

    int item;

    class NewGoodsHolder extends RecyclerView.ViewHolder {
        ImageView mIvGood, mIvDelete;
        TextView mTvGoodName;
        RelativeLayout mRelative;

        public NewGoodsHolder(View itemView) {
            super(itemView);
            mIvGood = (ImageView) itemView.findViewById(R.id.iv_goods);
            mIvDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            mTvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            mRelative = (RelativeLayout) itemView.findViewById(R.id.layout);
            item = getPosition();

        }
    }

}
