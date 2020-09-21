package com.mirobarsa.analytic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mirobarsa.analytic.downloader.DownloadData;
import com.mirobarsa.analytic.downloader.NewRadioCredentials;

/**
 *
 * @author mbarsocchi
 */
public class TestAccesParsing {

    private static final Pattern ZIP_PATTERN = Pattern.compile("stream_logs_radiocit-....-..-..\\.zip");

    public static void main(String[] args) throws IOException, FileNotFoundException, ParseException, ClassNotFoundException, SQLException, InterruptedException {
        if (args.length == 0) {
            System.out.println("ERROR. No property file given");
            return;
        }
        InputStream input = new FileInputStream(args[0]);
        if (input == null) {
            System.out.println("Sorry, unable to find " + args[0]);
            return;
        }

        Properties prop = new Properties();
        prop.load(input);

        String folder = prop.getProperty("folder.path");
        String chromeDriver = prop.getProperty("chrome.driver");

        NewRadioCredentials nrCred = new NewRadioCredentials(prop.getProperty("newradio.homepage"), prop.getProperty("newradio.username"), prop.getProperty("newradio.password"));

        String dbhost = prop.getProperty("database.host");
        String dbusername = prop.getProperty("database.user");
        String dbpassword = prop.getProperty("database.password");

        if (folder == null
                || dbhost == null
                || dbusername == null
                || dbpassword == null
                || chromeDriver == null
                || nrCred == null) {
            System.out.println("Error parsing property file: " + args[0]);
            return;
        }

        DownloadData dl = new DownloadData();
        dl.downloadReport(folder, chromeDriver, nrCred);
        FileProcessor fp = new FileProcessor(folder);
        File processedFolder = new File(folder + File.separator + "processed");
        if (!processedFolder.exists() || !processedFolder.isDirectory()) {
            processedFolder.mkdir();
        }

        File[] files = new File(folder).listFiles();
        if (files != null) {
            for (File file : files) {
                Matcher matchAcces = ZIP_PATTERN.matcher(file.getName());
                if (file.isFile() && matchAcces.find()) {
                    fp.unzipArchive(file.getName());
                }
            }
        }

        DataBaseConnector db = DataBaseConnector.getInstance(dbhost, dbusername, dbpassword);
        fp.processFiles(db.getConn());
        db.close();
    }
}
