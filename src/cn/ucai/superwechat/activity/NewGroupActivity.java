package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;
import java.util.Random;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.I;

public class NewGroupActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = NewGroupActivity.class.getSimpleName();
    private EditText groupNameEditText;
    private EditText introductionEditText;
    private ImageView mIvGroupAvatar;

    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;

    private OnSetAvatarListener mAvatarListener;
    private String mGroupId;
    public String mTime;
    public static final boolean IS_PUBLIC = true;
    public static final boolean IS_PRIVATE = true;
    public static final int USER_REGISTER = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();
        setListener();

    }

    private void setListener() {
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        mIvGroupAvatar.setOnClickListener(this);
        findViewById(R.id.tv_Group_Avatar).setOnClickListener(this);

    }

    private void initView() {
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
        mIvGroupAvatar = (ImageView) findViewById(R.id.iv_Group_Avatar);
        mGroupId = "123";

    }

    /**
     * 点击保存 从这里开始
     */
    public void save(View v) {
        mTime = System.currentTimeMillis() + "";
        String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Intent intent = new Intent(this, AlertDialog.class);
            intent.putExtra("msg", str6);
            startActivity(intent);
        } else {
            // 进通讯录选人
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), USER_REGISTER);
        }
    }

    int i;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        if (resultCode != RESULT_OK) {
            return;
        }

        //拿到图片
        mAvatarListener.setAvatar(requestCode, data, mIvGroupAvatar);
        if (requestCode == OnSetAvatarListener.REQUEST_CROP_PHOTO) {
            i = OnSetAvatarListener.REQUEST_CROP_PHOTO;
            Log.i("main", TAG + "剪切图片成功" + resultCode);

        }
        if (requestCode == USER_REGISTER && i == 3) {
            //新建群组
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(st1);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 调用sdk创建群组方法
                    String groupName = groupNameEditText.getText().toString().trim();
                    String desc = introductionEditText.getText().toString();
                    String[] members = data.getStringArrayExtra("newmembers");
                    try {
                        EMGroup emGroup;
                        if (checkBox.isChecked()) {
                            //创建公开群，此种方式创建的群，可以自由加入
                            //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                            emGroup = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                            mGroupId = emGroup.getGroupId();
                            //创建本地服务器公开群
                            CreateIsPublicOrPrivateGroup(IS_PUBLIC, groupName, desc);
                        } else {
                            //创建不公开群
                            emGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                            mGroupId = emGroup.getGroupId();
                            //创建本地服务器不公开群
                            CreateIsPublicOrPrivateGroup(IS_PRIVATE, groupName, desc);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } catch (final EaseMobException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }).start();
        } else {
            SuperWeChatApplication.mMyUtils.toast(NewGroupActivity.this, "亲选择一张图片吧");
        }

    }

    //?request=create_group&m_group_hxid=&m_group_name=&m_group_description=
    // &m_group_owner=&m_group_is_public=&m_group_allow_invites=
    private void CreateIsPublicOrPrivateGroup(boolean isPublic, String groupName, String groupAbout) {
        SuperWeChatApplication.mMyUtils.toast(NewGroupActivity.this, "本地服务器群组创建");
        File file = new File(OnSetAvatarListener.getAvatarPath(NewGroupActivity.this, I.AVATAR_TYPE_GROUP_PATH), mGroupId + I.AVATAR_SUFFIX_JPG);
        String userName = SuperWeChatApplication.getInstance().getUserName();
        String strUrl = I.SERVER_URL + "?request=create_group"
                + "&m_group_hxid=" + mGroupId
                + "&m_group_name=" + groupName
                + "&m_group_description=" + groupAbout
                + "&m_group_owner=" + userName;
        if (isPublic) {
            strUrl += "&m_group_is_public=" + isPublic + "&m_group_allow_invites=" + isPublic;
            superGroupCreateRegister(file, strUrl);
        } else {
            strUrl += "&m_group_is_public=" + isPublic + "&m_group_allow_invites=" + isPublic;
            superGroupCreateRegister(file, strUrl);

        }

    }

    //本地服务器新建群
    private void superGroupCreateRegister(File file, String strUrl) {

    }

    public void back(View view) {
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_Group_Avatar:
                mAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.linearNewGroup
                        , mGroupId, I.AVATAR_TYPE_GROUP_PATH);
                break;
            case R.id.iv_Group_Avatar:
                mAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.linearNewGroup
                        , mGroupId, I.AVATAR_TYPE_GROUP_PATH);
                break;

        }
    }
}
