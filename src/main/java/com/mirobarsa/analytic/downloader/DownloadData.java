/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.downloader;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.mirobarsa.analytic.FileProcessor;

/**
 *
 * @author mbarsocchi
 */
public class DownloadData {

    static WebDriver browser;
    private String folderPath;

    public void downloadReport(String downloadFilepath, String chromeDriver, NewRadioCredentials cred) throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", chromeDriver);
        HashMap<String, Object> chromePrefs = new HashMap<>();
        this.folderPath = downloadFilepath;
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--start-maximized");
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        browser = new ChromeDriver(cap);

        enterArea(cred.getHomepage(), cred.getUsername(), cred.getPassword());
        DownloadReport(cred.getHomepage());
        browser.close();
        browser.quit();
    }

    private void enterArea(String url, String username, String password) throws IOException {
        String passwordField = "//input[@id='passwordfield']";

        browser.get(url);
        browser.findElement(By.xpath("//input[@id='usernamefield']")).sendKeys(username);
        Wait wait = new FluentWait(browser)
                .withTimeout(30, SECONDS)
                .pollingEvery(5, SECONDS)
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(passwordField)));
        browser.findElement(By.xpath(passwordField)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(passwordField)));
        browser.findElement(By.xpath(passwordField)).sendKeys(password);
        browser.findElement(By.cssSelector("#login_block > input[type=\"submit\"]")).click();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#navigation-content")));
        } catch (NoSuchElementException e) {
            takeScreenshot();
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, e.getMessage());
            browser.close();
            browser.quit();
        }
    }

    private void DownloadReport(String startpage) throws InterruptedException {
        String logXpath = "//a[@id='log_download']";
        String downloadButton = "#progresswindow > div.buttons > button:nth-child(1)";

        browser.get(startpage + "/client/index.php?page=logs");
        Wait wait = new FluentWait(browser)
                .withTimeout(30, SECONDS)
                .pollingEvery(5, SECONDS)
                .ignoring(NoSuchElementException.class);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(logXpath)));
            WebElement allLog = browser.findElement(By.xpath(logXpath));
            allLog.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(downloadButton)));
            WebElement downloadLog = browser.findElement(By.cssSelector(downloadButton));
            downloadLog.click();
            Thread.sleep(10000);
        } catch (NoSuchElementException e) {
            takeScreenshot();
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, e.getMessage());
            browser.close();
            browser.quit();
        }

    }

    private void takeScreenshot() {
        File scrFile = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
        String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date());
        String path = this.folderPath + File.separator + ts + ".png";
        Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, "Save screenshot " + path);
        try {
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException ex) {
            Logger.getLogger(DownloadData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
