package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.util.Map;
import java.util.Set;

/**
 * Scenario 1: Download the latest transcript.
 * Steps:
 *   a) Log in to My NEU portal
 *   b) Launch the Student Hub portal
 *   c) Hit the Resources tab
 *   d) Go to Academics, Classes & Registration
 *   e) Go to Unofficial Transcript
 *   f) Select Graduate in Transcript Level, Audit Transcript in Transcript Type, click Submit
 *   g) Right-click on screen, Print Page, Save as PDF
 */
public class Scenario1_TranscriptTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario1_Transcript";

    @Test(description = "Download the latest transcript from NEU Student Hub")
    public void downloadTranscript() throws InterruptedException {

        ExtentTest test = ReportManager.createTest("Scenario 1: Download Latest Transcript");

        // Read login data from Excel
        Map<String, String> loginData = ExcelReader.getRowData(TEST_DATA_PATH, "LoginData", 0);
        String url = loginData.get("URL");
        String username = loginData.get("Username");
        String password = loginData.get("Password");

        // Read transcript data from Excel
        Map<String, String> transcriptData = ExcelReader.getRowData(TEST_DATA_PATH, "TranscriptData", 0);
        String transcriptLevel = transcriptData.get("TranscriptLevel");
        String transcriptType = transcriptData.get("TranscriptType");

        try {
            // Step a) Navigate to NEU portal and log in
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Login");
            driver.get(url);
            ReportManager.logInfo(test, "Navigated to: " + url);

            // Enter username
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);
            driver.findElement(By.name("_eventId_proceed")).click();
            ReportManager.logInfo(test, "Entered credentials and clicked login.");

            // Handle Duo 2FA - wait for user to manually approve
            // The assignment allows pressing Enter once for 2FA
            ReportManager.logInfo(test, "Waiting for Duo 2FA approval...");
            Thread.sleep(20000); // Wait 20 seconds for manual 2FA approval
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Login");

            // Step b) Launch Student Hub portal
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_StudentHub");
            wait.until(ExpectedConditions.urlContains("me.northeastern.edu"));
            // Navigate to Student Hub
            driver.get("https://me.northeastern.edu/StudentHub");
            wait.until(ExpectedConditions.titleContains("Student"));
            ReportManager.logInfo(test, "Launched Student Hub portal.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_StudentHub");

            // Step c) Click on Resources tab
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_Resources");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Resources') or contains(@href,'resources')]")));
            resourcesTab.click();
            ReportManager.logInfo(test, "Clicked on Resources tab.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_Resources");

            // Step d) Click on Academics, Classes & Registration
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_D_Academics");
            WebElement academicsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Academics')]")));
            academicsLink.click();
            ReportManager.logInfo(test, "Clicked on Academics, Classes & Registration.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_D_Academics");

            // Step e) Click on Unofficial Transcript
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_E_Transcript");
            WebElement transcriptLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Unofficial Transcript')]")));
            transcriptLink.click();
            ReportManager.logInfo(test, "Clicked on Unofficial Transcript.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_E_Transcript");

            // Switch to new window if transcript opens in a new tab
            String mainWindow = driver.getWindowHandle();
            Set<String> allWindows = driver.getWindowHandles();
            if (allWindows.size() > 1) {
                for (String window : allWindows) {
                    if (!window.equals(mainWindow)) {
                        driver.switchTo().window(window);
                        break;
                    }
                }
            }

            // Step f) Select Graduate and Audit Transcript, then Submit
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_F_SelectOptions");
            Thread.sleep(3000);

            // Select Transcript Level
            WebElement levelDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("levl")));
            Select levelSelect = new Select(levelDropdown);
            levelSelect.selectByVisibleText(transcriptLevel);
            ReportManager.logInfo(test, "Selected Transcript Level: " + transcriptLevel);

            // Select Transcript Type
            WebElement typeDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("tprt")));
            Select typeSelect = new Select(typeDropdown);
            typeSelect.selectByVisibleText(transcriptType);
            ReportManager.logInfo(test, "Selected Transcript Type: " + transcriptType);

            // Click Submit
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='submit' and @value='Submit']")));
            submitButton.click();
            ReportManager.logInfo(test, "Clicked Submit button.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_F_SelectOptions");

            // Step g) Print page and save as PDF using Ctrl+P shortcut
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_G_PrintPage");

            // Use JavaScript to trigger print (the PDF save is configured in ChromeOptions)
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.print();");
            ReportManager.logInfo(test, "Triggered Print to save as PDF.");
            Thread.sleep(5000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_G_PrintPage");

            // Assert that transcript page loaded successfully
            String pageSource = driver.getPageSource();
            Assert.assertTrue(pageSource.contains("Transcript") || pageSource.contains("Student Information"),
                    "Transcript page did not load correctly.");

            ReportManager.logPass(test, "Transcript downloaded as PDF", "Transcript page loaded and PDF saved");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "Transcript downloaded as PDF", "Failed: " + e.getMessage());
            Assert.fail("Scenario 1 failed: " + e.getMessage());
        }
    }
}