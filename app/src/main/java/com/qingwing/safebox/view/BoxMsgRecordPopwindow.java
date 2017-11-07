package com.qingwing.safebox.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.adapter.MsgRecordHistoryAdapter;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.dialog.DoubleDatePickerDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.ObtainRecordInfoReq;
import com.qingwing.safebox.net.response.ObtainRecordInfoResponse;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class BoxMsgRecordPopwindow extends PopupWindow implements NetCallBack {
    private View progress;
    private View mMenuView;
    private XListView mPullRefreshListView;
    private MsgRecordHistoryAdapter mAdapter;
    private Context context;
    private String select = "2";//默认等于2
    private Vector<String> mListItems = new Vector<String>();
    //	private TextView txt;
    private ImageButton ib_date;
    /**
     * 是否是第一次请求
     */
    private boolean isFirstRequest = true;
    /**
     * 选定日期请求，一次返回30  超出为分页
     */
    private int dataPage = 1;
    private String selectDateStart = "";
    private String selectDateEnd = "";
    private boolean isHasNext = true;

    public BoxMsgRecordPopwindow(final Context context) {
        this.context = context;
        isFirstRequest = true;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_box_msg, null);
        setContentView(mMenuView);
        progress = (View) mMenuView.findViewById(R.id.progress);
        setWidth(QWApplication.screenWidthPixels);
        setHeight(QWApplication.screenHeightPixels);
        // 设置SelectPicPopupWindow弹出窗体可点击 ;//这里必须设置为true才能点击区域外或者消失
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        mMenuView.findViewById(R.id.title_bar_a).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mMenuView.findViewById(R.id.calback).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ib_date = (ImageButton) mMenuView.findViewById(R.id.ib_date);
        ib_date.setOnClickListener(new OnClickListener() {
            Calendar c = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
                DoubleDatePickerDialog dialog = new DoubleDatePickerDialog(BoxMsgRecordPopwindow.this.context, 0,
                        new DoubleDatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                                  int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                                  int endDayOfMonth) {
                                //// // 年份
                                String year = c.get(Calendar.YEAR) + "";
                                //// // 月份
                                String month = c.get(Calendar.MONTH) + "";
                                //// //日
                                int day = c.get(Calendar.DAY_OF_MONTH);// 得到天
                                String today = year + month + day;
                                String endDate = endYear + "" + endMonthOfYear + "" + endDayOfMonth;
                                if (Integer.parseInt(endDate) > Integer.parseInt(today)) {
                                    ToastTool.showShortBigToast(BoxMsgRecordPopwindow.this.context, "日期选择不可超过当天日期");
                                    return;
                                }
                                String startString = String.format("%d-%d-%d\n", startYear, startMonthOfYear + 1,
                                        startDayOfMonth);
                                String endString = String.format("%d-%d-%d\n", endYear, endMonthOfYear + 1,
                                        endDayOfMonth);
                                String startStringInt = String.format("%d%d%d", startYear, startMonthOfYear + 1,
                                        startDayOfMonth);
                                String endStringInt = String.format("%d%d%d", endYear, endMonthOfYear + 1,
                                        endDayOfMonth);
                                if (Integer.parseInt(startStringInt) > Integer.parseInt(endStringInt)) {
                                    ToastTool.showShortBigToast(BoxMsgRecordPopwindow.this.context, "起始日期不可大于终止日期");
                                } else {
                                    // 请求后台服务器获取这段时间段的数据
                                    mListItems.clear();
                                    dataPage = 1;
                                    isHasNext = true;
                                    selectDateStart = startString;
                                    selectDateEnd = endString;
                                    getThisTimeRecord(startString, endString);
                                }
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true);

                dialog.show();
            }
        });

        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.white));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.animation_popwindow);
        mPullRefreshListView = (XListView) mMenuView.findViewById(R.id.pull_refresh_list);
        mAdapter = new MsgRecordHistoryAdapter(context);
        mPullRefreshListView.setAdapter(mAdapter);
