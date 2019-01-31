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

public class HomePage extends Page {

    private List<String> itemPreviewInfo = new ArrayList<>();

    @FindBy(css = "[class='b-top-logo']")
    public WebElement onlinerLogo;

    @FindBy(css = "[class='b-main-navigation__link'][href*='ab.onliner.by']")
    private WebElement carsAnnouncement;

    @FindBy(css = "form .fast-search__input")
    private WebElement searchField;

    @FindBy(css = "[class*='result__item']")
    private List<WebElement> searchResult;

    @FindBy(css = ".modal-iframe")
    private WebElement searchResultFrame;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage verifyHomePageLogo() {
        logger.info("Verify onliner logo");
        ElementsUtil.waitForVisible(onlinerLogo);
        Assert.assertTrue(onlinerLogo.isDisplayed());
        return this;
    }

    @Step("Click on carsAnnouncement")
    public void clickOnCarAnnouncement() {
        logger.info("Click on car market button");
        carsAnnouncement.click();
    }

    @Step("Open Car Market page")
    public AutoMarketPage openAnnouncementCarPage() {
        ElementsUtil.waitForClickable(carsAnnouncement);
        clickOnCarAnnouncement();
        return new AutoMarketPage(driver);
    }

    @Step("Enter search data")
    public HomePage enterSearchData() {
        logger.info("Set data into search field");
        ElementsUtil.waitForVisible(searchField);
        searchField.click();
        searchField.sendKeys("iphone");
        return this;
    }

    private List<String> getSearchItemInfo(WebElement element) {
        return Arrays.asList(
                element.findElement(By.cssSelector(".product__title-link")).getText(),
                element.findElement(By.cssSelector(".product__description")).getText()
        );
    }

    @Step("Select first search result")
    public List<String> selectFirstSearchResult() {
        logger.info("Open first result from search");
        driver.switchTo().frame(searchResultFrame);

        ElementsUtil.waitForVisible(driver.findElement(By.cssSelector(".search__results")));
        WebElement element = searchResult.get(0);
        List<String> searchItemInfo = getSearchItemInfo(element);

        element.click();
        return searchItemInfo;
    }

}
