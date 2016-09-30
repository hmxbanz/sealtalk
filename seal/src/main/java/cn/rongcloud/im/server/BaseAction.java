package cn.rongcloud.im.server;

import android.content.Context;

import cn.rongcloud.im.server.network.http.SyncHttpClient;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public class BaseAction {

    private static final String DOMAIN = "http://api.sealtalk.im/";
    protected Context mContext;
    protected SyncHttpClient httpManager;

    /**
     * 构造方法
     * @param context
     */
    public BaseAction(Context context) {
        this.mContext = context;
        this.httpManager = SyncHttpClient.getInstance(context);
    }

    /**
     * 获取完整URL方法
     * @param url
     * @return
     */
    protected String getURL(String url) {
        return getURL(url, new String[] {});
    }

    /**
     * 获取完整URL方法
     * @param url
     * @param params
     * @return
     */
    protected String getURL(String url, String... params) {
        StringBuilder urlBilder = new StringBuilder(DOMAIN).append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBilder.toString().endsWith("/")) {
                    urlBilder.append("/");
                }
                urlBilder.append(param);
            }
        }
        return urlBilder.toString();
    }
}
