package com.zwyl.myhomework.main.detaile;

public class BeanDetaile {
    /**
     * onlineWorkName : 第一节-家庭作业
     * workPushLogId : workpushid001
     * onlineWorkId : wonlineworkid001
     * startTime : 2019-01-17 15:59:59
     * endTime : 2019-01-17 15:59:59
     * onlineWorkType : 50101
     * status : 40101
     */

    public String onlineWorkName;
    public String workPushLogId;
    public String onlineWorkId;
    public String startTime;
    public String endTime;
    public int onlineWorkType;
    public int Status;

    @Override
    public String toString() {
        return "BeanDetaile{" +
                "onlineWorkName='" + onlineWorkName + '\'' +
                ", workPushLogId='" + workPushLogId + '\'' +
                ", onlineWorkId='" + onlineWorkId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", onlineWorkType=" + onlineWorkType +
                ", status=" + Status +
                '}';
    }
}
