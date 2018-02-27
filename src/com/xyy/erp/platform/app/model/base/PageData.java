package com.xyy.erp.platform.app.model.base;

import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class PageData {

    private String hasData  ;
    private List<Record> recordList;

    public String gethasData () {
        return hasData ;
    }

    public void sethasData (String hasData ) {
        this.hasData  = hasData ;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
