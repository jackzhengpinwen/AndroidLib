package com.zpw.net.stock;

import java.util.List;

public class GZLLResponse {
    private String ycDefId;
    private String ycDefName;
    private String worktime;
    private List<List<Double>> seriesData;
    private Boolean isPoint;
    private Double dcq;
    private String qxywmc;

    public String getYcDefId() {
        return ycDefId;
    }

    public void setYcDefId(String ycDefId) {
        this.ycDefId = ycDefId;
    }

    public String getYcDefName() {
        return ycDefName;
    }

    public void setYcDefName(String ycDefName) {
        this.ycDefName = ycDefName;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

    public List<List<Double>> getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(List<List<Double>> seriesData) {
        this.seriesData = seriesData;
    }

    public Boolean getPoint() {
        return isPoint;
    }

    public void setPoint(Boolean point) {
        isPoint = point;
    }

    public Double getDcq() {
        return dcq;
    }

    public void setDcq(Double dcq) {
        this.dcq = dcq;
    }

    public String getQxywmc() {
        return qxywmc;
    }

    public void setQxywmc(String qxywmc) {
        this.qxywmc = qxywmc;
    }
}
