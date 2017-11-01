/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mirobarsa.analytic.data.HistoricListner;

/**
 *
 * @author mbarsocchi
 */
public class ErrorLogParser {

    private static final Pattern ERROR_PATTERN = Pattern.compile("error\\.log(\\.\\d)?");
    String[] filenameArrays = {"error.log.4", "error.log.3", "error.log.2", "error.log.1", "error.log"};

    private static final int MAX_PER_BATCH = 100;
    private List<HistoricListner> listnerArray = new ArrayList<>();
    private static final Pattern p = Pattern.compile("\\[(....\\-..\\-..  ..:..:..)\\] INFO source\\/source_main listener count on \\/stream now (\\d+)");
    private int parsedLines;
    private File errorFile;

    public static Pattern getERROR_PATTERN() {
        return ERROR_PATTERN;
    }

    public void setErrorFile(File errorFile) {
        this.errorFile = errorFile;
    }

    public void parseFile(Connection conn) throws IOException, ParseException, SQLException {
        String readLine = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd  kk:mm:ss");
        parsedLines = 0;
        Date latestEntry = getlatestEntry(conn);
        latestEntry = latestEntry == null ? new Date(0) : latestEntry;
        BufferedReader b = new BufferedReader(new FileReader(errorFile));

        while ((readLine = b.readLine()) != null) {
            Matcher m = p.matcher(readLine);
            if (m.find()) {
                Date parsedDate = df.parse(m.group(1));
                if (parsedDate.after(latestEntry)) {
                    int actualListner = Integer.parseInt(m.group(2));
                    listnerArray.add(new HistoricListner(parsedDate, actualListner));
                } else {
                    break;
                }

            }
            parsedLines++;
        }
        b.close();
    }

    private Date getlatestEntry(Connection conn) throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("SELECT date FROM `listner` ORDER BY `listner`.`date` DESC LIMIT 1");
        ResultSet result = Statement.executeQuery();
        Date resultDate = null;
        while (result.next()) {
            resultDate = new Date(result.getTimestamp("date").getTime());
        }
        return resultDate;

    }

    public void insertInDb(Connection conn) throws SQLException {
        String query = " insert into listner (date, listner_number)"
                + " values (?, ?)";

        PreparedStatement statement = conn.prepareStatement(query);
        int i = 0;
        for (HistoricListner entity : listnerArray) {
            statement.setTimestamp(1, new Timestamp(entity.getEventDate().getTime()));
            statement.setInt(2, entity.getListner());
            statement.addBatch();
            i++;
            if (i % MAX_PER_BATCH == 0 || i == listnerArray.size()) {
                statement.executeBatch();
            }
        }
    }

    public List<HistoricListner> getListnerArray() {
        return listnerArray;
    }

    public void setListnerArray(List<HistoricListner> listnerArray) {
        this.listnerArray = listnerArray;
    }

    public int getParsedLines() {
        return parsedLines;
    }

    private Date getOldestEntry(Connection conn) throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("SELECT date FROM `listner` ORDER BY `listner`.`date` ASC LIMIT 1");
        ResultSet result = Statement.executeQuery();
        Date resultDate = null;
        while (result.next()) {
            resultDate = new Date(result.getTimestamp("date").getTime());
        }
        return resultDate;
    }

    String[] getFilenameArrays() {
        return filenameArrays;
    }

}
