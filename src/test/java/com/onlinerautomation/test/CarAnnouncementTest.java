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

    @Test
    public void testOpenCarAnnouncement() {
        autoMarketPage.openFirstAnnouncement().verifyCarInformation();
    }


    @Override
    public void openTargetPage() {
        driver.get(baseUrl);
    }
}
