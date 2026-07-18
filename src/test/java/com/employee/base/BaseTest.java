package com.employee.base;

import com.employee.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class BaseTest {

    protected WebDriver driver;

    @Parameters({"browser"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        driver = DriverFactory.initDriver(browser);
        String url = ConfigReader.getProperty("url");
        if (url == null) url = "http://localhost:5173";
        driver.get(url);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
