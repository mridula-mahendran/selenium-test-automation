package base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import utils.ReportManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Base test class that all test scenario classes will extend.
 * Handles browser setup, teardown, report initialization, and NEU login.
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
     * Handles NEU login via Microsoft SSO + Duo 2FA.
     * Step 1: Enter email on Microsoft login page
     * Step 2: Enter password
     * Step 3: Wait for Duo 2FA manual approval
     * Step 4: Handle "Is this your device?" prompt
     *
     * @param url      - the URL to navigate to (triggers SSO)
     * @param username - NEU email or username
     * @param password - NEU password
     */
    protected void performNEULogin(String url, String username, String password) throws InterruptedException {
        driver.get(url);

        // Step 1: Microsoft SSO - Enter email
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0116")));
        emailField.clear();
        emailField.sendKeys(username);

        // Click Next
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
        nextButton.click();
        System.out.println("Entered email and clicked Next.");

        // Step 2: Enter password
        Thread.sleep(2000);
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
        passwordField.clear();
        passwordField.sendKeys(password);

        // Click Sign In
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("idSIButton9")));
        signInButton.click();
        System.out.println("Entered password and clicked Sign In.");

        // Step 3: Wait for Duo 2FA - manual approval
        System.out.println("Waiting for Duo 2FA approval...");
        Thread.sleep(20000);

        // Step 4: Handle "Is this your device?" prompt
        try {
            WebElement yesButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(text(),'Yes, this is my device')]")));
            yesButton.click();
            System.out.println("Clicked 'Yes, this is my device'.");
        } catch (Exception e) {
            System.out.println("No device prompt appeared.");
        }

        Thread.sleep(3000);
        System.out.println("Login completed.");
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