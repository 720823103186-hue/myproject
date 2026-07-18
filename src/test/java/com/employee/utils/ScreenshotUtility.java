package com.employee.utils;

import com.employee.base.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtility {

    public static String takeScreenshot(String testName) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) return null;

        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);

        String destination = System.getProperty("user.dir") + "/reports/Screenshots/" + testName + "_" + dateName + ".png";
        File finalDestination = new File(destination);

        try {
            Files.createDirectories(finalDestination.getParentFile().toPath());
            Files.copy(source.toPath(), finalDestination.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destination;
    }
}
