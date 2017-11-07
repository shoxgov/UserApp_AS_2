package com.qingwing.safebox.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingwing.safebox.QWApplication;
import com.qingwing.safebox.R;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.utils.ToastTool;

import java.lang.reflect.Field;

public class SelectLockTypeFragment extends Fragment implements OnClickListener {
    private TextView shoushi;
    private TextView number;
    private Fragment newContent;

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
        View view = inflater.inflate(R.layout.fragment_select_locktype, null);
        shoushi = (TextView) view.findViewById(R.id.shousi);
        number = (TextView) view.findViewById(R.id.number);
        //得到当前解锁方式并提示用户当前所用解锁方式，a:手势   b:指纹  c:数字
        if (TextUtils.isEmpty(UserInfo.lock_style)) {
            Toast.makeText(getActivity(), "查无此开锁方式", Toast.LENGTH_LONG).show();
        } else if (UserInfo.lock_style.equals("a")) {
            shoushi.setTextColor(Color.RED);
            shoushi.setText("手势密码(当前选中)");
            number.setTextColor(Color.BLACK);
            number.setText("数字密码");
        } else if (UserInfo.lock_style.equals("c")) {
            shoushi.setTextColor(Color.BLACK);
            shoushi.setText("手势密码");
            number.setTextColor(Color.RED);
            number.setText("数字密码(当前选中)");
        }
        number.setOnClickListener(this);
        shoushi.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d("onDestroyView", "执行啦");
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        //得到解锁方式，a:手势   b:指纹  c:数字
        Log.i("haha", "tab中得到的解锁方式" + UserInfo.lock_style);
        switch (v.getId()) {
            case R.id.shousi:
                //已经是手势解锁
                if (!TextUtils.isEmpty(UserInfo.lock_style)) {
                    if (newContent == null) {
                        //得到手势密码字符串
                        Log.i("haha", "得到的手势密码是" + UserInfo.lock_password);
                        //判断是否有用过。没用过则跳转设置手势密码。用过则提示已经切换成手势解锁
                        if (!TextUtils.isEmpty(UserInfo.lock_password)) {
                            ToastTool.showShortBigToast(getActivity(), "解锁方式设置为手势解锁");
                            // 将当前选中信息修改
                            shoushi.setTextColor(Color.RED);
                            shoushi.setText("手势密码(当前选中)");
                            number.setTextColor(Color.BLACK);
                            number.setText("数字密码");
                            UserInfo.lock_style = "a";
                            QWApplication.mPreferences.edit().putString("lock_style", "a")
                                    .commit();
                        } else {
                            UserAlterPassword();
                        }
                    }
                } else if (UserInfo.lock_style.equals("a")) {
                    ToastTool.showShortBigToast(getActivity(), "您当前设置的已经是手势解锁");
                    //				Toast.makeText(mBoxActivity, "您当前设置的已经是手势解锁", Toast.LENGTH_LONG).show();
                    //得到手势密码字符串
                    Log.i("haha", "得到的手势密码是" + UserInfo.lock_password);
                }
                break;
//		case R.id.zhiwen:
//
//			break;

            case R.id.number:
                if (UserInfo.lock_style.equals("a")) {
                    //进行手势验证
//				newContent = new Foutor_number_fragment();
//				mBoxActivity.switchContent(newContent, "切换数字密码验证");
                    shoushi.setTextColor(Color.BLACK);
                    shoushi.setText("手势密码");
                    number.setTextColor(Color.RED);
                    number.setText("数字密码(当前选中)");
                    ToastTool.showShortBigToast(getActivity(), "解锁方式设置为数字解锁");
                    //将数字解锁style放入shareperference
                    UserInfo.lock_style = "c";
                    QWApplication.mPreferences.edit().putString("lock_style", "c").commit();
                } else if (UserInfo.lock_style.equals("c")) {
                    ToastTool.showShortBigToast(getActivity(), "您当前设置的是数字密码");
                    //将数字解锁style放入shareperference
                    UserInfo.lock_style = "c";
                    QWApplication.mPreferences.edit().putString("lock_style", "c").commit();
                }
                break;

            default:
                break;
        }
    }

    EditText oldPassword;

    public void UserAlterPassword() {
        if (!UserInfo.UserBindState) {
            ToastTool.showShortBigToast(getActivity(), "当前蓝牙未绑定");
            return;
        }
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
//		build.setTitle("确认开箱密码");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.zidingyi_alert2, null);
        oldPassword = (EditText) view.findViewById(R.id.old_password);
        build.setView(view);
        build.setPositiveButton("确定", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String oldPasswordString = oldPassword.getText().toString().trim();
                Log.i("haha", "填的密码" + oldPasswordString + "存的密码" + UserInfo.UserOpenBoxPassowrd);
                if (oldPasswordString.length() == 6 && oldPasswordString.equals(UserInfo.UserOpenBoxPassowrd)) {
                    System.out.println("成功可以继续设置");
                    setHandPwd();
                    try {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        // 将mShowing变量设置为false,表示对话框已经关闭。
                        field.set(dialog, true);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastTool.showShortBigToast(getActivity(), "密码错误");
                    try {
                        oldPassword.setText("");
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        // 将mShowing变量设置为false,表示对话框已经关闭。
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        build.setNegativeButton("取消", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    // 将mShowing变量设置为false,表示对话框已经关闭。
                    field.set(dialog, true);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        build.show();
    }

    private void setHandPwd() {
        newContent = new SettingGuesturePwdFragment();
//        mBoxActivity.switchContent(newContent, "手势解锁");
    }
}