package com.employee.tests;

import com.employee.base.BaseTest;
import com.employee.pages.*;
import com.employee.utils.WaitUtility;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EndToEndWorkflowTest extends BaseTest {

    private void sleepForDemo() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1)
    public void testLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithDefaultCredentials();
        
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard not displayed after login");
        sleepForDemo();
    }

    @Test(priority = 2)
    public void testDashboard() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        Assert.assertTrue(!dashboardPage.getTotalEmployees().isEmpty(), "Total employees count is empty");
        sleepForDemo();
    }

    @Test(priority = 3)
    public void testDirectory() {
        DirectoryPage directoryPage = new DirectoryPage(driver);
        directoryPage.navigateToDirectory();
        Assert.assertTrue(directoryPage.isDirectoryDisplayed(), "Directory is not displayed");
        
        directoryPage.search("Rahul");
        Assert.assertTrue(directoryPage.getCardCount() >= 1, "Search returned no results");
        directoryPage.clearSearch();
        sleepForDemo();
    }

    @Test(priority = 4)
    public void testAddEmployee() {
        DirectoryPage directoryPage = new DirectoryPage(driver);
        directoryPage.navigateToDirectory();
        directoryPage.clickAddEmployee();

        EmployeePage employeePage = new EmployeePage(driver);
        Assert.assertTrue(employeePage.isFormDisplayed(), "Employee Form not displayed");

        employeePage.addEmployee("Test Automation", "testauto@nexahr.com", "1234567890", 
                                 "QA", "SDET", "90000", "Remote");
        
        WaitUtility.waitForVisible(By.cssSelector(".toast-success"));
        
        directoryPage.search("Test Automation");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            .until(d -> directoryPage.getCardCount() >= 1);
        Assert.assertTrue(directoryPage.getCardCount() >= 1, "Employee was not added successfully");
        directoryPage.clearSearch();
        sleepForDemo();
    }

    @Test(priority = 5)
    public void testEditEmployee() {
        DirectoryPage directoryPage = new DirectoryPage(driver);
        directoryPage.navigateToDirectory();
        
        directoryPage.search("Test Automation");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            .until(d -> directoryPage.getCardCount() >= 1);
        
        WaitUtility.waitForClickable(By.xpath("//button[text()='Edit']")).click();

        EmployeePage employeePage = new EmployeePage(driver);
        Assert.assertTrue(employeePage.isFormDisplayed(), "Employee Form not displayed");
        employeePage.fillSalary("95000");
        employeePage.clickSave();
        
        WaitUtility.waitForVisible(By.cssSelector(".toast-success"));
        
        directoryPage.clearSearch();
        directoryPage.search("Test Automation");
        sleepForDemo();
    }

    @Test(priority = 6)
    public void testDeleteEmployee() {
        DirectoryPage directoryPage = new DirectoryPage(driver);
        directoryPage.navigateToDirectory();
        
        directoryPage.search("Test Automation");
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            .until(d -> directoryPage.getCardCount() >= 1);
        
        WaitUtility.waitForClickable(By.xpath("//button[text()='Delete']")).click();

        EmployeePage employeePage = new EmployeePage(driver);
        employeePage.confirmDelete();
        
        WaitUtility.waitForVisible(By.cssSelector(".toast-success"));
        sleepForDemo();
    }

    @Test(priority = 7)
    public void testAnalytics() {
        AnalyticsPage analyticsPage = new AnalyticsPage(driver);
        analyticsPage.navigateToAnalytics();
        Assert.assertTrue(analyticsPage.isAnalyticsDisplayed(), "Analytics page not displayed");
        Assert.assertTrue(analyticsPage.isDeptChartDisplayed(), "Dept chart not displayed");
        sleepForDemo();
    }

    @Test(priority = 8)
    public void testSettings() {
        SettingsPage settingsPage = new SettingsPage(driver);
        settingsPage.navigateToSettings();
        Assert.assertTrue(settingsPage.isSettingsDisplayed(), "Settings page not displayed");
        
        settingsPage.toggleTheme();
        sleepForDemo();
    }

    @Test(priority = 9)
    public void testLogout() {
        SettingsPage settingsPage = new SettingsPage(driver);
        settingsPage.navigateToSettings();
        
        settingsPage.clickSidebarLogout();
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page not displayed after logout");
        sleepForDemo();
    }
}
