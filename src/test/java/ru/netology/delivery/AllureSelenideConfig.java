package ru.netology.delivery;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

public class AllureSelenideConfig {
    public static void setup() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true) // Включает прикрепление скриншотов при падении
                .savePageSource(false)); // Можно включить, если нужно сохранять HTML страницы
    }
}