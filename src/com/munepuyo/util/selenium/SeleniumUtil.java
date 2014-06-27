package com.munepuyo.util.selenium;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.munepuyo.enums.EnumBrowser;

import static com.munepuyo.util.selenium.SeleniumUtilProps.*;

/**
 *
 * @author munepuyo
 *
 */
public interface SeleniumUtil {

	/**
	 * ログ出力 タグ生成用
	 * @return
	 */
	default public String makeLogTag(){
		String format = "[{0}#{1}]";
		return MessageFormat.format(format, new Object[]{ Thread.currentThread().getStackTrace()[2].getClassName(), Thread.currentThread().getStackTrace()[2].getMethodName() });
	}

	/**
	 * WebDriver を初期化します
	 * @param browser
	 * @return
	 */
	default public WebDriver initWebDriver(EnumBrowser browser){
		WebDriver driver = null;

		if( browser == null ){
			System.err.println("Please input your browser...");
			return driver;
		}

		switch (browser) {
		case InternetExploler :
			System.setProperty("webdriver.ie.driver", webdriver_ie_driver);
			DesiredCapabilities capability=DesiredCapabilities.internetExplorer();
			capability.setCapability( InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true );
			driver = new InternetExplorerDriver();
			break;
		case Firefox :
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.download.useDownloadDir", browser_download_useDownloadDir);
			profile.setPreference("browser.download.folderList", browser_download_folderList); // Custom Location
			profile.setPreference("browser.download.manager.showWhenStarting", browser_download_manager_showWhenStarting); // Whether show dialog or not
			profile.setPreference("browser.download.dir", new File("").getAbsolutePath() + File.separator + browser_download_dir); // Set Directory (\ と / の混合パスはダメだった)
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk", browser_helperApps_neverAsk_saveToDisk); // Set MIME type

			driver = new FirefoxDriver(profile);
			// ファイルダウンロードテストを自動化したい場合は、
			//http://atmarkplant-dj.blogspot.jp/2012/04/selenium-with-junit5-download-file.html
			//http://design-ambience.com/wordpress/?p=114

			break;
		case Chrome :
			System.setProperty("webdriver.chrome.driver", webdriver_chrome_driver);
			driver = new ChromeDriver();	// 他の情報は、https://code.google.com/p/selenium/wiki/ChromeDriver 参照。
			break;
		default :
			System.err.println("Unknown Browser Type [" + browser + "]");
			break;
		}

		return driver;
	}

	/**
	 * 現在のウィンドウ名を取得します
	 * @param driver
	 * @return
	 */
	default public String getCurrentWindowName(WebDriver driver) {
		return driver.getWindowHandle();
	}

