package com.qingwing.safebox.bluetooth;

public class BleObserverConstance {
    /**
     * 登录互斥 强制退出登录
     */
    public static final int LOGIN_SINGLE_STOP_ACTION = 20001;
    /**
     * 手动关闭蓝牙事件
     */
    public static final int BT_OFF_HAND_ACTION = 20002;
    /**
     * 手动关闭网络事件
     */
    public static final int NETWORK_OFF_HAND_ACTION = 20003;
    /**
     * BOX_CONNTEC_BLE_ON: 连接保管箱BLE连接状态
     */
    public static final int BOX_CONNTEC_BLE_STATUS = 10100;
    /**
     * 正在连接蓝牙
     */
    public static final int BOX_CONNTECING_BLE = 10101;
    /**
     * BOX_QUERY_STATUS_CALLBACK: 保管箱状态查询
     */
    public static final int BOX_QUERY_STATUS_CALLBACK = 10118;
    /**
     * 绑定时，连接BT没有找到设备
     */
    public static final int BOX_BIND_CONNECT_NODEVICE = 10129;
    /**
     * BOX_RECEIVER_READINFO:  接收保管箱读取记录信息
     */
    public static final int BOX_RECEIVER_READINFO = 10117;

    /**
     * BOX_USER_MODIFY_PASSWORD_RESULT: 用户修改密码结果
     */
    public static final int BOX_USER_MODIFY_PASSWORD_RESULT = 10116;
    /**
     * BOX_USER_UNBIND_EXIT: 用户解除绑定(退箱检测)
     */
    public static final int BOX_USER_UNBIND_EXIT = 10119;
    /**
     * BOX_USER_UNBIND_BY_HAND: 用户解除绑定(用户注销)
     */
    public static final int BOX_USER_UNBIND_BY_HAND = 10120;
    public static final int DIALOG_USER_UNBIND_STATUS_CALLBACK = 10121;
    /**
     * BOX_USER_UNBIND_RESULT: 用户绑定
     */
    public static final int BOX_USER_BIND_RESULT = 10125;
    /**
     * BOX_USER_OPENBOX_STATUS: 用户开箱 :成功 ;堵转;不予开箱,检测开关超时 ;密码比对失败，
     */
    public static final int BOX_USER_OPENBOX_STATUS = 10112;
    /**
     * 手势解锁 开箱
     */
    public static final int BOX_USER_OPENBOX_STATUS_HAND = 11112;

    /**
     * BOX_CLOSE_KEYBOARD_STATUS: 解除绑定：关闭键盘防止用户键盘开箱，
     */
    public static final int BOX_CLOSE_KEYBOARD_STATUS = 10113;
    /**
     * RECEIVER_BOX_DATA: 接收的信息：报警数据；
     */
    public static final int RECEIVER_BOX_DATA_ALARM = 10010;
    /**
     * RECEIVER_BOX_DATA: 用户在保管箱开箱数据；
     */
    public static final int RECEIVER_BOX_DATA_OPENBOX = 10011;
    /**
     * RECEIVER_BOX_DATA: 关保管箱数据
     */
    public static final int RECEIVER_BOX_DATA_CLOSEBOX = 10012;
    /**
     * RECEIVER_BOX_DATA: 插入充电线主动上传APP
     */
    public static final int RECEIVER_BOX_DATA_RECHARGE = 10013;
    public static final int BOX_RECEIVER_INFO_UNKNOW = 10031;
    /**
     * GATT创建并连接成功，此时可以发指令了
     */
    public static final int ACTION_GATT_CONNECT_SUCCESS = 32222;
    /**
     * 添加操作记录消息
     */
    public static final int ACTION_ADD_MSG = 32223;
}
