package com.onlinerautomation;

import com.onlinerautomation.utils.SuiteConfiguration;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import ru.stqa.selenium.factory.WebDriverPool;
import utils.listeners.AllureListener;

import java.io.IOException;
import java.lang.reflect.Method;

import static com.onlinerautomation.utils.WebDriverManager.setupWebDriver;

@Listeners({AllureTestNg.class, AllureListener.class})
public abstract class TestBase {


    protected WebDriver driver;
    protected static String baseUrl;
    protected static String basePath;
    protected static Capabilities capabilities;
    protected Logger logger;

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

    @BeforeClass
    public void initTestClass() {
        logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Initialize test class");
    }

    @BeforeMethod
    public void prepareForTestMethod(Method method) {
        logger.info("Method name: " + method.getName());
        initWebDriver();
        openTargetPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (!WebDriverPool.DEFAULT.isEmpty()) {
            logger.info("Close WebDriver");
            WebDriverPool.DEFAULT.dismissAll();
        }
    }

    private void initWebDriver() {
        logger.info("Web driver was initialized");
        driver = WebDriverPool.DEFAULT.getDriver(capabilities);
        driver.manage().window().maximize();
    }

    public abstract void openTargetPage();
}
