package com.onlinerautomation.page;

import com.onlinerautomation.utils.ElementsUtil;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

@Slf4j
public class ProductPage extends Page {

    @FindBy(css = "[class*='masthead__title']")
    private WebElement goodTitle;

    @FindBy(css = "[itemprop=description]")
    private WebElement itemDescription;

    @FindBy(css = "[id='specs']")
    private WebElement productSpec;

    public List<String> getProductInfo() {
        return asList(goodTitle.getText().replaceAll("Смартфон ", ""), itemDescription.getText());
    }

    @Step("Verify product information")
    public ProductPage verifyProductInfo(List<String> title) {
        log.info("Verify product information");
        ElementsUtil.waitForVisible(goodTitle);
        assertEquals(getProductInfo(), title, "Product information is not correct");
        return this;
    }

    @Step("Verify shop item elements")
    public ProductPage verifyItemElements() {
        log.info("Verify page elements");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(goodTitle.isDisplayed(), "Item title not displayed");
        softAssert.assertTrue(itemDescription.isDisplayed() && !itemDescription.getText().isEmpty(), "Description is not displayed");
        softAssert.assertTrue(productSpec.isDisplayed(), "Product specification is not displayed");
        softAssert.assertAll();
        return this;
    }

    public ProductPage(WebDriver driver) {
        super(driver);
    }
}
