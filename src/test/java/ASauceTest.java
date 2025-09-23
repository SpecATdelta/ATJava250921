import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.codeborne.selenide.Condition.*;

enum UserList {
    standard_user,
    problem_user,
    performance_glitch_user,
    error_user,
    visual_user
}

public class ASauceTest {
    String password = "secret_sauce"; //todo move to .env
    String lockedOutUser = "locked_out_user";

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://www.saucedemo.com/";
        Configuration.browser = "chrome";
        Configuration.browserSize = "480x1024";
        Configuration.timeout = 3_000;
    }

    @BeforeEach
    void setUp() {
        Selenide.open("");
    }

    @Test
    void lockedTest() {
        shouldTest("#user-name", lockedOutUser);
        shouldTest("#password", password);
        Selenide.$("[type=submit]").click();
        Selenide.$("[data-test=error]").should(exist);
        Selenide.$("[data-test=error]").shouldHave(text("Epic"));
        Selenide.$$(".form_group")
                .forEach(se -> se.$("svg").should(exist)); // ⮾
        Selenide.sleep(400);
    }

    @ParameterizedTest
    @EnumSource(UserList.class)
    void listNamesTest(UserList value) {
        shouldTest("#user-name", value.name());
        shouldTest("#password", password);
        Selenide.$("[type=submit]").click();
        Selenide.$("#header_container").should(exist);
        Selenide.$("#inventory_container").should(exist);
        Selenide.sleep(400);
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    void shouldTest(String id, String value) {
        SelenideElement sU = Selenide.$(id);
        sU.setValue(value);
        sU.shouldHave(value(value));
    }
}

class HabrTest {
    @Test
    void habr() {
        Configuration.pageLoadStrategy = "eager";
        Selenide.open("https://habr.com/ru/articles/");
        Selenide.$(".tm-header-user-menu__search").click();
        Selenide.$(".tm-input-text-decorated__input")
                .setValue("selenide parameterized test").pressEnter();
        Selenide.sleep(3_000);
    }
}