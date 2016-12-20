package cn.rongcloud.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.rongcloud.im.R;
import cn.rongcloud.im.server.network.http.HttpException;
import cn.rongcloud.im.server.response.GetBlackListResponse;
import cn.rongcloud.im.server.widget.LoadDialog;
import cn.rongcloud.im.ui.adapter.BlackListAdapter;

/**
 * Created by Bob on 2015/4/9.
 */
public class BlackListActivity extends BaseActionBarActivity {

    private static final int GETBLACKLIST = 66;
    private String TAG = BlackListActivity.class.getSimpleName();
    private TextView isShowData;
    private ListView blackList;
    private List<GetBlackListResponse.ResultEntity> dataList;
    private BlackListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        requestData();
    }

    private void initView() {
        setContentView(R.layout.fragment_black);
        getSupportActionBar().setTitle(R.string.the_blacklist);
        isShowData = (TextView) findViewById(R.id.blacklsit_show_data);
        blackList = (ListView) findViewById(R.id.blacklsit_list);
    }

    private void requestData() {
        LoadDialog.show(mContext);
        request(GETBLACKLIST);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return action.getBlackList();
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            GetBlackListResponse response = (GetBlackListResponse) result;
            if (response.getCode() == 200) {
                LoadDialog.dismiss(mContext);
                dataList =  response.getResult();
                if (dataList != null) {
                    if (dataList.size() > 0) {
                        adapter = new BlackListAdapter(this,dataList);
                        blackList.setAdapter(adapter);
                    } else {
                        isShowData.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }

}
