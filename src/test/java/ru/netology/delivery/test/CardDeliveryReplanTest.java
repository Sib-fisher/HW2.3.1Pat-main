package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator; // <-- Если ошибка, поменяйте на ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryReplanTest {

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.timeout = 15000;
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Replan meeting with date change")
    void shouldReplanMeetingSuccessfully() {
        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        String firstDate = DataGenerator.generateDate(3);
        String secondDate = DataGenerator.generateDate(5);

        createFirstOrder(user, firstDate);
        checkSuccessNotification(firstDate);

        replanMeeting(user, secondDate);

        $("[data-test-id='replan-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Необходимо подтверждение"));

        $("[data-test-id='replan-notification'] button").click();
        checkSuccessNotification(secondDate);
    }

    @Step("Fill form and send first order for date {date}")
    private void createFirstOrder(DataGenerator.UserInfo user, String date) {
        $("[data-test-id='city'] input").setValue(user.getCity());
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(date);
        $("[data-test-id='name'] input").setValue(user.getName());
        $("[data-test-id='phone'] input").setValue(user.getPhone());
        $("[data-test-id='agreement']").click();
        $("button.button").click();
    }

    @Step("Check success notification for date {date}")
    private void checkSuccessNotification(String date) {
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"))
                .shouldHave(Condition.text("Встреча успешно запланирована на " + date));
    }

    @Step("Replan meeting to date {newDate}")
    private void replanMeeting(DataGenerator.UserInfo user, String newDate) {
        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE)
                .setValue(newDate);
        $("[data-test-id='name'] input")
                .press(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE)
                .setValue(user.getName());
        $("[data-test-id='phone'] input")
                .press(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE)
                .setValue(user.getPhone());

        if (!$("[data-test-id='agreement'] input").isSelected()) {
            $("[data-test-id='agreement']").click();
        }
        $("button.button").click();
    }
}