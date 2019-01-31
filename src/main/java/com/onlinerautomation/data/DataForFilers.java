package com.onlinerautomation.data;

import org.testng.annotations.DataProvider;

public class DataForFilers {
    @DataProvider(name = "Filter by car body types")
    public static Object[] getCarBodyTypes() {
        return new Object[][]{
                {"Седан"},
                {"Универсал"},
                {"Хетчбэк"},
                {"Минивэн"},
                {"Внедорожник"},
                {"Купе"},
                {"Кабриолет"},
                {"Микроавтобус"},
                {"Пикап"},
                {"Фургон"}
        };
    }

    @DataProvider(name = "Filter by engine's type")
    public static Object[] getEnginesType() {
        return new Object[][]{
                {"Бензин"},
                {"Дизель"},
                {"Газ (бензин))"},
                {"Гибрид (бензин)"},
                {"Электромобиль"}
        };
    }

    @DataProvider(name = "Filter by transmission type")
    public static Object[] getTransmissionType() {
        return new Object[][]{
                {"Автоматическая"},
                {"Механическая"}
        };
    }

}
