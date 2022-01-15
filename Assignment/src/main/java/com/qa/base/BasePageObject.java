package com.qa.base;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.qa.config.FrameworkConfig;
import com.qa.config.PropertyFileReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePageObject {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected AjaxElementLocatorFactory ajaxElementLocatorFactory;
    protected Properties test_data;
    private final Properties config;

    public BasePageObject(WebDriver driver) {
        this(driver, FrameworkConfig.getInstance().getConfigProperties());
    }

    // constructor of the basepage
    public BasePageObject(WebDriver driver, Properties config) {
        this.driver = driver;
        this.config = config;
        wait = new WebDriverWait(driver, parseInt(config.getProperty("WEBDRIVERWAIT_TIMEOUT")),
                parseInt(config.getProperty("WEBDRIVERWAIT_POLL")));
        ajaxElementLocatorFactory = new AjaxElementLocatorFactory(driver, parseInt(config.getProperty("LOCATOR_FACTORY_TIMEOUT")));
        test_data = new PropertyFileReader(new File(String.format("%s/src/test/resources/test_data/data.properties", System.getProperty("user.dir"))))
                            .getPropertyFile();
        setTimeouts();

        isLoaded();
    }

    // setting timeouts
    private void setTimeouts() {
        driver.manage().timeouts().implicitlyWait(parseInt(config.getProperty("IMPLICITWAIT_TIMEOUT")),
                TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(parseInt(config.getProperty("PAGE_LOAD_TIMEOUT")),
                TimeUnit.SECONDS);
    }

    protected abstract By getUniqueElement();

    private void isLoaded() throws Error {
        //Define a list of WebElements that match the unique element locator for the page
        By uniqElement = getUniqueElement();
        List<WebElement> uniqueElements = driver.findElements(uniqElement);
        log.debug("found {} element(s) for the specified unique locator {}", uniqueElements.size(), uniqElement.toString());

        // Assert that the unique element is present in the DOM
        Assert.assertTrue((uniqueElements.size() > 0),
                format("Unique Element %s not found for %s", uniqElement.toString(), this.getClass().getSimpleName()));

        // Wait until the unique element is visible in the browser and ready to use. This helps make sure the page is
        // loaded before the next step of the tests continue.
        wait.until(ExpectedConditions.visibilityOfAllElements(uniqueElements));
    }

}
