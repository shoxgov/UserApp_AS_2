package com.qingwing.safebox.net.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.UploadAlarmDataResponse;
import com.qingwing.safebox.net.response.UploadOpenBoxDataResponse;
import com.qingwing.safebox.network.ServerAddress;
import com.qingwing.safebox.utils.LogUtil;


public class UploadAlarmOrOpenBoxDataReq extends BaseCommReq {
    private String url = ServerAddress.USER_MOREUPLOADRECORD;
    private String data;
    private String BlueId;
    private List<String> list;
    private UpdateType type;// 999:报警  111:开关箱
    private UploadAlarmDataResponse uploadAlarmDataResponse;
    private UploadOpenBoxDataResponse uploadOpenBoxDataResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    public enum UpdateType {
        ALARM, OPENBOX
    }

    @Override
    public String generUrl() {
        setTag("UploadAlarmOrOpenBoxDataReq");
        postParams.put("data", getData());
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        switch (type) {
            case ALARM:
                return UploadAlarmDataResponse.class;
            case OPENBOX:
                return UploadOpenBoxDataResponse.class;
            default:
                return BaseResponse.class;
        }
    }

    @Override
    public BaseResponse getResBean() {
        switch (type) {
            case ALARM:
                if (uploadAlarmDataResponse == null) {
                    uploadAlarmDataResponse = new UploadAlarmDataResponse();
                }
                return uploadAlarmDataResponse;
            case OPENBOX:
                if (uploadOpenBoxDataResponse == null) {
                    uploadOpenBoxDataResponse = new UploadOpenBoxDataResponse();
                }
                return uploadOpenBoxDataResponse;
            default:
                return new BaseResponse();
        }
    }

    public String getData() {
        ArrayList<String> boxInfo = new ArrayList<String>();
        switch (type) {
            case ALARM:
                for (String string : list) {
                    String level = string.substring(0, 2);
                    if (level.contains("1")) {
                        boxInfo.add(BlueId + string.substring(2) + 8);
                    } else if (level.contains("2")) {
                        boxInfo.add(BlueId + string.substring(2) + 9);
                    } else if (level.contains("3")) {
                        boxInfo.add(BlueId + string.substring(2) + 10);
                    }
                }
                LogUtil.d("上传报警数据：" + boxInfo.toString());
                break;
            case OPENBOX:
                for (String string : list) {
                    String level = string.substring(0, 2);
                    if (level.equals("11")) {
                        boxInfo.add(BlueId + string.substring(2) + 1);
                    } else if (level.equals("12")) {
                        boxInfo.add(BlueId + string.substring(2) + 20);
                    } else if (level.equals("13")) {
                        boxInfo.add(BlueId + string.substring(2) + 21);
                    } else if (level.equals("21")) {
                        boxInfo.add(BlueId + string.substring(2) + 2);
                    } else if (level.equals("22")) {
                        boxInfo.add(BlueId + string.substring(2) + 28);
                    } else if (level.equals("23")) {
                        boxInfo.add(BlueId + string.substring(2) + 29);
                    } else if (level.equals("31")) {
                        boxInfo.add(BlueId + string.substring(2) + 24);
                    } else if (level.equals("32")) {
                        boxInfo.add(BlueId + string.substring(2) + 25);
                    } else if (level.equals("33")) {
                        boxInfo.add(BlueId + string.substring(2) + 26);
                    } else if (level.equals("04")) {
                        boxInfo.add(BlueId + string.substring(2) + 34);
                    } else if (level.equals("08")) {
                        boxInfo.add(BlueId + string.substring(2) + 33);
                    } else if (level.equals("44")) {
                        boxInfo.add(BlueId + string.substring(2) + 7);
                    } else if (level.equals("55")) {
                        boxInfo.add(BlueId + string.substring(2) + 31);
                    } else if (level.equals("66")) {
                        boxInfo.add(BlueId + string.substring(2) + 32);
                    }
                }
                LogUtil.d("上传开关箱记录：" + boxInfo.toString());
                break;
        }
        return boxInfo.toString();
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public String getBlueId() {
        return BlueId;
    }

    public void setBlueId(String blueId) {
        BlueId = blueId;
    }

}
