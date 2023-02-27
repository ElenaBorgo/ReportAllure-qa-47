import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    void shouldRegisterByCardDelivery() {
        open("http://localhost:9999/");
        DataGenerator.RegistrationInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForMeeting);
        $x("//span [@data-test-id = 'city']// input").setValue(validUser.getCity());
        $x("//span[@data-test-id = 'date']//input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $x("//span[@data-test-id = 'date']//input").setValue(firstMeetingDate);
        $x("//span[@data-test-id = 'name']//input").setValue(validUser.getName());
        $x("//span[@data-test-id = 'phone']//input").setValue("+7" + validUser.getPhone());
        $x("//label[@data-test-id = 'agreement']").click();
        $x("//*[contains(text(), 'Запланировать')]").click();
        $("[data-test-id = 'success-notification'] .notification__content")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate));
    }

    @Test
    void shouldPlanMeetOnAnyDate() {
        open("http://localhost:9999/");
        DataGenerator.RegistrationInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $x("//span [@data-test-id = 'city']// input").setValue(validUser.getCity());
        $x("//span[@data-test-id = 'date']//input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $x("//span[@data-test-id = 'date']//input").setValue(firstMeetingDate);
        $x("//span[@data-test-id = 'name']//input").setValue(validUser.getName());
        $x("//span[@data-test-id = 'phone']//input").setValue("+7" + validUser.getPhone());
        $x("//label[@data-test-id = 'agreement']").click();
        $x("//*[contains(text(), 'Запланировать')]").click();
        $("[data-test-id = 'success-notification'] .notification__content")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $x("//span[@data-test-id = 'date']//input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $x("//span[@data-test-id = 'date']//input").setValue(secondMeetingDate);
        $x("//*[contains(text(), 'Запланировать')]").click();
        $x("//*[contains(text(), 'Необходимо подтверждение')]")
                .shouldBe(Condition.visible, Duration.ofSeconds(8))
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $("[data-test-id = 'replan-notification'] .button__content").click();
        $("[data-test-id = 'success-notification'] .notification__content")
                .shouldBe(Condition.visible, Duration.ofSeconds(8))
                .shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));

    }
}
