package com.qingwing.safebox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class SettingAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mListItems = new ArrayList<String>();

    public SettingAdapter(Context context) {
        mContext = context;
    }


    public void setData(List<String> aList) {
        this.mListItems = aList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mListItems == null || mListItems.isEmpty()) {
            return 0;
        }
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_setting_item, null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.setting_item_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String content = mListItems.get(position);
        if (!UserInfo.UserBindState && (position == 1 || position == 2)) {
            viewHolder.text1.setTextColor(mContext.getResources().getColor(R.color.textcolor_gray));
            viewHolder.text1.setText(content);
        } else {
            viewHolder.text1.setTextColor(mContext.getResources().getColor(R.color.textcolor_black));
            viewHolder.text1.setText(content);
        }
        return convertView;
    }

    class ViewHolder {
        TextView text1;
    }
}
