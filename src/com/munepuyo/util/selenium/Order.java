package com.munepuyo.util.selenium;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * Usage : set this annotation before function (e.g. @Order(order=1), @Order(order=2), ...)
 *
 * @author munepuyo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
	public int order();
}
