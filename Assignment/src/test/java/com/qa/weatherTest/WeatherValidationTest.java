package com.qa.weatherTest;


import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.qa.Exception.NoSuchCityException;
import com.qa.Utils.Utils;
import com.qa.baseApi.ApiBase;
import com.qa.baseApi.EndPoints;
import com.qa.baseClass.BaseTestNGTest;
import com.qa.pages.AccuHomePO;
import com.qa.pages.AccuWeatherPO;
import com.qa.weatherInfo.WeatherInfo;
import com.qa.Context.*;

import groovy.json.JsonOutput;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeatherValidationTest extends BaseTestNGTest{

	private AccuWeatherPO weatherPO;
    private ApiBase apiBase;
    private Response response;
    private WeatherInfo weatherInfoWeb, weatherInfoApi;
    private final Gson gson = new Gson();

    @BeforeMethod
    public void setupTest(ITestContext testContext, Object DRIVER) throws InterruptedException, NoSuchCityException {
        driver = (WebDriver) testContext.getAttribute(DRIVER.toString());
        String cityName = testData.getProperty("cityName");
        initAPIBase(testContext, cityName);

        captureWeatherInfoFromWeb(cityName);
        captureWeatherInfoFromApi(cityName);
    }

    private void captureWeatherInfoFromApi(String cityName) {
        response = apiBase.get_response(Method.GET, EndPoints.WEATHER.toString());

        response
                .then()
                .statusCode(200);

        // need to do this as an int is being returned and autoboxing or casting is not working
        float tempDegrees = Float.parseFloat(response.jsonPath().get("main.temp") + "");
        Map<String, Object> weatherResponseMap = new HashMap<>();
        weatherResponseMap.put("condition", response.jsonPath().getList("weather.main"));
        weatherResponseMap.put("cityName", cityName);
        weatherResponseMap.put("windSpeed", response.jsonPath().get("wind.speed"));
        weatherResponseMap.put("windGust", response.jsonPath().get("wind.gust"));
        weatherResponseMap.put("tempDegrees", tempDegrees);
        weatherResponseMap.put("tempFahrenheit", tempDegrees * 1.8 + 32);
        weatherResponseMap.put("humidity", response.jsonPath().get("main.humidity"));

        weatherInfoApi = gson.fromJson(JsonOutput.toJson(weatherResponseMap), WeatherInfo.class);
    }
    
    private void captureWeatherInfoFromWeb(String cityName) throws InterruptedException, NoSuchCityException {
        //weatherPO = homePO.selectLocation();
        Map<String, Object> weatherWeb = new HashMap<>();
        try {
            weatherWeb = weatherPO
                                 .searchForCity(testData.getProperty("searchTerm"), cityName)
                                 .displayCityWeatherInfo(cityName)
                                 .storeWeatherInfo(cityName);
        } catch (InterruptedException e) {
            Utils.log_exception(e);
        } catch (NoSuchCityException e) {
            Utils.log_exception(e);
        } catch (NullPointerException npe) {
            Utils.log_exception(npe);
        }
        weatherInfoWeb = gson.fromJson(JsonOutput.toJson(weatherWeb), WeatherInfo.class);
    }

    public void initAPIBase(ITestContext testContext, String cityName) {
        apiBase = new ApiBase(config.getProperty("baseUrl"),
                Integer.parseInt(config.getProperty("basePort")),
                config.getProperty("basePath"));
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("appid", config.getProperty("APIKey"));
        queryParams.put("q", cityName);
        queryParams.put("units", "metric");
        apiBase.set_query_params(queryParams);
    }

    @AfterMethod(alwaysRun = true)
    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        RestAssured.reset();
    }

    Comparator<WeatherInfo> tempComparator = ((o1, o2) -> {
        float absValue = Math.abs(o1.getTempDegrees() - o2.getTempDegrees());
        float tempVariance = Float.parseFloat(testData.getProperty("tempVariance"));
        return (absValue >= 0 && absValue <= tempVariance) ? 0 : 1;
    });

    @Test
    public void validateCityWeatherInfoIsDisplayed() {
        log.info("Weather data from API: {}", weatherInfoApi.toString());
        log.info("Weather data from Web: {}", weatherInfoWeb.toString());
        boolean compareTemp = tempComparator.compare(weatherInfoApi, weatherInfoWeb) == 0;
        log.info("Is the temparature within the variance {}? {}", testData.getProperty("tempVariance"), compareTemp);
        
        if (compareTemp) assertTrue(true);
        else {
            String failureMessage = buildFailureMessage(compareTemp);
            fail(failureMessage);
        }
    }

    private String buildFailureMessage(boolean compareTemp) {
        StringBuilder failureMessage = new StringBuilder();
        failureMessage.append("One or more comparators (");
        if (!compareTemp) failureMessage.append(" compareTemp");
        failureMessage.append("), returned a value outside the acceptable range(s)");
        return String.valueOf(failureMessage);
    }

}
