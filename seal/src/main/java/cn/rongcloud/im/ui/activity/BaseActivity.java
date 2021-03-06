package cn.rongcloud.im.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import java.util.concurrent.Callable;

import cn.rongcloud.im.R;
import cn.rongcloud.im.server.SealAction;
import cn.rongcloud.im.server.broadcast.BroadcastManager;
import cn.rongcloud.im.server.network.async.AsyncTaskManager;
import cn.rongcloud.im.server.network.async.OnDataListener;
import cn.rongcloud.im.server.network.http.HttpException;
import cn.rongcloud.im.server.utils.NToast;

public abstract class BaseActivity extends ActionBarActivity implements OnDataListener {

    protected Context mContext;
    private AsyncTaskManager mAsyncTaskManager;
    protected SealAction action;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.de_actionbar_back);

        mAsyncTaskManager = AsyncTaskManager.getInstance(mContext);
        // Activity管理
        action = new SealAction(mContext);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();

    }
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 发送请求（需要检查网络）
     *
     * @param requestCode 请求码
     */
    public void request(int requestCode) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, this);
        }
    }
    /**
     * 发送请求
     *
     * @param requestCode    请求码
     * @param isCheckNetwork 是否需检查网络，true检查，false不检查
     */
    public void request(int requestCode, boolean isCheckNetwork) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, isCheckNetwork, this);
        }
    }
    /**
     * 取消所有请求
     */
    public void cancelRequest() {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.cancelRequest();
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        return null;
    }
    @Override
    public void onSuccess(int requestCode, Object result) {

    }
    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (state) {
            // 网络不可用给出提示
            case AsyncTaskManager.HTTP_NULL_CODE:
                NToast.shortToast(mContext, "当前网络不可用");
                break;

            // 网络有问题给出提示
            case AsyncTaskManager.HTTP_ERROR_CODE:
                NToast.shortToast(mContext, "网络问题请稍后重试");
                break;

            // 请求有问题给出提示
            case AsyncTaskManager.REQUEST_ERROR_CODE:
                // NToast.shortToast(mContext, R.string.common_request_error);
                break;
        }
    }


}
