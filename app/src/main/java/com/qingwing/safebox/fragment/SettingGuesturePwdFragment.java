package com.qingwing.safebox.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.fab.DraggableFloatingButton;
import com.qingwing.safebox.view.LocusPassWordView;

public class SettingGuesturePwdFragment extends Fragment implements
        OnCheckedChangeListener, OnClickListener {

    private RadioGroup bottom_radioGroup;
    private Fragment mContent;
    private String password;
    private String passwordb;
    private LocusPassWordView mLocusPassWordView;
    private LinearLayout box_bottom_status;
    private int setPassword = 1;
    private TextView Inscreen_state;
    private Button states_a;
    private Button states_b;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_guesture_password, null);
        box_bottom_status = (LinearLayout) view
                .findViewById(R.id.box_bottom_status);
        Inscreen_state = (TextView) view.findViewById(R.id.quereng);
        box_bottom_status.setVisibility(view.GONE);
        mLocusPassWordView = (LocusPassWordView) view
                .findViewById(R.id.mLocusPassWordView);
        states_a = (Button) view.findViewById(R.id.states_a);
        states_b = (Button) view.findViewById(R.id.states_b);
        states_b.setOnClickListener(this);
        bottom_radioGroup = (RadioGroup) view
                .findViewById(R.id.bottom_radioGroup);
        bottom_radioGroup.setVisibility(view.GONE);
        bottom_radioGroup.setOnCheckedChangeListener(this);
        box_bottom_status = (LinearLayout) view
                .findViewById(R.id.box_bottom_status);
        box_bottom_status.setVisibility(view.GONE);
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_base,
                (ViewGroup) view, true);
        states_a.setOnClickListener(this);
        states_b.setEnabled(false);
        Inscreen_state.setText("绘制新解锁图案,请至少连接四个点");
        Inscreen_state.setTextColor(Color.BLACK);
        Inscreen_state.setTextSize(20);
        DraggableFloatingButton floatbuttona = (DraggableFloatingButton) view
                .findViewById(R.id.float_action_button);
        mLocusPassWordView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Inscreen_state.setText("完成后松开手指");
                Inscreen_state.setTextColor(Color.BLACK);
                return false;
            }
        });
        mLocusPassWordView.setOnCompleteListener(new LocusPassWordView.OnCompleteListener() {

            @Override
            public void onComplete(String mpassword) {
                states_a.setVisibility(View.VISIBLE);
                states_b.setVisibility(View.VISIBLE);
                Inscreen_state.setText("完成后请松开手指");
                Inscreen_state.setTextColor(Color.BLACK);
                if (setPassword == 1) {
                    password = mpassword;
                    Inscreen_state.setVisibility(View.VISIBLE);
                    Inscreen_state.setText("请再次绘制解锁图案");
                    Inscreen_state.setTextColor(Color.BLACK);
                    mLocusPassWordView.clearPassword();
                    setPassword++;
                } else {
                    //两次图案一样
                    if (password.equals(mpassword)) {
                        passwordb = password;
                        Inscreen_state.setText("您的新解锁图案");
                        Inscreen_state.setTextColor(Color.BLACK);
                        states_b.setEnabled(true);
                        setPassword = 1;
                    } else {
                        mLocusPassWordView.markError();
                        mLocusPassWordView.clearPassword();
                        Inscreen_state.setText("两次图案绘制不一致，请重试");
                        Inscreen_state.setTextColor(Color.RED);
                        /* password = null; */
                    }
                }
            }
        });

        floatbuttona.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (box_bottom_status.getVisibility() == v.VISIBLE) {
                    box_bottom_status.setVisibility(v.GONE);
                } else {
                    box_bottom_status.setVisibility(v.VISIBLE);
                }
            }
        });
        floatbuttona.setVisibility(view.GONE);
        floatbuttona.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box_bottom_status.setVisibility(v.GONE);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d("onDestroyView", "执行啦");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "执行啦");
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_tab_home:
                mContent = null;
                mContent = new PasswordUnlockFragment();
//                mBoxActivity.switchContent(mContent, "密码解锁");
                break;
            case R.id.main_tab_column:
                mContent = null;
                mContent = new SettingGuesturePwdFragment();
                String a = "Box";
//                getActivity().switchContent(mContent, "手势解锁");
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.states_b:
                if (!TextUtils.isEmpty(password) && passwordb.equals(password)) {
                    //设置密码
                    Toast.makeText(getActivity(), "密码设置成功 ", Toast.LENGTH_LONG).show();
                    UserInfo.lock_style = "a";
                    UserInfo.lock_password = password;
                    QWApplication.mPreferences.edit().putString("lock_password", password)
                            .putString("lock_style", "a").commit();
//                startActivity(new Intent(mBoxActivity, User_Setting.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "密码设置错误", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.states_a:
                Inscreen_state.setText("绘制解锁图案,请至少连接四个点");
                Inscreen_state.setTextColor(Color.BLACK);
                password = null;
                setPassword = 1;
                mLocusPassWordView.clearPassword();
                break;
            default:
                break;
        }
    }
}
