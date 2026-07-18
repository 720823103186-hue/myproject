package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.employee.utils.WaitUtility;
import com.employee.utils.ConfigReader;

public class LoginPage {
    private WebDriver driver;

    // Locators
    private By emailInput = By.id("login-email");
    private By passwordInput = By.id("login-password");
    private By loginBtn = By.id("login-submit");
    private By togglePassword = By.id("toggle-password");
    private By rememberMe = By.id("remember-me");
    private By forgotPassword = By.id("forgot-password");
    private By loginForm = By.id("login-form");
    private By loginError = By.id("login-error");
    private By emailError = By.cssSelector("#login-email ~ .field-error");
    private By passwordError = By.cssSelector("#login-password ~ .field-error, .password-wrapper ~ .field-error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoginPageDisplayed() {
        return WaitUtility.waitForVisible(loginForm) != null;
    }

    public void enterEmail(String email) {
        WaitUtility.clearAndType(emailInput, email);
    }

    public void enterPassword(String password) {
        WaitUtility.clearAndType(passwordInput, password);
    }

    public void clickLogin() {
        WaitUtility.waitForClickable(loginBtn).click();
    }

    public void clickTogglePassword() {
        WaitUtility.waitForClickable(togglePassword).click();
    }

    public void clickRememberMe() {
        WaitUtility.waitForClickable(rememberMe).click();
    }

    public void clickForgotPassword() {
        WaitUtility.waitForClickable(forgotPassword).click();
    }

    public String getPasswordInputType() {
        return WaitUtility.waitForVisible(passwordInput).getAttribute("type");
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return WaitUtility.waitForVisible(loginError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    public void loginWithDefaultCredentials() {
        String email = ConfigReader.getProperty("login.email");
        String password = ConfigReader.getProperty("login.password");
        login(email, password);
    }
}
