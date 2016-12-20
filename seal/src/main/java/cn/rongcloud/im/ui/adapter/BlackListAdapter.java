package cn.rongcloud.im.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.rongcloud.im.App;
import cn.rongcloud.im.R;
import cn.rongcloud.im.server.response.GetBlackListResponse;

/**
 * Created by hmxbanz on 2016/12/19.
 */

public class BlackListAdapter extends BaseAdapter {
    private List<GetBlackListResponse.ResultEntity> dataList;
    public BlackListAdapter(Context context, List<GetBlackListResponse.ResultEntity> dataList) {
        super(context);
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler viewHolder = null;
        GetBlackListResponse.ResultEntity bean = dataList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHoler();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.black_item_new, null);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.blackname);
            viewHolder.mHead = (ImageView) convertView.findViewById(R.id.blackuri);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHoler) convertView.getTag();
        }
        viewHolder.mName.setText(bean.getUser().getNickname());
        ImageLoader.getInstance().displayImage(bean.getUser().getPortraitUri(), viewHolder.mHead, App.getOptions());
        return convertView;
    }


    class ViewHoler {
        ImageView mHead;
        TextView mName;
    }
}




