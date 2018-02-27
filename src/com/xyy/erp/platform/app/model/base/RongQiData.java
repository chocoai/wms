package com.xyy.erp.platform.app.model.base;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class RongQiData {

    private Record record;

    private List<Record> recordList;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
