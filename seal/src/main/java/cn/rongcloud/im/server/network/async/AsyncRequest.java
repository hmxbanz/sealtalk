/*
    ShengDao Android Client, DownLoad
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package cn.rongcloud.im.server.network.async;

public class AsyncRequest {

    private int requestCode;           /**     * 请求id     */
    private boolean isCheckNetwork;   /**     * 是否检查网络，true表示检查，false表示不检查     */
    private OnDataListener listener; /**     * 处理监听     */
    private String id;

    public AsyncRequest() {
        super();
    }
    public AsyncRequest(int requestCode, boolean isCheckNetwork, OnDataListener listener) {
        this.requestCode = requestCode;
        this.isCheckNetwork = isCheckNetwork;
        this.listener = listener;
    }
    public AsyncRequest(String id, int requestCode, boolean isCheckNetwork, OnDataListener listener) {
        this.requestCode = requestCode;
        this.isCheckNetwork = isCheckNetwork;
        this.listener = listener;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public int getRequestCode() {
        return requestCode;
    }
    public boolean isCheckNetwork() {
        return isCheckNetwork;
    }
    public OnDataListener getListener() {
        return listener;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
    public void setCheckNetwork(boolean isCheckNetwork) {
        this.isCheckNetwork = isCheckNetwork;
    }
    public void setListener(OnDataListener listener) {
        this.listener = listener;
    }
}
