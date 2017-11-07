package com.qingwing.safebox.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwing.safebox.R;

import java.util.Vector;

public class MsgRecordHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private Vector<String> mListItems = new Vector<String>();

    public MsgRecordHistoryAdapter(Context context) {
        mContext = context;
    }

    public void addData(String data) {
        mListItems.insertElementAt(data, 0);
        if (mListItems.size() > 2) {
            mListItems.removeElementAt(mListItems.size() - 1);
        }
        notifyDataSetChanged();
    }

    public void setData(Vector<String> aList, int maxCount) {
        mListItems.clear();
        if (aList.size() > 2) {
            mListItems.addElement(aList.get(0));//添加到尾部
            mListItems.addElement(aList.get(1));//添加到尾部
        } else {
            mListItems.addAll(aList);
        }
        notifyDataSetChanged();
    }

    public void setData(Vector<String> aList) {
        mListItems.clear();
        mListItems.addAll(aList);
        notifyDataSetChanged();
    }

    public int getSelectedPosition(String lastRecord) {
        return mListItems.indexOf(lastRecord);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_msg_recorder_history, null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.line = (ImageView) convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String content = mListItems.get(position);
        if (TextUtils.isEmpty(content)) {
            viewHolder.line.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.line.setVisibility(View.VISIBLE);
        }
        viewHolder.text1.setText(content);
//        int hight = parent.getHeight() / count;
//        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
//                android.view.ViewGroup.LayoutParams.FILL_PARENT, hight);
//        convertView.setLayoutParams(param);
        return convertView;
    }

    class ViewHolder {
        TextView text1;
        ImageView line;
    }
}
