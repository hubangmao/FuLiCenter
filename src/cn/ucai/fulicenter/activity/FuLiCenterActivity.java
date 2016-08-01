package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import cn.ucai.fulicenter.R;

public class FuLiCenterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuli_main);
        Log.i("main","FuLiCenter启动");
    }

}
