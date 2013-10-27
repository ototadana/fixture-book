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
package com.xpfriend.junk.temp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.Config;

/**
 * 
 * 
 * @author Ototadana
 */
public class Formatter {
	
	private String defaultTimestampFormat;

	/**
	 * 日付フォーマット。
	 */
	private ThreadLocal<Map<String, DateFormat>> dateFormatMap =
		new ThreadLocal<Map<String, DateFormat>>() {
			@Override
			protected Map<String, DateFormat> initialValue() {
				return new HashMap<String, DateFormat>();
			}
		};

	public static Formatter getInstance() {
		return Classes.newInstance(Formatter.class);
	}
	
	public Formatter() {
		initialize();
	}
	
	/**
	 * デフォルトの日時書式を取得する。
	 * この書式は、設定ファイルの {@value #TIMESTAMP_FORMAT} で設定可能。
	 * @return デフォルトの日時書式。
	 */
	public String getDefaultTimestampFormat() {
		return defaultTimestampFormat;
	}

	/**
	 * 日付フォーマットを取得する。
	 * @param formatText 書式文字列。
	 * @param timeZone タイムゾーン。
	 * @return 日付フォーマット。
	 */
	public DateFormat getDateFormat(String formatText, TimeZone timeZone) {
		DateFormat dateFormat = getDateFormatMap().get(formatText);
		if(dateFormat == null) {
			dateFormat = new SimpleDateFormat(formatText);
			dateFormat.setLenient(false);
			getDateFormatMap().put(formatText, dateFormat);
		}
		dateFormat.setTimeZone(timeZone);
		return dateFormat;
	}

	private Map<String, DateFormat> getDateFormatMap() {
		return dateFormatMap.get();
	}

	public java.sql.Timestamp toTimestamp(Calendar cal) {
		if(cal==null) {
			return null;
		}

		return new java.sql.Timestamp(cal.getTime().getTime());
	}
	
	public Calendar toCalendar(XMLGregorianCalendar cal) {
		if(cal==null) {
			return null;
		}
		return cal.toGregorianCalendar();
	}

	public Calendar toCalendar(java.util.Date dd) {
		if(dd==null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dd);
		return cal;
	}
	
	public void initialize() {
		defaultTimestampFormat = Config.get("Junk.Formi.defaultTimestampFormat", "yyyy-MM-dd HH:mm:ss");
		dateFormatMap.get().clear();
	}

	public String format(java.util.Date date, String formatText, TimeZone timeZone) {
		if(date == null) {
			return null;
		}
		if(timeZone == null) {
			timeZone = TimeZone.getDefault();
		}
		return getDateFormat(formatText, timeZone).format(date);
	}
}
