import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static java.time.Duration.ofSeconds;

public class BLoanCalcTest {
    SelenideElement amount;
    SelenideElement term;
    SelenideElement rate;
    SelenideElement btnCalc;
    SelenideElement btnClr;
    String err = "Ошибка: Введите с";
    int timeOut = 16;

    @BeforeAll
    static void afterAll() {
//        Configuration.baseUrl = "http://localhost:8000";
        Configuration.baseUrl = "https://slqamsk.github.io/cases/loan-calc/v01";
        Configuration.browser = "firefox";
        Configuration.browserSize = "1024x800";
        Configuration.timeout = 3_000;
    }

    @BeforeEach
    void setUp() {
        open("");
        amount = $("#amount");
        term = $("#term");
        rate = $("#rate");
        btnCalc = $("#calculate-btn");
        btnClr = $("#clear-btn");
    }

    @AfterEach
    void tearDown() {
        closeWindow();
    }

    @Test
    void errors() {
        amount.setValue("999");
        btnCalc.click();
        $("#amount-hint").shouldHave(text(err));
        btnClr.click();
        amount.shouldBe(empty);
        amount.setValue("10000001");
        btnCalc.click();
        $("#amount-hint").shouldHave(text(err));

        $("#amount-hint").shouldHave(Condition.attributeMatching("class", "hint error"));
        btnClr.click();
        term.setValue("0.1");
        btnCalc.click();

        $("#term-hint").shouldHave(text(err));
        term.setValue("361");
        btnCalc.click();
        $("#term-hint").shouldHave(Condition.attributeMatching("class", "hint error"));
        btnClr.click();

        rate.setValue("0");
        btnCalc.click();
        $("#rate-hint").shouldHave(text(err));
        term.setValue("101");
        btnCalc.click();
        $("#rate-hint").shouldHave(Condition.attributeMatching("class", "hint error"));
    }

    @Test
    void test00() {
        String amountValue = "314159";
        String termValue = "256";
        String rateValue = "12.7";

        amount.setValue(amountValue);
        term.setValue(termValue);
        rate.setValue(rateValue);

        $x("//*[@id=\"input-container\"]/div[4]/label[2]/input").click();
        btnCalc.click();
        $("#calculation-modal").shouldHave(Condition.attributeMatching("style", ".*display:\\s*block.*"));
        $("#progress-bar").shouldBe(Condition.visible, ofSeconds(timeOut))
                .shouldHave(Condition.attributeMatching("style", ".*width:\\s*100%.*"), ofSeconds(timeOut));
        $("#result-amount").shouldHave(text(amountValue + ".00"));
        $("#result-term").shouldHave(text(termValue));
        $("#result-rate").shouldHave(text(rateValue));
        $("#result-payment-type").shouldHave(text("Дифференцированный"));

        $("#show-schedule-btn").click();
        switchTo().window(1);
        $x("/html/body/div/div/p[1]/span").shouldHave(text(amountValue + ".00"));
        $x("/html/body/div/div/p[2]/span").shouldHave(text(termValue));
        $x("/html/body/div/div/p[3]/span").shouldHave(text(rateValue));
        ElementsCollection trs = $$x("//table/tbody/tr");
        assert(Integer.toString(trs.size() - 1).equals(termValue)); //кол-во строк таблицы
    }

    @ParameterizedTest
    @CsvSource({"100000, 12, 15.5", "10000000, 360, 100", "1000, 1, 0.01"})
    void test01(String amountValue, String termValue, String rateValue) {
        amount.setValue(amountValue);
        term.setValue(termValue);
        rate.setValue(rateValue);

        btnCalc.click();
        $("#calculation-modal").shouldHave(Condition.attributeMatching("style", ".*display:\\s*block.*"));
        $("#progress-bar").shouldBe(Condition.visible, ofSeconds(timeOut))
                .shouldHave(Condition.attributeMatching("style", ".*width:\\s*100%.*"), ofSeconds(timeOut));
        $("#result-amount").shouldHave(text(amountValue + ".00"));
        $("#result-term").shouldHave(text(termValue));
        $("#result-rate").shouldHave(text(rateValue));
        $("#show-schedule-btn").click();
        switchTo().window(1);
        $x("/html/body/div/div/p[1]/span").shouldHave(text(amountValue + ".00"));
        $x("/html/body/div/div/p[2]/span").shouldHave(text(termValue));
        $x("/html/body/div/div/p[3]/span").shouldHave(text(rateValue));
        ElementsCollection trs = $$x("//table/tbody/tr");
        assert(Integer.toString(trs.size() - 1).equals(termValue));
    }
    //todo Проверить математику вычислений...
}
