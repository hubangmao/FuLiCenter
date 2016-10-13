package cn.hbm.fulicenter.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.activity.activity.BoutiqueInfoActivity;
import cn.hbm.fulicenter.activity.activity.CategoryInfoActivity;
import cn.hbm.fulicenter.activity.bean.CategoryChildBean;
import cn.hbm.fulicenter.activity.bean.CategoryGroupBean;
import cn.hbm.fulicenter.utils.F;
import cn.hbm.fulicenter.utils.UserUtils;
import cn.hbm.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ExpandableAdapter extends BaseExpandableListAdapter {
    Context mCntext;
    ArrayList<CategoryGroupBean> mMaxList;//大类型
    ArrayList<ArrayList<CategoryChildBean>> mMinList;//小类型

    public ExpandableAdapter(Context context, ArrayList<CategoryGroupBean> maxList, ArrayList<ArrayList<CategoryChildBean>> minList) {
        this.mCntext = context;
        this.mMaxList = maxList;
        this.mMinList = minList;
    }

    public void listClear() {
        mMaxList.clear();
        mMinList.clear();

    }

    public void updateMax(ArrayList<CategoryGroupBean> maxList) {
        this.mMaxList.addAll(maxList);
        notifyDataSetChanged();
    }


    public void updateMin(ArrayList<CategoryChildBean> minList) {
        this.mMinList.add(minList);
        notifyDataSetChanged();
    }

    //大类
    @Override
    public View getGroupView(int maxPosition, boolean isExpand, View maxView, ViewGroup viewGroup) {
        CategoryGroupBean bean = mMaxList.get(maxPosition);
        GroupHolder holder;
        if (maxView == null) {
            holder = new GroupHolder();
            maxView = View.inflate(mCntext, R.layout.max_item_layout, null);
            holder.mIvMaxImage = (ImageView) maxView.findViewById(R.id.ivMaxImage);
            holder.mTvMaxName = (TextView) maxView.findViewById(R.id.tvMaxName);
            holder.mIvMaxIsShow = (ImageView) maxView.findViewById(R.id.ivMaxIsShow);
            maxView.setTag(holder);
        } else {
            holder = (GroupHolder) maxView.getTag();
        }
        if (isExpand) {
            holder.mIvMaxIsShow.setImageResource(R.drawable.expand_off);
        } else {
            holder.mIvMaxIsShow.setImageResource(R.drawable.expand_on);
        }
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_CATEGORY_GROUP_IMAGE + "&" + F.Boutique.IMAGE_URL + "=" + bean.getImageUrl();
        UserUtils.setImage(mCntext, holder.mIvMaxImage, imageUrl);
        holder.mTvMaxName.setText(bean.getName());
        return maxView;
    }

    class GroupHolder {
        ImageView mIvMaxImage;
        TextView mTvMaxName;
        ImageView mIvMaxIsShow;
    }

    //小类
    @Override
    public View getChildView(final int maxPosition, int minPosition, boolean isListChild, View minView, ViewGroup viewGroup) {
        final CategoryChildBean bean = mMinList.get(maxPosition).get(minPosition);
        ChildHolder holder;
        if (minView == null) {
            holder = new ChildHolder();
            minView = View.inflate(mCntext, R.layout.min_item_layout, null);
            holder.mIvMinImage = (ImageView) minView.findViewById(R.id.ivMinImage);
            holder.mRelative = (RelativeLayout) minView.findViewById(R.id.minRelative);
            holder.mTvName = (TextView) minView.findViewById(R.id.tvMinName);
            minView.setTag(holder);
        } else {
            holder = (ChildHolder) minView.getTag();
        }
        String imageUrl = F.SERVIEW_URL + F.REQUEST_DOWNLOAD_CATEGORY_CHILD_IMAGE + "&" + F.Boutique.IMAGE_URL + "=" + bean.getImageUrl();
        UserUtils.setImage(mCntext, holder.mIvMinImage, imageUrl);
        holder.mTvName.setText(bean.getName());
        holder.mRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCntext.startActivity(new Intent(mCntext, CategoryInfoActivity.class).putExtra("childList", mMinList.get(maxPosition))
                        .putExtra("GroupName", mMaxList.get(maxPosition).getName())
                        .putExtra("id", bean.getId()));
            }
        });
        return minView;

    }

    class ChildHolder {
        RelativeLayout mRelative;
        ImageView mIvMinImage;
        TextView mTvName;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return mMaxList.size() == 0 ? 0 : mMaxList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mMinList.get(i).size() == 0 ? 0 : mMinList.get(i).size();
    }

    @Override
    public CategoryGroupBean getGroup(int i) {
        return mMaxList.get(i) == null ? null : mMaxList.get(1);
    }

    @Override
    public CategoryChildBean getChild(int maxId, int minId) {
        return mMinList.get(maxId).get(minId) == null ? null : mMinList.get(maxId).get(minId);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
