/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.data;

/**
 *
 * @author mbarsocchi
 */
public class SessionMetadata {

    private int listnerTotalTime;
    private String userAgent;
    private String id;
    private String ip;

    public SessionMetadata(int listnerTotalTime, String userAgent, String id, String ip) {
        this.listnerTotalTime = listnerTotalTime;
        this.userAgent = userAgent;
        this.id = id;
        this.ip = ip;
    }

    public int getListnerTotalTime() {
        return listnerTotalTime;
    }

    public void setListnerTotalTime(int listnerTotalTime) {
        this.listnerTotalTime = listnerTotalTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


}
