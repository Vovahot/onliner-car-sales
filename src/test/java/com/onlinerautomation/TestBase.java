package com.onlinerautomation;

import com.onlinerautomation.utils.SuiteConfiguration;
import io.qameta.allure.testng.AllureTestNg;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import ru.stqa.selenium.factory.WebDriverPool;
import utils.listeners.AllureListener;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.onlinerautomation.utils.WebDriverManager.setupWebDriver;

@Slf4j
@Listeners({AllureTestNg.class, AllureListener.class})
public abstract class TestBase {


    protected WebDriver driver;
    protected static String baseUrl;
    protected static String basePath;
    protected static Capabilities capabilities;

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeSuite
    public void initTestSuite() throws IOException {
        SuiteConfiguration config = new SuiteConfiguration();
        baseUrl = config.getProperty("site.url");
        basePath = config.getProperty("server.base");
        capabilities = config.getCapabilities();
        setupWebDriver(config);
    }

    @BeforeMethod
    public void prepareForTestMethod(Method method) {
        log.info("Method name: " + method.getName());
        initWebDriver();
        openTargetPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (!WebDriverPool.DEFAULT.isEmpty()) {
            log.info("Close WebDriver");
            WebDriverPool.DEFAULT.dismissAll();
        }
    }

    private void initWebDriver() {
        log.info("Web driver was initialized");
        driver = WebDriverPool.DEFAULT.getDriver(capabilities);
        driver.manage().window().maximize();
    }

    public abstract void openTargetPage();
}
