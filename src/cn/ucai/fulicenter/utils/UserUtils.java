package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.DemoHXSDKHelper;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.domain.User;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if (user == null) {
            user = new User(username);
        }

        if (user != null) {
            //demo没有这些数据，临时填充
            if (TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        setMyAvatar(context, username, imageView);
    }

    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * 设置Super当前用户头像
     */
    public static void setMyUserAvatar(Context context, ImageView imageView) {
        String userName = SuperWeChatApplication.getInstance().getUserName();
        setMyAvatar(context, userName, imageView);
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        User user = getUserInfo(username);
        if (user != null) {
            textView.setText(user.getNick());
        } else {
            textView.setText(username);
        }
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (textView != null) {
            textView.setText(user.getNick());
        }
    }

    /**
     * 保存或更新某个用户
     *
     * @param newUser
     */
    public static void saveUserInfo(User newUser) {
        if (newUser == null || newUser.getUsername() == null) {
            return;
        }
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
    }

    //设置用户昵称
    public static void setMyUserNick(String username, TextView nameTextView) {
        Map<String, UserAvatar> map = SuperWeChatApplication.getInstance().getMap();
        UserAvatar userAvatar = SuperWeChatApplication.getInstance().getMap().get(username.trim());
        if (userAvatar != null) {
            nameTextView.setText(userAvatar.getMUserNick());
            Log.i("main", "userUtils.setMyUserNick()用户名长度测试=" + username.length() + username + "Map.size()="
                    + map.size() + "昵称" + map.get(username));
        } else {
            nameTextView.setText(username);
        }
    }

    //设置添加好友的昵称
    public static void setMyNewUserNick(UserAvatar username, TextView nameTextView) {
        if (username != null) {
            nameTextView.setText(username.getMUserNick());
        } else {
            nameTextView.setText(username.getMUserName());

        }
    }


    //设置自己资料显示自己信息
    public static void setMyUserNick1(String username, TextView nameTextView) {
        UserAvatar userAvatar = SuperWeChatApplication.getInstance().getUser();
        if (userAvatar != null) {
            nameTextView.setText(userAvatar.getMUserNick());
        } else {
            nameTextView.setText(username);
        }
    }
    //显示本地服务器头像

    /**
     * 设置当前用户头像
     * ?request=download_avatar&name_or_hxid=&avatarType=
     */
    public static void setMyAvatar(Context context, String userName, ImageView imageView) {
        String path = I.SERVER_URL + "?request=download_avatar&name_or_hxid=" + userName + "&avatarType=user_avatar";
        if (userName.equals(SuperWeChatApplication.getInstance().getUserName())) {
            File file = new File(OnSetAvatarListener.getAvatarPath(context, I.AVATAR_TYPE_USER_PATH + "/" + userName + I.AVATAR_SUFFIX_JPG));
            if (!file.exists()) {
                Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
                Log.i("main", "=本地头像不存在正在下载");
                return;
            }
            Log.i("main", "UserUtills.setMyAvatar()个人头像下载成功及路径" + userName + file.getAbsolutePath() + "\n头像下载链接=" + path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
        }
    }

    //下载群头像
    public static void setMyGroupAvatar(Context mContext, String groupId, ImageView viewById) {
        String path = I.SERVER_URL + "?request=download_avatar&name_or_hxid=" + groupId + "&avatarType=" + I.AVATAR_TYPE_GROUP_PATH;
        Log.i("main", "UserUtills.setMyAvatar()个人头像下载成功及路径" + "\n群头像下载链接=" + path);
        Picasso.with(mContext).load(path).placeholder(R.drawable.group_icon).into(viewById);
    }

    /**
     * 得到MemberUserAvatar
     */
    public static MemberUserAvatar getMember(String hxId, String username) {
        MemberUserAvatar member = null;
        HashMap<String, MemberUserAvatar> memberMap = SuperWeChatApplication.getInstance().getMemberMap().get(username);
        if (memberMap == null) {
            return null;
        } else {
            member = memberMap.get(hxId);
            Log.i("main", "2=" + memberMap.toString());
        }
        return member;
    }

    /**
     * 设置群聊天列表好友昵称显示
     */
    public static void setGroupUserNick(String userName, String hxId, TextView textView) {
        MemberUserAvatar member = getMember(hxId, userName);
        if (member != null && member.getMUserNick() != null) {
            textView.setText(member.getMUserNick());
            Log.i("main", "UserUtils.setGroupUserNick()群消息列表用户昵称=" + member.getMUserNick());
        } else {
            textView.setText(userName);
            Log.i("main", "UserUtils.setGroupUserNick()群消息列表用户昵称=");

        }
    }

}


