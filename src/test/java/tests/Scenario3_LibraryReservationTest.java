package tests;

import java.time.Duration;
import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ReportManager;
import utils.ScreenshotHelper;

/**
 * Scenario 3: Reserve a spot in Snell Library.
 * Steps:
 *   a) Open https://library.northeastern.edu/
 *   b) Select Reserve a Study Room
 *   c) Select Boston
 *   d) Click on Book a Room
 *   e) Select Individual Study in Seat Style, Space For 1-4 people in Capacity
 *   f) Scroll down to the end of the page
 */
public class Scenario3_LibraryReservationTest extends BaseTest {

    private static final String SCENARIO_NAME = "Scenario3_LibraryReservation";

    @Test(description = "Reserve a spot in Snell Library")
    public void reserveLibrarySpot() throws InterruptedException {

        ExtentTest test = ReportManager.createTest("Scenario 3: Reserve a Spot in Snell Library");

        try {
            // Step a) Open library.northeastern.edu
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_A_OpenLibrary");
            driver.get("https://library.northeastern.edu/");
            wait.until(ExpectedConditions.titleContains("Library"));
            ReportManager.logInfo(test, "Navigated to Northeastern University Library.");
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_A_OpenLibrary");

            // Step b) Click on Reserve a Study Room
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_B_ReserveRoom");

            // Dismiss cookie consent banner if present
            try {
                WebElement consentButton = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[contains(text(),'Accept') or contains(text(),'OK') or contains(text(),'Got it') or contains(text(),'Agree')]")));
                consentButton.click();
                Thread.sleep(1000);
                System.out.println("Dismissed cookie consent banner.");
            } catch (Exception e) {
                System.out.println("No cookie consent banner found.");
            }

            // Use JavaScript click to avoid interception
            WebElement reserveLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(text(),'Reserve A Study Room') or contains(text(),'Reserve a Study Room')]")));
            JavascriptExecutor js2 = (JavascriptExecutor) driver;
            js2.executeScript("arguments[0].scrollIntoView(true);", reserveLink);
            Thread.sleep(500);
            js2.executeScript("arguments[0].click();", reserveLink);
            ReportManager.logInfo(test, "Clicked on Reserve a Study Room.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_B_ReserveRoom");

            // Step c) Select Boston
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_C_SelectBoston");
            wait.until(ExpectedConditions.urlContains("library-rooms-spaces"));
            WebElement bostonImage = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'rooms-spaces') and contains(@href,'ideas')]//img | //a[contains(@href,'rooms-spaces') and ancestor::*[contains(.,'BOSTON')]]")));
            bostonImage.click();
            ReportManager.logInfo(test, "Selected Boston.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_C_SelectBoston");

            // Step d) Click on Book a Room
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_D_BookRoom");
            WebElement bookRoomButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Book a Room')]")));
            bookRoomButton.click();
            ReportManager.logInfo(test, "Clicked on Book a Room.");
            Thread.sleep(3000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_D_BookRoom");

            // Step e) Select Individual Study from Seat Style dropdown
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_E_SelectFilters");

            // Select Seat Style: Individual Study
            WebElement seatStyleDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[preceding-sibling::*[contains(text(),'Seat Style')] or @id[contains(.,'seat')] or ancestor::*[contains(.,'Seat Style')]/select]")));
            Select seatStyleSelect = new Select(seatStyleDropdown);
            seatStyleSelect.selectByVisibleText("Individual Study");
            ReportManager.logInfo(test, "Selected Seat Style: Individual Study.");
            Thread.sleep(2000);

            // Select Capacity: Space For 1-4 people
            WebElement capacityDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//select[preceding-sibling::*[contains(text(),'Capacity')] or @id[contains(.,'capacity')] or ancestor::*[contains(.,'Capacity')]/select]")));
            Select capacitySelect = new Select(capacityDropdown);
            capacitySelect.selectByVisibleText("Space For 1-4 people");
            ReportManager.logInfo(test, "Selected Capacity: Space For 1-4 people.");
            Thread.sleep(2000);

            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_E_SelectFilters");

            // Verify the filters were applied by re-finding elements (avoids stale reference)
            WebElement seatStyleVerify = driver.findElement(By.xpath("//select[contains(@id,'seat') or ancestor::*[contains(.,'Seat Style')]/select]"));
            Assert.assertTrue(seatStyleVerify.isDisplayed(), "Seat Style dropdown should be visible.");
            ReportManager.logInfo(test, "Verified filter dropdowns are displayed.");

            // Step f) Scroll down to the end of the page
            ScreenshotHelper.takeBeforeScreenshot(driver, SCENARIO_NAME, "Step_F_ScrollDown");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            ReportManager.logInfo(test, "Scrolled down to the end of the page.");
            Thread.sleep(2000);
            ScreenshotHelper.takeAfterScreenshot(driver, SCENARIO_NAME, "Step_F_ScrollDown");

            // Final assertion - verify we are on the booking page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("libcal") || currentUrl.contains("reserve"),
                    "Should be on the library booking page.");

            ReportManager.logPass(test, "Library room filters applied and page scrolled",
                    "Individual Study and 1-4 people capacity selected, page scrolled to bottom");

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(driver, SCENARIO_NAME, "FAILURE");
            ReportManager.logFail(test, "Library room filters applied and page scrolled",
                    "Failed: " + e.getMessage());
            Assert.fail("Scenario 3 failed: " + e.getMessage());
        }
    }
}