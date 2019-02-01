package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static java.util.Arrays.asList;

@Slf4j
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

    @FindBy(css = ".autoba-msgphotos-slider img")
    private List<WebElement> photoSlideBar;

    @FindBy(css = ".autoba-viewoptions")
    private WebElement carOptionsForm;

    @Step("get announcement car information")
    public List<String> getCarInformation() {
        return asList(carName.getText(), carYear.getText(), carDist.getText(), carDescription.getText());
    }

    @Step("Verify car info")
    public CarAnnouncementPage verifyCarInformation() {
        log.info("Verify car information");
        ElementsUtil.waitForVisible(announcementTitle);
        SoftAssert softAssert = new SoftAssert();
        ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollIntoView();", carName);
        autoMarketPage.getCarPreviewInfo().forEach(actual -> {
            this.getCarInformation().forEach(expected -> softAssert.assertTrue(actual.contains(expected), "Car information from preview not presented in announcement"));
        });

        return this;
    }

    @Step("Verify car pape elements")
    public CarAnnouncementPage verifyCarAnnouncementElements() {
        log.info("Verify announcement page elements");
        SoftAssert softAssert = new SoftAssert();
        ElementsUtil.waitForVisible(announcementTitle);
        softAssert.assertTrue(!photoSlideBar.isEmpty(), "Photo slidebar is empty");
        softAssert.assertTrue(announcementTitle.isDisplayed(), "Announcement title not displayed");
        softAssert.assertTrue(carYear.isDisplayed(), "Car year not displayed");
        softAssert.assertTrue(carDist.isDisplayed(), "Car distance not displayed");
        softAssert.assertTrue(carDescription.isDisplayed(), "Car description not displayed");
        softAssert.assertTrue(carOptionsForm.isDisplayed(), "Car optipns form not displayed");
        return this;
    }

    public CarAnnouncementPage(WebDriver driver) {
        super(driver);
    }
}
