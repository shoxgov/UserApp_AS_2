package com.qingwing.safebox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.FeedBackReq;
import com.qingwing.safebox.net.response.FeedBackResponse;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.ToastTool;

public class FeedBackFragment extends Fragment implements NetCallBack {

    private Button bt_present_useranswer;
    private EditText et_useranswer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, null);
        et_useranswer = (EditText) view.findViewById(R.id.et_useranswer);
        bt_present_useranswer = (Button) view.findViewById(R.id.bt_present_useranswer);
        bt_present_useranswer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CommUtils.isFastClick()) {
                    return;
                }
                String answer = et_useranswer.getText().toString().trim();
                if (TextUtils.isEmpty(answer)) {
                    ToastTool.showShortBigToast(getActivity(), "您当前未填写反馈意见");
                } else {
                    FeedBackReq feedBackReq = new FeedBackReq();
                    feedBackReq.setNetCallback(FeedBackFragment.this);
                    feedBackReq.setSuggestion(answer);
                    feedBackReq.setUserId(UserInfo.userId + "");
                    feedBackReq.setRequestType(Request.Method.POST);
                    feedBackReq.addRequest();
                }
            }
        });
        return view;
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof FeedBackResponse) {
            FeedBackResponse feedBackResponse = (FeedBackResponse) baseRes;
            String message = feedBackResponse.getMessage();
            int statusCode = feedBackResponse.getStatusCode();
            if (statusCode == 200) {
                et_useranswer.setText("");
            }
            ToastTool.showShortBigToast(getActivity(), message);
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {

    }
}
