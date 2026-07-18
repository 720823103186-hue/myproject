package com.employee.utils;

import com.employee.base.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtility {

    private static int getTimeout() {
        try {
            return Integer.parseInt(ConfigReader.getProperty("explicit.wait"));
        } catch (Exception e) {
            return 15;
        }
    }

    public static WebElement waitForVisible(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(getTimeout()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(getTimeout()));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitForInvisible(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(getTimeout()));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static WebElement waitForPresence(By locator) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(getTimeout()));
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static boolean waitForUrlContains(String fragment) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(getTimeout()));
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    public static void clearAndType(By locator, String text) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = waitForVisible(locator);
                JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
                String tag = element.getTagName().toLowerCase();

                if (tag.equals("select")) {
                    // Use native setter for React-controlled select
                    js.executeScript(
                        "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLSelectElement.prototype, 'value').set;" +
                        "nativeSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        element, text
                    );
                } else if (tag.equals("textarea")) {
                    // Use native setter for React-controlled textarea
                    js.executeScript(
                        "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLTextAreaElement.prototype, 'value').set;" +
                        "nativeSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                        element, text
                    );
                } else {
                    // Use native setter for React-controlled input
                    js.executeScript(
                        "var nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                        element, text
                    );
                }
                break;
            } catch (StaleElementReferenceException e) {
                attempts++;
            }
        }
    }
}