//        mPullRefreshListView.setHeaderPullRefresh(false);// 取消下拉刷新
//        mPullRefreshListView.setFooterPullRefresh(false);// 取消下拉刷新
        mPullRefreshListView.setOnItemClickListener(pullOnItemClickListener);
        mPullRefreshListView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onHeaderRefresh() {
                if (TextUtils.isEmpty(UserInfo.BlueId)) {
                    mPullRefreshListView.stopRefresh();
                    return;
                }
                if (select.equals("1")) {
                    if (isHasNext) {
                        dataPage++;
                        getThisTimeRecord(selectDateStart, selectDateEnd);
                    } else {
                        ToastTool.showShortBigToast(context, "无更多数据");
                    }
                }
                mPullRefreshListView.stopRefresh();
            }

            @Override
            public void onFooterRefresh() {
                if (select.equals("1")) {
                    mPullRefreshListView.stopRefresh();
                    return;
                }
                if (!UserInfo.UserBindState) {
                    ToastTool.showShortBigToast(context, "暂未绑定保管箱");
                    mPullRefreshListView.stopRefresh();
                } else if (TextUtils.isEmpty(UserInfo.BlueId)) {
                    mPullRefreshListView.stopRefresh();
                    LogUtil.d(TextUtils.isEmpty(UserInfo.BlueId) + "<---onFooterRefresh当前的蓝牙ID");
                    ToastTool.showShortBigToast(context, "暂未连接保管箱");
                } else if (!isFirstRequest) {
                    WaitTool.showDialog(context);
                    requestFifteenRecordInfo();
                } else {
                    mPullRefreshListView.stopRefresh();
                }
            }
        });
