/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.analytic.downloader;

/**
 *
 * @author mbarsocchi
 */
public class NewRadioCredentials {

    private final String homepage;
    private final String username;
    private final String password;

    public NewRadioCredentials(String homepage, String username, String password) {
        this.homepage = homepage;
        this.username = username;
        this.password = password;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


}
