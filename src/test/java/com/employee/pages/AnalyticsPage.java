package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.employee.utils.WaitUtility;

public class AnalyticsPage {
    private WebDriver driver;

    private By analyticsPage = By.id("analytics-page");
    private By statTotal = By.id("stat-total");
    private By statActive = By.id("stat-active");
    private By statDepts = By.id("stat-depts");
    private By statAvgSalary = By.id("stat-avg-salary");
    private By deptChart = By.id("dept-chart");
    private By statusChart = By.id("status-chart");
    private By salaryChart = By.id("salary-chart");
    private By deptTable = By.id("dept-table");
    private By navAnalytics = By.id("nav-analytics");

    public AnalyticsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isAnalyticsDisplayed() {
        return WaitUtility.waitForVisible(analyticsPage) != null;
    }

    public void navigateToAnalytics() {
        WaitUtility.waitForClickable(navAnalytics).click();
    }

    public boolean isDeptChartDisplayed() {
        return WaitUtility.waitForVisible(deptChart).isDisplayed();
    }

    public boolean isStatusChartDisplayed() {
        return WaitUtility.waitForVisible(statusChart).isDisplayed();
    }

    public boolean isSalaryChartDisplayed() {
        return WaitUtility.waitForVisible(salaryChart).isDisplayed();
    }

    public boolean isDeptTableDisplayed() {
        return WaitUtility.waitForVisible(deptTable).isDisplayed();
    }
}
