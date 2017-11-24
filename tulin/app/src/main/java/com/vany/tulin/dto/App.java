package com.vany.tulin.dto;

/**
 * Created by van元 on 2017/2/21.
 */

public class App {
    private String appName;
    private String packageName;

    public App() {
    }

    public App(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
