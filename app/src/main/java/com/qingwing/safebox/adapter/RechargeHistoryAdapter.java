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

import java.util.ArrayList;

public class RechargeHistoryAdapter extends BaseAdapter {

//    private int count = 6;
    private Context mContext;
    private ArrayList<String> mListItems = new ArrayList<String>();

    public RechargeHistoryAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<String> aList) {
        this.mListItems.clear();
        if (aList != null) {
            this.mListItems.addAll(aList);
            notifyDataSetChanged();
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recharge_history_list, null);
            viewHolder.text1 = (TextView) convertView.findViewById(R.id.text1);
            viewHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
            viewHolder.text3 = (TextView) convertView.findViewById(R.id.text3);
            viewHolder.text4 = (TextView) convertView.findViewById(R.id.text4);
            viewHolder.line = (ImageView) convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String content = mListItems.get(position);
        String[] a = content.split(",");
        String jine = a[0];
        String shijian = a[1].substring(0, a[1].length() - 2);
        String taocan = a[2];
        String zhifu = a[3];
        if (TextUtils.isEmpty(content)) {
            viewHolder.line.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.line.setVisibility(View.VISIBLE);
        }
        viewHolder.text1.setText(shijian);
        viewHolder.text2.setText(jine);
        viewHolder.text3.setText(taocan);
        viewHolder.text4.setText(zhifu);

//        int hight = parent.getHeight() / count;
//        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
//                hight);
//        convertView.setLayoutParams(param);
        return convertView;
    }

    class ViewHolder {
        TextView text1;
        TextView text2;
        TextView text3;
        TextView text4;
        ImageView line;
    }
}
