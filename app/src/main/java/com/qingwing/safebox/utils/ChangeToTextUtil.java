package com.qingwing.safebox.utils;

public class ChangeToTextUtil {

    private static StringBuffer sb = new StringBuffer();

    public static StringBuffer changeToText(String data) {
        sb.delete(0, sb.length());
        String caozuo = data.substring(0, 2);
        String riqi = data.substring(2);
        String year = riqi.substring(0, 2);
        String month = riqi.substring(2, 4);
        String day = riqi.substring(4, 6);
        String hh = riqi.substring(6, 8);
        String mm = riqi.substring(8, 10);
        String ss = riqi.substring(10);

        if (caozuo != null) {
            switch (Integer.parseInt(caozuo)) {
                case 11:
                    sb.append("密码键盘开箱成功");
                    break;
                case 12:
                    sb.append("密码键盘开箱堵转");
                    break;
                case 13:
                    sb.append("密码键盘开箱超时");
                    break;
                case 21:
                    sb.append("App验证密码开箱成功");
                    break;
                case 22:
                    sb.append("App开箱堵转");
                    break;
                case 23:
                    sb.append("App开箱检测连接超时");
                    break;
                case 31:
                    sb.append("被动开箱成功");
                    break;
                case 32:
                    sb.append("被动开箱堵转");
                    break;
                case 33:
                    sb.append("被动开箱超时");
                    break;
                case 44:
                    sb.append("关箱成功");
                    break;
                case 55:
                    sb.append("关箱堵转");
                    break;
                case 66:
                    sb.append("关箱超时");
                    break;
                case 01:
                    sb.append("蓝色警报");
                    break;
                case 02:
                    sb.append("黄色警报");
                    break;
                case 03:
                    sb.append("红色警报");
                    break;
                case 04:
                    sb.append("开箱密码错误");
                    break;
                default:
                    sb.append("箱门已打开");
                    break;
            }
        }
        sb.append("\r\n" + "20" + year + "-" + month + "-" + day + "   " + hh + ":" + mm + ":" + ss).toString();
        return sb;
    }

}
