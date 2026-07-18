package com.employee.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import com.employee.utils.ConfigReader;

import java.time.Duration;

public class DriverFactory {
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver initDriver(String browser) {
        if (browser == null || browser.isEmpty()) {
            browser = ConfigReader.getProperty("browser");
        }
        if (browser == null) {
            browser = "chrome";
        }

        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
            System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\candy\\Desktop\\employee management\\EmployeeManagement\\chromedriver.exe");
            tlDriver.set(new ChromeDriver(options));
        } else if (browser.equalsIgnoreCase("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            tlDriver.set(new EdgeDriver(options));
        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\candy\\Desktop\\employee management\\EmployeeManagement\\chromedriver.exe");
            tlDriver.set(new ChromeDriver(options));
        }

        long implicitWait = 10;
        try {
            implicitWait = Long.parseLong(ConfigReader.getProperty("implicit.wait"));
        } catch (Exception ignored) {}

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        getDriver().manage().deleteAllCookies();

        return getDriver();
    }

    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove();
        }
    }
}
