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

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Section.SectionType;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.fixture.toolkit.PathUtil;
import com.xpfriend.junk.ConfigException;

/**
 * @author Ototadana
 *
 */
public class ObjectOperatorBase {
	
	protected static final String NULL = "${NULL}";
	protected static final String EMPTY = "${EMPTY}";
	private static final char[] FILE_PATH_MARK = new char[] {'\\', '.'};

	protected Case testCase;
	protected SectionType sectionType;

	protected ObjectOperatorBase(SectionType sectionType) {
		this.sectionType = sectionType;
	}
	
	protected Section getSection() {
		return testCase.getSection(sectionType);
	}
	
	public void initialize(Case testCase) {
		this.testCase = testCase;
	}

	/**
	 * 指定された名前のテーブルを取得する。
	 * @param section テーブル取得元セクション。
	 * @param cls テーブルの内容を格納するクラス。
	 * @param typeName テーブル定義名。
	 * @return テーブル。
	 */
	protected Table getTable(Section section, Class<?> cls, String typeName) {
		String tableName = getTableName(section, cls, typeName);
		return section.getTable(tableName);
	}

	protected String getTableName(Section section, Class<?> cls, String typeName) {
		if(typeName != null) {
			return typeName;
		}
		
		List<String> tableNames = section.getTableNames();
		if(tableNames.size() == 1) {
			return tableNames.get(0);
		}
		
		if(cls != null) {
			return cls.getSimpleName();
		}
		throw new ConfigException("M_Fixture_Temp_ObjectOperator_GetTableName", section);
	}

	protected boolean hasArraySeparator(String textValue) {
		return textValue != null && textValue.indexOf('|') > -1;
	}
	
	protected Object toArray(Class<?> componentType, String textValue) {
		if(componentType.equals(byte.class)) {
			return toByteArray(textValue);
		}
		return toArrayInternal(componentType, textValue);
	}

	private Object toArrayInternal(Class<?> componentType, String textValue) {
		String[] textValues = textValue.split("\\|");
		if(String.class.equals(componentType)) {
			return textValues;
		}
		
		Object values = Array.newInstance(componentType, textValues.length);
		for(int i = 0; i < Array.getLength(values); i++) {
			Array.set(values, i, TypeConverter.changeType(textValues[i], componentType));
		}
		return values;
	}

	private Object toByteArray(String textValue) {
		if(isFilePath(textValue)) {
			String filePath = PathUtil.getFilePath(textValue);
			if(filePath == null) {
				filePath = textValue;
			}
			return readAllBytes(filePath);
		}
		
		if(isBarSeparatedArray(textValue)) {
			return toArrayInternal(byte.class, textValue);
		}
		
		return Base64.decodeBase64(textValue);
	}

	private byte[] readAllBytes(String filePath) {
		try {
			FileInputStream input = new FileInputStream(filePath);
			try {
				return IOUtils.toByteArray(input);
			} finally {
				IOUtils.closeQuietly(input);
			}
		} catch(IOException e) {
			throw new ConfigException(e);
		}
	}
	
	protected boolean isFilePath(String textValue) {
		for(int i = 0; i < FILE_PATH_MARK.length; i++) {
			if(textValue.indexOf(FILE_PATH_MARK[i]) > -1) {
				return true;
			}
		}
		return false;
	}

	protected boolean isBarSeparatedArray(String textValue) {
		return textValue.length() == 1 || textValue.indexOf('|') > -1;
	}
}
