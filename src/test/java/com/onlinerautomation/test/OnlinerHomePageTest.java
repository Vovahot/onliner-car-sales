package com.onlinerautomation.test;

import com.onlinerautomation.TestBase;
import com.onlinerautomation.page.HomePage;
import org.testng.annotations.Test;

public class OnlinerHomePageTest extends TestBase {
    @Override
    public void openTargetPage() {
        driver.get(baseUrl);
    }

    @Test
    public void testOnlinerHomePage() {
        HomePage homePage = new HomePage(driver);
        homePage.verifyHomePageLogo();
    }

}
