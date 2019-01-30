package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class HomePage extends Page {

    @FindBy(css = "[class='b-top-logo']")
    public WebElement onlinerLogo;

    @FindBy(css = "[class='b-main-navigation__link'][href*='ab.onliner.by']")
    private WebElement carsAnnouncement;

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
}
