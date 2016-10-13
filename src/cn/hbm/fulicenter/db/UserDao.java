/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hbm.fulicenter.db;

import java.util.List;
import java.util.Map;

import android.content.Context;


import cn.hbm.fulicenter.bean.UserAvatar;
import cn.hbm.fulicenter.domain.RobotUser;
import cn.hbm.fulicenter.domain.User;

public class UserDao {
    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public static final String PREF_TABLE_NAME = "pref";
    public static final String COLUMN_NAME_DISABLED_GROUPS = "disabled_groups";
    public static final String COLUMN_NAME_DISABLED_IDS = "disabled_ids";

    public static final String ROBOT_TABLE_NAME = "robots";
    public static final String ROBOT_COLUMN_NAME_ID = "username";
    public static final String ROBOT_COLUMN_NAME_NICK = "nick";
    public static final String ROBOT_COLUMN_NAME_AVATAR = "avatar";

    //SuperWeChat SQL变量
    public static final String SUPER_TABLE_NAME = "t_superwechat_user";
    public static final String SUPER_USER_ID = "muserName";
    public static final String SUPER_NAME_NICK = "muserNick";
    public static final String SUPER_NAME_AVATAR = "mavatarId";
    public static final String SUPER_NAME_AVATAR_PATH = "mavatarPath";
    public static final String SUPER_NAME_AVATAR_TYPE = "mavatarType";
    public static final String SUPER_NAME_AVATAR_TIME = "mavatarLastUpdateTime";


    public UserDao(Context context) {
        DemoDBManager.getInstance().onInit(context);
    }

    /**
     * 保存好友list
     *
     * @param contactList
     */
    public void saveContactList(List<User> contactList) {
        DemoDBManager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, User> getContactList() {

        return DemoDBManager.getInstance().getContactList();
    }

    /**
     * 删除一个联系人
     * @param username
     */
    public void deleteContact(String username) {
        DemoDBManager.getInstance().deleteContact(username);
    }

    /**
     * 保存一个联系人
     * @param user
     */
    public void saveContact(User user) {
        DemoDBManager.getInstance().saveContact(user);
    }

    public void setDisabledGroups(List<String> groups) {
        DemoDBManager.getInstance().setDisabledGroups(groups);
    }

    public List<String> getDisabledGroups() {
        return DemoDBManager.getInstance().getDisabledGroups();
    }

    public void setDisabledIds(List<String> ids) {
        DemoDBManager.getInstance().setDisabledIds(ids);
    }

    public List<String> getDisabledIds() {
        return DemoDBManager.getInstance().getDisabledIds();
    }

    public Map<String, RobotUser> getRobotUser() {
        return DemoDBManager.getInstance().getRobotList();
    }

    public void saveRobotUser(List<RobotUser> robotList) {
        DemoDBManager.getInstance().saveRobotList(robotList);
    }
    //

    /**
     * @param 保存SuperWechat的用户信息
     */
    public  void saveSuperData(UserAvatar user) {
        DemoDBManager.getInstance().saveSuperData(user);
    }

    public UserAvatar getDBUserInfo(String userName) {
        return DemoDBManager.getInstance().getDBUserInfo(userName);

    }
}
