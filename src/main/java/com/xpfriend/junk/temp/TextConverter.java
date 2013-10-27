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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.Formi;

/**
 * @author Ototadana
 */
public class TextConverter {

	public static final String TIMESTAMP_FORMAT;
	
	static {
		TIMESTAMP_FORMAT = Formi.getDefaultTimestampFormat();
	}
	
	public static TextConverter getInstance() {
		return Classes.newInstance(TextConverter.class);
	}

	/**
	 * オブジェクトを文字列に変換する。
	 * @param object 変換元オブジェクト。
	 * @return 変換された文字列。null は "" に変換して返される。
	 */
	public String toString(Object object) {
		if(object==null) {
			return "";
		} else if(object instanceof String) {
			return (String)object;
		} else if(object instanceof Date) {
			return Formi.formatTimestamp((Date)object);
		} else if(object instanceof BigDecimal) {
			return ((BigDecimal)object).toPlainString();
		} else if(object instanceof Number) {
			return object.toString();
		} else if(object instanceof Boolean) {
			return object.toString();
		} else if(object instanceof java.util.Calendar) {
			return Formi.formatTimestamp((Calendar)object);
		} else if(object instanceof XMLGregorianCalendar) {
			return Formi.formatTimestamp((XMLGregorianCalendar)object);
		} else if(object instanceof List) {
			return listToString((List<?>)object);
		} else if(isArray(object.getClass())) {
			return arrayToString(object);
		} else {
			return object.toString();
		}
	}
	
	/**
	 * 配列を "|" 区切りの文字列に変換する。
	 * @param array 配列。
	 * @return 変換された文字列。
	 */
	protected String arrayToString(Object array) {
		StringBuilder sb = new StringBuilder();
		int length = Array.getLength(array);
		for(int i=0; i<length; i++) {
			sb.append(toString(Array.get(array, i)));
			if(i < length-1) {
				sb.append("|");
			}
		}
		return sb.toString();
	}

	/**
	 * リストを "|" 区切りの文字列に変換する。
	 * @param list リスト。
	 * @return 変換された文字列。
	 */
	protected String listToString(List<?> list) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++) {
			sb.append(toString(list.get(i)));
			if(i < list.size()-1) {
				sb.append("|");
			}
		}
		return sb.toString();
	}

	protected boolean isArray(Class<?> type) {
		return type.isArray();
	}
}
