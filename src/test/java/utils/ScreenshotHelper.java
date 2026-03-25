package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots before and after each test step.
 * Screenshots are saved in folders named after the test scenario.
 */
public class ScreenshotHelper {

    // Base directory where all screenshots will be stored
    private static final String SCREENSHOT_BASE_DIR = "screenshots";

    /**
     * Takes a screenshot and saves it in a folder named after the scenario.
     *
     * @param driver       - the WebDriver instance
     * @param scenarioName - name of the test scenario (used as folder name)
     * @param stepName     - name of the step (used as file name)
     */
    public static String takeScreenshot(WebDriver driver, String scenarioName, String stepName) {
        try {
            // Create the folder path: screenshots/ScenarioName/
            String folderPath = SCREENSHOT_BASE_DIR + File.separator + scenarioName;
            Path directory = Paths.get(folderPath);

            // Create the directory if it does not exist
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Add timestamp to avoid duplicate file names
            String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
            String fileName = stepName + "_" + timestamp + ".png";
            String fullPath = folderPath + File.separator + fileName;

            // Capture the screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Copy to destination
            Files.copy(srcFile.toPath(), Paths.get(fullPath));

            System.out.println("Screenshot saved: " + fullPath);
            return fullPath;

        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to take a "before" screenshot for a step.
     */
    public static String takeBeforeScreenshot(WebDriver driver, String scenarioName, String stepName) {
        return takeScreenshot(driver, scenarioName, "BEFORE_" + stepName);
    }

    /**
     * Method to take an "after" screenshot for a step.
     */
    public static String takeAfterScreenshot(WebDriver driver, String scenarioName, String stepName) {
        return takeScreenshot(driver, scenarioName, "AFTER_" + stepName);
    }
}