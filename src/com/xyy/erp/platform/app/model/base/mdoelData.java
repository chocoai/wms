package com.xyy.erp.platform.app.model.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class mdoelData implements Serializable {
    private List<Long> data;
    private String name;
    private String type;
    private boolean show=true;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public List<Long> getData() {
        return data;
    }

    public void setData(List<Long> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
