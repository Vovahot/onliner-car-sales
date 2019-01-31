package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoMarketPage extends Page {

    private LeftMenu leftMenu = new LeftMenu(driver);

    @FindBy(css = ".js-search-title")
    private WebElement autoMarketTitle;

    @FindBy(css = "b-update-btn-1")
    private WebElement refreshButton;

    @FindBy(css = "tbody [id*='car']")
    private List<WebElement> announcementList;

    @FindBy(css = ".dist")
    private WebElement distance;

    @FindBy(css = ".year")
    private WebElement year;

    @FindBy(css = "span [href*='/car/']")
    private WebElement announcementTitle;

    @FindBy(css = "li .add-bookmark")
    private WebElement addBookmark;

    public AutoMarketPage(WebDriver driver) {
        super(driver);
    }

    private List<WebElement> carsRow;
    private WebElement carBodyType;
    private List<String> carPreviewInfo = new ArrayList<>();


    public List<WebElement> getCarBodyTypeList() {
        return leftMenu.getCarBodyTypeList();
    }

    public List<WebElement> getCarEngineTypeList() {
        return leftMenu.getCarEngineTypeList();
    }

    public List<WebElement> getCarTransmissionTypeList() {
        return leftMenu.getCarTransmissionTypeList();
    }

    @Step("Get cars announcement list")
    public AutoMarketPage verityCardList() {
        logger.info("Verify cars annoumcement list");
        ElementsUtil.waitForVisible(autoMarketTitle);
        Assert.assertTrue(!announcementList.isEmpty(), "Not fount any announcement");
        return this;
    }

    @Step("Select {} car")
    public AutoMarketPage setCarFilteryType(List<WebElement> dataList, String filterData) {
        logger.info("Select [" + filterData + "] car in car filter");
        ElementsUtil.waitForClickable(leftMenu.getCarType(dataList, filterData));
        leftMenu.getCarType(dataList, filterData).click();
        return this;
    }

    @Step("Verify car count for filter")
    public AutoMarketPage varifyCarCountForFilter(List<WebElement> dataList, String filterData) {
        logger.info("Verify car count after filtering");
        ElementsUtil.waitForVisible(leftMenu.getCarCount(dataList, filterData));
        Assert.assertTrue(leftMenu.getHeaderAnnouncementCount().equals(leftMenu.getCarCount(dataList, filterData).getText()),
                "Car count is wrong");
        return this;
    }

    private void getAnnouncementInfo(WebElement element) {
        ElementsUtil.waitForVisible(announcementTitle);
        carPreviewInfo = Arrays.asList(element.findElement(By.cssSelector("span [href*='/car/']")).getText(),
                element.findElement(By.cssSelector(".year")).getText(),
                element.findElement(By.cssSelector(".dist strong")).getText(),
                element.findElement(By.cssSelector(".txt p")).getText());
    }

    public List<String> getCarPreviewInfo() {
        return carPreviewInfo;
    }

    @Step("One first car announcement")
    public CarAnnouncementPage openFirstAnnouncement() {
        logger.info("Open car announcement");
        ElementsUtil.waitForVisible(announcementTitle);
        WebElement element = announcementList.get(0);
        getAnnouncementInfo(element);
        element.findElement(By.cssSelector("span [href*='/car/']")).click();
        return new CarAnnouncementPage(driver);
    }

}
