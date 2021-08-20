package com.example.todolist;

import java.io.Serializable;
import java.util.Date;

public class Job implements Serializable {
    private  int id;
    private  String jobName;
    private  String jobDetail;
    private  Date dateTime;

    public Job(int id, String jobName, String jobDetail, Date dateTime) {
        this.id = id;
        this.jobName = jobName;
        this.jobDetail = jobDetail;
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDetail() {
        return jobDetail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobDetail(String jobDetail) {
        this.jobDetail = jobDetail;
    }
}
