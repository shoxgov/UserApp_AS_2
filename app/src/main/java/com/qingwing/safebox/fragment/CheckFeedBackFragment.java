package com.qingwing.safebox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.UserSuggestionReq;
import com.qingwing.safebox.net.response.UserSuggestionResponse;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;

import java.util.List;

public class CheckFeedBackFragment extends Fragment implements NetCallBack {

    private TextView tv_checkfeedback;
    private Button bt_userrefresh;
    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkfeedback, null);
        tv_checkfeedback = (TextView) view.findViewById(R.id.tv_checkfeedback);
        bt_userrefresh = (Button) view.findViewById(R.id.bt_userrefresh);
        initData();
        bt_userrefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initData();
            }
        });
        return view;
    }

    private void initData() {
        WaitTool.showDialog(getActivity(), "加载中...");
        UserSuggestionReq suggestionReq = new UserSuggestionReq();
        suggestionReq.setNetCallback(this);
        suggestionReq.setUserId(UserInfo.userId + "");
        suggestionReq.setRequestType(Request.Method.POST);
        suggestionReq.addRequest();
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof UserSuggestionResponse) {
            WaitTool.dismissDialog();
            UserSuggestionResponse suggestionResponse = (UserSuggestionResponse) baseRes;
            int statusCode = suggestionResponse.getStatusCode();
            String message = suggestionResponse.getMessage();
            if (statusCode == 200) {
                List<String> suggestions = suggestionResponse.getDataMap().getSuggestions();
                System.out.println(suggestionResponse.toString());
                if (stringBuffer.length() > 0) {
                    stringBuffer.delete(0, stringBuffer.length());
                }
                for (String data : suggestions) {
                    stringBuffer.append(data + "\n");
                }
                tv_checkfeedback.setText(stringBuffer.toString());
            } else {
                ToastTool.showShortBigToast(getActivity(), message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        WaitTool.dismissDialog();
        ToastTool.showShortBigToast(getActivity(), "网络请求异常，请重试");
    }
}
