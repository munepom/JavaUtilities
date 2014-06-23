package com.munepuyo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MiscUtility {

	/**
	 * String の byte 配列から文字コードを判別して返します
	 * @param bytes
	 * @return
	 */
	public Charset detectCharset(byte[] bytes){
		return Arrays.stream( EnumCharset.values() )
				.map( a -> a.getCharset() )
				.map( a -> detectCharset(bytes, a))
				.filter( a -> a != null )
				.findFirst()
				.orElse(null)
		;
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

	public void convertString(Path in_file_path, Path out_file_path, Charset out_charset) {
		try {
			System.out.println("StringEncodingConverter Timer Start ---------------------");
			long timerStart = System.currentTimeMillis();
			// どのようなエンコーディングの文字列が来るか分からないので、一旦 バイナリ化する。
			List<String> in_list = Files.readAllLines(in_file_path, EnumCharset.Latain1.getCharset());
//			Optional<List<String>> opt = Optional.ofNullable(in_list);

			String document = String.join("", in_list);
			System.out.println(document);
			byte[] docBytesLatain1 = document.getBytes(EnumCharset.Latain1.getCharset().name());
			// 文字コード解析
			Charset in_charset = detectCharset(docBytesLatain1);
			if( in_charset == null ){
				// 判別できない場合、強制的に UTF-8 とする。
				System.out.println("ファイルの文字コードを判別できません！");
				in_charset = EnumCharset.UTF8.getCharset();
			}
			System.out.println("in_charset : " + in_charset.name());

			// UTF-8 の文字列に変換すれば、new String( str.getByte("変換したい文字エンコーディング"), "変換したい文字エンコーディング" ) で変換可能。
			String outputStr = "";
			if( in_charset.equals( out_charset ) ){
				outputStr = decodeString( docBytesLatain1, out_charset);
			}
			else {
				outputStr =  decodeString( decodeString2UTF8( docBytesLatain1, in_charset ).getBytes(out_charset), out_charset );
			}
			long timerEnd = System.currentTimeMillis();
			System.out.printf("convert done... %d ms\n", timerEnd - timerStart );
			System.out.println("StringEncodingConverter Timer end -----------------------");

			System.out.printf("out_charset : %s\n", out_charset);
			System.out.printf("document_out is as below......\n%s\n", outputStr);

			Files.write(out_file_path, Files.readAllLines(in_file_path, in_charset), out_charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally {
		}

	}

	/**
	 * エンコードされたバイト配列を、指定文字エンコードで復元します
	 * 生成できない場合、空文字を返します。
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public String decodeString(byte[] bytes, Charset charset){
		try {
			return new String( bytes, charset.name() );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}


	public String decodeString2UTF8(byte[] bytesLatain1) {
		return decodeString2UTF8(bytesLatain1, EnumCharset.UTF8.getCharset());
	}

	/**
	 * ISO-8859-1 でエンコードされたバイト配列を、UTF-8 文字列に復元します
	 * @param bytesLatain1
	 * @param encoding
	 * @return
	 */
	public String decodeString2UTF8(byte[] bytesLatain1, Charset charset){
		Charset charset_utf8 = EnumCharset.UTF8.getCharset();
		String decodedStr = "";
		if( charset.equals( charset_utf8 ) ){
			decodedStr = decodeString( bytesLatain1, charset_utf8 );
		}
		else {
			decodedStr = decodeString( decodeString( bytesLatain1, charset ).getBytes(charset_utf8), charset_utf8 );
		}
		return decodedStr;
	}

	/**
	 * for test
	 * @param args
	 */
	public static void main(String[] args) {
		MiscUtility util = new MiscUtility();
		Path in_file_path = Paths.get(".", "data", "EUC_JP.txt");
		Path out_file_path = Paths.get(".", "data", "EUC_JP_2_W31J.txt");
		Charset out_charset = EnumCharset.CP932.getCharset();
		util.convertString(in_file_path, out_file_path, out_charset);
	}

}
