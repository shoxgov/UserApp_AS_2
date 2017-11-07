package com.qingwing.safebox.bean;


/**
{"data":null,"dataMap":
 {
 "isOverDate":"false",//true为用户已欠费，false为服务时间内
 "isPayment":"1",//1为到期时间大于当前时间，0为当前时间大于到期时间
 "startDate":"unbind",//绑定时间，unbind为未绑定
 "onlineCode":"889344",//心跳码
 "requestCode":"75oy7k",//邀请码
 "userId":410,
 "ID":"724142145",
 "userStatus":"unbind",//bind为已经绑定保管箱，unbind反之
 "endDate":"2017-08-11T20:38:16",//到期时间，未绑定则返回时间等同于注册时间
 "blueId":"unbind",//保管箱二维码，unbind为未绑定保管箱
 "openPassword":"unbind",//开箱密码，unbind为未绑定
 "mobile":"17773119860"
 },
 "message":"登录成功！","status":"success","statusCode":200}
 */
public class UserInfo {
    /**
     * 个人头像
     */
    public static String imagePicUrl;
    /**
     * BlueId:蓝牙ID，用的太多了，全局化
     */
    public static String BlueId = "";
    /**
     * 扫描的蓝牙id
     */
    public static String QrBtId = "";
    /**
     * UserBindState: 用户是否绑定了保管箱
     */
    public static boolean UserBindState = false;

    /**
     * UserOpenBoxPassowrd: 用户开箱密码
     */
    public static String UserOpenBoxPassowrd = "";

    //用户登录密码
    public static String userLoginPassword = "";
    ///////////////////////////////
    //
    //原来的user_info存放的数据
    //版本更新的数据
    public static String updateUrl;
    //////////////////////////////
    /**
     * lock_style: 手势类型  a:手势   b:指纹  c:数字
     */
    public static String lock_style = "";
    /**
     * 服务器设备绑定和解绑
     */

    public static String isBindMeseage;
    /**
     * 环信登录密码
     */
    public static String pass = "";
    //当前用户保管箱是否已经过期
    public static boolean isOverDate = true;
    /**
     * 用户上一次退出时的记录信息
     */
    public static String recordListData;

    /**
     * lock_password: 手势密码
     */
    public static String lock_password = "";
    /**
     * userId: 用户登录的ID
     */
    public static int userId;

    //是否登录成功
    public static boolean isLoginSuccess = false;

    /**
     * account_Id: 用户看的id
     */
    public static String account_Id = "";

    /**
     * starttime endstringtime: 开始时间
     */
    public static String starttime = "";
    /**
     * 到期时间
     */
    public static String endstringtime = "";
    /**
     * 优惠码
     */
    public static String requestCode = "";
    /**
     * 在线code
     */
    public static String onlineCode = "";
    /**
     * 手机号码
     */
    public static String mobile = "";

}

	