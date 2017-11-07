package com.qingwing.safebox.net.response;

import com.qingwing.safebox.net.BaseResponse;

import java.util.List;


/**
 * @Class: SafePasswordQuestionResponse
 * @version: V1.0
 */
public class SafePasswordQuestionResponse extends BaseResponse {
    /**
     * serialVersionUID: TODO(描述变量)
     */
    private static final long serialVersionUID = 1L;
    private String status;
    private String message;

    private DataMap dataMap;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(DataMap dataMap) {
        this.dataMap = dataMap;
    }

    public class DataMap {
        private List<QuestAndAnswer> passprotectList;

        public List<QuestAndAnswer> getPassprotectList() {
            return passprotectList;
        }

        public void setPassprotectList(List<QuestAndAnswer> passprotectList) {
            this.passprotectList = passprotectList;
        }

    }

    public class QuestAndAnswer {
        private String answer1;
        private String question1;
        private String answer2;
        private String question2;
        private String answer3;
        private String question3;

        public String getAnswer1() {
            return answer1;
        }

        public void setAnswer1(String answer1) {
            this.answer1 = answer1;
        }

        public String getQuestion1() {
            return question1;
        }

        public void setQuestion1(String question1) {
            this.question1 = question1;
        }

        public String getAnswer2() {
            return answer2;
        }

        public void setAnswer2(String answer2) {
            this.answer2 = answer2;
        }

        public String getQuestion2() {
            return question2;
        }

        public void setQuestion2(String question2) {
            this.question2 = question2;
        }

        public String getAnswer3() {
            return answer3;
        }

        public void setAnswer3(String answer3) {
            this.answer3 = answer3;
        }

        public String getQuestion3() {
            return question3;
        }

        public void setQuestion3(String question3) {
            this.question3 = question3;
        }

    }
}
