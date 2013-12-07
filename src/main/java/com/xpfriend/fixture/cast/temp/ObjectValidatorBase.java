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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.fixture.toolkit.PathUtil;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;
import com.xpfriend.junk.Formi;
import com.xpfriend.junk.Strings;
import com.xpfriend.junk.temp.Formatter;

/**
 * @author Ototadana
 *
 */
public abstract class ObjectValidatorBase extends ObjectOperatorBase implements ObjectValidator {

	private static final String TODAY = "${TODAY}";
	private static Formatter dateFormatter = Formatter.getInstance();
    private static final String[] DATEFORMATS = new String[]{
    	"yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss",
    	"yyyyMMdd", "yyyyMMddHHmm", "yyyyMMddHHmmss"};

	private ObjectValidator parent;
	
	protected ObjectValidatorBase(ObjectValidator parent) {
		super(Section.SectionType.EXPECTED_RESULT);
		this.parent = parent;
	}
	
	@Override
	public void validate(Object obj, String typeName) {
		if(obj == null) {
			assertNull(typeName);
			return;
		}

		Table expected;
		Collection<?> actual;
		Class<?> type;
		if(obj.getClass().isArray()) {
			actual = Arrays.asList((Object[])obj);
			type = obj.getClass().getComponentType();
		} else if(obj instanceof Iterable) {
			type = null;
			for(Object o : (Iterable<?>)obj) {
				if(o != null) {
					type = o.getClass();
					break;
				}
			}
			actual = toCollection((Iterable<?>)obj);
		} else {
			actual = Collections.singletonList(obj);
			type = obj.getClass();
		}
		expected = getTable(getSection(), type, typeName);
		assertEquals(expected, actual);
	}

	private Collection<?> toCollection(Iterable<?> obj) {
		if(obj instanceof Collection) {
			return (Collection<?>)obj;
		} else {
			List<Object> list = new ArrayList<Object>();
			for(Object o : obj) {
				list.add(o);
			}
			return list;
		}
	}

	protected void assertNull(String typeName) {
		if(getSection().hasTable(typeName) && !isNull(getSection().getTable(typeName))) {
			Assertie.fail("M_Fixture_Temp_ObjectValidator_AssertNull", typeName, getSection().getTable(typeName));
		}
	}

	private boolean isNull(Table table) {
		if(table.getRows().size() != 1) {
			return false;
		}
		Map<String, String> values = table.getRows().get(0).getValues();
		return values.size() == 1 && values.containsKey(OWN) &&
				NULL.equals(values.get(OWN));
	}

	protected void assertEquals(Table table, Collection<?> actualList) {
		List<Row> expectedList = table.getRows();
		assertLineNumber(table, expectedList.size(), actualList.size());

		int i = 0;
		for(Object actual : actualList) {
			assertEquals(table, expectedList.get(i++), actual);
		}
	}
	
	protected void assertLineNumber(Table table, int expected, int actual) {
		if(expected != actual) {
			Assertie.assertEqualsInt(expected, actual, "M_Fixture_Temp_ObjectValidator_AssertLineNumber", 
					table, table.getName(), expected, actual);
		}
	}

	protected void assertEquals(Table table, Row row, Object actualObject) {
		if(row.isDeleted()) {
			return;
		}
		List<Column> columns = table.getColumns();
		Map<String, String> expectedObject = row.getValues();
		for(Column column : columns) {
			if(column != null) {
				String name = column.getName();
				String expectedPropertyValue = expectedObject.get(name);
				Object actualPropertyValue = getPropertyValue(actualObject, name, table, row);
				assertEquals(table, row, name, expectedPropertyValue, actualPropertyValue);
			}
		}
	}
	
