package com.munepuyo.util.selenium;

import static com.munepuyo.util.selenium.SeleniumUtilProps.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.munepuyo.enums.EnumBrowser;
import com.munepuyo.util.FileUtil;

/**
 *
 * @author munepuyo
 *
 */
public abstract class AbsSeleniumTest
	implements
		SeleniumUtil,
		FileUtil
{
	/** WebDriver (Global) */
	public static WebDriver DRIVER;

	/** ブラウザ名 (Global) */
	public static EnumBrowser BROWSER = EnumBrowser.Chrome;

	/**
	 * キャプチャ保存ディレクトリ (SimpleDateFormat 対応)
	 */
	public static String CAPTURE_DIR_NAME = "'capture'/yyyyMMdd";

	@BeforeClass
	public static void setUpBeforeClass(){
		// init driver
		if( DRIVER == null ) {
			DRIVER = initWebDriverStatic(BROWSER);
		}
	}

	@AfterClass
	public static void tearDownAfterClass(){
		// quit WebDriver
		if( DRIVER != null ) {
			DRIVER.quit();
			DRIVER = null;
		}
	}


	/**
	 * WebDriver を初期化します
	 * @param browser
	 * @return
	 */
	public static WebDriver initWebDriverStatic(EnumBrowser browser){
		WebDriver driver = null;

		if( browser == null ){
			System.err.println("Please input your browser...");
			return driver;
		}

		//switch 文は使わない。
		//https://bugs.eclipse.org/bugs/show_bug.cgi?id=434442
		if (browser == EnumBrowser.InternetExploler) {
			System.setProperty("webdriver.ie.driver", webdriver_ie_driver);
			DesiredCapabilities capability=DesiredCapabilities.internetExplorer();
			capability.setCapability( InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true );
			driver = new InternetExplorerDriver();
		} else if (browser == EnumBrowser.Firefox) {
			String download_dir = new File("").getAbsolutePath() + File.separator + browser_download_dir;
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.download.useDownloadDir", browser_download_useDownloadDir);
			profile.setPreference("browser.download.folderList", browser_download_folderList);                             // Custom Location
			profile.setPreference("browser.download.manager.showWhenStarting", browser_download_manager_showWhenStarting); // Whether show dialog or not
			profile.setPreference("browser.download.dir", download_dir);                                                   // Set Directory (\ と / の混合パスはダメだった)
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk", browser_helperApps_neverAsk_saveToDisk);       // Set MIME type (application/octet)
			driver = new FirefoxDriver(profile);
		} else if (browser == EnumBrowser.Chrome) {
			System.setProperty("webdriver.chrome.driver", webdriver_chrome_driver);
			driver = new ChromeDriver();	// 他の情報は、https://code.google.com/p/selenium/wiki/ChromeDriver 参照。
		} else {
			System.err.println("Unknown Browser Type [" + browser + "]");
		}

		return driver;
	}
}
