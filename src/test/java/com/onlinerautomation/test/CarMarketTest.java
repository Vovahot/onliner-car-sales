package com.onlinerautomation.test;

import com.onlinerautomation.TestBase;
import com.onlinerautomation.data.DataForFilers;
import com.onlinerautomation.page.AutoMarketPage;
import com.onlinerautomation.page.HomePage;
import com.sun.javafx.binding.StringFormatter;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CarMarketTest extends TestBase {

    private AutoMarketPage autoMarketPage;

    @BeforeMethod
    public void beforeMethod() {
        autoMarketPage = new HomePage(driver).openAnnouncementCarPage();
    }

    @Test(description = "Select car by body type",
            dataProvider = "Filter by car body types", dataProviderClass = DataForFilers.class)
    public void testFilterByCarBodyTypes(String data) {
        List<WebElement> expected = autoMarketPage.getCarBodyTypeList();
        autoMarketPage.verityCardList()
                .setCarFilteryType(expected, data)
                .varifyCarCountForFilter(expected, data);
    }

    @Test(description = "Select car by engine type",
            dataProvider = "Filter by engine's type", dataProviderClass = DataForFilers.class)
    public void testFilterByCarEngineType(String data) {
        List<WebElement> expected = autoMarketPage.getCarEngineTypeList();
        autoMarketPage.verityCardList()
                .setCarFilteryType(expected, data)
                .varifyCarCountForFilter(expected, data);
    }


    @Test(description = "Select car by transmission type",
            dataProvider = "Filter by transmission type", dataProviderClass = DataForFilers.class)
    public void testFilterByCarTransmissionType(String data) {
        List<WebElement> expected = autoMarketPage.getCarTransmissionTypeList();
        autoMarketPage.verityCardList()
                .setCarFilteryType(expected, data)
                .varifyCarCountForFilter(expected, data);
    }

    @Test(description = "Filtering car announcement by price",
    dataProvider = "Filter by price", dataProviderClass = DataForFilers.class)
    public void testMinPrice(String minPrice, String maxPrice) {
        autoMarketPage.selectkMinPrice(minPrice)
                .selectMaxPrice(maxPrice)
                .verifyCarList(minPrice, maxPrice);
    }

    @Override
    public void openTargetPage() {
        driver.get(baseUrl);
    }
}
