package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LeftMenu extends Page {

    public LeftMenu(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = "ofm-forms autoba-filters")
    private WebElement autoMarketFiltersForm;

    @FindBy(css = "[class*=body_type]")
    private List<WebElement> carBodyTypeList;

    @FindBy(css = "[class*='fuel-'] ")
    private List<WebElement> carEngineTypeList;

    @FindBy(css = "[class*=transmission-]")
    private List<WebElement> transmissionList;

    @FindBy(css = ".autoba-count .count")
    private WebElement headerAnnouncementCount;

    @Step("Get list of car filtered by body type")
    public List<WebElement> getCarBodyTypeList() {
        return carBodyTypeList;
    }

    @Step("Get list of car filtered by endinetype")
    public List<WebElement> getCarEngineTypeList() {
        return carEngineTypeList;
    }

    @Step("Get list of car filtered by transmission type")
    public List<WebElement> getCarTransmissionTypeList() {
        return transmissionList;
    }

    @Step("Get car announcement count")
    public String getHeaderAnnouncementCount() {
        ElementsUtil.waitForVisible(headerAnnouncementCount);
        return headerAnnouncementCount.getText();
    }

    public WebElement getCarType(List<WebElement> dataList, String filterData) {
        List<String> bodyTypeNames;
        List<String> carBodyTypeNames = new ArrayList<>();
        bodyTypeNames = dataList.stream().map(WebElement::getText).collect(Collectors.toList());
        for (String str : bodyTypeNames) carBodyTypeNames.add(str.split(" ")[0]);
        return dataList.get(carBodyTypeNames.indexOf(filterData)).findElement(By.cssSelector("input"));
    }

    public WebElement getCarCount(List<WebElement> dataList, String filterData) {
        List<String> bodyTypeNames;
        List<String> carBodyTypeNames = new ArrayList<>();
        bodyTypeNames = dataList.stream().map(WebElement::getText).collect(Collectors.toList());
        for (String str : bodyTypeNames) carBodyTypeNames.add(str.split(" ")[0]);
        return dataList.get(carBodyTypeNames.indexOf(filterData)).findElement(By.cssSelector(".count"));
    }
}
