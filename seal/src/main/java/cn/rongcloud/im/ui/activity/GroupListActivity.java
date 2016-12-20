package cn.rongcloud.im.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.rongcloud.im.App;
import cn.rongcloud.im.R;
import cn.rongcloud.im.db.DBManager;
import cn.rongcloud.im.db.Groups;
import cn.rongcloud.im.server.SealAction;
import cn.rongcloud.im.server.network.async.AsyncTaskManager;
import cn.rongcloud.im.server.network.async.OnDataListener;
import cn.rongcloud.im.server.network.http.HttpException;
import cn.rongcloud.im.server.response.GetBlackListResponse;
import cn.rongcloud.im.server.response.GetGroupResponse;
import cn.rongcloud.im.server.utils.NToast;
import cn.rongcloud.im.server.utils.RongGenerate;
import cn.rongcloud.im.server.widget.LoadDialog;
import cn.rongcloud.im.server.widget.SelectableRoundedImageView;
import cn.rongcloud.im.ui.adapter.BlackListAdapter;
import cn.rongcloud.im.ui.adapter.GroupAdapter;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by AMing on 16/3/8.
 * Company RongCloud
 */
public class GroupListActivity extends BaseActionBarActivity {

    private static final int REFRESHGROUPUI = 22;
    private ListView mGroupListView;
    private GroupAdapter adapter;
    private TextView mNoGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fr_group_list);
        getSupportActionBar().setTitle(R.string.my_groups);

        mGroupListView = (ListView) findViewById(R.id.group_listview);
        mNoGroups = (TextView) findViewById(R.id.show_no_group);
        initData();
        requestData();
    }

    private void requestData() {
        LoadDialog.show(mContext);
        request(REFRESHGROUPUI);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return action.getGroups();
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetGroupResponse response = (GetGroupResponse) result;
            if (response.getCode() == 200) {
                LoadDialog.dismiss(mContext);
                if (response.getResult().size() != DBManager.getInstance(mContext).getDaoSession().getGroupsDao().loadAll().size()) {
                    DBManager.getInstance(mContext).getDaoSession().getGroupsDao().deleteAll();
                    List<GetGroupResponse.ResultEntity> list = response.getResult();
                    if (list.size() > 0 && list != null) { //服务端上也没有群组数据
                        for (GetGroupResponse.ResultEntity g : list) {
                            DBManager.getInstance(mContext).getDaoSession().getGroupsDao().insertOrReplace(
                                    new Groups(g.getGroup().getId(), g.getGroup().getName(), g.getGroup().getPortraitUri(), String.valueOf(g.getRole()))
                            );
                        }
                    }
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Groups> list = DBManager.getInstance(mContext).getDaoSession().getGroupsDao().loadAll();
                            if (adapter != null) {
                                adapter.updateListView(list);
                            } else {
                                GroupAdapter gAdapter = new GroupAdapter(mContext, list);
                                mGroupListView.setAdapter(gAdapter);
                            }
                        }
                    }, 500);
                }
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        NToast.shortToast(mContext, "刷新群组数据请求失败");
    }

//    private void initNetUpdateUI() {
//        AsyncTaskManager.getInstance(mContext).request(REFRESHGROUPUI, new OnDataListener() {
//            @Override
//            public Object doInBackground(int requestCode, String id) throws HttpException {
//                return new SealAction(mContext).getGroups();
//            }
//        });
//    }

    private void initData() {
        List<Groups> list = DBManager.getInstance(mContext).getDaoSession().getGroupsDao().loadAll();
        if (list != null && list.size() > 0) {
            adapter = new GroupAdapter(mContext, list);
            mGroupListView.setAdapter(adapter);
            mNoGroups.setVisibility(View.GONE);
            mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(mContext, NewGroupDetailActivity.class);
//                    intent.putExtra("QunBean", (Serializable) adapter.getItem(position));
//                    startActivityForResult(intent, 99);
                    Groups bean = (Groups) adapter.getItem(position);
                    RongIM.getInstance().startGroupChat(GroupListActivity.this, bean.getQunId(), bean.getName());
                }
            });
        } else {
            mNoGroups.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
