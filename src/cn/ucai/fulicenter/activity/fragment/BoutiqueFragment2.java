package cn.ucai.fulicenter.activity.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ucai.fulicenter.R;

/**
 * 精选界面
 */
public class BoutiqueFragment2 extends Fragment {
    View mView;

    public BoutiqueFragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.boutique_fragment2, container, false);
    }

}
