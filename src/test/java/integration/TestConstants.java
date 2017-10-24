package integration;

public class TestConstants {
	public static final String CHROME_PATH_WINDOWS = "./src/test/resources/chromedriver.exe";
	public static final String CHROME_PATH_MAC = "./src/test/resources/chromedriver";
	public static final String CHROME_DRIVER = "webdriver.chrome.driver";
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static final String CHROME_PATH = ((OS.indexOf("win") >= 0) ? CHROME_PATH_WINDOWS: CHROME_PATH_MAC);

	public static final String HOME_URL = "http://localhost:8080";
}
