package com.employee.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports createInstance() {
        String path = System.getProperty("user.dir") + "/reports/ExtentReport.html";
        new File(path).getParentFile().mkdirs();

        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(path);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("Employee Management Automation Report");
        htmlReporter.config().setReportName("Automation Test Results");
        htmlReporter.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Organization", "Company");
        extent.setSystemInfo("Browser", ConfigReader.getProperty("browser"));
        
        return extent;
    }
}
