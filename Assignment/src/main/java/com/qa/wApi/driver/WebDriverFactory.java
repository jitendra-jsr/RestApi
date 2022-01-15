package com.qa.wApi.driver;

import java.util.Collections;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import com.qa.Exception.NoSuchDriverException;
import com.qa.config.FrameworkConfig;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class WebDriverFactory {

	private static volatile WebDriverFactory instance;
    private static WebDriver webDriverInstance = null;
    private static ThreadLocal<WebDriver> driver;
    private final Properties config;

 // webdriver instantiation properties
    private final String browser;

    public void closeDriver() {
        driver.remove();
        if (driver != null) driver.get().quit();
    }

    private WebDriverFactory() {
        config = FrameworkConfig.getInstance().getConfigProperties();
        browser = System.getProperty("browser", config.getProperty("BROWSER"));
    }

    public static WebDriverFactory getInstance() {
        if (instance == null) {
            synchronized (WebDriverFactory.class) {
                if (instance == null) {
                    instance = new WebDriverFactory();
                }
            }
        }

        return instance;
    }

    public WebDriver getDriver(String driverType) throws NullPointerException {
        driver = ThreadLocal.withInitial(() -> {
            WebDriver tempDriver = null;
            try {
                switch (driverType.toLowerCase()) {
                    case "local": {
                        tempDriver = getLocalDriverInstance();
                        break;
                    }
                    default:
                        throw new NoSuchDriverException(String.format("UnSupported driver type requested: %s", driverType));
                }
            } catch (NoSuchDriverException e) {
                e.printStackTrace();
            }

            return tempDriver;
        });

        return driver.get();
    }

    private WebDriver getLocalDriverInstance() {
        if (webDriverInstance == null) {
            if (browser.contains("chrome")) {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("no-sandbox", "disable-dev-shm-usage"/*, "incognito"*/);
                // added the experimental option to disable automation notification bar
                options.setExperimentalOption("useAutomationExtension", false);
                options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                if (System.getProperty("headless", config.getProperty("headless")).equalsIgnoreCase("true")) {
                    options.addArguments("window-size=1920, 1050", "headless");
                }
                //options.merge(sslError);
                webDriverInstance = new ChromeDriver(options);
            } else if (browser.contains("firefox") || browser.contains("ff")) {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-private");
                if (System.getProperty("headless", config.getProperty("headless")).equalsIgnoreCase("true")) {
                    options.addArguments("-width 1920", "-height 1080", "-headless");
                }
                //options.merge(sslError);
                webDriverInstance = new FirefoxDriver(options);
            } else if (System.getProperty("os.name").indexOf("win") == 0 && (browser.contains("iexplore") || browser.contains("internet"))) {
                WebDriverManager.iedriver().setup();
                InternetExplorerOptions options = new InternetExplorerOptions();
                //options.merge(sslError);
                webDriverInstance = new InternetExplorerDriver(options);
            }
        }

        return webDriverInstance;
    }

}
