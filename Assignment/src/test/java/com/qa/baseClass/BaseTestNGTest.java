package com.qa.baseClass;

import static com.qa.Context.Context.DRIVER;
import static java.lang.String.format;
import static org.testng.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.qa.config.FrameworkConfig;
import com.qa.config.PropertyFileReader;
import com.qa.pages.AccuHomePO;
import com.qa.wApi.driver.WebDriverFactory;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class BaseTestNGTest {
    private WebDriverFactory driverFactory;
    protected AccuHomePO homePO;
    protected WebDriver driver;
    protected Properties config;
    protected Properties testData;

	
    @BeforeTest(alwaysRun = true)
    public void initTest(ITestContext testContext) {
        // create a WebDriver instance on the basis of the settings
        // provided in framework config properties file
        init_test_variables();
        driver.manage().window().maximize();

        loadApplication(testContext);
        testContext.setAttribute(DRIVER.toString(), driver);
    }

    private void init_test_variables() {
        config = FrameworkConfig.getInstance().getConfigProperties();
        testData = new PropertyFileReader(new File(format("%s/src/test/resources/test_data/data.properties",
                System.getProperty("user.dir"))))
                           .getPropertyFile();
        driverFactory = WebDriverFactory.getInstance();
        driver = driverFactory.getDriver(System.getProperty("driverType", config.getProperty("DRIVERTYPE")));
    }

    protected void loadApplication(ITestContext testContext) {
        driver.navigate().to(config.getProperty("url"));
        
    }
    
    
    
    
    @AfterTest(alwaysRun = true)
    public void teardownTest() {
        driverFactory.closeDriver();
    }
	
}
