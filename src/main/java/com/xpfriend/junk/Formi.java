/*
 * Copyright 2013 XPFriend Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xpfriend.junk;

import java.util.Calendar;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import com.xpfriend.junk.temp.Formatter;

/**
 * 書式を使った文字列とオブジェクトとのコンバータ。
 * 
 * @author Ototadana
 */
public final class Formi {

	private static Formatter instance = Formatter.getInstance();
	
	static {
		new Formi(); // for coverage
	}
	
	private Formi() {
	}
	
	/**
	 * デフォルトの日時書式を取得する。
	 * @return デフォルトの日時書式。
	 */
	public static String getDefaultTimestampFormat() {
		return instance.getDefaultTimestampFormat();
	}

	/**
	 * {@link #getDefaultTimestampFormat()} で取得できる書式に従って指定された日時型データを文字列に変換する。
	 * @param date 文字列変換する日時データ。
	 * @return 文字列変換された日時。
	 */
	public static String formatTimestamp(java.util.Date date) {
		return format(date, getDefaultTimestampFormat());
	}

	/**
	 * {@link #getDefaultTimestampFormat()} で取得できる書式に従って指定された日時型データを文字列に変換する。
	 * @param calendar 文字列変換する日時データ。
	 * @return 文字列変換された日時。
	 */
	public static String formatTimestamp(Calendar calendar) {
		return format(calendar, getDefaultTimestampFormat());
	}

	/**
	 * {@link #getDefaultTimestampFormat()} で取得できる書式に従って指定された日時型データを文字列に変換する。
	 * @param calendar 文字列変換する日時データ。
	 * @return 文字列変換された日時。
	 */
	public static String formatTimestamp(XMLGregorianCalendar calendar) {
		return format(calendar, getDefaultTimestampFormat());
	}

	/**
	 * 指定された日付型データを指定された書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(java.util.Date date, String formatText) {
		return format(date, formatText, null);
	}

	/**
	 * 指定された日付型データを指定された書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(java.util.Calendar date, String formatText) {
		return format(date, formatText, null);
	}

	/**
	 * 指定された日付型データを指定された書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(XMLGregorianCalendar date, String formatText) {
		return format(date, formatText, null);
	}

	/**
	 * 指定された日付型データを指定されたタイムゾーンと書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(java.util.Date date, String formatText, TimeZone timeZone) {
		return instance.format(date, formatText, timeZone);
	}

	/**
	 * 指定された日付型データを指定されたタイムゾーンと書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(java.util.Calendar date, String formatText, TimeZone timeZone) {
		return instance.format(toTimestamp(date), formatText, timeZone);
	}

	/**
	 * 指定された日付型データを指定されたタイムゾーンと書式の文字列に変換する。
	 * @param date 文字列変換する日付データ。
	 * @param formatText 書式。
	 * @return 文字列変換された日時。
	 */
	public static String format(XMLGregorianCalendar date, String formatText, TimeZone timeZone) {
		return instance.format(toTimestamp(date), formatText, timeZone);
	}
	
	/**
	 * Calendar 型の値を java.sql.Timestamp 型の値に変換する。
	 * @param calendar 値を変換する日付データ項目。
	 * @return 変換された java.sql.Timestamp 型のデータ。
	 */
	public static java.sql.Timestamp toTimestamp(Calendar calendar) {
		return instance.toTimestamp(calendar);
	}

	/**
	 * XMLGregorianCalendar 型の値を java.sql.Timestamp 型の値に変換する。
	 * @param calendar 値を変換する日付データ項目。
	 * @return 変換された java.sql.Timestamp 型のデータ。
	 */
	public static java.sql.Timestamp toTimestamp(XMLGregorianCalendar calendar) {
		return toTimestamp(toCalendar(calendar));
	}

	/**
	 * java.util.Date 型の値を java.sql.Timestamp 型の値に変換する。
	 * @param date 値を変換する日付データ項目。
	 * @return 変換された java.sql.Timestamp 型のデータ。
	 */
	public static java.sql.Timestamp toTimestamp(java.util.Date date) {
		return toTimestamp(toCalendar(date));
	}

	/**
	 * java.util.Date 型の値を Calendar 型の値に変換する。
	 * @param date 値を変換する日付データ項目。
	 * @return 変換された Calendar 型のデータ。
	 */
	public static Calendar toCalendar(java.util.Date date) {
		return instance.toCalendar(date);
	}

	/**
	 * XMLGregorianCalendar 型の値を Calendar 型の値に変換する。
	 * @param date 値を変換する日付データ項目。
	 * @return 変換された Calendar 型のデータ。
	 */
	public static Calendar toCalendar(XMLGregorianCalendar cal) {
		return instance.toCalendar(cal);
	}

	static void initialize() {
		instance.initialize();
	}
}
