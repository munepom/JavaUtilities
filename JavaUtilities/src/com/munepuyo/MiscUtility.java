package com.munepuyo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class MiscUtility {

	/**
	 * String の byte 配列から文字コードを判別して返します
	 * @param bytes
	 * @return
	 */
	public Charset detectCharset(byte[] bytes){
		String[] charset_name_list = {
			"UTF-8",
			"Windows-31J",
			"Shift_JIS",
			"EUC-JP",
			"ISO-2022-JP"
		};

		Charset charset = null;

		for( String charset_name : charset_name_list){
			charset = detectCharset(bytes, Charset.forName(charset_name));
			if( charset != null ){
				break;
			}
		}

		return charset;
	}

	/**
	 * byte 配列を復元して文字コードを判別します
	 * @param bytes
	 * @param charset
	 * @return
	 */
	public Charset detectCharset(byte[] bytes, Charset charset){
		try{
			charset.newDecoder().decode(ByteBuffer.wrap(bytes));
		}
		catch (CharacterCodingException e) {
			return null;
		}
		return charset;
	}

	/**
	 * エンコードされたバイト配列を、指定文字エンコードで復元します
	 * 生成できない場合、空文字を返します。
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public String decodeString(byte[] bytes, String encoding){
		try {
			return new String( bytes, encoding );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * ISO-8859-1 でエンコードされたバイト配列を、UTF-8 文字列に復元します
	 * @param bytesLatain1
	 * @param encoding
	 * @return
	 */
	public String decodeString2UTF8(byte[] bytesLatain1, String encoding){
		String encUTF8 = "UTF-8";
		String enc = encoding;
		if( enc == null ){
			enc = encUTF8;
		}

		String decodedStr = "";
		if( enc.equals(encUTF8) ){
			decodedStr = decodeString( bytesLatain1, encUTF8 );
		}
		else {
			try {
				decodedStr = decodeString( decodeString( bytesLatain1, encoding ).getBytes(encUTF8), encUTF8 );
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return decodedStr;
	}


}
