package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Utility class to manage ExtentReports for HTML report generation.
 * Generates a report with: Test Scenario Name, Actual, Expected, Pass/Fail.
 */
public class ReportManager {

    private static ExtentReports extent;
    private static ExtentTest test;

    /**
     * This part initializes the ExtentReports instance and configures the HTML report.
     * Call this once before all tests start running.
     */
    public static void initReport() {
        // Configure the HTML report file location and appearance
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("reports/TestReport.html");
        sparkReporter.config().setDocumentTitle("Selenium Test Automation Report");
        sparkReporter.config().setReportName("INFO6255 Test Scenarios Report");
        sparkReporter.config().setTheme(Theme.STANDARD);

        // Create the ExtentReports instance and attach the reporter
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("Tester", "Team");
    }

    /**
     * This part creates a new test entry in the report.
     *
     * @param testName - name of the test scenario
     * @return ExtentTest instance for logging
     */
    public static ExtentTest createTest(String testName) {
        test = extent.createTest(testName);
        return test;
    }

    /**
     * This part logs a passed step with expected and actual values.
     *
     * @param test     - the ExtentTest instance
     * @param expected - expected result
     * @param actual   - actual result
     */
    public static void logPass(ExtentTest test, String expected, String actual) {
        test.log(Status.PASS, "Expected: " + expected);
        test.log(Status.PASS, "Actual: " + actual);
        test.log(Status.PASS, "Result: PASS");
    }

    /**
     * This part logs a failed step with expected and actual values.
     *
     * @param test     - the ExtentTest instance
     * @param expected - expected result
     * @param actual   - actual result
     */
    public static void logFail(ExtentTest test, String expected, String actual) {
        test.log(Status.FAIL, "Expected: " + expected);
        test.log(Status.FAIL, "Actual: " + actual);
        test.log(Status.FAIL, "Result: FAIL");
    }

    /**
     * This part logs an informational step in the report.
     *
     * @param test    - the ExtentTest instance
     * @param message - info message
     */
    public static void logInfo(ExtentTest test, String message) {
        test.log(Status.INFO, message);
    }

    /**
     * This part flushes and saves the report to the HTML file.
     * Call this once after all tests are done.
     */
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("Report saved to: reports/TestReport.html");
        }
    }
}