package cn.rongcloud.im.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.rongcloud.im.App;
import cn.rongcloud.im.R;
import cn.rongcloud.im.db.Groups;
import cn.rongcloud.im.server.utils.RongGenerate;
import cn.rongcloud.im.server.widget.SelectableRoundedImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by hmxbanz on 2016/12/20.
 */

public class GroupAdapter extends BaseAdapter {
    private Context context;
    private List<Groups> list;

    public GroupAdapter(Context context, List<Groups> list) {
        super(context);
        this.context = context;
        this.list = list;
    }

    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<Groups> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Groups mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item_new, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.groupname);
            viewHolder.mImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.groupuri);
            viewHolder.groupChat = (Button) convertView.findViewById(R.id.group_chat);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvTitle.setText(mContent.getName());
        if (TextUtils.isEmpty(mContent.getPortraitUri())) {
            ImageLoader.getInstance().displayImage(RongGenerate.generateDefaultAvatar(mContent.getName(), mContent.getQunId()), viewHolder.mImageView, App.getOptions());
        } else {
            ImageLoader.getInstance().displayImage(mContent.getPortraitUri(), viewHolder.mImageView, App.getOptions());
        }
        viewHolder.groupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.GROUP, mContent.getQunId(), mContent.getName());
            }
        });
        return convertView;
    }


    class ViewHolder {
        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 头像
         */
        SelectableRoundedImageView mImageView;
        /**
         * userid
         */
        Button groupChat;
    }
}
