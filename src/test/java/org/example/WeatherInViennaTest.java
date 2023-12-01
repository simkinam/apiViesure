package org.example;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import org.assertj.core.api.SoftAssertions;
import java.io.IOException;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WeatherInViennaTest {

    Map<Integer,String> conditions =  new HashMap<>();

    protected SoftAssertions softAssert;

    @BeforeMethod(alwaysRun = true)
    public void testSetup() {

        fillConditions();
        softAssert = new SoftAssertions();
    }

    public static String viesure = "https://backend-interview-sydney.tools.gcp.viesure.io/";

    @DataProvider(name = "apiTestData")
    public Object[][] apiTestData() {
        return new Object[][] {
                {10},
                {60},
                {20},
                {-11},
                {100},
                {10000},
                {22222},
                {-1111},
        };
    }

    @Test(dataProvider = "apiTestData")
    @Description("Check the weather is represented correctly with GET method")
    public void getWeatherTest(int inputParameter) throws IOException {
        updateTemperature(inputParameter);
        RestAssured.baseURI= viesure;
        Response result = getCurrentWeather();

        JsonPath jsonPath = result.jsonPath();
        int dot = jsonPath.getString("icon").indexOf(".");
        int tempFahrenheit = Integer.valueOf(jsonPath.getString("weather.tempInFahrenheit"));
        int tempInCelsius = ((tempFahrenheit - 32) * 5) /9;

        softAssert.assertThat(jsonPath.getString("condition"))
                .as("Condition has incorrect value!")
                .isIn(conditions.values());
        softAssert.assertThat(jsonPath.getString("city"))
                .as("City has incorrect format/value!")
                .isInstanceOf(String.class);
        softAssert.assertThat(jsonPath.getString("icon"))
                .as("Icon has incorrect file type!")
                .endsWith("png");
        softAssert.assertThat(jsonPath.getString("icon").substring(0, dot))
                .as("Condition has incorrect value inside an icon filename!")
                .isIn(jsonPath.getString("condition"));
        softAssert.assertThat(jsonPath.getInt("weather.tempInCelsius"))
                .as("Temp in Celsius is not correctly calculated or rounded!")
                .isEqualTo(Math.round(tempInCelsius));
        softAssert.assertThat(jsonPath.getString("description"))
                .as("Description is incorrect!")
                .isEqualTo("The weather is " + classifyTemperature(tempInCelsius));
        softAssert.assertAll();
    }

    @Step("Update temperature with a new fahrenheit value")
    public void updateTemperature(int inputParameter) {
        RestAssured.baseURI= viesure;
        given()
                .header("Content-Type", ContentType.JSON)
                .header("accept", "*/*")
                .basePath("weather/temp")
                .body("{ \"tempInFahrenheit\": " + inputParameter +"}")
                .put()
                .then()
                .statusCode(200)
                .extract().response();

        Response result = given()
                .accept(ContentType.JSON)
                .get("weather")
                .then()
                .extract().response();
        JsonPath jsonPath = result.jsonPath();
        assertThat(jsonPath.getString("weather.tempInFahrenheit"))
                .as("Temperature is not updated to a new value!")
                .isEqualTo(String.valueOf(inputParameter));
    }

    @Step("Get current weather")
    public Response getCurrentWeather() {
        return given()
                .accept(ContentType.JSON)
                .get("weather")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public void fillConditions() {
        conditions.put(1, "clear");
        conditions.put(2, "windy");
        conditions.put(3, "mist");
        conditions.put(4, "drizzle");
        conditions.put(5, "dust");
    }

    private static String classifyTemperature(double celsiusTemperature) {
        if (celsiusTemperature <= 0) {
            return "freezing";
        } else if (celsiusTemperature < 10) {
            return "cold";
        } else if (celsiusTemperature < 20) {
            return "mild";
        } else if (celsiusTemperature < 25) {
            return "warm";
        } else {
            return "hot";
        }
    }
}
