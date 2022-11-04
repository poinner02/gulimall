package com.merchen.gulimall.ware.vo;

import java.util.Date;

/**
 * @author MrChen
 * @create 2022-09-06 21:45
 */

public class Traces {

    private String AcceptStation;
    private Date AcceptTime;
    public void setAcceptStation(String AcceptStation) {
        this.AcceptStation = AcceptStation;
    }
    public String getAcceptStation() {
        return AcceptStation;
    }

    public void setAcceptTime(Date AcceptTime) {
        this.AcceptTime = AcceptTime;
    }
    public Date getAcceptTime() {
        return AcceptTime;
    }

}
