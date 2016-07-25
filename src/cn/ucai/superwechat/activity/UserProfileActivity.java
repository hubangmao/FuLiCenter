package cn.ucai.superwechat.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMValueCallBack;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;

import com.easemob.chat.EMChatManager;

import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.db.DemoDBManager;
import cn.ucai.superwechat.domain.User;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.UserUtils;

import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private ImageView headAvatar;
    private ImageView headPhotoUpdate;
    private ImageView iconRightArrow;
    private TextView tvNickName;
    private TextView tvUsername;
    public static ProgressDialog dialog;
    private RelativeLayout rlNickName;
    public OnSetAvatarListener mSetAvatar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_user_profile);
        initView();
        initListener();
    }

    private void initView() {
        headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
        headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
        tvUsername = (TextView) findViewById(R.id.user_username);
        tvNickName = (TextView) findViewById(R.id.user_nickname);
        rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
        iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);

    }

    //显示好友信息

    private void initListener() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        boolean enableUpdate = intent.getBooleanExtra("setting", false);
        if (enableUpdate) {
            headPhotoUpdate.setVisibility(View.VISIBLE);
            iconRightArrow.setVisibility(View.VISIBLE);
            rlNickName.setOnClickListener(this);
            headAvatar.setOnClickListener(this);
        } else {
            headPhotoUpdate.setVisibility(View.GONE);
            iconRightArrow.setVisibility(View.INVISIBLE);
        }

        if (username == null || username.equals(EMChatManager.getInstance().getCurrentUser())) {
            Log.i("main", "UserProfileActivity.initListener()设置个人资料" + username);
            username = SuperWeChatApplication.getInstance().getUserName();
            tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
            UserUtils.setMyAvatar(UserProfileActivity.this, username, headAvatar);
            UserUtils.setMyUserNick1(username, tvNickName);
        } else {
            tvUsername.setText(username);
            UserUtils.setMyUserNick(username, tvNickName);
            UserUtils.setMyAvatar(this, username, headAvatar);
//			asyncFetchUserInfo(username);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_avatar:
                //更改用户头像
                uploadHeadPhoto();
                break;
            //更新用户昵称
            case R.id.rl_nickname:
                final EditText editText = new EditText(UserProfileActivity.this);
                new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    SuperWeChatApplication.mMyUtils.toastResources(UserProfileActivity.this, R.string.toast_nick_not_isnull);
                                    return;
                                }
                                UserProfileActivity.dialog = ProgressDialog.show(UserProfileActivity.this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
                                //更新本地服务器昵称
                                updateSuperUserNick(nickString);
                                //更新环信昵称
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;

        }
    }


    //更新本地用户昵称
    private void updateSuperUserNick(final String nickString) {
        String updateNickUrl = I.SERVER_URL + "?request=update_nick&m_user_name="
                + SuperWeChatApplication.getInstance().getUserName() + "&m_user_nick=" + nickString;
        OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
        utils2.url(updateNickUrl)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            dialog.dismiss();
                            //更新全局变量用户昵称数据
                            UserAvatar user = SuperWeChatApplication.getInstance().getUser();
                            user.setMUserNick(nickString);
                            SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "本地服务器更新昵称成功=" + user.getMUserNick());
                            //更新数据库数据
                            DemoDBManager.getInstance().updateDBInfo(nickString);
                        } else {
                            dialog.dismiss();
                            SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "本地服务器更新昵称失败");
                        }

                    }

                    @Override
                    public void onError(String error) {
                        dialog.dismiss();
                        SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "网络错误");
                    }
                });

    }

    private void uploadHeadPhoto() {
        //打开popupWindows选择图片
        mSetAvatar = new OnSetAvatarListener(UserProfileActivity.this,R.id.linearPopupWindow, SuperWeChatApplication.getInstance().getUserName().trim(), I.AVATAR_TYPE_USER_PATH);


      /*  AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("main", "requestCode=" + requestCode + "]resultCode=" + resultCode);
        if (resultCode != RESULT_OK) {
            return;
        }
        mSetAvatar.setAvatar(requestCode, data, headAvatar);//得到数据后传入setAvatar()方法
        //更新本地服务器头像
        if (requestCode == 3) {
            updateUserAvatarData(SuperWeChatApplication.getInstance().getUserName());
        }

      /*  switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            //
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;

        }*/
    }

    //更新本地服务器头像
    private void updateUserAvatarData(String userName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        String updateAvatarUrl = I.SERVER_URL + "?request=upload_avatar&avatarType=" + I.AVATAR_TYPE_USER_PATH + "&name_or_hxid=" + userName;
        final File file = new File(OnSetAvatarListener.getAvatarPath(UserProfileActivity.this, I.AVATAR_TYPE_USER_PATH),userName + I.AVATAR_SUFFIX_JPG);
        Log.i("main", "头像路径" + file.getAbsolutePath());
        OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
        utils2.url(updateAvatarUrl)
                .targetClass(Result.class)
                .addFile(file)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            dialog.dismiss();
                            SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "头像修改成功");
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            headAvatar.setImageBitmap(bitmap);

                        } else {
                            dialog.dismiss();
                            SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "头像修改失败");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        dialog.dismiss();
                        SuperWeChatApplication.mMyUtils.toast(UserProfileActivity.this, "服务器异常");
                    }
                });


    }

    //????????????????????????????????????????????????研究这段代码
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            headAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String avatarUrl = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().uploadUserAvatar(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        if (avatarUrl != null) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        }).start();

        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //更新环信昵称
    private void updateRemoteNick(final String nickName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean updatenick = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().updateParseNickName(nickName);
                if (UserProfileActivity.this.isFinishing()) {
                    return;
                }
                if (!updatenick) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                                    .show();
                            dialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                                    .show();
                            tvNickName.setText(nickName);
                        }
                    });
                }
            }
        }).start();
    }

    public void asyncFetchUserInfo(String username) {
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null) {
                    tvNickName.setText(user.getNick());
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        Picasso.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(headAvatar);
                    } else {
                        Picasso.with(UserProfileActivity.this).load(R.drawable.default_avatar).into(headAvatar);
                    }
                    UserUtils.saveUserInfo(user);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
            }
        });
    }
}
