package com.onlinerautomation.test;

import com.onlinerautomation.TestBase;
import com.onlinerautomation.page.AutoMarketPage;
import com.onlinerautomation.page.CarAnnouncementPage;
import com.onlinerautomation.page.HomePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CarAnnouncementTest extends TestBase {

    private AutoMarketPage autoMarketPage;
    private CarAnnouncementPage carAnnouncementPage;

    @BeforeMethod
    public void beforeMethod() {
        autoMarketPage = new HomePage(driver).openAnnouncementCarPage();
    }

    @Test(description = "Compare preview data with opened announcement data")
    public void testComparePreviewAndOpenedAnnouncementData() {
        autoMarketPage.openFirstAnnouncement().verifyCarInformation();
    }

    @Test(description = "Verify car announcement elements")
    public void testVerifyPageElements(){
        autoMarketPage.openFirstAnnouncement().verifyCarAnnouncementElements();
    }

    @Override
    public void openTargetPage() {
        driver.get(baseUrl);
    }
}
