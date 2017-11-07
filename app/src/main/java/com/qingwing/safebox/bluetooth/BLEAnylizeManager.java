package com.qingwing.safebox.bluetooth;

import android.text.TextUtils;

import com.qingwing.safebox.bean.BoxStatusBean;
import com.qingwing.safebox.bean.OpenBoxRecoder;
import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.BlueDeviceUtils;
import com.qingwing.safebox.utils.LogUtil;

/**
 * 解析蓝牙数据类
 */
public class BLEAnylizeManager {
    /**
     * 解析
     *
     * @param data
     */

    public static void anylize(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        LogUtil.d(" anylize >>>" + data);
        String recevier = BLECommandManager.getSendBlueId(data.substring(0, data.length() - 4), "", "");
        LogUtil.d("转换之后的字符串：" + recevier);
        if (!recevier.equals(data)) {
            LogUtil.d("校验错误，数据传输异常请重发！");
            return;
        }
        String interceptData = data.substring(12, 16);
        LogUtil.d("QWBLE analyzeBT   interceptData：" + interceptData);
        // 保管箱状态查询 A00050021 0118 170521 04 00 AA 64 00 0004
        if (interceptData.equals("0118")) {
            LogUtil.d("在BlueService： 收到状态查询0118返回给BoxActivity的指令了");
            String serviceEndDate = data.substring(16, 22);//服务到期时间
            String lifeStatus = data.substring(22, 24);//生命周期 //00代表出厂初始状态（状态0）;02代表出厂检验完成并已经设置保管箱ID号（状态1）;
            // 04代表位置绑定完成但尚未有客户绑定（状态2）;08代表客户绑定，且处于正常缴费期（状态3）;0x10代表客户绑定，但是服务已经到期。
//            String troubleStatus = data.substring(24, 26);//故障状态
            String openStatus = data.substring(26, 28);//开关箱状态 //0代表关，0xaa代表门开，0x22代表处于不明确的中间位置（堵转）
            String electricValue = data.substring(28, 30);//电量值  16进制
//            String rechargeStatus = data.substring(30, 32);//充电状态  //为0，代表没有充电。为1，代表正在充电且没有冲满为2，代表正在充电且已经冲满。
            String recordCountInfo = data.substring(32, 36);//记录条数信息
            ///////////////////////
            String year = serviceEndDate.substring(0, 2);
            String month = serviceEndDate.substring(2, 4);
            String day = serviceEndDate.substring(4, 6);
            BoxStatusBean.serviceEndTimeBineary = year + month + day;
            BoxStatusBean.serviceEndTime = "20" + year + "/" + month + "/" + day;
            if (lifeStatus.equals("00")) {
                BoxStatusBean.lifeStatus = 0;
            } else if (lifeStatus.equals("02")) {
                BoxStatusBean.lifeStatus = 1;
            } else if (lifeStatus.equals("04")) {
                BoxStatusBean.lifeStatus = 2;
            } else if (lifeStatus.equals("08")) {
                BoxStatusBean.lifeStatus = 3;
            } else if (lifeStatus.equals("10")) {
                BoxStatusBean.lifeStatus = 4;
            }
            BoxStatusBean.openStatus = openStatus.toLowerCase();
            BoxStatusBean.electricValue = BlueDeviceUtils.hexStringToInteger(electricValue) + "";
            BoxStatusBean.recordAlarmCount = BlueDeviceUtils.hexStringToInteger(recordCountInfo.substring(0, 2));
            BoxStatusBean.recordOpenCount = BlueDeviceUtils.hexStringToInteger(recordCountInfo.substring(2, 4));
            LogUtil.d("服务到期时间无格式 serviceEndTimeBineary=" + BoxStatusBean.serviceEndTimeBineary);
            LogUtil.d("服务到期时间带格式 serviceEndTime=" + BoxStatusBean.serviceEndTime);
            LogUtil.d("生命周期 状态 lifeStatus=" + BoxStatusBean.lifeStatus);
            LogUtil.d("开关箱状态 openStatus=" + BoxStatusBean.openStatus);
            LogUtil.d("电量值 electricValue=" + BoxStatusBean.electricValue);
            LogUtil.d("报警记录条信息 recordAlarmInfo=" + BoxStatusBean.recordAlarmCount + "条");
            LogUtil.d("开关箱记录条信息 recordOpenInfo=" + BoxStatusBean.recordOpenCount + "条");
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_QUERY_STATUS_CALLBACK);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } // 接收保管箱读取记录信息
        else if (interceptData.equals("0117")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_RECEIVER_READINFO);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } // 用户修改密码成功
        else if (interceptData.equals("0116")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_MODIFY_PASSWORD_RESULT);
            obj.setObject(true);
            ObserverManager.getObserver().setMessage(obj);
        } // 用户修改密码失败
        else if (interceptData.equals("0216")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_MODIFY_PASSWORD_RESULT);
            obj.setObject(false);
            ObserverManager.getObserver().setMessage(obj);
        } // 用户解除绑定(退箱检测)
        else if (interceptData.equals("0119")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_UNBIND_EXIT);
            obj.setObject(data.substring(22, 24));//异常状态位
            ObserverManager.getObserver().setMessage(obj);
        } // 用户解除绑定(用户注销)
        else if (interceptData.equals("0120")) {//01代表密码比对正确，解除绑定成功
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_UNBIND_BY_HAND);
            obj.setObject(true);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("0220")) {//02为解除绑定失败
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_UNBIND_BY_HAND);
            obj.setObject(false);
            ObserverManager.getObserver().setMessage(obj);
        } // 用户解除绑定(锁定键盘成功)  锁定触摸面板10秒
        else if (interceptData.equals("0153")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_CLOSE_KEYBOARD_STATUS);
            obj.setObject(true);
            ObserverManager.getObserver().setMessage(obj);
        }  // 用户解除绑定(锁定键盘失败)  此时，APP应该终止解绑流程。
        else if (interceptData.equals("0253")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_CLOSE_KEYBOARD_STATUS);
            obj.setObject(false);
            ObserverManager.getObserver().setMessage(obj);
        }// 用户绑定成功 ：AAAA 00050021 0125 170526 E234    发送0025  绑定成功回复0125指令  绑定失败回复0225指令
        else if (interceptData.equals("0125")) {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_BIND_RESULT);//BOX_USER_UNBIND_RESULT//是否是忘记密码的修改密码
            obj.setObject(true);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("0225")) {// 用户绑定失败
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_USER_BIND_RESULT);//BOX_USER_MODIFY_PASSWORD_RESULTFROMFORGET//是否是忘记密码的修改密码
            obj.setObject(false);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("0112")) {
            LogUtil.d("app开箱接收到的指令" + data);
            ObservableBean obj = new ObservableBean();
            if (UserInfo.lock_style.equals("a")) {//手势
                LogUtil.d("手势开箱");
                obj.setWhat(BleObserverConstance.BOX_USER_OPENBOX_STATUS_HAND);
            } else {
                LogUtil.d("数字密码开箱");
                obj.setWhat(BleObserverConstance.BOX_USER_OPENBOX_STATUS);
            }
            OpenBoxRecoder.timeFactor = data.substring(16, 22);
            OpenBoxRecoder.operationResult = data.substring(22, 24);
            OpenBoxRecoder.openTime = data.substring(24, 36);//AAAA 00050021 0112 165404 04 170625165404 AF07
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("00A0")) { // 接收报警数据
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.RECEIVER_BOX_DATA_ALARM);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("00A1")) { // 接收到用户在保管箱开箱数据
            BoxStatusBean.openStatus = "aa";
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.RECEIVER_BOX_DATA_OPENBOX);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("00A4")) { // 接收到用户关保管箱数据
            BoxStatusBean.openStatus = "00";
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.RECEIVER_BOX_DATA_CLOSEBOX);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } else if (interceptData.equals("00A2")) { // 接收到插入充电线主动上传APP
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.RECEIVER_BOX_DATA_RECHARGE);
            obj.setObject(data);
            ObserverManager.getObserver().setMessage(obj);
        } else {
            ObservableBean obj = new ObservableBean();
            obj.setWhat(BleObserverConstance.BOX_RECEIVER_INFO_UNKNOW);
            ObserverManager.getObserver().setMessage(obj);
        }
    }
}