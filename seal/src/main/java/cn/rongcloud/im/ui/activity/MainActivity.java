package cn.rongcloud.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.im.R;
import cn.rongcloud.im.SealConst;
import cn.rongcloud.im.server.broadcast.BroadcastManager;
import cn.rongcloud.im.server.utils.NLog;
import cn.rongcloud.im.server.utils.NToast;
import cn.rongcloud.im.server.widget.LoadDialog;
import cn.rongcloud.im.ui.adapter.ConversationListAdapterEx;
import cn.rongcloud.im.ui.fragment.ContactsFragment;
import cn.rongcloud.im.ui.fragment.DiscoverFragment;
import cn.rongcloud.im.ui.fragment.MineFragment;
import cn.rongcloud.im.ui.widget.DragPointView;
import cn.rongcloud.im.ui.widget.MorePopWindow;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private List<Fragment> mFragment = new ArrayList<>();
    private ImageView moreImage, mImageChats, mImageContact, mImageFind, mImageMe, mMineRed;
    private TextView mTextChats, mTextContact, mTextFind, mTextMe;
    private DragPointView mUnreadNumView;
    /**
     * 会话列表的fragment
     */
    //private Fragment mConversationListFragment = null;
    private boolean isDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        isDebug = sp.getBoolean("isDebug", false);
        initViews();
        initMianViewPager();
        changeTextViewColor();
        changeSelectedTabState(0);
        if (RongIM.getInstance() != null && RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect();
                }
            }, 300);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seal_chat:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.seal_contact_list:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.seal_find:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.seal_me:
                mViewPager.setCurrentItem(3, false);
                mMineRed.setVisibility(View.GONE);
                break;
            case R.id.seal_more:
                MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
                morePopWindow.showPopupWindow(moreImage);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(mContext).destroy(SealConst.EXIT);
        BroadcastManager.getInstance(mContext).destroy(MineFragment.SHOWRED);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void connect() {
        String token = sp.getString("loginToken", "");
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
            }
            @Override
            public void onSuccess(String s) {
            }
            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        });
    }
    private void initViews() {
        RelativeLayout chatRLayout, contactRLayout, foundRLayout, mineRLayout;
        chatRLayout = (RelativeLayout) findViewById(R.id.seal_chat);
        contactRLayout = (RelativeLayout) findViewById(R.id.seal_contact_list);
        foundRLayout = (RelativeLayout) findViewById(R.id.seal_find);
        mineRLayout = (RelativeLayout) findViewById(R.id.seal_me);
        mImageChats = (ImageView) findViewById(R.id.tab_img_chats);
        mImageContact = (ImageView) findViewById(R.id.tab_img_contact);
        mImageFind = (ImageView) findViewById(R.id.tab_img_find);
        mImageMe = (ImageView) findViewById(R.id.tab_img_me);
        mTextChats = (TextView) findViewById(R.id.tab_text_chats);
        mTextContact = (TextView) findViewById(R.id.tab_text_contact);
        mTextFind = (TextView) findViewById(R.id.tab_text_find);
        mTextMe = (TextView) findViewById(R.id.tab_text_me);
        mMineRed = (ImageView) findViewById(R.id.mine_red);
        moreImage = (ImageView) findViewById(R.id.seal_more);
        chatRLayout.setOnClickListener(this);
        contactRLayout.setOnClickListener(this);
        foundRLayout.setOnClickListener(this);
        mineRLayout.setOnClickListener(this);
        moreImage.setOnClickListener(this);
    }
    private void initMianViewPager() {
        Fragment mConversationList;
        FragmentPagerAdapter mFragmentPagerAdapter; //将 tab  页面持久在内存中

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mUnreadNumView = (DragPointView) findViewById(R.id.seal_num);
        mUnreadNumView.setOnClickListener(this);
        mUnreadNumView.setDragListencer(new DragListencer());
        mConversationList = initConversationList();
        mFragment.add(mConversationList);
        mFragment.add(ContactsFragment.getInstance());
        mFragment.add(DiscoverFragment.getInstance());
        mFragment.add(MineFragment.getInstance());
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }
            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(new PageChangerListener());
        initData();
    }
    private void initData() {
        BroadcastManager.getInstance(mContext).addAction(MineFragment.SHOWRED, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMineRed.setVisibility(View.VISIBLE);
            }
        });
        BroadcastManager.getInstance(mContext).addAction(SealConst.EXIT, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editor.putBoolean("exit", true);
                editor.apply();
                RongIM.getInstance().logout();
                MainActivity.this.finish();
                try {
                    Thread.sleep(500);
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Conversation.ConversationType[] conversationTypes = {Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RongIM.getInstance().setOnReceiveUnreadCountChangedListener(mCountListener, conversationTypes);
            }
        }, 500);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }
    private Fragment initConversationList() {
        //if (mConversationListFragment == null) {
            ConversationListFragment listFragment = ConversationListFragment.getInstance();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            if (isDebug) {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                      .appendPath("conversationlist")
                      .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                      .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                      .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                      .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                      .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                      .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                      .build();
            } else {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                      .appendPath("conversationlist")
                      .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                      .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                      .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                      .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                      .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                      .build();
            }
            listFragment.setUri(uri);
            return listFragment;
        //} else {
        //    return mConversationListFragment;
        //}
    }
    private void changeTextViewColor() {
        mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat));
        mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts));
        mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_found));
        mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me));
        mTextChats.setTextColor(Color.parseColor("#abadbb"));
        mTextContact.setTextColor(Color.parseColor("#abadbb"));
        mTextFind.setTextColor(Color.parseColor("#abadbb"));
        mTextMe.setTextColor(Color.parseColor("#abadbb"));
    }
    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                mTextChats.setTextColor(Color.parseColor("#0099ff"));
                mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_chat_hover));
                break;
            case 1:
                mTextContact.setTextColor(Color.parseColor("#0099ff"));
                mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_contacts_hover));
                break;
            case 2:
                mTextFind.setTextColor(Color.parseColor("#0099ff"));
                mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_found_hover));
                break;
            case 3:
                mTextMe.setTextColor(Color.parseColor("#0099ff"));
                mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_me_hover));
                break;
        }
    }
    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");

            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {
                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                      .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }
    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                String cacheToken = sp.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                        LoadDialog.show(mContext);
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {

                            }

                            @Override
                            public void onSuccess(String s) {
                                LoadDialog.dismiss(mContext);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                    }
                }
            }
        }
    }
    public RongIM.OnReceiveUnreadCountChangedListener mCountListener = new RongIM.OnReceiveUnreadCountChangedListener() {
        @Override
        public void onMessageIncreased(int count) {
            if (count == 0) {
                mUnreadNumView.setVisibility(View.GONE);
            } else if (count > 0 && count < 100) {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(count + "");
            } else {
                mUnreadNumView.setVisibility(View.VISIBLE);
                mUnreadNumView.setText(R.string.no_read_message);
            }
        }
    };
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private class PageChangerListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            changeTextViewColor();
            changeSelectedTabState(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    private class DragListencer implements DragPointView.OnDragListencer{
        @Override
        public void onDragOut() {
            mUnreadNumView.setVisibility(View.GONE);
            NToast.shortToast(mContext, getString(R.string.clear_success));
            List<Conversation> conversations = RongIM.getInstance().getConversationList();
            if (conversations != null && conversations.size() > 0) {
                for (Conversation c : conversations) {
                    RongIM.getInstance().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId(), null);
                }
            }
        }
    }
}
