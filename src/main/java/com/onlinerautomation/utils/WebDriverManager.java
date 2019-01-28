package com.onlinerautomation.utils;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

import java.io.IOException;

public class WebDriverManager {
    public static void setupWebDriver(SuiteConfiguration suiteConfiguration) throws IOException {
        switch (suiteConfiguration.getCapabilities().getBrowserName()) {
            case "firefox":
                FirefoxDriverManager.firefoxdriver().version(suiteConfiguration.getProperty("firefox-driver.version")).setup();
                break;
            case "MicrosoftEdge":
                EdgeDriverManager.edgedriver().version(suiteConfiguration.getProperty("edge-driver.version")).setup();
                break;
            case "internet explorer":
                InternetExplorerDriverManager.iedriver().version(suiteConfiguration.getProperty("ie-driver.version")).arch32().setup();
                break;
            case "chrome":
            default:
                ChromeDriverManager.chromedriver().version(suiteConfiguration.getProperty("chrome-driver.version")).setup();
        }
    }
}
