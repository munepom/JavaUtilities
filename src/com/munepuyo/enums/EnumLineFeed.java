package com.munepuyo.enums;

/**
 *
 * Enums of line feed
 *
 * @author munepuyo
 *
 */
public enum EnumLineFeed {
	CR("\r"),
	LF("\n"),
	CRLF("\r\n"),
	;

	private String line_feed;
	private EnumLineFeed(String line_feed) {
		this.line_feed = line_feed;
	}

	public String getLineFeed() {
		return this.line_feed;
	}
}
