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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mirobarsa.analytic.data.AdditionalMetadata;

/**
 *
 * @author mbarsocchi
 */
public class AccessLogParser {

    String[] filenameArrays = {"access.log.4", "access.log.3", "access.log.2", "access.log.1", "access.log"};
    private static final Pattern ACCESS_PATTERN = Pattern.compile("access\\.log(\\.\\d)?");

    private int parsedLines;
    private List<AdditionalMetadata> accessArray = new ArrayList<>();
    private static final int MAX_PER_BATCH = 100;
    private static final Pattern p = Pattern.compile("(.+?) - - \\[(.+?)\\] \"GET \\/stream HTTP.+?\".+?\"(.+?)\" \"(.+?)\" (.+)");
    private static final Pattern ipMatch = Pattern.compile("\\[ip:(.+)\\]");
    private File accessFile;

    public void setAccessFile(File accessFile) {
        this.accessFile = accessFile;
    }

    public static Pattern getACCESS_PATTERN() {
        return ACCESS_PATTERN;
    }

    public String[] getFilenameArrays() {
        return filenameArrays;
    }

    public void parseFile(Connection conn) throws IOException, SQLException {
        String readLine = "";
        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy:kk:mm:ss Z", Locale.US);
        parsedLines = 0;
        Date latestEntry = getlatestEntry(conn);
        latestEntry = latestEntry == null ? new Date(0) : latestEntry;
        BufferedReader a = new BufferedReader(new FileReader(accessFile));

        while ((readLine = a.readLine()) != null) {
            Matcher m = p.matcher(readLine);
            if (m.find()) {
                Date parsedDate = null;
                try {
                    parsedDate = df.parse(m.group(2));
                } catch (ParseException ex) {
                    Logger.getLogger(AccessLogParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (parsedDate != null && parsedDate.after(latestEntry)) {
                    String ip = m.group(1);
                    String refer = m.group(3);
                    String userAgent = m.group(4);
                    Matcher matcher = ipMatch.matcher(userAgent);
                    String ipInternal = null;
                    if (matcher.find()) {
                        ipInternal = matcher.group(1);
                        if (matcher.group(0).equals(userAgent)) {
                            userAgent = "-";
                        }
                    }

                    if (ipInternal != null && !"".equals(ipInternal)) {
                        ip = ipInternal;
                    }
                    int secondOfListening = Integer.parseInt(m.group(5));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    calendar.set(Calendar.SECOND, (calendar.get(Calendar.SECOND) - secondOfListening));
                    parsedDate = calendar.getTime();
                    if (secondOfListening > 0) {
                        accessArray.add(new AdditionalMetadata(parsedDate, ip, refer, userAgent, secondOfListening));
                    }
                } else {
                    continue;
                }

            }
            parsedLines++;
        }
        a.close();
    }

    private Date getlatestEntry(Connection conn) throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("SELECT date FROM `access` ORDER BY `access`.`date` DESC LIMIT 1");
        ResultSet result = Statement.executeQuery();
        Date resultDate = null;
        while (result.next()) {
            resultDate = new Date(result.getTimestamp("date").getTime());
        }
        return resultDate;
    }

    private Date getOldestEntry(Connection conn) throws SQLException {
        PreparedStatement Statement = conn.prepareStatement("SELECT date FROM `access` ORDER BY `access`.`date` ASC LIMIT 1");
        ResultSet result = Statement.executeQuery();
        Date resultDate = null;
        while (result.next()) {
            resultDate = new Date(result.getTimestamp("date").getTime());
        }
        return resultDate;
    }

    public void insertInDb(Connection conn) throws SQLException {
        String query = " insert into access (date, hash, ip, user_agent, refer, listening_seconds)"
                + " values (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = conn.prepareStatement(query);
        int i = 0;
        for (AdditionalMetadata entity : accessArray) {
            statement.setTimestamp(1, new Timestamp(entity.getDate().getTime()));
            statement.setString(2, entity.getDayHash());
            statement.setString(3, entity.getIp());
            statement.setString(4, entity.getUseragent());
            statement.setString(5, entity.getRefer());
            statement.setInt(6, entity.getSecondOfListening());
            statement.addBatch();
            i++;
            if (i % MAX_PER_BATCH == 0 || i == accessArray.size()) {
                statement.executeBatch();
            }
        }
    }

    public int getParsedLines() {
        return parsedLines;
    }

    public List<AdditionalMetadata> getAccessArray() {
        return accessArray;
    }

    public void setAccessArray(List<AdditionalMetadata> accessArray) {
        this.accessArray = accessArray;
    }

}
