package com.munepuyo.util.selenium;

public interface SeleniumUtilProps {
	/**
	 * driver path for IE
	 */
	public String webdriver_ie_driver = "hoge";

	/**
	 * driver path for Chrome
	 */
	public String webdriver_chrome_driver = "fuga";

	/**
	 * ファイルを自動的にダウンロードディレクトリへ保存するなら、true
	 */
	public boolean browser_download_useDownloadDir = true;

	/**
	 * ダウンロードするファイルの保存先フォルダを指定  0:デスクトップ 1：ダウンロードフォルダ 2:ダウンロードに指定された最後のフォルダ
	 */
	public int browser_download_folderList = 1;

	/**
	 * ダウンロードマネージャダイアログの表示を行うなら、true
	 */
	public static boolean browser_download_manager_showWhenStarting = false;

	/**
	 * ダウンロードディレクトリのパス名 (コンテキストからの相対パス)
	 */
	public static String browser_download_dir = "download";

	/**
	 * ダイアログ表示されずにダウンロードされる MIME タイプ
	 */
	public static String browser_helperApps_neverAsk_saveToDisk = "application/octet-stream";
}
