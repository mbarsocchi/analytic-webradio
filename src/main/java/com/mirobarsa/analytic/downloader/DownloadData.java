/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.downloader;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mirobarsa.analytic.FileProcessor;

/**
 *
 * @author mbarsocchi
 */
public class DownloadData {

    static WebDriver browser;

    public void downloadReport(String downloadFilepath, String chromeDriver, NewRadioCredentials cred) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", chromeDriver);
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        browser = new ChromeDriver(cap);
        browser.get(cred.getHomepage());
        browser.findElement(By.xpath("//input[@id='usernamefield']")).sendKeys(cred.getUsername());
        browser.findElement(By.xpath("//input[@id='passwordfield']")).sendKeys(cred.getPassword());
        browser.findElement(By.cssSelector("#login_block > input[type=\"submit\"]")).click();
        browser.get(cred.getHomepage() + "/client/index.php?page=logs");
        WebElement allLog = browser.findElement(By.xpath("//a[@id='log_download']"));
        Boolean isPresent = browser.findElements(By.xpath("//a[@id='log_download']")).size() > 0;
        if (isPresent) {
            allLog.click();
            WebDriverWait wait = new WebDriverWait(browser, 20);
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#progresswindow > div.buttons > button:nth-child(1)")));
            WebElement downloadLog = browser.findElement(By.cssSelector("#progresswindow > div.buttons > button:nth-child(1)"));
            downloadLog.click();
            Thread.sleep(10000);
        } else {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, "Unable to locate element: //a[@id='log_download']");
        }
        browser.close();
        browser.quit();
    }
}