//        WaitTool.showDialog(context);
//        requestFifteenRecordInfo();
    }

    public void addData(String data) {
        if (!isFirstRequest) {
            mListItems.insertElementAt(data, 0);
            mAdapter.setData(mListItems);
        }
    }

    private void getThisTimeRecord(String startDate, String endDate) {
        Log.i("haha", "蓝牙id" + UserInfo.BlueId);
        if (!UserInfo.UserBindState) {
            ToastTool.showShortBigToast(context, "暂未绑定保管箱");
            return;
        }
        if (!TextUtils.isEmpty(UserInfo.BlueId)) {
            WaitTool.showDialog(context, "正在读取数据请稍等..");
            ObtainRecordInfoReq req = new ObtainRecordInfoReq();
            req.setNetCallback(this);
            req.setUserId(UserInfo.userId + "");
            req.setBarcode(UserInfo.BlueId);
            req.setSelectType("1");
            select = "1";
            req.setPageNo("" + dataPage);//从第一页开始查看
            req.setStartDate(startDate);
            req.setEndDate(endDate);
            req.setRequestType(Request.Method.POST);
            req.addRequest();
        } else {
            LogUtil.d(UserInfo.BlueId + "<---getThisTimeRecord当前的蓝牙ID");
            ToastTool.showShortBigToast(context, "暂未连接保管箱");
        }
    }

    /**
     * 每次获取15条数据
     */
    private void requestFifteenRecordInfo() {
        if (!CommUtils.isNetworkAvailable(context)) {// 用户第一次点击刷15条记录出来
            LogUtil.d(" isNet==false requestFifteenRecordInfo");
//            GetFirstLoadRecordData();
        } else {// 有网则按正常逻辑判断
            LogUtil.d(" isNet==true  requestFifteenRecordInfo");
            if (!mListItems.isEmpty()) {// 不是第一次询问数据
                // 得到显示的记录中最后的一条
                getLastRecordData();
            } else {
                // 第一次询问数据
                GetFirstLoadRecordData();
            }
        }
    }

    private void GetFirstLoadRecordData() {
        //查看15条记录信息
        ObtainRecordInfoReq req = new ObtainRecordInfoReq();
        req.setNetCallback(this);
        req.setUserId(UserInfo.userId + "");
        req.setBarcode(UserInfo.BlueId);
        req.setSelectType("2");
        select = "2";
        req.setThisDate("");
        req.setRequestType(Request.Method.POST);
        req.addRequest();
        isFirstRequest = false;
    }

    private void getLastRecordData() {
        String lastRecord = mListItems.get(mListItems.size() - 1);
        String[] ss = lastRecord.split("\r\n");
        String s1 = ss[0];
        String s2 = ss[1];
        LogUtil.d("s2的长度是：" + s2.length());
        String record = textToNumber(s1, s2);

        Log.i("haha", "记录是" + record);
        ObtainRecordInfoReq req = new ObtainRecordInfoReq();
        req.setNetCallback(this);
        req.setUserId(UserInfo.userId + "");
        req.setBarcode(UserInfo.BlueId);
        req.setSelectType("2");
        select = "2";
        req.setThisDate(record);
        req.setRequestType(Request.Method.POST);
        req.addRequest();
    }

    private String textToNumber(String caozuo, String shijian) {
        StringBuffer sb = new StringBuffer();
        String nian = shijian.substring(2, 4);
        String yue = shijian.substring(5, 7);
        String ri = shijian.substring(8, 10);
        String shi = shijian.substring(13, 15);
        String fen = shijian.substring(16, 18);
        String miao = shijian.substring(19, 21);

        sb.append("20" + nian + "-");
        sb.append(yue + "-");
        sb.append(ri + " ");
        sb.append(shi + ":");
        sb.append(fen + ":");
        sb.append(miao);
        return sb.toString();
    }

    /**
     * //注意，因为显示加载的引用了ListviewHeader,所以要减1
     */
    OnItemClickListener pullOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        }
    };

    public void show_center(View parent) {
        if (!this.isShowing()) {
            //			this.showAsDropDown(parent, Utils.getScreenWidth(context) / 6,  parent.getMeasuredHeight());
//            if (Build.VERSION.SDK_INT >= 24) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, -parent.getMeasuredHeight());
//            } else {
//                this.showAsDropDown(parent, 0, -parent.getMeasuredHeight());
//            }
        } else {
            this.dismiss();
        }
    }

    public void requestRecoderInfo() {
        if ((isFirstRequest || mListItems.isEmpty()) && UserInfo.UserBindState) {
            // 第一次询问数据
            GetFirstLoadRecordData();
        } else {
            progress.setVisibility(View.GONE);
        }
        isFirstRequest = false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (select.equals("1")) {
            mListItems.clear();
            dataPage = 1;
            selectDateStart = "";
            selectDateEnd = "";
            isHasNext = true;
            select = "2";
            isFirstRequest = true;
            mAdapter.setData(mListItems);
        }
    }

    /**
     * 删除重复元素,保持顺序
     */
    public ArrayList<String> removeDuplicateWithOrder(ArrayList<String> list) {
        Set<String> set = new HashSet<String>();
        List<String> newList = new ArrayList<String>();
        for (Iterator<String> iter = list.iterator(); iter.hasNext(); ) {
            String element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        return list;
    }


    private void getFifteenData(List<String> list) {
        if (list != null && !list.isEmpty()) {
            mListItems.addAll(list);
            mAdapter.setData(mListItems);
        } else {
            // 没有刷新出数据
            mAdapter.setData(mListItems);
            ToastTool.showShortBigToast(context, "无更多操作数据");
        }
    }

    private void getThirtyData(List<String> list) {
        if (list != null && !list.isEmpty()) {
            String lastRecord = list.get(list.size() - 1);
            mListItems.addAll(list);
            mAdapter.setData(mListItems);
            // 得到刷新出来的数据最后一条的位置并跳转到该条
            int position = mAdapter.getSelectedPosition(lastRecord);
            if (position == -1) {
                position = mListItems.indexOf(lastRecord);
            }
            mPullRefreshListView.setSelection(position);
        } else {
            // 没有刷新出数据
            mAdapter.setData(mListItems);
            ToastTool.showShortBigToast(context, "无更多操作数据");
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        if (UserInfo.UserBindState) {
            ToastTool.showShortBigToast(context, "网络异常,请检查您的网络");
        }
        mPullRefreshListView.stopRefresh();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ObtainRecordInfoResponse) {
            ObtainRecordInfoResponse ottr = (ObtainRecordInfoResponse) baseRes;
            String status = ottr.getStatus();
            LogUtil.d("ObtainRecordInfoResponse status:" + status);
            WaitTool.dismissDialog();
            progress.setVisibility(View.GONE);
            isFirstRequest = false;
            if (!TextUtils.isEmpty(status) && status.equals("success")) {
                List<String> list = ottr.getDataMap().getUseRecordList();
                if (list == null || list.isEmpty()) {
                    if (select.equals("1")) {
                        isHasNext = false;
                    }
                    ToastTool.showShortBigToast(context, "无更多操作数据");
                    mPullRefreshListView.stopRefresh();
                    return;
                }
                if (select.equals("1")) {
                    //根据日期查询记录的
                    if (list.size() < 30) {
                        isHasNext = false;
                    }
                    getThirtyData(list);
                    mPullRefreshListView.setSelection(0);
                } else if (select.equals("2")) {
                    //查询15条记录
                    getFifteenData(list);
                } else {
                    getFifteenData(list);
                }
            } else {
                mAdapter.setData(mListItems);
            }
            mPullRefreshListView.stopRefresh();
        }
    }

}
