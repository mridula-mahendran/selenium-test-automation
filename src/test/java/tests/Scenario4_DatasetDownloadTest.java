package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.io.File;

/**
 * Scenario 4: Download a DATASET (Negative Scenario - This test MUST FAIL).
 * Steps:
 *   a) Open Scholar OneSearch and click on Digital Repository Service
 *   b) Click on Datasets under Featured Content and open any dataset
 *   c) Click on "Zip File" and attempt to download the dataset
 *
 * Negative Assertion: The test asserts the downloaded file is a .csv file,
 * but the actual download is a .zip file, causing the test to FAIL.
 */
public class Scenario4_DatasetDownloadTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario4_DatasetDownload";

    @Test(description = "Download a Dataset - Negative Scenario (must fail)")
    public void downloadDataset() throws InterruptedException {

        ExtentTest test = ReportManager.createTest("Scenario 4: Download a Dataset (Negative)");

        try {
            // Step a) Open Scholar OneSearch
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_OpenOneSearch");
            driver.get("https://onesearch.library.northeastern.edu/discovery/search?vid=01NEU_INST:NU&lang=en");
            wait.until(ExpectedConditions.titleContains("Scholar"));
            ReportManager.logInfo(test, "Navigated to Scholar OneSearch.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_OpenOneSearch");

            // Click on Digital Repository Service
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_ClickDRS");
            WebElement drsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[@translate='mainmenu.label.digitalrepository']/ancestor::a | //span[contains(text(),'digital repository')]/..")));
            drsLink.click();
            ReportManager.logInfo(test, "Clicked on Digital Repository Service.");
            Thread.sleep(3000);

            // Switch to new tab if DRS opened in a new tab
            String mainWindow = driver.getWindowHandle();
            for (String window : driver.getWindowHandles()) {
                if (!window.equals(mainWindow)) {
                    driver.switchTo().window(window);
                    break;
                }
            }

            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_ClickDRS");

            // Step b) Click on Datasets under Featured Content
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_ClickDatasets");

            // Scroll down to find Datasets link
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement datasetsLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[@href='/datasets']")));
            js.executeScript("arguments[0].scrollIntoView(true);", datasetsLink);
            Thread.sleep(1000);
            datasetsLink.click();
            ReportManager.logInfo(test, "Clicked on Datasets under Featured Content.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_ClickDatasets");

            // Open the first dataset
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_OpenDataset");
            WebElement firstDataset = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//a[contains(@href,'/datasets/')])[1]")));
            String datasetName = firstDataset.getText();
            firstDataset.click();
            ReportManager.logInfo(test, "Opened dataset: " + datasetName);
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_OpenDataset");

            // Step c) Click on "Zip File" to download
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_ClickZipFile");
            WebElement zipFileButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Zip File')]")));
            zipFileButton.click();
            ReportManager.logInfo(test, "Clicked on Zip File to download.");
            Thread.sleep(5000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_ClickZipFile");

            // NEGATIVE ASSERTION: Assert that the downloaded file is a .csv file
            // The actual file is a .zip file, so this assertion will FAIL
            String downloadDir = System.getProperty("user.dir") + "\\downloads";
            File downloadFolder = new File(downloadDir);
            File[] files = downloadFolder.listFiles();

            boolean csvFileFound = false;
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".csv")) {
                        csvFileFound = true;
                        break;
                    }
                }
            }

            // This assertion WILL FAIL because the file is .zip, not .csv
            Assert.assertTrue(csvFileFound,
                    "Expected dataset to be downloaded as a .csv file, but the file was downloaded as .zip format instead.");

            ReportManager.logPass(test, "Dataset downloaded as .csv file", "CSV file found in downloads");

        } catch (AssertionError e) {
            // Handle the expected failure
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "EXPECTED_FAILURE");
            ReportManager.logFail(test, "Dataset downloaded as .csv file",
                    "File was downloaded as .zip, not .csv. Negative test case failed as expected.");
            // Re-throw to mark the test as failed in TestNG
            throw e;
        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "Dataset downloaded as .csv file",
                    "Failed: " + e.getMessage());
            Assert.fail("Scenario 4 failed: " + e.getMessage());
        }
    }
}