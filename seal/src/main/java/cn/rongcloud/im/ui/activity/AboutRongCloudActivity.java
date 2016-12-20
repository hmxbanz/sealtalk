package cn.rongcloud.im.ui.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.rongcloud.im.R;
import cn.rongcloud.im.SealConst;
import cn.rongcloud.im.server.UpdateService;
import cn.rongcloud.im.server.broadcast.BroadcastManager;
import cn.rongcloud.im.server.utils.CommonUtils;
import cn.rongcloud.im.server.utils.NToast;
import cn.rongcloud.im.server.widget.DialogWithYesOrNoUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2015/3/3.
 */
public class AboutRongCloudActivity extends BaseActionBarActivity implements View.OnClickListener {
    private TextView version,mSDKVersion;
    private boolean isHasNewVersion;
    private ImageView mNewVersionView;
    private String url;
    private RelativeLayout mCloseDebug;
    private RelativeLayout mstartDebug;
    private RelativeLayout mUpdateLog;
    private RelativeLayout mFunctionIntroduce;
    private RelativeLayout mRongCloudWeb;
    private RelativeLayout mVersionItem;
    long[] mHits = new long[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initViews();
        initData();
    }
    private void initViews() {
        mUpdateLog = (RelativeLayout) findViewById(R.id.rl_update_log);
        mFunctionIntroduce = (RelativeLayout) findViewById(R.id.rl_function_introduce);
        mRongCloudWeb = (RelativeLayout) findViewById(R.id.rl_rongcloud_web);
        mVersionItem = (RelativeLayout) findViewById(R.id.rl_version);
        mNewVersionView = (ImageView) findViewById(R.id.about_sealtalk_version);
        mSDKVersion = (TextView) findViewById(R.id.sdk_version_text);
        version = (TextView) findViewById(R.id.sealtalk_version);
        mstartDebug = (RelativeLayout) findViewById(R.id.start_debug);
        mCloseDebug = (RelativeLayout) findViewById(R.id.close_debug);
    }
    private void initData() {
        getSupportActionBar().setTitle(R.string.set_rongcloud);
        mUpdateLog.setOnClickListener(this);
        mFunctionIntroduce.setOnClickListener(this);
        mRongCloudWeb.setOnClickListener(this);
        mVersionItem.setOnClickListener(this);
        mstartDebug.setOnClickListener(this);
        mCloseDebug.setOnClickListener(this);
        version.setText(SealConst.SEALTALKVERSION);
        url = getIntent().getStringExtra("url");
        isHasNewVersion = getIntent().getBooleanExtra("isHasNewVersion", false);
        if (isHasNewVersion) {
            mNewVersionView.setVisibility(View.VISIBLE);
        }
        if (sp.getBoolean("isDebug", false)) {
            mCloseDebug.setVisibility(View.VISIBLE);
        }
        String[] versionInfo = cn.rongcloud.im.utils.CommonUtils.getVersionInfo(this);
        mSDKVersion.setText(versionInfo[1]);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_update_log:
                startActivity(new Intent(AboutRongCloudActivity.this, UpdateLogActivity.class));
                break;
            case R.id.rl_function_introduce:
                startActivity(new Intent(AboutRongCloudActivity.this, FunctionIntroducedActivity.class));
                break;
            case R.id.rl_rongcloud_web:
                startActivity(new Intent(AboutRongCloudActivity.this, RongWebActivity.class));
                break;
            case R.id.rl_version:
                mNewVersionView.setVisibility(View.GONE);
                final AlertDialog dlg = new AlertDialog.Builder(AboutRongCloudActivity.this).create();
                dlg.show();
                Window window = dlg.getWindow();
                window.setContentView(R.layout.dialog_download);
                TextView browserDownload_TextView = (TextView) window.findViewById(R.id.browserDownload_txt);
                TextView localDownload_TextView = (TextView) window.findViewById(R.id.localDownload_txt);
                browserDownload_TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(url);
                        intent.setData(content_url);
                        startActivity(intent);
                        dlg.cancel();
                    }
                });
                localDownload_TextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NToast.shortToast(mContext, getString(R.string.downloading_apk));
                        UpdateService.Builder.create(url)
                                .setStoreDir("update/flag")
                                .setDownloadSuccessNotificationFlag(Notification.DEFAULT_ALL)
                                .setDownloadErrorNotificationFlag(Notification.DEFAULT_ALL)
                                .build(mContext);
                        dlg.cancel();
                    }
                });
                isHasNewVersion = false;
                break;
            case R.id.start_debug:
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] > SystemClock.uptimeMillis() - 10000) {
                    if (sp.getBoolean("isDebug", false)) {
                        NToast.shortToast(mContext, "debug 模式已开启");
                    } else {
                        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否开启 App Debug 模式(需要重新登录应用)?", new DialogWithYesOrNoUtils.DialogCallBack() {
                            @Override
                            public void execEvent() {
                                editor.putBoolean("isDebug", true);
                                editor.apply();
                                BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.EXIT);
                            }

                            @Override
                            public void execEdit(String editText) {
                            }

                            @Override
                            public void execUpdatePassword(String oldPassword, String newPassword) {
                            }
                        });
                    }
                }
                break;
            case R.id.close_debug:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "是否关闭 App Debug 模式(需要重新登录应用)?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void execEvent() {
                        editor.putBoolean("isDebug", false);
                        editor.apply();
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.EXIT);
                    }

                    @Override
                    public void execEdit(String editText) {
                    }

                    @Override
                    public void execUpdatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;

        }

    }
}
