package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static java.util.Arrays.asList;

public class CarAnnouncementPage extends Page {

    protected AutoMarketPage autoMarketPage = new AutoMarketPage(driver);

    @FindBy(css = ".m-title")
    private WebElement announcementTitle;

    @FindBy(css = ".autoba-fastchars-ttl")
    private WebElement carName;

    @FindBy(css = ".year strong")
    private WebElement carYear;

    @FindBy(css = ".dist strong")
    private WebElement carDist;

    @FindBy(css = "[class*='autoba-msglongcont'] p:nth-of-type(2)")
    private WebElement carDescription;

    @Step("get announcement car information")
    public List<String> getCarInformation() {
        return asList(carName.getText(), carYear.getText(), carDist.getText(), carDescription.getText());
    }

    @Step("Verify car info")
    public CarAnnouncementPage verifyCarInformation() {
        logger.info("Verify car information");
        ElementsUtil.waitForVisible(announcementTitle);
        SoftAssert softAssert = new SoftAssert();
        ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollIntoView();", carName);
        autoMarketPage.getCarPreviewInfo().forEach(actual -> {
            this.getCarInformation().forEach(expected -> softAssert.assertTrue(actual.contains(expected), "Car information from preview not presented in announcement"));
        });

        return this;
    }

    public CarAnnouncementPage(WebDriver driver) {
        super(driver);
    }
}