	/**
	 * 子ウィンドウ名を取得します。<br />
	 * 無い場合は、空文字を返します。
	 * @param driver
	 * @param currentWindowId
	 * @return
	 */
	default public String getChildWindowName(WebDriver driver, String currentWindowId){
		// 待機メソッド
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d){
				return d.getWindowHandles().size() > 1;	// 子ウィンドウが開くまで待つ
			}
		});

		String newWindowId = driver.getWindowHandles().stream().findFirst().orElse("");
		return newWindowId;
	}

	/**
	 * CSS 指定により WebElement を取得します
	 * @param driver
	 * @param s
	 * @return
	 */
	default public WebElement $( WebDriver driver, String s ) {
		if( driver == null || s == null || s.isEmpty() ){
			return null;
		}
		return driver.findElement( By.cssSelector(s) );
	}

	/**
	 * CSS の id 形式文字列を返します。
	 * @param s
	 * @return
	 */
	default public <T> String getCssId( T s ){
		return setPrefix2Css( s, "#" );
	}

	/**
	 * CSS の class 形式文字列を返します。
	 * @param s
	 * @return
	 */
	default public <T> String getCssCls( T s ){
		return setPrefix2Css(s, ".");
	}

	/**
	 * CSS に prefix を付加した文字列を返します。
	 * @param s
	 * @param prefix
	 * @return
	 */
	default public <T> String setPrefix2Css( T s, String prefix ) {
		return s != null
				? ( s.toString().startsWith(prefix) ?  "" : prefix ) + s
				: "";
	}

	/**
	 * WebElement からターゲットを指定し、値を入力します。
	 * @param elm
	 * @param name
	 * @param keys
	 */
	default public void sendKeysByName(WebElement elm, String name, String keys){
		if( elm == null || name == null || name.isEmpty() ){
			return;
		}
		elm.findElement( By.name(name) ).sendKeys(keys);
	}

	/**
	 * 10 秒待機して CSS 指定要素が click 可能なら、click します。
	 * @param driver
	 * @param selector
	 */
	default public void clickElm(WebDriver driver, String selector) {
		clickElm(driver, selector, 10);
	}

	/**
	 * CSS 指定要素が click 可能なら、click します。
	 * @param driver
	 * @param selector
	 * @param seconds : click 可能になるまで待機する秒数
	 */
	default public void clickElm(WebDriver driver, String selector, int seconds) {
		getClickableElement(driver, selector, seconds).click();
	}

	default public void clickAlert(WebDriver driver) {
		Alert alert = driver.switchTo().alert();
		alert.accept();
	}

	/**
	 * CSS 指定要素が click 可能になれば、取得して返します。
	 * @param driver
	 * @param selector
	 * @return
	 */
	default public WebElement getClickableElement(WebDriver driver, String selector) {
		return getClickableElement(driver, selector, 10);
	}

	/**
	 * CSS 指定要素が click 可能になれば、取得して返します。
	 * @param driver
	 * @param selector
	 * @param seconds : click 可能になるまで待機する秒数
	 * @return
	 */
	default public WebElement getClickableElement(WebDriver driver, String selector, int seconds) {
		return new WebDriverWait(driver, seconds).until( ExpectedConditions.elementToBeClickable( By.cssSelector(selector) ) );
	}

	/**
	 * CSS 指定要素が visible になれば、取得して返します。
	 * @param driver
	 * @param selector
	 * @return
	 */
	default public WebElement getVisibleElement(WebDriver driver, String selector) {
		return getVisibleElement(driver, selector, 10);
	}

	/**
	 * CSS 指定要素が visible になれば、取得して返します。
	 * @param driver
	 * @param selector
	 * @param seconds : visible になるまで待機する秒数
	 * @return
	 */
	default public WebElement getVisibleElement(WebDriver driver, String selector, int seconds) {
		return new WebDriverWait(driver, 10).until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector(selector) ) );
	}

	/**
	 * CSS 指定要素が invisible になれば、true を返します。
	 * @param driver
	 * @param selector
	 * @return
	 */
	default public boolean waitInvisible(WebDriver driver, String selector) {
		return waitInvisible(driver, selector, 10);
	}

	/**
	 * CSS 指定要素が invisible になれば、true を返します。
	 * @param driver
	 * @param selector
	 * @param seconds : invisible になるまで待機する秒数
	 * @return
	 */
	default public boolean waitInvisible(WebDriver driver, String selector, int seconds) {
		return new WebDriverWait(driver, 10).until( ExpectedConditions.invisibilityOfElementLocated( By.cssSelector(selector) ) );
	}

	/**
	 * CSS 指定要素が visible になれば、true を返します。
	 * @param driver
	 * @param selector
	 * @return
	 */
	default public boolean waitVisible(WebDriver driver, String selector) {
		return waitVisible(driver, selector, 10);
	}

	/**
	 * CSS 指定要素が visible になれば、true を返します。
	 * @param driver
	 * @param selector
	 * @param seconds  :  指定要素が visible になるまで待機する秒数
	 * @return
	 */
	default public boolean waitVisible(WebDriver driver, String selector, int seconds) {
		// Lambda 式が使えない。。。
		return new WebDriverWait(driver, 10).until( new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver d){
				return d.findElement( By.cssSelector(selector) ).isDisplayed();
			}
		});
	}

	/**
	 * png 形式でキャプチャを取得します。
	 * @param driver
	 * @param file_path_name : ファイルパス名
	 * @return
	 */
	default public boolean captureScreen(WebDriver driver, String file_path_name) {
		if( driver instanceof TakesScreenshot ) {
			TakesScreenshot screen = (TakesScreenshot) driver;
			Path file_path = Paths.get(file_path_name);
			try {
				Files.write(file_path, screen.getScreenshotAs(OutputType.BYTES));
			} catch (WebDriverException | IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * png 形式でキャプチャを取得します。
	 * @param driver
	 * @param capture_dir : キャプチャ画像保管ディレクトリ
	 * @param file_name : キャプチャ画像名
	 * @return
	 */
	default public boolean captureScreen(WebDriver driver, String capture_dir_name, String file_name) {
		if( driver instanceof TakesScreenshot ) {
			TakesScreenshot screen = (TakesScreenshot) driver;
			Path capture_dir = Paths.get(capture_dir_name);
			Path capture = capture_dir.resolve(file_name);
			try {
				Files.write(capture, screen.getScreenshotAs(OutputType.BYTES));
			} catch (WebDriverException | IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			return true;
		}
		else {
			return false;
		}
	}
}
