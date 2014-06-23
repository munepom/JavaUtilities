package com.munepuyo;

import java.nio.charset.Charset;

public enum EnumCharset {
	UTF8 (Charset.forName("UTF-8")),
	CP932 (Charset.forName("Windows-31J")),
	SJIS (Charset.forName("Shift-JIS")),
	EUC_JP (Charset.forName("EUC-JP")),
	JIS (Charset.forName("ISO-2022-JP")),
	Latain1 (Charset.forName("ISO-8859-1")),
	;

	private Charset charset;
	private EnumCharset(Charset charset) {
		this.charset = charset;
	}

	public Charset getCharset() {
		return this.charset;
	}
}
