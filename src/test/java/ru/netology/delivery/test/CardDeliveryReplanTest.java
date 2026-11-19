package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryReplanTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Перепланирование встречи с изменением даты")
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

    @Step("Заполнить форму и отправить первый заказ на дату {date}")
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

    @Step("Проверить уведомление об успехе для даты {date}")
    private void checkSuccessNotification(String date) {
        $("[data-test-id='success-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"))
                .shouldHave(Condition.text("Встреча успешно запланирована на " + date));
    }

    @Step("Перепланировать встречу на дату {newDate}")
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