package com.employee.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.employee.utils.WaitUtility;

public class EmployeePage {
    private WebDriver driver;

    // Form locators
    private By formModal = By.cssSelector(".modal-overlay");
    private By nameInput = By.id("name");
    private By emailInput = By.id("email");
    private By phoneInput = By.id("phone");
    private By departmentInput = By.id("department");
    private By roleInput = By.id("role");
    private By salaryInput = By.id("salary");
    private By statusInput = By.id("status");
    private By locationInput = By.id("location");
    private By saveBtn = By.id("save-employee");
    private By cancelBtn = By.cssSelector(".modal-footer .btn-secondary");
    private By confirmDeleteBtn = By.id("confirm-delete");

    public EmployeePage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isFormDisplayed() {
        try {
            return WaitUtility.waitForVisible(formModal) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void fillName(String name) {
        WaitUtility.clearAndType(nameInput, name);
    }

    public void fillEmail(String email) {
        WaitUtility.clearAndType(emailInput, email);
    }

    public void fillPhone(String phone) {
        WaitUtility.clearAndType(phoneInput, phone);
    }

    public void fillDepartment(String department) {
        WaitUtility.clearAndType(departmentInput, department);
    }

    public void fillRole(String role) {
        WaitUtility.clearAndType(roleInput, role);
    }

    public void fillSalary(String salary) {
        WaitUtility.clearAndType(salaryInput, salary);
    }

    public void fillLocation(String location) {
        WaitUtility.clearAndType(locationInput, location);
    }

    public void clickSave() {
        WaitUtility.waitForClickable(saveBtn).click();
    }

    public void clickCancel() {
        WaitUtility.waitForClickable(cancelBtn).click();
    }

    public void confirmDelete() {
        WaitUtility.waitForClickable(confirmDeleteBtn).click();
    }

    public void addEmployee(String name, String email, String phone,
                            String department, String role, String salary, String location) {
        fillName(name);
        fillEmail(email);
        fillPhone(phone);
        fillDepartment(department);
        fillRole(role);
        fillSalary(salary);
        fillLocation(location);
        clickSave();
    }
}
