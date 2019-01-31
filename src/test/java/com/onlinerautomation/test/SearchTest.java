package com.onlinerautomation.test;

import com.onlinerautomation.TestBase;
import com.onlinerautomation.page.HomePage;
import com.onlinerautomation.page.ProductPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class SearchTest extends TestBase {

    private HomePage homePage;
    private ProductPage shopGoodPage;

    @BeforeMethod
    public void beforeMethod() {
        homePage = new HomePage(driver);
        shopGoodPage = new ProductPage(driver);
    }

    @Test(description = "Search product in catalog")
    public void testOnlinerPositiveSearch() {
        List<String> info = homePage
                .enterSearchData()
                .selectFirstSearchResult();

        shopGoodPage.verifyProductInfo(info);
    }

    @Test(description = "Verify product page elements")
    public void testProductPageElements() {
        homePage.enterSearchData()
                .selectFirstSearchResult();

        shopGoodPage
                .verifyItemElements();
    }

    @Override
    public void openTargetPage() {
        driver.get(baseUrl);
    }
}
