package org.example;

import groovy.util.logging.Slf4j;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class OpenWeatherMapTest {
    protected WebDriver driver = new ChromeDriver();
    WebDriverWait webDriverWait;
    SoftAssertions softAssertions;
    String placeholder = "Weather in your city";

    By noResultsFoundNotification= By.cssSelector(".section-content .widget-notification span");
    By submitSearch = By.cssSelector(".search [type='submit']");
    By searchCity = By.cssSelector("[placeholder='Search city']");
    By placeholderText = By.cssSelector("[id='desktop-menu'] [name='q']");

    @BeforeTest
    public void setup(){
        softAssertions = new SoftAssertions();
        webDriverWait = new WebDriverWait(driver, Duration.ofMillis(222222));
        navigateToOpenWeatherWebsite();
    }
    @Test
    @Description("Check placeholder value, look up for Sydney and verify date and time")
    public void searchFieldHappyPath(){
        String key = "Sydney";
        navigateToOpenWeatherWebsite();

        softAssertions.assertThat(driver.findElement(placeholderText).getAttribute("placeholder"))
                .as("Placeholder is a wrong text message!, it should be " + placeholder)
                .isEqualTo(placeholder);

        searchCity(key);
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector(".search-dropdown-menu li"))));

        clickOnFirstOptionFromDropdownSearch();
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.cssSelector(".current-container.mobile-padding h2")), key));

        String currentDateAndTime = driver.findElement(By.cssSelector(".current-container.mobile-padding .orange-text")).getText();
        softAssertions.assertThat(currentDateAndTime)
                .as("Date and time of chosen city is incorrect!")
                .isEqualTo(getCurrentDateTimeInSydney());
        softAssertions.assertAll();
    }

    @Test
    @Description("Look up for Vienna instead of Sydney and verify date and time are different")
    public void searchCityNegativePath(){
        String key = "Vienna";
        navigateToOpenWeatherWebsite();
        searchCity(key);
        selectFirstRowInDropdownOfCities();
        webDriverWait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.cssSelector(".current-container.mobile-padding h2")), key));
        String currentDateAndTime = driver.findElement(By.cssSelector(".current-container.mobile-padding .orange-text")).getText();
        softAssertions.assertThat(currentDateAndTime)
                .as("Date and time of chosen city is incorrect!")
                .isNotEqualTo(getCurrentDateTimeInSydney());
        softAssertions.assertAll();
    }

    @Test
    @Description("Look up for non existent city and verify notification is shown")
    public void searchNonExistentCity(){
        String key = "IDontExist";
        navigateToOpenWeatherWebsite();
        searchCity(key);
        softAssertions.assertThat(driver.findElement(noResultsFoundNotification).isDisplayed())
                .as("User should be notified that no results were found")
                .isTrue();
    }

    @Step("Navigate to open weather website")
    public  void navigateToOpenWeatherWebsite() {
        driver.get("https://openweathermap.org/");
    }

    @Step("Click on a first one from dropdown menu of city search")
    public  void clickOnFirstOptionFromDropdownSearch() {
        driver.findElements(By.cssSelector(".search-dropdown-menu li")).get(0).click();
    }

    @Step("Select first row in dropdown of cities")
    public  void selectFirstRowInDropdownOfCities() {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofMillis(222222));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector(".search-dropdown-menu li"))));

        driver.findElements(By.cssSelector(".search-dropdown-menu li")).get(0).click();
    }

    @Step("Return current date and time from world city")
    public static String getCurrentDateTimeInSydney() {
        String timeZoneId = "Australia/Sydney";
        ZonedDateTime sydneyTime = ZonedDateTime.now(java.time.ZoneId.of(timeZoneId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, hh:mma");
        return sydneyTime.format(formatter);
    }

    @Step("Search for a city through a search input field")
    public void searchCity(String key) {
        Actions action = new Actions(driver);
        action.sendKeys(driver.findElement(searchCity), key)
                .sendKeys(Keys.ENTER)
                .perform();
        driver.findElement(submitSearch).click();
    }

    @AfterTest
    public void quitDriver() {
        driver.quit();
    }
}