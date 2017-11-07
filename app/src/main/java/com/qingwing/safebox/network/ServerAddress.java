package com.qingwing.safebox.network;

public class ServerAddress {
//    public static final String SERVER_URL = "http://www.keenzy.cn/depositbox/";
    	public  static final String SERVER_URL = "http://120.25.224.159/depositbox/";
    public static final String Register_URL = SERVER_URL + "user-register.action";
    public static final String USER_LOGIN_URL = SERVER_URL + "user-login.action";
    public static final String USER_REGISTER_URL = SERVER_URL + "user-register.action";
    public static final String USER_SETOPENPASSWORD_URL = SERVER_URL + "userBox-setOpenPassword.action";
    public static final String USER_OPENPASSWORDCHECK_URL = SERVER_URL + "userBox-openBox.action";
    public static final String USER_CHANGE_PASSOWRD_URL = SERVER_URL + "user-checkCardId.action";
    public static final String USER_OPEN_BOX = SERVER_URL + "userBox-openBox.action";
    public static final String USER_BIND = SERVER_URL + "userBox-bindBox.action";
    public static final String NEW_USER_BIND = SERVER_URL + "userBox-bindNewUser.action";//老用户未解绑时绑定新用户
    public static final String USER_BINDREQUEST = SERVER_URL + "userBox-requestBindBox.action";
    public static final String USER_GETTIME = SERVER_URL + "box-systemTime.action";
    public static final String OPEN_BOX_ASTOK = SERVER_URL + "userBox-openBox.action";
    public static final String USER_UNBINDBOX = SERVER_URL + "userBox-unbindBox.action";
    public static final String USER_ISCAN_UNBIND = SERVER_URL + "userBox-unbindRequest.action";
    public static final String USER_MOREUPLOADRECORD = SERVER_URL + "box-uploadrecord.action";
    public static final String Server_host = "http://192.168.10.100:8080/depositbox/package-list.action?schilId=1";
    public static final String USER_CODE = SERVER_URL + "user-code.action";
    public static final String CHECK_STUDENTNO = SERVER_URL + "user-checkStudentNo.action";//验证学号信息
    public static final String THREE_CODE = SERVER_URL + "user-passProtect.action";
    public static final String USER_PASSWORD = SERVER_URL + "user-updatePassword.action";
    public static final String SCHOOL_LIST = SERVER_URL + "school-list.action";
    public static final String SChOOL_ID = SERVER_URL + "package-list.action";
    public static final String REGSITER_PHONE = SERVER_URL + "user-registCode.action";
    public static final String REGISTER = SERVER_URL + "user-register.action";
    public static final String SAVE_QUESTION = SERVER_URL + "user-savePassProtect.action";
//    public static final String Register_code = SERVER_URL + "user-code.action";//停止使用
    public static final String Register_password = SERVER_URL + "user-updatePassword.action";
    public static final String PAY_AFTERBIND = SERVER_URL + "userBox-renew.action";
    public static final String PAY_BEFOREBIND = SERVER_URL + "payment-beforePay.action";
    //帮助中心文档网页地址
    public static final String HELP_CENTER = SERVER_URL + "mobilePages/helpMean.jsp";
    //用户分享按钮的网页
    public static final String SHARE_CENTER = SERVER_URL + "mobilePages/keenzy.jsp";
    //保管箱说明协议
    public static final String USER__AGREEMENT = SERVER_URL + "mobilePages/agree.jsp";
    //从后台得到支付宝订单详细信息
    public static final String PAY = SERVER_URL + "user-getAlipayStr.action";
    //从后台得到支付宝订单支付结果
    public static final String PAYRESULT = SERVER_URL + "user-judgResult.action";
    //从后台得到微信订单详细信息
    public static final String PAY_WEIXIN = SERVER_URL + "user-getWeiXinPay.action";
    //从后台得到微信订单支付结果
    public static final String PAY_WEIXIN_RESULT = SERVER_URL + "user-payResult.action";
    //从后台获得保管箱状态信息的接口路径
//	public static final String GET_MESSAGE = SERVER_URL+"box-findUseRecord.action";
    public static final String GET_MESSAGE1 = SERVER_URL + "box-selectUseRecord.action";
    //从后台查找15条记录
//	public static final String GET_FIFMESSAGE = SERVER_URL+"box-useRecord.action";
    //充值卡充值接口
    public static final String PAY_CARD = SERVER_URL + "refillCard-refill.action";
    //查询身份证号码
    public static final String USERCARDID = SERVER_URL + "user-findCardId.action";
    //保存身份证号码
    public static final String SAVECARDID = SERVER_URL + "user-saveCardId.action";
    //通过用户id获取手机验证码
    public static final String GETCODEBYID = SERVER_URL + "user-findCodeByUserId.action";
    //修改绑定手机号码
    public static final String BINDNEWPHONE = SERVER_URL + "user-updateMobile.action";
    //登录互斥的CODE
    public static final String ONLINECODE = SERVER_URL + "user-heartUser.action";
    //后台更新APP
    public static final String APK_UPDATE = SERVER_URL + "appVersions-checkVersions.action";
    //用户提交反馈意见
    public static final String FEEDBACK = SERVER_URL + "suggestion-userAdvice.action";
    //获取学生入学年份
    public static final String GRADE_YEAR = SERVER_URL + "user-selectYear.action";
    //查询用户意见
    public static final String USERSUGESTION = SERVER_URL + "suggestion-userSuggestion.action";
    //得到用户的密保问题和密保答案
    public static final String REQUESTQUESTION = SERVER_URL + "user-passProtect.action";
    //从服务器获取图片
    public static final String UPLOADIMG = SERVER_URL + "upload-uploadPros.action";
    //查看押金
    public static final String DEPOSITAMOUNT = SERVER_URL + "user-checkDepositAmount.action";
    //请求退还押金接口
    public static final String ISREFUNDDEPOSIT = SERVER_URL + "user-isRefundDeposit.action";
    //退还押金接口
    public static final String RETURNDEPOSIT = SERVER_URL + "userDeposit-refund.action";
    //后台发送开箱密码到手机
    public static final String SENDOPENPASSWORD = SERVER_URL + "userBox-setOpenPassword.action";
    //开关箱信息及时上传
    public static final String OPENBOXUPLOAD = SERVER_URL + "box-openorclose.action";
    //判断验证码是否过期
    public static final String CODEISDUE = SERVER_URL + "user-isDue.action";
    public static final String PAYRECORD = SERVER_URL + "payment-list.action";
    //报警信息及时上传
    public static final String BOXWARNBOXUPLOAD = SERVER_URL + "box-boxWarn.action";
}