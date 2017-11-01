/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.data;

import java.util.Date;

/**
 *
 * @author mbarsocchi
 */
public class HistoricListner {

    private Date eventDate;
    private int listner;

    public HistoricListner(Date eventDate, int listner) {
        this.eventDate = eventDate;
        this.listner = listner;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getListner() {
        return listner;
    }

    public void setListner(int listner) {
        this.listner = listner;
    }

}
