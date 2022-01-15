package com.qa.Utils;

import static java.util.Arrays.stream;
import static org.testng.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;

import com.qa.base.BasePageObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {

    public static String getDate() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Date dateobj = new Date();
        return (df.format(dateobj));
    }

    public static String getTimeStamp() {
        return getTimeStamp("dd-MMM-yyyy HH:mm:ss").replaceAll(" ", "");
    }

    public static String getTimeStamp(String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date dateobj = new Date();
        return (df.format(dateobj));
    }

    public static <T extends BasePageObject> T get_instance(Class<T> clazz, WebDriver driver) {
        T instance = null;
        if (clazz != null) {
            try {
                instance = clazz.getConstructor(WebDriver.class).newInstance(driver);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Found an issue while creating instance of {}", clazz.getSimpleName());
                log_exception(e);
            }
        }
        return instance;
    }

    public static void log_exception(Throwable throwable) {
        log.error(throwable.getMessage());
        log.error(String.valueOf(throwable.getCause()));
        stream(ExceptionUtils.getRootCauseStackTrace(throwable)).forEach(log::error);
    }

    public static void log_exception(String message, Exception exception) {
        log.error(message);
        log.error(Arrays.toString(exception.getStackTrace()));
    }

    public static void log_exception_and_fail(Exception exception) {
        log_exception(exception);
        fail();
    }

    public static void log_exception_and_fail(String message, Exception exception) {
        log_exception(message, exception);
        fail();
    }

    public static int get_random_index(final int min, final int max) {
        return (new Random().nextInt(max - min + 1) + min) - 1;
    }

}
