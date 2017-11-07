package com.qingwing.safebox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.dialog.LoadingDialog;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.CheckStudentGradeReq;
import com.qingwing.safebox.net.request.ObtainIdentifyCodeAndCheckRegisterReq;
import com.qingwing.safebox.net.request.RegisterUserReq;
import com.qingwing.safebox.net.response.CheckStudentGradeResponse;
import com.qingwing.safebox.net.response.ObtainIdentifyCodeAndCheckRegisterResponse;
import com.qingwing.safebox.net.response.RegisterUserResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.MD5Utils;
import com.qingwing.safebox.utils.ToastTool;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends Activity implements OnClickListener, NetCallBack, AdapterView.OnItemSelectedListener {

    private Button button_code;
    private EditText passworda, passwordb;
    private Button regtister;
    private String codeinga = "";
    private TimeCount time = null;
    private EditText phone;
    private EditText code;
    private ImageView calback;
    private Spinner sp_grade;
    private String selectedYear = "";
    private List<String> gradeList = new ArrayList<>();
    private TextView terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        AcitivityCollector.addActivity(this);
        initView();
        initData();
        long starttime = QWApplication.mPreferences.getLong("RegisterVertifyCodeStartTime", 0L);
        int spanTime = (int) (System.currentTimeMillis() - starttime);
        int runtime = 60000 - spanTime;
        if (spanTime > 0 && starttime > 0 && spanTime < 60000 && runtime > 1000) {
            codeinga = QWApplication.mPreferences.getString("RegisterVertifyCode", "0000");
            time = new TimeCount(runtime, 1000);
            time.start();
            button_code.setEnabled(false);
        } else if (spanTime > 0 && spanTime < 600000) {//10分钟内有效
            codeinga = "000000";
            time = new TimeCount(60000, 1000);
        } else {
            time = new TimeCount(60000, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
            time.onFinish();
        }
        AcitivityCollector.removeActivity(this);
    }

    private void initData() {
//        terms.setOnClickListener(this);
//        terms.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
        //这个一定要记得设置，不然点击不生效
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringBuilder spannable = new SpannableStringBuilder("点击确定，即表示已阅读并同意《青熠保管箱用户使用协议》");
        spannable.setSpan(new TextClick(), 14, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        terms.setText(spannable);
        ///////////////////
        CheckStudentGradeReq checkStudentGradeReq = new CheckStudentGradeReq();
        checkStudentGradeReq.setRequestType(Request.Method.POST);
        checkStudentGradeReq.setNetCallback(this);
        checkStudentGradeReq.addRequest();
    }

    private void initView() {
        button_code = (Button) findViewById(R.id.button_code);
        phone = (EditText) findViewById(R.id.base_sure);
        sp_grade = (Spinner) findViewById(R.id.register_grade_year);
        sp_grade.setOnItemSelectedListener(this);
        calback = (ImageView) findViewById(R.id.calback);
        calback.setOnClickListener(this);
        code = (EditText) findViewById(R.id.code);
        passworda = (EditText) findViewById(R.id.passworda);
        passwordb = (EditText) findViewById(R.id.passwordb);
        regtister = (Button) findViewById(R.id.regtister);
        regtister.setOnClickListener(this);
        button_code.setOnClickListener(this);
        terms = (TextView) findViewById(R.id.tv_user_terms);
    }

    private class TextClick extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            //在此处理点击事件
            if (CommUtils.isNetworkAvailable(RegisterActivity.this)) {
                Intent user_terms = new Intent(RegisterActivity.this, UserTermsActivity.class);
                startActivity(user_terms);
            } else {
                ToastTool.showShortBigToast(RegisterActivity.this, "当前无网络连接");
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.colorAccent));
            ds.setUnderlineText(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_code:
                if (CommUtils.isNetworkAvailable(RegisterActivity.this)) {
                    obtainVerityCodeIsCanRegister();
                } else {
                    ToastTool.showShortBigToast(this, "当前无网络连接");
                }
                break;
            case R.id.tv_user_terms:
                if (CommUtils.isNetworkAvailable(RegisterActivity.this)) {
                    Intent user_terms = new Intent(this, UserTermsActivity.class);
                    startActivity(user_terms);
                } else {
                    ToastTool.showShortBigToast(this, "当前无网络连接");
                }
                break;
            case R.id.regtister:
                System.out.println("selectedYear===" + selectedYear);
                if (!CommUtils.isNetworkAvailable(RegisterActivity.this)) {
                    ToastTool.showShortBigToast(this, "当前无网络连接");
                    return;
                }
                if (!MD5Utils.matchPhone(phone.getText().toString().trim())) {
                    ToastTool.showShortBigToast(this, "手机号码输入有误");
                    return;
                }
                if (TextUtils.isEmpty(code.getText().toString())) {
                    ToastTool.showShortBigToast(this, "请输入您的验证码");
                    return;
                }
//                if (!code.getText().toString().equals(codeinga)) {
//                    ToastTool.showShortBigToast(this, "您输入的验证码不正确");
//                    return;
//                }
                if (TextUtils.isEmpty(selectedYear) || selectedYear.equals("请选择")) {
                    ToastTool.showShortBigToast(this, "未选择入学年份");
                    return;
                }
                registerpassword(code.getText().toString());
                break;
            case R.id.calback:
                finish();
                overridePendingTransition(0, R.anim.out);
                break;
            default:
                break;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        sp_grade.setSelection(position);
        selectedYear = gradeList.get(position);
        System.out.println("选择的年份:" + selectedYear);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    // 为spinner设置数组适配器
    private ArrayAdapter<String> getLevelAdapter(Context activity) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, gradeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void registerpassword(String code) {
        String passworString = passwordb.getText().toString().trim();
        String passwod_b = passworda.getText().toString().trim();
        if (!passworString.isEmpty() && !passwod_b.isEmpty()) {
            if (passwod_b.length() < 6 || passwod_b.length() > 16) {
                ToastTool.showShortBigToast(this, "请输入6-16位密码");
            } else {
                if (!passwod_b.equals(passworString)) {
                    ToastTool.showShortBigToast(this, "两次密码输入不一致");
                } else {
                    RegisterUserReq req = new RegisterUserReq();
                    req.setNetCallback(this);
                    req.setRequestType(Request.Method.POST);
                    req.setMobile(phone.getText().toString());
                    req.setCode(code);
                    req.setGrade(selectedYear);
                    req.setPassword(passwordb.getText().toString());
                    req.addRequest();
                    LoadingDialog.showDialog(this, "请稍等...");
                }
            }
        } else {
            ToastTool.showShortBigToast(this, "密码不能为空");
        }
    }

    private long exitTime = 0;

    // 获取验证码同时判断手机号码是否被注册。
    private void obtainVerityCodeIsCanRegister() {
        if (!MD5Utils.matchPhone(phone.getText().toString().trim())) {
            ToastTool.showShortBigToast(this, "手机号码输入有误");
            return;
        } else {
            if ((System.currentTimeMillis() - exitTime) > 5000) {
                ObtainIdentifyCodeAndCheckRegisterReq req = new ObtainIdentifyCodeAndCheckRegisterReq();
                req.setNetCallback(this);
                req.setRequestType(Request.Method.POST);
                req.setMobile(phone.getText().toString());
                req.addRequest();
                exitTime = System.currentTimeMillis();
            }
        }
    }

    // 验证码再次获取的倒计时
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            button_code.setText("获取验证码");
            button_code.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button_code.setEnabled(false);
            button_code.setText(millisUntilFinished / 1000 + "s");
        }
    }

    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof ObtainIdentifyCodeAndCheckRegisterResponse) {
            ObtainIdentifyCodeAndCheckRegisterResponse resp = (ObtainIdentifyCodeAndCheckRegisterResponse) baseRes;
            try {
                String status = resp.getStatus();
                String message = resp.getMessage();
                if (!TextUtils.isEmpty(status) && status.equals("success")) {
                    time.start();
                    codeinga = resp.getDataMap().getCode();
                    QWApplication.mPreferences.edit().putLong("RegisterVertifyCodeStartTime", System.currentTimeMillis()).putString("RegisterVertifyCode", codeinga).commit();
                } else {
                    button_code.setText("获取验证码");
                    ToastTool.showShortBigToast(this, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (baseRes instanceof CheckStudentGradeResponse) {
            CheckStudentGradeResponse gradeResponse = (CheckStudentGradeResponse) baseRes;
            String status = gradeResponse.getStatus();
            String message = gradeResponse.getMessage();
            if (status.equals("success")) {
                gradeList.clear();
                gradeList.add(0, "请选择");
                gradeList.addAll(gradeResponse.getDataMap().getDateList());
                sp_grade.setAdapter(getLevelAdapter(RegisterActivity.this));
                sp_grade.setSelection(0);
            } else {
                ToastTool.showShortBigToast(this, message);
            }
        } else if (baseRes instanceof RegisterUserResponse) {
            LoadingDialog.dismissDialog();
            RegisterUserResponse rur = (RegisterUserResponse) baseRes;
            String status = rur.getStatus();
            String message = rur.getMessage();
            if (status.equals("success")) {
                int userID = rur.getDataMap().getUserId();
                String ID = rur.getDataMap().getID();
                String requestCode = rur.getDataMap().getRequestCode();
                String pass = rur.getDataMap().getPass();
                if (userID > 0 && ID != null) {
                    UserInfo.userId = userID;
                    UserInfo.account_Id = ID;
                    UserInfo.pass = pass;
                    UserInfo.requestCode = requestCode;
                    SharedPreferences mPreferences = getSharedPreferences("QingWing_SP", MODE_PRIVATE);
                    mPreferences.edit().putString("account_Id", ID)
                            .putInt("userId", userID).commit();
                    ToastTool.showShortBigToast(this, "恭喜您,注册账号成功");
                    finish();
                } else {
                    ToastTool.showShortBigToast(this, message);
                }
            } else {
                ToastTool.showShortBigToast(this, message);
            }
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        if (tag.equals("RegisterUserReq")) {
            LoadingDialog.dismissDialog();
        } else if (tag.equals("CheckStudentGradeReq")) {
            gradeList.clear();
            gradeList.add(0, "请选择");
            gradeList.add("2018");
            gradeList.add("2017");
            gradeList.add("2016");
            gradeList.add("2015");
            sp_grade.setAdapter(getLevelAdapter(RegisterActivity.this));
            sp_grade.setSelection(0);
        }
        ToastTool.showShortBigToast(this, "网络异常,请检查您的网络");
        button_code.setText("获取验证码");
    }

}
