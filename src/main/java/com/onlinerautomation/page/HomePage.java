package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

public class HomePage extends Page {

    @FindBy(css = "[class='b-top-logo']")
    public WebElement onlinerLogo;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage verifyHomePageLogo() {
        logger.info("Verify onliner logo");
        ElementsUtil.waitForVisible(onlinerLogo);
        Assert.assertTrue(onlinerLogo.isDisplayed());
        return this;
    }
}
