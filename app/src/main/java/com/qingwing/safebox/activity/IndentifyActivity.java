package com.qingwing.safebox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.NetCallBack;
import com.qingwing.safebox.net.request.SaveUserBindInfoReq;
import com.qingwing.safebox.net.response.SaveUserBindInfoResponse;
import com.qingwing.safebox.utils.AcitivityCollector;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.IDCardUtil;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;
import com.qingwing.safebox.utils.WaitTool;


public class IndentifyActivity extends Activity implements NetCallBack {
    private TextView txt_name, txt_number, txt_nointernet;
    private EditText et_content, et_namecontent;
    private Button btn_sure;
    private ImageView calback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indentify);
        AcitivityCollector.addActivity(this);
        txt_name = (TextView) findViewById(R.id.txt_name);
        et_namecontent = (EditText) findViewById(R.id.et_namecontent);
        et_content = (EditText) findViewById(R.id.et_content);
        //默认数字但是也可以输入字母
        String digists = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        et_content.setKeyListener(DigitsKeyListener.getInstance(digists));
        txt_number = (TextView) findViewById(R.id.txt_number);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        calback = (ImageView) findViewById(R.id.calback);
        txt_nointernet = (TextView) findViewById(R.id.txt_nointernet);

        //根据是否有设置身份证号码动态加载页面
        String cardId = getIntent().getStringExtra("cardId");
        // 如果200则查询成功已经设置了身份证号码
        if (!TextUtils.isEmpty(cardId) && !cardId.equals("null")) {
            //初始化信息
            txt_nointernet.setVisibility(View.INVISIBLE);
            txt_name.setText("您的身份证号码为：");
            // 将身份证号码中间的数字改成*
            StringBuffer sb = new StringBuffer(cardId);
            sb.replace(5, 13, "********");
            txt_number.setText(sb);
            btn_sure.setVisibility(View.GONE);
            et_content.setVisibility(View.GONE);
            et_namecontent.setVisibility(View.GONE);
            txt_number.setVisibility(View.VISIBLE);
        } else {
            // 未设置身份证
            txt_name.setText("温馨提示:身份证号码是您仲裁申诉的唯一凭证,请完善");
            txt_name.setTextSize(15);
            et_content.setVisibility(View.VISIBLE);
            et_namecontent.setVisibility(View.VISIBLE);
            txt_number.setVisibility(View.GONE);
            btn_sure.setVisibility(View.VISIBLE);
        }

        calback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                IndentifyActivity.this.finish();
            }
        });

        // 查询后台是否有身份证信息设置
        btn_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 身份证必须是合法的才能点击
                if (CommUtils.isNetworkAvailable(IndentifyActivity.this)) {
                    cardnumber = et_content.getText().toString().trim();
                    name = et_namecontent.getText().toString().trim();
                    //
                    //					if (mark) {
                    if (Judge(cardnumber, name)) {
                        boolean mark = IDCardUtil.isIDCard(cardnumber);
                        if (mark) {
                            saveCardId(cardnumber, name);
                        } else {
                            ToastTool.showShortBigToast(IndentifyActivity.this, "请正确输入您的身份证号码!");
                        }
                    }
                } else {
                    ToastTool.showShortBigToast(IndentifyActivity.this, "网络异常");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AcitivityCollector.removeActivity(this);
    }

    protected boolean Judge(String cardnumber2, String name2) {
        if (TextUtils.isEmpty(cardnumber2)) {
            ToastTool.showShortBigToast(IndentifyActivity.this, "请输入您的身份证卡号");
            return false;
        } else if (TextUtils.isEmpty(name2)) {
            ToastTool.showShortBigToast(IndentifyActivity.this, "请输入您的姓名");
            return false;
        } else {
            return true;
        }
    }

    private String cardnumber, name;

    // 保存用户填写的身份证号码
    private void saveCardId(String cardnumber, String name) {
        LogUtil.d("haha saveCardId userid" + UserInfo.userId + " , cardnumber" + cardnumber);
        SaveUserBindInfoReq req = new SaveUserBindInfoReq();
        req.setNetCallback(this);
        req.setRequestType(Request.Method.POST);
        req.setUserId(UserInfo.userId);
        req.setCardId(cardnumber);
        req.setName(name);
        req.addRequest();
        WaitTool.showDialog(this, "正在上传请稍等...");
    }


    @Override
    public void onNetResponse(BaseResponse baseRes) {
        if (baseRes instanceof SaveUserBindInfoResponse) {
            WaitTool.dismissDialog();
            SaveUserBindInfoResponse subir = (SaveUserBindInfoResponse) baseRes;
            int statusCode = subir.getStatusCode();
            ToastTool.showShortBigToast(IndentifyActivity.this, subir.getMessage());
            if (statusCode == 200) {
                setResult(RESULT_OK);
            }
            IndentifyActivity.this.finish();
        }
    }

    @Override
    public void onNetErrorResponse(String tag, Object error) {
        ToastTool.showShortBigToast(IndentifyActivity.this, "无网络连接，请检查您的网络配置");
        WaitTool.dismissDialog();
        IndentifyActivity.this.finish();
    }

}
