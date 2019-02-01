package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import com.sun.javafx.binding.StringFormatter;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.onlinerautomation.utils.ElementsUtil.waitForVisible;

@Slf4j
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
        log.info("Verify cars annoumcement list");
        waitForVisible(autoMarketTitle);
        Assert.assertTrue(!announcementList.isEmpty(), "Not fount any announcement");
        return this;
    }

    @Step("Select {} car")
    public AutoMarketPage setCarFilteryType(List<WebElement> dataList, String filterData) {
        log.info("Select [" + filterData + "] car in car filter");
        ElementsUtil.waitForClickable(leftMenu.getCarType(dataList, filterData));
        leftMenu.getCarType(dataList, filterData).click();
        return this;
    }

    @Step("Verify car count for filter")
    public AutoMarketPage varifyCarCountForFilter(List<WebElement> dataList, String filterData) {
        log.info("Verify car count after filtering");
        waitForVisible(leftMenu.getCarCount(dataList, filterData));
        Assert.assertTrue(leftMenu.getHeaderAnnouncementCount().equals(leftMenu.getCarCount(dataList, filterData).getText()),
                "Car count is wrong");
        return this;
    }

    private void getAnnouncementInfo(WebElement element) {
        waitForVisible(announcementTitle);
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
        log.info("Open car announcement");
        waitForVisible(announcementTitle);
        WebElement element = announcementList.get(0);
        getAnnouncementInfo(element);
        element.findElement(By.cssSelector("span [href*='/car/']")).click();
        return new CarAnnouncementPage(driver);
    }

    @Step("Select min price")
    public AutoMarketPage selectkMinPrice(String data) {
        log.info("Select from min price");
        leftMenu.setMinPriceValue(data);
        return this;
    }

    @Step("Select max price")
    public AutoMarketPage selectMaxPrice(String data) {
        log.info("Select from max price");
        leftMenu.setMaxPrice(data);
        return this;
    }

    @Step("Verify filtering by price")
    public AutoMarketPage verifyCarList(String minData, String maxData) {
        log.info("Verify car list with min [" + minData + " ] and max { " + maxData + " ] price");
        waitForVisible(autoMarketTitle);
        announcementList.forEach(element -> Assert.assertTrue(element.findElement(By.cssSelector(".small")).getText().split("\n")[0].equals(minData + " $") ||
                        element.findElement(By.cssSelector(".small")).getText().split("\n")[0].equals(maxData + " $"),
                "Announcement Car list not contains min[ " + minData + "$" + " ] max { " + maxData + "$" + " ] prices"));
        return this;
    }


}
