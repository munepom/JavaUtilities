package com.munepuyo.util.string;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.munepuyo.enums.EnumCharset;
import com.munepuyo.util.PuyoException;

/**
 *
 * Utilities for Java
 *
 * @author munepuyo
 *
 */
public class StringUtility {

	/**
	 * CSV をパースします (囲み文字無し)
	 * @param s
	 * @return
	 */
	public String[] parseCsv(String s) {
		return parseString(s, ",", "");
	}

	/**
	 * 指定囲み文字を考慮して、CSV をパースします
	 * @param s
	 * @param enclosure
	 * @return
	 */
	public String[] parseCsv(String s, String enclosure) {
		return parseString(s, ",", enclosure);
	}

	/**
	 * 指定区切り文字で文字列を区切ります。
	 * @param s : 区切られる文字列
	 * @param delimiter : 区切り文字
	 * @param enclosure : 囲み文字
	 * @return
	 */
	public String[] parseString(String s, String delimiter, @Nonnull String enclosure)
	{
		//TODO: 正規表現改良
		String format = "(?<=%s|^)([^%s]*)(?=%s|$)";
		String reg_delimiter = String.format(format, delimiter, delimiter, delimiter);

		if( enclosure != null && enclosure.length() > 0 ) {
			String format_enclosure = "%s([^%s]*)%s";
			String reg_enclosure = String.format(format_enclosure, enclosure, enclosure, enclosure);
			reg_delimiter = reg_enclosure + "|" + reg_delimiter;
		}

		Pattern p = Pattern.compile(reg_delimiter);
		Matcher m = p.matcher(s);

		ArrayList<String> s_list = new ArrayList<>();
		String tmp_s = "";
		while( m.find() ) {
			tmp_s = m.group();
			System.out.printf("tmp_s -> [%s]\n", tmp_s);
			if( tmp_s.startsWith(enclosure) && tmp_s.endsWith(enclosure) ) {
				tmp_s = tmp_s.substring(1, tmp_s.length() - 1);
				System.out.printf("removed enclosure from tmp_s -> [%s]\n", tmp_s);
			}
			s_list.add(tmp_s);
		}
		return s_list.toArray(new String[0]);
	}

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

	/**
	 * Path 対象のファイルの文字エンコードを判定して返します。<br />
	 * @param file_path
	 * @return : Charset or null
	 */
	public Charset detectCharset(Path file_path) {
		// どのようなエンコーディングの文字列が来るか分からないので、一旦 ISO-8859-1 で読み込む。
		List<String> in_list = null;
		try {
			in_list = Files.readAllLines(file_path, EnumCharset.Latain1.getCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String document = String.join("", in_list);

		byte[] docBytesLatain1 = null;
		try {
			docBytesLatain1 = document.getBytes(EnumCharset.Latain1.getCharset().name());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 文字コード解析
		Charset charset = detectCharset(docBytesLatain1);
		System.out.printf("Charset of %s : %s\n", file_path, charset);
		return detectCharset(docBytesLatain1);
	}

	/**
	 * 対象ファイルの文字コードを変換して指定ファイルへ保存します。
	 * @param in_file_path
	 * @param out_file_path
	 * @param out_charset
	 */
	public void convertString(Path in_file_path, Path out_file_path, Charset out_charset) {
		try {
			Charset in_charset = Optional.ofNullable(detectCharset(in_file_path))
								.orElseThrow(() -> new PuyoException("文字コード判別不可能！"))
			;
			System.out.printf("out_charset : %s\n", out_charset);
			Files.write(out_file_path, Files.readAllLines(in_file_path, in_charset), out_charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch (IOException | PuyoException e){
			e.printStackTrace();
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

	/**
	 * ISO-8859-1 エンコードの byte 配列を UTF-8 文字列へ変換します
	 * @param bytesLatain1
	 * @return
	 */
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
		StringUtility util = new StringUtility();
//		Path in_file_path = Paths.get(".", "data", "EUC_JP.txt");
//		Path out_file_path = Paths.get(".", "data", "EUC_JP_2_W31J.txt");
//		Charset out_charset = EnumCharset.CP932.getCharset();
//		util.convertString(in_file_path, out_file_path, out_charset);

		util.parseCsv("テスト,\" :,: \",\" , , , \",\"\",\" テステス \"", "\"");
	}

}
