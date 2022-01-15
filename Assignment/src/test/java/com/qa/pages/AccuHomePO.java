package com.qa.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.qa.base.BasePageObject;
import com.qa.config.FrameworkConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccuHomePO extends BasePageObject {

	@FindBy(xpath = "/html[1]/body[1]/div[1]/div[1]/div[4]/a[1]/span[1]")
    private WebElement myLocation;
	
	@FindBy(xpath = "//body/div[1]/div[5]/div[1]/div[1]/a[1]/div[1]/div[1]/div[1]/div[1]/div[1]")
    private WebElement currentemp;

	
	public AccuHomePO(WebDriver driver) {
        this(driver, FrameworkConfig.getInstance().getConfigProperties());
    }

    public AccuHomePO(WebDriver driver, Properties config) {
        super(driver, config);
        PageFactory.initElements(ajaxElementLocatorFactory, this);
    }
    
    public AccuHomePO selectLocation() {
    	myLocation.click();
        wait.until(ExpectedConditions.visibilityOf(currentemp)).click();
        log.info("navigating to weather page");
        return new AccuHomePO(driver);
    }

	@Override
	protected By getUniqueElement() {
		return null;
	}
    
    

}
