package com.employee.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.employee.utils.ExtentManager;
import com.employee.utils.ScreenshotUtility;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.createInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        System.out.println("Test Suite started!");
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        System.out.println("Test Suite finished!");
        extent.flush();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getMethod().getMethodName());
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        test.set(extentTest);
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        System.out.println("Passed test: " + result.getMethod().getMethodName());
        test.get().pass("Test Passed");
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        System.out.println("Failed test: " + result.getMethod().getMethodName());
        String screenshotPath = ScreenshotUtility.takeScreenshot(result.getMethod().getMethodName());
        test.get().fail(result.getThrowable());
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath, "Test failure screenshot");
        }
        test.get().log(Status.FAIL, "Test Failed");
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        System.out.println("Skipped test: " + result.getMethod().getMethodName());
        test.get().skip(result.getThrowable());
    }

    @Override
    public synchronized void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not implemented
    }
}
