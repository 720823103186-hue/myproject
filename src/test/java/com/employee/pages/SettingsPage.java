package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.employee.utils.WaitUtility;

public class SettingsPage {
    private WebDriver driver;

    private By settingsPage = By.id("settings-page");
    private By navSettings = By.id("nav-settings");
    private By profileNameInput = By.id("profile-name");
    private By profileEmailInput = By.id("profile-email");
    private By saveProfileBtn = By.id("save-profile");
    private By themeToggle = By.id("theme-toggle-settings");
    private By currentPasswordInput = By.id("current-password");
    private By newPasswordInput = By.id("new-password");
    private By changePasswordBtn = By.id("change-password-btn");
    private By logoutBtn = By.id("logout-btn");
    private By sidebarLogoutBtn = By.id("sidebar-logout");

    public SettingsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isSettingsDisplayed() {
        return WaitUtility.waitForVisible(settingsPage) != null;
    }

    public void navigateToSettings() {
        WaitUtility.waitForClickable(navSettings).click();
    }

    public void updateProfile(String name, String email) {
        WaitUtility.clearAndType(profileNameInput, name);
        WaitUtility.clearAndType(profileEmailInput, email);
        WaitUtility.waitForClickable(saveProfileBtn).click();
    }

    public void toggleTheme() {
        WaitUtility.waitForClickable(themeToggle).click();
    }

    public void changePassword(String currentPass, String newPass) {
        WaitUtility.clearAndType(currentPasswordInput, currentPass);
        WaitUtility.clearAndType(newPasswordInput, newPass);
        WaitUtility.waitForClickable(changePasswordBtn).click();
    }

    public void clickLogout() {
        WaitUtility.waitForClickable(logoutBtn).click();
    }

    public void clickSidebarLogout() {
        WaitUtility.waitForClickable(sidebarLogoutBtn).click();
    }
}
