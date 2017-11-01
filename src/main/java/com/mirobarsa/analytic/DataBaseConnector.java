/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author mbarsocchi
 */
public class DataBaseConnector {

    private static DataBaseConnector instance = null;

    private Connection conn = null;

    public DataBaseConnector() {
    }

    public static DataBaseConnector getInstance(String host, String username, String password) throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DataBaseConnector();
            String myDriver = "com.mysql.jdbc.Driver";
            String myUrl = "jdbc:mysql://" + host;
            Class.forName(myDriver);
            instance.setConn(DriverManager.getConnection(myUrl, username, password));
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void close() {

    }
}
