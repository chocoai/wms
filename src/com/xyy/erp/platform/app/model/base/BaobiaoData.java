package com.xyy.erp.platform.app.model.base;

import com.jfinal.template.ext.directive.Str;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BaobiaoData implements Serializable {
    private List<mdoelData> xdatas;
    private List<String> ydatas;
    private List<String> legend;

    public List<mdoelData> getXdatas() {
        return xdatas;
    }

    public void setXdatas(List<mdoelData> xdatas) {
        this.xdatas = xdatas;
    }

    public List<String> getYdatas() {
        return ydatas;
    }

    public void setYdatas(List<String> ydatas) {
        this.ydatas = ydatas;
    }

    public List<String> getLegend() {
        return legend;
    }

    public void setLegend(List<String> legend) {
        this.legend = legend;
    }
}
