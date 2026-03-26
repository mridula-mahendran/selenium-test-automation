package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.EncryptionHelper;
import utils.ExcelReader;
import utils.ReportManager;
import utils.ScreenshotHelper;

import java.io.File;
import java.time.Duration;
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
 *   f) Select Graduate in Transcript Level, Audit Transcript in Transcript Type
 *   g) Save page as PDF
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
        // Decrypt the password from Excel (stored as Base64 encrypted)
        String password = EncryptionHelper.decrypt(loginData.get("Password"));

        // Read transcript data from Excel
        Map<String, String> transcriptData = ExcelReader.getRowData(TEST_DATA_PATH, "TranscriptData", 0);
        String transcriptLevel = transcriptData.get("TranscriptLevel");
        String transcriptType = transcriptData.get("TranscriptType");

        try {
            // Step a) Log in to NEU portal via Microsoft SSO
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_Login");
            performNEULogin(url, username, password);
            ReportManager.logInfo(test, "Logged in to NEU portal.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_Login");

            // Step b) Navigate to Student Hub
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_StudentHub");
            driver.get("https://student.me.northeastern.edu/");
            wait.until(ExpectedConditions.urlContains("student.me.northeastern"));
            ReportManager.logInfo(test, "Navigated to Student Hub portal.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_StudentHub");

            // Step c) Click on Resources tab
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_Resources");
            WebElement resourcesTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Resources')]")));
            resourcesTab.click();
            ReportManager.logInfo(test, "Clicked on Resources tab.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_Resources");

            // Step d) Click on Academics, Classes & Registration tile
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_D_Academics");
            WebElement academicsTile = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'fui-Tab__content') and contains(text(),'Academics')]")));
            academicsTile.click();
            ReportManager.logInfo(test, "Clicked on Academics, Classes & Registration.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_D_Academics");

            // Step e) Click on Unofficial Transcript from the EXPANDED LIST
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_E_Transcript");

            // Scroll down to find Unofficial Transcript in the expanded list
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement transcriptLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@data-gtm-resources-link='Unofficial Transcript' and @data-gtm-resources-link-section='Main']")));
            js.executeScript("arguments[0].scrollIntoView(true);", transcriptLink);
            Thread.sleep(1000);
            transcriptLink.click();
            ReportManager.logInfo(test, "Clicked on Unofficial Transcript from expanded list.");
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

            // Handle NEU Banner SSO login (different from Microsoft SSO)
            // Username is without @northeastern.edu
            Thread.sleep(3000);
            try {
                WebElement neuUsername = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("username")));
                // Strip @northeastern.edu from the email
                String bannerUsername = username.replace("@northeastern.edu", "");
                neuUsername.clear();
                neuUsername.sendKeys(bannerUsername);

                WebElement neuPassword = driver.findElement(By.id("password"));
                neuPassword.clear();
                neuPassword.sendKeys(password);

                WebElement loginButton = driver.findElement(
                        By.xpath("//button[@name='_eventId_proceed' or contains(text(),'Log In')]"));
                loginButton.click();
                ReportManager.logInfo(test, "Logged in to NEU Banner SSO.");

                // Duo 2FA - switch into the Duo iframe and click "Send Me a Push"
                Thread.sleep(3000);
                try {
                    WebElement duoIframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.id("duo_iframe")));
                    driver.switchTo().frame(duoIframe);

                    WebElement sendPushButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(text(),'Send Me a Push')]")));
                    sendPushButton.click();
                    System.out.println("Clicked 'Send Me a Push' in Duo iframe.");

                    // Switch back to main page
                    driver.switchTo().defaultContent();
                } catch (Exception duoEx) {
                    System.out.println("No Duo iframe/push button found.");
                    driver.switchTo().defaultContent();
                }

                // Wait for Duo 2FA approval
                System.out.println("Waiting for Duo 2FA approval (Banner SSO)...");
                Thread.sleep(20000);

                // Handle "Is this your device?" prompt if it appears
                try {
                    WebElement yesButton = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                            .until(ExpectedConditions.elementToBeClickable(
                                    By.xpath("//button[contains(text(),'Yes, this is my device')]")));
                    yesButton.click();
                    System.out.println("Clicked 'Yes, this is my device' (Banner SSO).");
                } catch (Exception e2) {
                    System.out.println("No device prompt appeared for Banner SSO.");
                }
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("No Banner SSO login required, already authenticated.");
            }

            // Step f) Select Graduate and Audit Transcript (Select2 custom dropdowns)
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_F_SelectOptions");
            Thread.sleep(3000);

            // Click the Transcript Level dropdown to open it
            WebElement levelDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='transcriptLevelSelection']//a[contains(@class,'select2-choice')]")));
            levelDropdown.click();
            Thread.sleep(1000);

            // Select the transcript level (e.g., "Graduate") from the dropdown list
            WebElement levelOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'ng-binding') and contains(text(),'" + transcriptLevel + "')]")));
            levelOption.click();
            ReportManager.logInfo(test, "Selected Transcript Level: " + transcriptLevel);
            Thread.sleep(2000);

            // Click the Transcript Type dropdown to open it
            WebElement typeDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@id='transcriptTypeSelection']//a[contains(@class,'select2-choice')]")));
            typeDropdown.click();
            Thread.sleep(1000);

            // Select the transcript type (e.g., "Audit Transcript") from the dropdown list
            WebElement typeOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class,'ng-binding') and contains(text(),'" + transcriptType + "')]")));
            typeOption.click();
            ReportManager.logInfo(test, "Selected Transcript Type: " + transcriptType);
            Thread.sleep(5000); // Wait for transcript content to load after both selections

            ReportManager.logInfo(test, "Transcript content loaded successfully.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_F_SelectOptions");

            // Step g) Save page as PDF using Chrome DevTools Protocol
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_G_SavePDF");

            // Use Chrome DevTools to print page to PDF
            String downloadDir = System.getProperty("user.dir") + "\\downloads";
            new File(downloadDir).mkdirs(); // Ensure directory exists

            // Execute CDP command to print page as PDF
            String pdfContent = ((ChromeDriver) driver).executeCdpCommand(
                    "Page.printToPDF",
                    Map.of("printBackground", true, "landscape", false)
            ).get("data").toString();

            // Decode Base64 and save to file
            byte[] pdfBytes = java.util.Base64.getDecoder().decode(pdfContent);
            String pdfPath = downloadDir + "\\Academic_Transcript.pdf";
            java.nio.file.Files.write(java.nio.file.Paths.get(pdfPath), pdfBytes);
            ReportManager.logInfo(test, "Saved transcript as PDF: " + pdfPath);

            // Verify PDF file was created
            File pdfFile = new File(pdfPath);
            Assert.assertTrue(pdfFile.exists() && pdfFile.length() > 0,
                    "PDF file should be created and non-empty.");
            ReportManager.logInfo(test, "PDF file verified: " + pdfFile.length() + " bytes.");

            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_G_SavePDF");

            ReportManager.logPass(test, "Transcript downloaded as PDF",
                    "Transcript page loaded and PDF saved successfully");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "Transcript downloaded as PDF",
                    "Failed: " + e.getMessage());
            Assert.fail("Scenario 1 failed: " + e.getMessage());
        }
    }
}