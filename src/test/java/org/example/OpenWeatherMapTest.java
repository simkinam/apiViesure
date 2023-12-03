package org.example;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
public class OpenWeatherMapTest {

    @FindBy(css = "[id='desktop-menu'] [name='q']")
    protected WebElement searchFieldText;

    protected WebDriver driver = new ChromeDriver();

    String placeholder = "Weather in your city";
    By submitSearch = By.cssSelector(".search [type='submit']");
    By searchCity = By.cssSelector("[placeholder='Search city']");
    By placeholderText = By.cssSelector("[id='desktop-menu'] [name='q']");

    @Test
    public void searchFieldTest(){
        String key = "Sydney";
        SoftAssertions softAssertions = new SoftAssertions();
        driver.get("https://openweathermap.org/");

        softAssertions.assertThat(driver.findElement(placeholderText).getAttribute("placeholder"))
                .as("Placeholder is a wrong text message!, it should be " + placeholder)
                .isEqualTo(placeholder);

        searchCity(key);
        driver.findElement(submitSearch).click();

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofMillis(222222));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector(".search-dropdown-menu li"))));

        driver.findElements(By.cssSelector(".search-dropdown-menu li")).get(0).click();
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.cssSelector(".current-container.mobile-padding h2")), key));

        String currentDateAndTime = driver.findElement(By.cssSelector(".current-container.mobile-padding .orange-text")).getText();
        softAssertions.assertThat(currentDateAndTime)
                .as("Date and time of chosen city is incorrect!")
                .isEqualTo(getCurrentDateTimeForCity(key));
        softAssertions.assertAll();
        driver.quit();
    }

    public String getDateAndTime(String dateTimeText) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("MMM d, hh:mma");
            Date date = originalFormat.parse(dateTimeText);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, hh:mma");
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Step("Compare Date and Time with actual ones")
    public boolean compareDateAndTime(String dateTime1, String dateTime2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MMM d, hh:mma");
            Date date1 = format.parse(dateTime1);
            Date date2 = format.parse(dateTime2);

            return date1.compareTo(date2) == 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Step("Function to return current date and time from world city")
    public String getCurrentDateTimeForCity(String cityName) {
        return "city";
        }

    @Step
    public void searchCity(String key) {
        Actions action = new Actions(driver);
        action.sendKeys(driver.findElement(searchCity), key)
                .sendKeys(Keys.ENTER)
                .perform();

    }
}