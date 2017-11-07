package com.qingwing.safebox.net.request;

import com.qingwing.safebox.net.BaseCommReq;
import com.qingwing.safebox.net.BaseResponse;
import com.qingwing.safebox.net.response.SavePasswordProtectResponse;
import com.qingwing.safebox.network.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class SavePasswordProtectReq extends BaseCommReq {
    private String url = ServerAddress.SAVE_QUESTION;
    private String userId;
    private String question1;
    private String question2;
    private String question3;
    private String answer1;
    private String answer2;
    private String answer3;
    private SavePasswordProtectResponse savePasswordProtectResponse;
    private Map<String, String> postParams = new HashMap<String, String>();

    @Override
    public String generUrl() {
        setTag("SavePasswordProtectReq");
        postParams.put("userId", userId);
        postParams.put("question1", question1);
        postParams.put("question2", question2);
        postParams.put("question3", question3);
        postParams.put("answer1", answer1);
        postParams.put("answer2", answer2);
        postParams.put("answer3", answer3);
        setPostParam(postParams);
        return url;
    }

    @Override
    public Class getResClass() {
        return SavePasswordProtectResponse.class;
    }

    @Override
    public BaseResponse getResBean() {
        if (savePasswordProtectResponse == null) {
            savePasswordProtectResponse = new SavePasswordProtectResponse();
        }
        return savePasswordProtectResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

}