	protected void assertEquals(Table table, Row row, String columnName, 
			String expected, Object actual) {
		if(assertEmpty(table, row, columnName, expected, actual)) {
			return;
		}
		if(assertNotEmpty(table, row, columnName, expected, actual)) {
			return;
		}
		String actualAsText = toString(expected, actual);
		if(assertEqualsStrictly(table, row, columnName, expected, actualAsText)) {
			return;
		}
		if(assertNestedObject(table, row, columnName, expected, actual)) {
			return;
		}
		if(assertPartialEquality(table, row, columnName, expected, actualAsText)) {
			return;
		}
		if(assertEqualsAsDate(table, row, columnName, expected, actual)) {
			return;
		}
		if(assertEqualsAsBoolean(table, row, columnName, expected, actual)) {
			return;
		}
		if(assertEqualsAsByteArray(table, row, columnName, expected, actual)) {
			return;
		}
		if(assertEqualsByRegex(table, row, columnName, expected, actualAsText)) {
			return;
		}
		Assertie.assertEquals(expected, actualAsText, 
				"M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, expected, actualAsText, getClass(actual));
	}
	
	private Object getClass(Object actual) {
		if(actual == null || actual instanceof String) {
			return "";
		}
		return "(" + actual.getClass() + ")";
	}

	protected boolean assertEqualsAsBoolean(Table table, Row row,
			String columnName, String expected, Object actual) {
		if(actual instanceof Boolean) {
			return actual.toString().equals(expected.toLowerCase());
		}
		return false;
	}

	protected boolean assertEqualsAsByteArray(Table table, Row row,
			String columnName, String expected, Object actual) {
		if(!(actual instanceof byte[])) {
			return false;
		}

		byte[] expectedBytes = (byte[])toArray(byte.class, expected);
		byte[] actualBytes = (byte[])actual;
		if(expectedBytes.length != actualBytes.length) {
			areEqual(table, row, columnName, expected, actualBytes);
			return false;
		}
		
		for(int i = 0; i < expectedBytes.length; i++) {
			if (expectedBytes[i] != actualBytes[i]) {
				areEqual(table, row, columnName, expected, actualBytes);
				return false;
			}
		}
		return true;
	}

	private void areEqual(Table table, Row row, String columnName,
			String expected, byte[] actualBytes) {
		String actualAsText = toStringFromByteArray(actualBytes, expected);
		Assertie.assertEquals(expected, actualAsText,
			"M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, expected, actualAsText, getClass(actualBytes));
	}

	protected boolean assertEqualsByRegex(Table table, Row row,
			String columnName, String expected, String actual) {
		if(expected.length() > 2 && expected.startsWith("`") && expected.endsWith("`")) {
			String regex = expected.substring(1, expected.length()-1);
			return actual.matches(regex);
		}
		return false;
	}

	protected boolean assertEqualsAsDate(Table table, Row row, String columnName,
			String expected, Object actual) {
		if(expected.indexOf(TODAY) > -1 && actual instanceof String) {
			actual = toDate((String)actual);
		}
		
		if(expected.indexOf(TODAY) > -1 && actual instanceof Date) {
			String today = Formi.format(new Date(), "yyyy-MM-dd");
			expected = expected.replace(TODAY, today);
			if (assertEqualsStrictly(table, row, columnName, expected, dateTimeToString((Date)actual, today))) {
				return true;
			}
			Assertie.assertEquals(expected, actual,
				"M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, expected, actual, getClass(actual));
		}

		if(actual instanceof Date) {
			if(assertEqualsAsSqlDate(expected, actual)) {
				return true;
			}
			if(assertEqualsStrictly(table, row, columnName, expected, actual.toString())) {
				return true;
			}
			String actualAsText = dateTimeToString((Date)actual, expected);
			if(assertEqualsStrictly(table, row, columnName, expected, actualAsText)) {
				return true;
			}
			Assertie.assertEquals(expected, actualAsText,
					"M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, expected, actualAsText, getClass(actual));
		}
		return false;
	}

	private Object toDate(String actual) {
		Date date;
		if((date = parse(DateFormat.getDateInstance(), actual)) != null) {
			return date;
		}
		if((date = parse(DateFormat.getDateTimeInstance(), actual)) != null) {
			return date;
		}

		TimeZone timeZone = TimeZone.getDefault();
		for(int i = 0; i < DATEFORMATS.length; i++) {
			DateFormat dateFormat = dateFormatter.getDateFormat(DATEFORMATS[i], timeZone);
			if((date = parse(dateFormat, actual)) != null) {
				return date;
			}
		}
		return actual;
	}
	
	private Date parse(DateFormat dateFormat, String actual) {
		try {
			return dateFormat.parse(actual);
		} catch(ParseException e) {
			ExceptionHandler.ignore(e);
		}
		return null;
	}

	private boolean assertEqualsAsSqlDate(String expected, Object actual) {
		try {
			if(actual instanceof java.sql.Date && java.sql.Date.valueOf(expected).equals(actual)) {
				return true;
			} else if(actual instanceof java.sql.Time && java.sql.Time.valueOf(expected).equals(actual)) {
				return true;
			} else if(actual instanceof java.sql.Timestamp && java.sql.Timestamp.valueOf(expected).equals(actual)) {
				return true;
			}
			return false;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}

	private String dateTimeToString(Date actual, String expected) {
		return Formi.format(actual, TypeConverter.getDateTimeFormat(expected));
	}

	protected boolean assertPartialEquality(Table table, Row row,
			String columnName, String expected, String actual) {
		if("%".equals(expected)) {
			expected = "%%";
		}

		if(expected.startsWith("%")) {
			if(expected.endsWith("%")) {
				return actual.indexOf(expected.substring(1, expected.length()-1)) > -1;
			}
			return actual.endsWith(expected.substring(1));
		} else if(expected.endsWith("%")) {
			return actual.startsWith(expected.substring(0, expected.length()-1));
		}
		return false;
	}

	protected boolean assertEqualsStrictly(Table table, Row row, String columnName,
			String expected, String actual) {
		return expected.equals(actual);
	}

	protected boolean assertNestedObject(Table table, Row row,
			String columnName, String expected, Object actual) {
		if(!getSection().hasTable(expected)) {
			return false;
		}
		parent.validate(actual, expected);
		return true;
	}

	protected boolean assertNotEmpty(Table table, Row row, String columnName,
			String expected, Object actual) {
		if("*".equals(expected)) {
			if(actual != null) {
				if(!(actual instanceof String)) {
					return true;
				}
				if(!Strings.isEmpty((String)actual)) {
					return true;
				}
			}
			Assertie.fail("M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, "*", actual, getClass(actual));
		}
		return false;
	}

	protected boolean assertEmpty(Table table, Row row, String columnName, 
			String expected, Object actual) {
		if(Strings.isEmpty(expected)) {
			if(actual == null) {
				return true;
			}
			if(actual instanceof String && Strings.isEmpty((String)actual)) {
				return true;
			}
			Assertie.fail("M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, "", actual, getClass(actual));
		}

		if(NULL.equals(expected)) {
			if(actual != null) {
				Assertie.fail("M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, NULL, actual, getClass(actual));
			}
			return true;
		}

		if(EMPTY.equals(expected)) {
			if(actual == null || !(actual instanceof String) || !((String)actual).isEmpty()) {
				Assertie.fail("M_Fixture_Temp_ObjectValidator_AssertEquals", table, row, columnName, EMPTY, actual, getClass(actual));
			}
			return true;
		}
		return false;
	}

	private String toStringFromByteArray(byte[] actualBytes, String expected) {
		if(isFilePath(expected)) {
			String filePath = PathUtil.getFilePath(expected);
			if(filePath != null) {
				String actualFilePath = new File(filePath + ".tmp").getAbsolutePath();
				writeAllBytes(actualFilePath, actualBytes);
				return actualFilePath;
			}
		}
		if(isBarSeparatedArray(expected)) {
			return fromArrayToString(actualBytes);
		}
		return Base64.encodeBase64String(actualBytes);
	}

	private void writeAllBytes(String filePath, byte[] bytes) {
		try {
			FileOutputStream output = new FileOutputStream(filePath);
			try {
				IOUtils.write(bytes, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} catch(IOException e) {
			throw new ConfigException(e);
		}
	}

	protected String toString(String expected, Object actual) {
		if(!getSection().hasTable(expected)) {
			if(actual instanceof List) {
				return fromListToString((List<?>)actual);
			}
			if(actual != null && actual.getClass().isArray()) {
				return fromArrayToString(actual);
			}
		}
		return toString(actual);
	}
	
	protected String fromArrayToString(Object array) {
		StringBuilder sb = new StringBuilder();
		int length = Array.getLength(array);
		for(int i = 0; i < length; i++) {
			if(i != 0) {
				sb.append('|');
			}
			sb.append(toString(Array.get(array, i)));
		}
		return sb.toString();
	}

	protected String fromListToString(List<?> list) {
		StringBuilder sb = new StringBuilder();
		for(Object o : list) {
			if(sb.length() > 0) {
				sb.append('|');
			}
			sb.append(toString(o));
		}
		return sb.toString();
	}

	protected String toString(Object object) {
		return TypeConverter.toString(object);
	}

	protected abstract Object getPropertyValue(Object object, String name, Table table, Row row);

	@Override
	public void validate(Class<? extends Throwable> exceptionClass,
			Callable<?> action, String typeName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasRole(Object object, String typeName) {
		return getSection() != null && getSection().hasTable() && canValidate(object);
	}

	protected boolean canValidate(Object object) {
		if(object == null) {
			return false;
		}
		
		if(object instanceof List<?>) {
			List<?> list = (List<?>)object;
			if(list.size() == 0) {
				return false;
			}
			return isValidatableObject(list.get(0));
		}
		
		if(object.getClass().isArray()) {
			if(Array.getLength(object) == 0) {
				return false;
			}
			return isValidatableObject(Array.get(object, 0));
		}
		
		return isValidatableObject(object);
	}

	protected abstract boolean isValidatableObject(Object object);
}
