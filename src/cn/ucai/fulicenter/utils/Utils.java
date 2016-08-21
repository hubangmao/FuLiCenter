package cn.ucai.fulicenter.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.bean.CartBean;
import cn.ucai.fulicenter.bean.Pager;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.super_activity.BaseActivity;

/**
 * Created by clawpo on 16/3/28.
 */
public class Utils {
    private static TextView mTv;

    public static void setmTv(TextView mTv) {
        Utils.mTv = mTv;
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static void showToast(Context context, String text, int time) {
        Toast.makeText(context, text, time).show();
    }

    public static void showToast(Context context, int strId, int time) {
        Toast.makeText(context, strId, time).show();
    }

    /**
     * 将数组转换为ArrayList集合
     *
     * @param ary
     * @return
     */
    public static <T> ArrayList<T> array2List(T[] ary) {
        if (ary == null) return null;
        List<T> list = Arrays.asList(ary);
        ArrayList<T> arrayList = new ArrayList<T>(list);
        return arrayList;
    }

    /**
     * 添加新的数组元素：数组扩容
     *
     * @param array：数组
     * @param t：添加的数组元素
     * @return：返回添加后的数组
     */
    public static <T> T[] add(T[] array, T t) {
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = t;
        return array;
    }

    public static String getResourceString(Context context, int msg) {
        if (msg <= 0) return null;
        String msgStr = msg + "";
        msgStr = I.MSG_PREFIX_MSG + msgStr;
        int resId = context.getResources().getIdentifier(msgStr, "string", context.getPackageName());
        return context.getResources().getString(resId);
    }

//    public static <T> datas getResultDate(Object o,final T t){
//        if(o!=null){
//            Type type = new TypeToken<t>(){}.getClass();
//            return new Gson().fromJson(o.toString(),type);
//        }
//        return null;
//    }


    public static <T> Result getResultFromJson(String jsonStr, Class<T> clazz) {
        Result result = new Result();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (!jsonObject.isNull("retCode")) {
                result.setRetCode(jsonObject.getInt("retCode"));
            } else if (!jsonObject.isNull("msg")) {
                result.setRetCode(jsonObject.getInt("msg"));
            }
            if (!jsonObject.isNull("retMsg")) {
                result.setRetMsg(jsonObject.getBoolean("retMsg"));
            } else if (!jsonObject.isNull("result")) {
                result.setRetMsg(jsonObject.getBoolean("result"));
            }
            if (!jsonObject.isNull("retData")) {
                JSONObject jsonRetData = jsonObject.getJSONObject("retData");
                if (jsonRetData != null) {
                    Log.e("Utils", "jsonRetData=" + jsonRetData);
                    String date;
                    try {
                        date = URLDecoder.decode(jsonRetData.toString(), I.UTF_8);
                        Log.e("Utils", "jsonRetData=" + date);
                        T t = new Gson().fromJson(date, clazz);
                        result.setRetData(t);
                        return result;

                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                        T t = new Gson().fromJson(jsonRetData.toString(), clazz);
                        result.setRetData(t);
                        return result;
                    }
                }
            } else {
                if (jsonObject != null) {
                    Log.e("Utils", "jsonObject=" + jsonObject);
                    String date;
                    try {
                        date = URLDecoder.decode(jsonObject.toString(), I.UTF_8);
                        Log.e("Utils", "jsonObject=" + date);
                        T t = new Gson().fromJson(date, clazz);
                        result.setRetData(t);
                        return result;

                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                        T t = new Gson().fromJson(jsonObject.toString(), clazz);
                        result.setRetData(t);
                        return result;
                    }
                }

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Result getListResultFromJson(String jsonStr, Class<T> clazz) {
        Result result = new Result();
        Log.e("Utils", "jsonStr=" + jsonStr);
        try {

            JSONObject jsonObject = new JSONObject(jsonStr);
            if (!jsonObject.isNull("retCode")) {
                result.setRetCode(jsonObject.getInt("retCode"));
            } else if (!jsonObject.isNull("msg")) {
                result.setRetCode(jsonObject.getInt("msg"));
            }
            if (!jsonObject.isNull("retMsg")) {
                result.setRetMsg(jsonObject.getBoolean("retMsg"));
            } else if (!jsonObject.isNull("result")) {
                result.setRetMsg(jsonObject.getBoolean("result"));
            }
            if (!jsonObject.isNull("retData")) {
                JSONArray array = jsonObject.getJSONArray("retData");
                if (array != null) {
                    List<T> list = new ArrayList<T>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonGroupAvatar = array.getJSONObject(i);
                        T ga = new Gson().fromJson(jsonGroupAvatar.toString(), clazz);
                        list.add(ga);
                    }
                    result.setRetData(list);
                    return result;
                }
            } else {
                JSONArray array = new JSONArray(jsonStr);
                if (array != null) {
                    List<T> list = new ArrayList<T>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonGroupAvatar = array.getJSONObject(i);
                        T ga = new Gson().fromJson(jsonGroupAvatar.toString(), clazz);
                        list.add(ga);
                    }
                    result.setRetData(list);
                    return result;
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Result getPageResultFromJson(String jsonStr, Class<T> clazz) {
        Result result = new Result();
        try {
            if (jsonStr == null || jsonStr.isEmpty() || jsonStr.length() < 3) return null;
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (!jsonObject.isNull("retCode")) {
                result.setRetCode(jsonObject.getInt("retCode"));
            } else if (!jsonObject.isNull("msg")) {
                result.setRetCode(jsonObject.getInt("msg"));
            }
            if (!jsonObject.isNull("retMsg")) {
                result.setRetMsg(jsonObject.getBoolean("retMsg"));
            } else if (!jsonObject.isNull("result")) {
                result.setRetMsg(jsonObject.getBoolean("result"));
            }
            if (!jsonObject.isNull("retData")) {
                JSONObject jsonPager = jsonObject.getJSONObject("retData");
                if (jsonPager != null) {
                    Pager pager = new Pager();
                    pager.setCurrentPage(jsonPager.getInt("currentPage"));
                    pager.setMaxRecord(jsonPager.getInt("maxRecord"));
                    JSONArray array = jsonPager.getJSONArray("pageData");
                    List<T> list = new ArrayList<T>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonGroupAvatar = array.getJSONObject(i);
                        T ga = new Gson().fromJson(jsonGroupAvatar.toString(), clazz);
                        list.add(ga);
                    }
                    pager.setPageData(list);
                    result.setRetData(pager);
                    return result;
                }
            } else {
                Log.i("main", "这是getPageResultFromJson");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static Toast toast;

    public static void toast(final Context context, String string) {
        View view = View.inflate(context, R.layout.toast_layout, null);
        TextView toast_Tv = (TextView) view.findViewById(R.id.toast_tv);
        toast_Tv.setText(string);
        toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 600);

    }

    static public void toastResources(Context context, int id) {
        String string = context.getResources().getString(id);
        Toast.makeText(context, string + "", Toast.LENGTH_SHORT).show();
    }

    static boolean b = true;
    static long exitTime;
    static Handler handler;

    static public boolean setBackListener(final Context context, final View view) {
        handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1700);
                b = true;
                exitTime = 0;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });

            }
        }.start();

        if (b) {
            b = false;
            exitTime = System.currentTimeMillis();
            view.setVisibility(View.VISIBLE);
            return true;
        }
        if (System.currentTimeMillis() - exitTime > 300) {
            Log.i("main", "关闭啦");
            ((Activity) context).finish();
            return true;
        }
        return true;
    }

    public static int px2dp(Context context, int px) {
        int density = (int) context.getResources().getDisplayMetrics().density;
        return px / density;
    }

    public static int dp2px(Context context, int dp) {
        int density = (int) context.getResources().getDisplayMetrics().density;
        return dp * density;
    }

    public static void initBack(final Activity activity) {
        activity.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    public static void initBackTitle(TextView tvTitle, String strTitle) {
        tvTitle.setText(strTitle);
    }

    static public void setIconImage(BaseActivity context) {
        Window window = context.getWindow();
        window.requestFeature(Window.FEATURE_LEFT_ICON);
        window.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
    }

    public static int getCartNumber() {
        int cartNumber = 0;
        List<CartBean> cartBeen = FuLiCenterApplication.getInstance().getCartBeen();
        for (CartBean s : cartBeen) {
            cartNumber += s.getCount();
        }
        return cartNumber;
    }
}
