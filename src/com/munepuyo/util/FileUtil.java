package com.munepuyo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * File Utilities
 * @author munepuyo
 *
 */
public interface FileUtil {

	/**
	 * properties を取得します。クラス名と同じ場所に .properties ファイルが必要です。
	 * @param cls
	 * @return
	 */
	default public Properties getProperties(Class<?> cls){
		Properties pList = new Properties();
		try (
			InputStream in = cls.getResourceAsStream(cls.getSimpleName() + ".properties");
			InputStreamReader isr = new InputStreamReader(in)
		){
			pList.load(isr);
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return pList;
	}

	/**
	 * properties ファイルを読み込みます。
	 * @param rootPath
	 * @param fileName
	 * @return
	 */
	default public Properties getProperties(String rootPath, String fileName){
		Properties pList = new Properties();
		File root = new File( rootPath );
		File file = new File( root, fileName );
		System.out.println("rootPath: " + rootPath);
		System.out.println("fileName: " + fileName);

		try (
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
		)
		{
			pList.load(isr);
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return pList;
	}

}
