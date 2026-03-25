package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import utils.ReportManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Base test class that all test scenario classes will extend.
 * Handles browser setup, teardown, and report initialization.
 * Provides shared WebDriver and WebDriverWait instances.
 */
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Path to the Excel file containing test data
    protected static final String TEST_DATA_PATH = "src/test/resources/TestData.xlsx";

    /**
     * Runs once before the entire test suite.
     * Initializes the HTML report.
     */
    @BeforeSuite
    public void setupSuite() {
        ReportManager.initReport();
        System.out.println("=== Test Suite Started ===");
    }

    /**
     * Runs before each test method.
     * Sets up Chrome browser with necessary configurations.
     */
    @BeforeMethod
    public void setUp() {
        // Auto-download and setup ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        // Configure download settings for file download scenarios
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", System.getProperty("user.dir") + "\\downloads");
        prefs.put("download.prompt_for_download", false);
        prefs.put("plugins.always_open_pdf_externally", true);
        options.setExperimentalOption("prefs", prefs);

        // Initialize the driver and wait
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("Browser launched successfully.");
    }

    /**
     * Runs after each test method.
     * Closes the browser session.
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }

    /**
     * Runs once after the entire test suite.
     * Saves the HTML report.
     */
    @AfterSuite
    public void tearDownSuite() {
        ReportManager.flushReport();
        System.out.println("=== Test Suite Completed. Report saved. ===");
    }
}