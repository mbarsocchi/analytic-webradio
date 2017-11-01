/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbarsocchi
 */
public class FileProcessor {

    private final String folderPath;

    public FileProcessor(String folderPath) {
        this.folderPath = folderPath;
        File processedFolder = new File(folderPath + File.separator + "processed");
        if (!processedFolder.exists() || !processedFolder.isDirectory()) {
            processedFolder.mkdir();
        }
    }

    public void unzipArchive(String archiveName) throws IOException {
        File archiveFile = new File(this.folderPath + archiveName);
        if (archiveFile.exists() && archiveFile.isFile()) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, "Unzipping " + archiveName);
            UnzipUtility unzipUtil = new UnzipUtility(this.folderPath);
            unzipUtil.unzip(archiveFile.getPath());
            Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, archiveName + " unzipped, deleting it");
            archiveFile.delete();
            Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, archiveName + " deleted.");
        } else {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, this.folderPath + archiveName + " not found or it's not a file.");
        }
    }

    public void processFiles(Connection dbCon) throws FileNotFoundException, IOException, ParseException, ClassNotFoundException, SQLException {
        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());

        AccessLogParser accessLogParser = new AccessLogParser();
        for (String filename : accessLogParser.getFilenameArrays()) {
            File accFile = new File(folderPath + filename);
            if (accFile.exists()) {
                accessLogParser.setAccessFile(accFile);
                Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, "Processing " + accFile.getName());
                accessLogParser.parseFile(dbCon);
                try {
                    accessLogParser.insertInDb(dbCon);
                    Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, accFile.getName() + " inserted in DB, move to folder processed" + File.separator + ts);
                    moveToProcessed(accFile, ts);
                } catch (SQLException ex) {
                    Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, "Can not find file " + accFile.getName());
            }
        }

        ErrorLogParser ep = new ErrorLogParser();
        for (String filename : ep.getFilenameArrays()) {
            File errFile = new File(folderPath + filename);
            if (errFile.exists()) {
                ep.setErrorFile(errFile);
                Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, "Processing " + errFile.getName());
                ep.parseFile(dbCon);
                try {
                    ep.insertInDb(dbCon);
                    Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, errFile.getName() + " inserted in DB, move to folder processed" + File.separator + ts);
                    moveToProcessed(errFile, ts);
                } catch (SQLException ex) {
                    Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.INFO, "Can not find file " + errFile.getName());
            }
        }
    }

    private void moveToProcessed(File fileToMove, String timestamp) {
        String processedDir = fileToMove.getParentFile().getPath() + File.separator + "processed" + File.separator + timestamp;
        File dir = new File(processedDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (dir.canWrite()) {
            fileToMove.renameTo(new File(processedDir + File.separator + fileToMove.getName()));
        } else {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, "Error for directory path: " + processedDir + " [not exists, not folder, can not write]");
        }
    }
}
