package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.employee.utils.WaitUtility;

import java.util.List;

public class DirectoryPage {
    private WebDriver driver;

    private By directoryPage = By.id("directory-page");
    private By searchInput = By.id("employee-search");
    private By deptFilter = By.id("dept-filter");
    private By statusFilter = By.id("status-filter");
    private By addEmployeeBtn = By.id("add-employee-btn");
    private By employeeCards = By.id("employee-cards");
    private By singleCard = By.cssSelector(".emp-card");
    private By pagination = By.id("pagination");
    private By noResults = By.id("no-results");
    private By navDirectory = By.id("nav-directory");

    public DirectoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDirectoryDisplayed() {
        return WaitUtility.waitForVisible(directoryPage) != null;
    }

    public void navigateToDirectory() {
        WaitUtility.waitForClickable(navDirectory).click();
    }

    public void search(String keyword) {
        WaitUtility.clearAndType(searchInput, keyword);
    }

    public void clearSearch() {
        WebElement el = WaitUtility.waitForVisible(searchInput);
        el.clear();
    }

    public void filterByDepartment(String dept) {
        WaitUtility.clearAndType(deptFilter, dept);
    }

    public void filterByStatus(String status) {
        WaitUtility.clearAndType(statusFilter, status);
    }

    public int getCardCount() {
        try {
            WaitUtility.waitForVisible(employeeCards);
            List<WebElement> cards = driver.findElements(singleCard);
            return cards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isNoResultsDisplayed() {
        try {
            return WaitUtility.waitForVisible(noResults).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPaginationDisplayed() {
        try {
            return driver.findElement(pagination).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickAddEmployee() {
        WaitUtility.waitForClickable(addEmployeeBtn).click();
    }

    public void clickEditOnCard(String empId) {
        WaitUtility.waitForClickable(By.id("edit-" + empId)).click();
    }

    public void clickDeleteOnCard(String empId) {
        WaitUtility.waitForClickable(By.id("delete-" + empId)).click();
    }

    public boolean isCardVisible(String empId) {
        try {
            return driver.findElement(By.id("emp-card-" + empId)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
