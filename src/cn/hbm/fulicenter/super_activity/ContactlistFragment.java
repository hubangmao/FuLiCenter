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
package cn.hbm.fulicenter.super_activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.hbm.fulicenter.Constant;
import cn.hbm.fulicenter.DemoHXSDKHelper;
import cn.hbm.fulicenter.R;
import cn.hbm.fulicenter.FuLiCenterApplication;
import cn.hbm.fulicenter.super_activity.adapter.ContactAdapter;
import cn.hbm.fulicenter.applib.controller.HXSDKHelper;
import cn.hbm.fulicenter.applib.controller.HXSDKHelper.HXSyncListener;
import cn.hbm.fulicenter.bean.Result;
import cn.hbm.fulicenter.bean.UserAvatar;
import cn.hbm.fulicenter.data.OkHttpUtils2;
import cn.hbm.fulicenter.db.InviteMessgeDao;
import cn.hbm.fulicenter.db.UserDao;
import cn.hbm.fulicenter.domain.User;
import cn.hbm.fulicenter.utils.I;
import cn.hbm.fulicenter.utils.Utils;
import cn.hbm.fulicenter.widget.Sidebar;

/**
 * 联系人列表页
 */
public class ContactlistFragment extends Fragment {
    public static final String TAG = "ContactlistFragment";
    private ContactAdapter adapter;
    private List<User> contactList;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    ImageButton clearSearch;
    Context mContext;
    EditText query;
    HXContactSyncListener contactSyncListener;
    HXBlackListSyncListener blackListSyncListener;
    HXContactInfoSyncListener contactInfoSyncListener;
    View progressBar;
    Handler handler = new Handler();
    private User toBeProcessUser;
    private String toBeProcessUsername;

    class HXContactSyncListener implements HXSDKHelper.HXSyncListener {
        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            ContactlistFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (success) {
                                progressBar.setVisibility(View.GONE);
                                refresh();
                            } else {
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            });
        }
    }

    class HXBlackListSyncListener implements HXSyncListener {

        @Override
        public void onSyncSucess(boolean success) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    blackList = EMContactManager.getInstance().getBlackListUsernames();
                    refresh();
                }

            });
        }

    }

    class HXContactInfoSyncListener implements HXSDKHelper.HXSyncListener {

        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        refresh();
                    }
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        updateContactListener();//创建广播设置好友请求昵称
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (ListView) getView().findViewById(R.id.list);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
        sidebar.setListView(listView);

        //黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();

        //搜索框
        query = (EditText) getView().findViewById(R.id.query);
        query.setHint(R.string.search);
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        // 设置adapter
        adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = adapter.getItem(position).getUsername();
                if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
                    // 进入申请与通知页面
                    User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
                    user.setUnreadMsgCount(0);
                    startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                } else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUsername()));
                }
            }
        });
        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        ImageView addContactView = (ImageView) getView().findViewById(R.id.iv_new_contact);
        // 进入添加好友页
        addContactView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });
        registerForContextMenu(listView);

        progressBar = (View) getView().findViewById(R.id.progress_bar);

        contactSyncListener = new HXContactSyncListener();
        HXSDKHelper.getInstance().addSyncContactListener(contactSyncListener);

        blackListSyncListener = new HXBlackListSyncListener();
        HXSDKHelper.getInstance().addSyncBlackListListener(blackListSyncListener);

        contactInfoSyncListener = new HXContactInfoSyncListener();
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);

        if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((AdapterContextMenuInfo) menuInfo).position > 1) {
            toBeProcessUser = adapter.getItem(((AdapterContextMenuInfo) menuInfo).position);
            toBeProcessUsername = toBeProcessUser.getUsername();
            getActivity().getMenuInflater().inflate(R.menu.context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            try {
                // 删除此联系人
                deleteContact(toBeProcessUser);
                // 删除相关的邀请消息
                InviteMessgeDao dao = new InviteMessgeDao(getActivity());
                dao.deleteMessage(toBeProcessUser.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else if (item.getItemId() == R.id.add_to_blacklist) {
            moveToBlacklist(toBeProcessUsername);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    //本地服务端好友
    private void deleteMyContact(User toBeProcessUser) {
        final String userName = toBeProcessUser.getUsername();
        String userName1 = FuLiCenterApplication.getInstance().getUserName();
        Log.i("main", "用户账号=" + userName + "当前登录账号=" + userName1);
        final String strUrl = I.SERVER_URL + "?request=delete_contact&m_contact_user_name=" + userName1 + "&m_contact_cname=" + userName;
        OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
        utils2.url(strUrl)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            Map<String, UserAvatar> map = FuLiCenterApplication.getInstance().getMap();
                            map.remove(userName);
                            FuLiCenterApplication.getInstance().getUserList().remove(map.get(userName));
                            //更新好友列表适配器数据
                            mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                            Log.i("main", "本地服务器删除好友成功" + result.isRetMsg() + strUrl);
                            Utils.toast(mContext, "本地服务器删除好友成功");
                        } else {
                            Utils.toast(mContext, "本地服务器删除失败稍后再试");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Utils.toast(mContext, "网络错误");
                    }
                });


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    /**
     * 删除联系人
     *
     * @param tobeDeleteUser
     */
    public void deleteContact(final User tobeDeleteUser) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
                    //删除本地服务端好友
                    deleteMyContact(toBeProcessUser);
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getActivity());
                    dao.deleteContact(tobeDeleteUser.getUsername());
                    ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().remove(tobeDeleteUser.getUsername());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (contactSyncListener != null) {
            HXSDKHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }

        if (blackListSyncListener != null) {
            HXSDKHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }

        if (contactInfoSyncListener != null) {
            ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
        super.onDestroy();
    }

    public void showProgressBar(boolean show) {
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();
        //获取本地好友列表
        Map<String, User> users = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME)
                    && !entry.getKey().equals(Constant.GROUP_USERNAME)
                    && !entry.getKey().equals(Constant.CHAT_ROOM)
                    && !entry.getKey().equals(Constant.CHAT_ROBOT)
                    && !blackList.contains(entry.getKey()))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUsername().compareTo(rhs.getUsername());
            }
        });

        if (users.get(Constant.CHAT_ROBOT) != null) {
            contactList.add(0, users.get(Constant.CHAT_ROBOT));
        }
        // 加入"群聊"和"聊天室"
//        if(users.get(Constant.CHAT_ROOM) != null)
//            contactList.add(0, users.get(Constant.CHAT_ROOM));

        if (users.get(Constant.GROUP_USERNAME) != null)
            contactList.add(0, users.get(Constant.GROUP_USERNAME));

        // 把"申请与通知"添加到首位
        if (users.get(Constant.NEW_FRIENDS_USERNAME) != null)
            contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));

    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }

    }

    class UpDateContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.notifyDataSetChanged();
        }

    }

    UpDateContactReceiver mUpdateContactReceiver;

    private void updateContactListener() {
        mUpdateContactReceiver = new UpDateContactReceiver();
        getActivity().registerReceiver(mUpdateContactReceiver, new IntentFilter("update_contact_list"));
        Log.i("main", "广播接收者测试" + ContactlistFragment.class.getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mUpdateContactReceiver);
    }
}
