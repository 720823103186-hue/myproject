package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.employee.utils.WaitUtility;

public class DashboardPage {
    private WebDriver driver;

    private By dashboardPage = By.id("dashboard-page");
    private By totalEmployees = By.id("total-employees");
    private By activeEmployees = By.id("active-employees");
    private By remoteEmployees = By.id("remote-employees");
    private By avgSalary = By.id("avg-salary");
    private By statCards = By.id("stat-cards");
    private By deptDistribution = By.id("dept-distribution");
    private By recentActivity = By.id("recent-activity");
    private By navDashboard = By.id("nav-dashboard");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDashboardDisplayed() {
        return WaitUtility.waitForVisible(dashboardPage) != null;
    }

    public String getTotalEmployees() {
        return WaitUtility.waitForVisible(totalEmployees).getText();
    }

    public String getActiveEmployees() {
        return WaitUtility.waitForVisible(activeEmployees).getText();
    }

    public String getRemoteEmployees() {
        return WaitUtility.waitForVisible(remoteEmployees).getText();
    }

    public String getAvgSalary() {
        return WaitUtility.waitForVisible(avgSalary).getText();
    }

    public boolean isStatCardsDisplayed() {
        return WaitUtility.waitForVisible(statCards).isDisplayed();
    }

    public boolean isDeptDistributionDisplayed() {
        return WaitUtility.waitForVisible(deptDistribution).isDisplayed();
    }

    public boolean isRecentActivityDisplayed() {
        return WaitUtility.waitForVisible(recentActivity).isDisplayed();
    }

    public void navigateToDashboard() {
        WaitUtility.waitForClickable(navDashboard).click();
    }
}
