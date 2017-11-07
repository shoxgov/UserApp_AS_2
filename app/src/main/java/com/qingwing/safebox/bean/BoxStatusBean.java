package com.qingwing.safebox.bean;

/**
 保管箱获取本机信息后返回应答给APP：
 AA AA|                 		包头
 00 01 00 03 | 				保管箱ID
 01 18 | 	    					命令
 20 12 12 |					服务到期时间（3字节）
 00|							保管箱生命周期状态（1字节）
 00|							保管箱故障状态位（1字节）APP对这位不处理因为该故障位并不是保管箱实时检测的结果
 00|							开关箱状态（1字节）
 32|							电量（1字节）
 00 |							充电状态（1字节）
 00 00 |						记录条数信息（2字节）
 XX XX						校验（2字节）
 保管箱生命周期状态：
 00代表出厂初始状态（状态0）
 02代表出厂检验完成并已经设置保管箱ID号（状态1）
 04代表位置绑定完成但尚未有客户绑定（状态2）
 08代表客户绑定，且处于正常缴费期（状态3）
 0x10代表客户绑定，但是服务已经到期。
 保管箱故障状态位，取或的关系，可能有多个故障出现，这里出现的异常仅仅只是程序在运行中发现的异常，保管箱重新复位后，可能所有的异常位会全部清零，直到运行中重新发现异常。断电不做保存。
 0x20代表外部EEPROM检测是否正常
 10代表flash检测是否异常
 08代表时钟检测是否异常
 04代表小无线通讯是否异常
 02代表左开关坏
 01代表右开关坏

 开关箱状态：，0代表关，0xaa代表门开，0x22代表处于不明确的中间位置（堵转）
 电量：返回的电量从十进制0-100，并以10为步进，代表电池商剩余的百分比为，10%-100%，如果返回的值为0xff代表当前测量错误，APP保持原有的值即可。
 充电状态：为0，代表没有充电。为1，代表正在充电且没有冲满为2，代表正在充电且已经冲满。
 记录信息第一字节代表报警数据条数，第二字节代表为开关箱数据条数。

 本条指令相当的重要，一般在用户手机重新连上保管箱后，即进行数据发送，此命令作用有：
 4、作为APP和保管箱之间的口令，在蓝牙模块连接上APP后，在5秒内必须获得此命令或者是开箱指令，否则就认为不是本公司APP连接，直接断开连接。（APP连接上模块后，如果用户点了开箱按钮，那么优先发送开箱指令，否则就要发送状态查询指令）
 5、保管箱时间校时，从服务器取得系统时间，对保管箱时间进行校正。
 6、获取保管箱存储的记录条数，并随后下发获取记录的指令得到保管箱内保存的数据记录。
 */
public class BoxStatusBean {
    /**
     * 00代表出厂初始状态（状态0）
     * 02代表出厂检验完成并已经设置保管箱ID号（状态1）
     * 04代表位置绑定完成但尚未有客户绑定（状态2）
     * 08代表客户绑定，且处于正常缴费期（状态3）
     * 0x10代表客户绑定，但是服务已经到期。(状态4）
     */
    public static int lifeStatus = -1;
    /**
     * 返回的电量从十进制0-100，并以10为步进，代表电池商剩余的百分比为，10%-100%，
     * 如果返回的值为0xff代表当前测量错误，APP保持原有的值即可。
     */
    public static String electricValue = "";
    /**
     * 服务到期时间
     */
    public static String serviceEndTime = "";
    //不加“/"符的原始格式
    public static String serviceEndTimeBineary = "";
    /**
     * 记录信息第一字节代表报警数据条数
     */
    public static int recordAlarmCount = 0;
    /**
     * 第二字节代表为开关箱数据条数
     */
    public static int recordOpenCount = 0;
    /**
     * 0代表关，
     * 0xaa代表门开，
     * 0x22代表处于不明确的中间位置（堵转）
     */
    public static String openStatus = "";
//    public static String chongdianzhuangtai = "";
    //    public static String BoxTroubleState = "";
}