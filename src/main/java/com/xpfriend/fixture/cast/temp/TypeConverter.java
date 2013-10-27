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
package com.xpfriend.fixture.cast.temp;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormatSymbols;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

import com.xpfriend.junk.Formi;
import com.xpfriend.junk.Strings;

/**
 * @author Ototadana
 *
 */
class TypeConverter {
	private static final String JDBC_FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	private static final String JDBC_FORMAT_DATE = "yyyy-MM-dd";
	private static final String JDBC_FORMAT_TIME = "HH:mm:ss";
	private static BeanUtilsBean beanUtils; 
	private static ConvertUtilsBean nullConverter;
	static {
		nullConverter = new BeanUtilsBean().getConvertUtils();
		beanUtils = new BeanUtilsBean();
		ConvertUtilsBean convertUtil = beanUtils.getConvertUtils();
		convertUtil.register(true, false, -1);
		convertUtil.register(getTextConverter(), String.class);
	}

	public static boolean isConvertible(Class<?> type) {
		return beanUtils.getConvertUtils().lookup(type) != null;
	}
	
	public static Object getNullValue(Class<?> type) {
		return nullConverter.convert((String)null, type);
	}
	
	public static Object changeType(String textValue, Class<?> type) {
		if(Strings.isEmpty(textValue) && !type.isPrimitive()) {
			return null;
		}
		if(Object.class.equals(type)) {
			return textValue;
		}
		if(Number.class.isAssignableFrom(type)) {
			textValue = removeGroupingSeparator(textValue);
		}
		if(Date.class.isAssignableFrom(type)) {
			return convertByCustomFormat(textValue, type);
		}
		return beanUtils.getConvertUtils().convert(textValue, type);
	}
	
	private static Object convertByCustomFormat(String textValue, Class<?> type) {
		String format = getDateTimeFormat(textValue);
		if(java.sql.Date.class.isAssignableFrom(type)) {
			return convert(new SqlDateConverter(), format, textValue, type);
		} else if(java.sql.Time.class.isAssignableFrom(type)) {
			return convert(new SqlTimeConverter(), format, textValue, type);
		} else {
			return convert(new SqlTimestampConverter(), format, textValue, type);
		}
	}

	private static Object convert(DateTimeConverter converter, String format, String value, Class<?> type) {
		try {
			return converter.convert(type, value);
		} catch(ConversionException e) {
			converter.setPattern(format);
			return converter.convert(type, value);
		}
	}

	private static String removeGroupingSeparator(String text) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		char groupingSeparator = symbols.getGroupingSeparator();
		StringBuilder sb = new StringBuilder(text.length());
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(c != groupingSeparator) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static BeanUtilsBean getBeanUtils() {
		return beanUtils;
	}

	private static Converter getTextConverter() {
		return new Converter() {
			@Override
			public Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {
				if(value == null) {
					return null;
				}
				if(value instanceof BigDecimal) {
					return ((BigDecimal) value).toPlainString();
				}
				if(value instanceof Date) {
					return Formi.format((Date)value, JDBC_FORMAT_TIMESTAMP);
				}
				return value.toString();
			}
		};
	}
		
	public static String toString(Object object) {
		return beanUtils.getConvertUtils().convert(object);
	}
	
	public static Class<?> getJavaType(int sqltype, String name, int precision, int scale) {
		Class<?> c = getJavaType(sqltype, precision, scale);
		// 変換できない場合は、型を示す文字列を見て判断する。
		if(name != null && c.equals(Object.class)) {
			if(name.startsWith("TIMESTAMP")) { // Oracle TIMESTAMP 型への対応
				c = java.sql.Timestamp.class;
			} else if(name.startsWith("NCHAR") || name.startsWith("NVARCHAR")) {
				c = String.class;
			}
		}

		// SQL Server の date/time 型対応
		if(String.class.equals(c)) {
			if("date".equals(name)) {
				return java.sql.Date.class;
			} else if("time".equals(name)) {
				return java.sql.Time.class;
			}
		}

		return c;
	}
	
	private static Class<?> getJavaType(int sqltype, int precision, int scale) {
		switch(sqltype) {
			case Types.BIGINT: 		return Long.class;
			case Types.BIT: 		return Boolean.class;
			case Types.BOOLEAN: 	return Boolean.class;
			case Types.CHAR: 		return String.class;
			case Types.DECIMAL: 	return getNumericType(precision, scale);
			case Types.DOUBLE: 		return Double.class;
			case Types.FLOAT: 		return Double.class;
			case Types.INTEGER: 	return Integer.class;
			case Types.LONGVARCHAR: return String.class;
			case Types.NUMERIC: 	return getNumericType(precision, scale);
			case Types.REAL: 		return Float.class;
			case Types.SMALLINT: 	return Short.class;
			case Types.DATE: 		return java.sql.Timestamp.class;
			case Types.TIME: 		return java.sql.Time.class;
			case Types.TIMESTAMP: 	return java.sql.Timestamp.class;
			case Types.TINYINT: 	return Byte.class;
			case Types.VARCHAR: 	return String.class;
			case Types.BLOB: 		return byte[].class;
			case Types.LONGVARBINARY: return byte[].class;
			case Types.CLOB: 		return String.class;
			case Types.BINARY: 		return byte[].class;
			case Types.VARBINARY: 	return byte[].class;
			case Types.NVARCHAR: 	return String.class;
			case Types.NCHAR: 		return String.class;
			case Types.LONGNVARCHAR: return String.class;
			case -155: 				return java.sql.Timestamp.class;
			default: 				return Object.class;
		}
	}

	private static Class<?> getNumericType(int precision, int scale) {
		if(scale > 0) {
			return java.math.BigDecimal.class;
		}
		if(precision > 18) {
			return java.math.BigDecimal.class;
		} else if(precision > 9) {
			return Long.class;
		} else {
			return Integer.class;
		}
	}

	public static String getDateTimeFormat(String instance) {
		String format = getDateTimeFormatInternal(instance);
		if(instance.indexOf('/') > -1) {
			format = format.replace(JDBC_FORMAT_DATE, "yyyy/MM/dd");
		}
		return format;
	}
	
	private static String getDateTimeFormatInternal(String instance) {
		if(instance.length() == JDBC_FORMAT_TIME.length()) {
			return JDBC_FORMAT_TIME;
		}
		if(instance.length() == JDBC_FORMAT_DATE.length()) {
			return JDBC_FORMAT_DATE;
		}
		if(instance.length() > JDBC_FORMAT_TIMESTAMP.length()) {
			String formatText = JDBC_FORMAT_TIMESTAMP;
			String mills = getMillis(instance);
			if(mills != null) {
				formatText = formatText + "." + mills;
			}
			return formatText;
		}
		return JDBC_FORMAT_TIMESTAMP;
	}

	private static String getMillis(String instance) {
		return instance.indexOf(".") > -1? "SSS" : null;
	}
}
