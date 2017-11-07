package com.qingwing.safebox.bluetooth;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.utils.BlueDeviceUtils;
import com.qingwing.safebox.utils.CommUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 发送蓝牙指令方法
 */
public class BLECommandManager {
    public static boolean isSupportBLE(Context context) {
        // 检查当前手机是否支持ble,蓝牙，如果不支持就退出
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        }
        return false;
    }

    public static boolean isEnable(Context context) {
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        // 检查设备上是否支持蓝牙
        if (bluetoothManager.getAdapter() == null) {
            return false;
        }
        return true;
    }

    public static String getSystemTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        return dateFormat.format(date);
    }

    private static String getThreeSystemTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(date);
    }

    /**
     * @param level 数据位为00代表新用户，01代表用户续费
     */
    public static void userBind(String startTime, String ServiceStopTime, String BlueId, String userpassword, Context context, String level) {
        try {
            LogUtil.d("执行用户绑定检查");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String strb = getSendBlueId(BlueId, "0025", startTime + level + ServiceStopTime + userpassword);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, strb);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UserSetPassword(Context context, String BlueId, String oldPassword, String newPassword) {
        try {
            LogUtil.d("执行用户修改密码");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(BlueId, "0016", getThreeSystemTime() + oldPassword + newPassword);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void UserOpen(Context context, String password) {
        Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
        //有网发0112 没网发0212
        LogUtil.d("执行用户开箱  当前没有读取信息记录，可以直接下发指令  有网发0112 没网发0212");
        if (CommUtils.isNetworkAvailable(context)) {
            String str = getSendBlueId(UserInfo.BlueId, "0112", getThreeSystemTime() + password);
            LogUtil.d("(当前有网络)  app开箱下发的指令" + str);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } else {
            String str = getSendBlueId(UserInfo.BlueId, "0212", getThreeSystemTime() + password);
            LogUtil.d("(当前没有网络)  app开箱下发的指令" + str);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        }
    }

    public static void UserReadBoxInfo(Context context, String time, String type, boolean isEnd) {
        try {
            LogUtil.d("执行读取信息记录 ");
            String str;
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            if (isEnd) {
                str = getSendBlueId(UserInfo.BlueId, "0017", time + "FFFFFF" + type + "00");
            } else {
                str = getSendBlueId(UserInfo.BlueId, "0017", time + getThreeSystemTime() + type + "00");
            }
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解绑保管箱 并退出
     */
    public static void UserUnBindExitBox(Context context) {
        try {
            LogUtil.d("执行用户解除绑定(退箱检测)");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(UserInfo.BlueId, "0019", getSystemTime());
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            ToastTool.showShortBigToast(context, "请检查您的蓝牙设置");
            e.printStackTrace();
        }
    }

    public static void UserUnBindUnRegister(Context context, String BlueId, String userPassword) {
        try {
            LogUtil.d("执行用户解除绑定(用户注销)");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(BlueId, "0020", getThreeSystemTime() + userPassword);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void HandleBoxActiveRecordData(Context context, String order, String time) {
        try {
            LogUtil.d("执行保管主动上传的数据");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(UserInfo.BlueId, order, time);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            ToastTool.showShortBigToast(context, "请检查您的蓝牙设置");
            e.printStackTrace();
        }
    }

    /**
     * 临时锁定触摸面板
     * 该指令用于退箱检测过程，作用是在APP进行解绑操作之前临时锁定保管箱触摸面板，确保退箱流程完成后保管箱的箱门处于关闭状态。
     */
    public static void CloseKeyBoard(Context context) {
        try {
            LogUtil.d("退箱关闭键盘  临时锁定触摸面板");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(UserInfo.BlueId, "0053", getThreeSystemTime());
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 状态查询，
     */
    public static void BoxStateCheck(Context context) {
        LogUtil.d("执行保管箱状态查询");
        Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
        String str;
        if (TextUtils.isEmpty(UserInfo.BlueId)) {
            str = getSendBlueId(UserInfo.QrBtId, "0018", getSystemTime());
        } else {
            str = getSendBlueId(UserInfo.BlueId, "0018", getSystemTime());
        }
        intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
        context.sendBroadcast(intent);
    }

    /**
     * 0118是更新蓝牙的系统时间，这个时间是从网上获取的，以保准确性 功能与上是一样的
     */
    public static void queryBoxStateSetBleDate(Context context, String date) {
        LogUtil.d("执行保管箱状态查询 并同步蓝牙系统时间");
        Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
        String str;
        if (TextUtils.isEmpty(UserInfo.BlueId)) {
            str = getSendBlueId(UserInfo.QrBtId, "0118", getSystemTime());
        } else {
            str = getSendBlueId(UserInfo.BlueId, "0118", getSystemTime());
        }
        intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
        context.sendBroadcast(intent);
    }

    public static void SendErrorRecordInfo(String BlueId, Context context) {
        try {
            LogUtil.d("回复保管箱读取记录信息失败");
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            String str = getSendBlueId(BlueId, "0217", getThreeSystemTime() + "000000" + "BBBB");
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            ToastTool.showShortBigToast(context, "请检查您的蓝牙设置");
            e.printStackTrace();
        }
    }

    public static void UserOpenSure(String interceptData, Context context, String timeFactor) {
        try {
            Intent intent = new Intent(BluetoothService.ACTION_GATT_WRITE_COMMAND);
            //有网发0124 没网发0224
            String str = getSendBlueId(UserInfo.BlueId, interceptData, timeFactor);
            LogUtil.d("发送了24 开箱确认指令" + str);
            intent.putExtra(BluetoothService.WRITE_COMMAND_VALUE, str);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析时也会用到
     *
     * @param blueInfo
     * @param code
     * @param time
     * @return
     */
    public static String getSendBlueId(String blueInfo, String code, String time) {
        String hexStr = "0123456789ABCDEF";
        //将小写字母编写成大写字母。
        String content;
        if (blueInfo.contains("AAAA")) {
            content = blueInfo;
        } else {
            content = "AAAA" + blueInfo.toUpperCase() + code.toUpperCase() + time.toUpperCase();
        }
        int postion = content.length() / 2;
        byte top = (byte) (hexStr.indexOf(content.charAt(0)) << 4 | (byte) (hexStr.indexOf(content.charAt(1))));
        byte bottom = 0;
        //校验位
        for (int i = 1; i < postion; i++) {
            top = (byte) (top ^ ((byte) (hexStr.indexOf(content.charAt(2 * i))) << 4 | (byte) (hexStr.indexOf(content.charAt(2 * i + 1)))));
        }
        for (int i = 0; i < postion; i++) {
            bottom += ((byte) (hexStr.indexOf(content.charAt(2 * i))) << 4 | (byte) (hexStr.indexOf(content.charAt(2 * i + 1))));
        }
        byte[] bts = {bottom, top};
        String str = BlueDeviceUtils.binaryToHexString(bts);
        LogUtil.d("执行getSendBlueId>>>>>>>>>>> content" + content + ",  校验码：" + str);
        return content + str.toUpperCase();
    }

}
