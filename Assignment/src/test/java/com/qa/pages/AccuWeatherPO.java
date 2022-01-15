package com.qa.pages;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfAllElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.qa.Exception.NoSuchCityException;
import com.qa.Utils.Utils;
import com.qa.base.BasePageObject;
import com.qa.config.FrameworkConfig;


public class AccuWeatherPO extends BasePageObject { 
	
	@FindBy(id = "searchBox")
    private WebElement citySearchtxtBox;

    private WebElement cityWeatherInfo, city, cityContainer;
    private List<WebElement> citySuggestions;
    private final List<WebElement> citySuggestionCheckboxes;
    private List<WebElement> weatherConditions;

    
    public AccuWeatherPO searchForCity(final String searchValue, final String cityName) throws InterruptedException, NoSuchCityException {
        // wait for the page scripts to finish loading. Temporarily using thread.sleep till we figure out how to
        // wait for the script on page load complete before we continue forward
        Thread.sleep(5000);
        if (isNotBlank(cityName) && isNotBlank(searchValue)) {
            try {
                citySearchtxtBox.sendKeys(searchValue);
                wait.ignoring(NoSuchElementException.class, TimeoutException.class)
                        .until(visibilityOfAllElements(citySuggestions = driver.findElements(By.xpath("//*[@id='messages']//div[not(contains(@style, 'none'))]"))));
                if (citySuggestions.size() > 0 && isCitySelectable(cityName)) city.click();
            } catch (TimeoutException e) {
                Utils.log_exception(e);
                /*if (e.getMessage().contains("Expected condition failed: waiting for visibility"))
                    throw new NoSuchCityException(format("Unable to locate any city with the search term %s", searchValue));*/
            }

            return this;
        }
        return null;
    }
    
    
    public AccuWeatherPO displayCityWeatherInfo(final String cityName) {
        String cityTempContainerLocator = format(".outerContainer[title='%s']", cityName);
        cityContainer = wait.until(visibilityOf(cityWeatherInfo = driver.findElement(By.cssSelector(cityTempContainerLocator))));
        cityContainer.click();
        return this;
    }
    
    public Map<String, Object> storeWeatherInfo(final String cityName) {
        WebElement weatherInfoContainer = wait.until(visibilityOf(driver.findElement(By.className("leaflet-popup-content"))));
        weatherConditions = weatherInfoContainer.findElements(By.cssSelector(".leaflet-popup-content span.heading"));

        return redoConditionsMap(populateWeatherConditions(weatherConditions, cityName));
    }

    private Map<String, Object> redoConditionsMap(Map<String, Object> interimWeatherConditions) {
        Map<String, Object> mapToReturn = new HashMap<>();
        if (interimWeatherConditions.containsKey("Condition")) {
            List<String> conditions = new ArrayList<>();
            conditions.add(interimWeatherConditions.get("Condition").toString());
            mapToReturn.put("condition", conditions);
        }
        if (interimWeatherConditions.containsKey("Wind")) {
            String[] windStringSplits = ((String) interimWeatherConditions.get("Wind")).trim().split(" ");
            mapToReturn.put("windSpeed", windStringSplits[0]);
            mapToReturn.put("windGust", windStringSplits[4]);
        }
        if (interimWeatherConditions.containsKey("Humidity")) {
            String humidity = ((String) interimWeatherConditions.get("Humidity")).trim();
            mapToReturn.put("humidity",
                    parseInt(humidity.substring(0, humidity.length() - 1)));
        }
        if (interimWeatherConditions.containsKey("Temp in Degrees"))
            mapToReturn.put("tempDegrees", parseFloat(interimWeatherConditions.get("Temp in Degrees").toString().trim()));
        if (interimWeatherConditions.containsKey("Temp in Fahrenheit"))
            mapToReturn.put("tempFahrenheit", parseFloat(interimWeatherConditions.get("Temp in Fahrenheit").toString().trim()));
        if (interimWeatherConditions.containsKey("cityName"))
            mapToReturn.put("cityName", interimWeatherConditions.get("cityName"));

        return mapToReturn;
    }

    private Map<String, Object> populateWeatherConditions(List<WebElement> weatherConditions, String cityName) {
        Map<String, Object> weatherConditionsMap = new HashMap<>();
        weatherConditionsMap.put("cityName", cityName);
        for (WebElement weatherState : weatherConditions) {
            String[] splits = weatherState.getText().split(":");
            weatherConditionsMap.put(splits[0].trim(), splits[1]);
        }
        return weatherConditionsMap;
    }

    private boolean isCitySelectable(final String cityName) {
        citySuggestions.forEach(suggestion -> citySuggestionCheckboxes.add(suggestion.findElement(By.xpath(".//input"))));
        for (WebElement element : citySuggestionCheckboxes)
            if (cityName.equalsIgnoreCase(element.getAttribute("id"))) {
                city = element;
                break;
            }

        return city != null && !city.isSelected();
    }

    public AccuWeatherPO(WebDriver driver) {
        this(driver, FrameworkConfig.getInstance().getConfigProperties());
    }

    public AccuWeatherPO(WebDriver driver, Properties config) {
        super(driver, config);
        PageFactory.initElements(ajaxElementLocatorFactory, this);
        citySuggestionCheckboxes = new ArrayList<>();
    }

    @Override
    protected By getUniqueElement() {
        return By.xpath("//div[@class='comment_cont']");
    }

    public boolean isWeatherInfoDisplayed() {
        return cityWeatherInfo.isDisplayed();
    }


}
