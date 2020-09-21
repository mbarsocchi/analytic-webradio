/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.data;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mbarsocchi
 */
public class AdditionalMetadata {

    private Date date;
    private String ip;
    private String refer;
    private String useragent;
    private int secondOfListening;
    private String dayHash;

    public AdditionalMetadata(Date date, String ip, String refer, String useragent, int secondOfListening) {
        this.date = date;
        this.ip = ip;
        this.refer = refer;
        this.useragent = useragent;
        this.secondOfListening = secondOfListening;
        this.calculateDayHash();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        calculateDayHash();
    }

    public int getSecondOfListening() {
        return secondOfListening;
    }

    public void setSecondOfListening(int secondOfListening) {
        this.secondOfListening = secondOfListening;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        calculateDayHash();
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
        calculateDayHash();
    }

    public String getDayHash() {
        return dayHash;
    }

    private void calculateDayHash() {
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        dayHash = formatter.format(date) + ip + useragent;
    }
}
