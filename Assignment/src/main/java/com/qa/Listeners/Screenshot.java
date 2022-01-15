package com.qa.Listeners;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.qa.Utils.Utils;
import com.qa.config.FrameworkConfig;
import com.qa.wApi.driver.WebDriverFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Screenshot extends TestListenerAdapter {

    public File take_screenshot(WebDriver driver, ITestResult testResult) throws NullPointerException {
        return take_screenshot(driver, testResult.getMethod().getMethodName());
    }

    public File take_screenshot(WebDriver driver, final String method_name) {
        if (driver == null) return null;

        File screenshotFile = null;
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            screenshotFile = create_screenshot_file(method_name,
                    createDirectory(String.format("%s/Screenshots/%s", System.getProperty("user.dir"), Utils.getDate())));
            FileUtils.copyFile(screenshot.getScreenshotAs(OutputType.FILE), screenshotFile);
        } catch (Exception ioe) {
            log.error("Encountered issue while creating screenshot for method/scenario {}", method_name);
            log.error("This was caused by {}", ioe.getCause());
            log.error(String.valueOf(ioe.getStackTrace()));
        }

        return screenshotFile;
    }

    private File create_screenshot_file(final String method_name, final File screenshot_dir) {
        return new File(String.format("%s/%s_%s.png", screenshot_dir.getPath(), method_name, Utils.getTimeStamp("dd-MM-yyyy_HH_mm_ss")));
    }

    private File createDirectory(String directoryPath) {
        File screenshotDir = new File(directoryPath);
        if (!screenshotDir.exists()) screenshotDir.mkdirs();
        return screenshotDir;
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        super.onTestFailure(iTestResult);
        take_screenshot(iTestResult);
    }

    private void take_screenshot(ITestResult iTestResult) {
        WebDriver driver = (WebDriver) iTestResult.getTestContext().getAttribute("driver");

        try {
            take_screenshot(driver, iTestResult);
        } catch (NullPointerException e) {
            log.error("encountered NPE while taking a screenshot!!");
            Utils.log_exception(e);

            driver = WebDriverFactory.getInstance().getDriver(System.getProperty("driverType",
                    FrameworkConfig.getInstance().getConfigProperties().getProperty("DRIVERTYPE")));
            take_screenshot(driver, iTestResult);
        }
    }

}
