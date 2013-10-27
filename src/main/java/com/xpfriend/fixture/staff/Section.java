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
package com.xpfriend.fixture.staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xpfriend.junk.ConfigException;

/**
 * セクション。
 * 
 * @author Ototadana
 */
public class Section {
	
	public enum SectionType {
		TEST_CASE_DESCRIPTION(1),
		DATA_TO_CREATE(2),
		DATA_TO_DELETE(3),
		DATA_AS_EXPECTED(4),
		OBJECT_FOR_EXEC(5),
		EXPECTED_RESULT(6),
		FILE_AS_EXPECTED(7);
		
		private int value;
		private SectionType(int number) {
			this.value = number;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	private static final SectionType[] SECTIONS;
	static {
		SECTIONS = new SectionType[128];
		SECTIONS['1'] = SectionType.TEST_CASE_DESCRIPTION;
		SECTIONS['2'] = SectionType.DATA_TO_CREATE;
		SECTIONS['3'] = SectionType.DATA_TO_DELETE;
		SECTIONS['4'] = SectionType.DATA_AS_EXPECTED;
		SECTIONS['5'] = SectionType.OBJECT_FOR_EXEC;
		SECTIONS['6'] = SectionType.EXPECTED_RESULT;
		SECTIONS['7'] = SectionType.FILE_AS_EXPECTED;

		SECTIONS['A'] = SectionType.TEST_CASE_DESCRIPTION;
		SECTIONS['a'] = SectionType.TEST_CASE_DESCRIPTION;
		SECTIONS['B'] = SectionType.DATA_TO_DELETE;
		SECTIONS['b'] = SectionType.DATA_TO_DELETE;
		SECTIONS['C'] = SectionType.DATA_TO_CREATE;
		SECTIONS['c'] = SectionType.DATA_TO_CREATE;
		SECTIONS['D'] = SectionType.OBJECT_FOR_EXEC;
		SECTIONS['d'] = SectionType.OBJECT_FOR_EXEC;
		SECTIONS['E'] = SectionType.EXPECTED_RESULT;
		SECTIONS['e'] = SectionType.EXPECTED_RESULT;
		SECTIONS['F'] = SectionType.DATA_AS_EXPECTED;
		SECTIONS['f'] = SectionType.DATA_AS_EXPECTED;
		SECTIONS['G'] = SectionType.FILE_AS_EXPECTED;
		SECTIONS['g'] = SectionType.FILE_AS_EXPECTED;
	}

	private Case testCase;
	private String sectionName;
	private int sectionNumber;
	private Map<String, Table> tables = new HashMap<String, Table>();
	private Map<String, Table> tablesWithAliases = new HashMap<String, Table>();
	private List<String> tableNames = new ArrayList<String>();

	Section(Case testCase, String sectionName) {
		this.testCase = testCase;
		this.sectionName = sectionName;
		this.sectionNumber = toSectionNumber(sectionName);
	}

	/**
	 * 最大のセクション番号を返す。
	 * @return 最大のセクション番号。
	 */
	public static int getMaxNumber() {
		return 7;
	}

	/**
	 * このセクションの名前を返す。
	 * @return セクション名。
	 */
	public String getName() {
		return sectionName;
	}
	
	/**
	 * このセクションのセクション番号を返す。
	 * @return セクション番号。
	 */
	public int getNumber() {
		return sectionNumber;
	}

	/**
	 * このセクションが属すテストケース定義を返す。
	 * @return このセクションが属すテストケース定義。
	 */
	public Case getCase() {
		return testCase;
	}

	/**
	 * このセクションに属するテーブル定義の名前を返す。
	 * @return このセクション内にあるテーブル定義名。
	 */
	public List<String> getTableNames() {
		return tableNames;
	}
	
	private int toSectionNumber(String sectionName) {
		try {
			return SECTIONS[sectionName.charAt(0)].getValue();
		} catch(Exception e) {
			return 0;
		}
	}
	
	Table createTable(String tableName) {
		Table table = tables.get(tableName);
		if(table == null) {
			table = new Table(this, tableName);
			tables.put(tableName, table);
			tablesWithAliases.put(tableName, table);
			int atIndex = tableName.indexOf('@');
			if(atIndex > -1) {
				tablesWithAliases.put(tableName.substring(0, atIndex), table);
			}
			tableNames.add(tableName);
		}
		return table;
	}

	/**
	 * 指定された名前のテーブルを取得する。
	 * @param tableName テーブル名。
	 * @return テーブル。
	 */
	public Table getTable(String tableName) {
		Table table = tables.get(tableName);
		if(table != null) {
			return table;
		}
		table = tablesWithAliases.get(tableName);
		if(table != null) {
			return table;
		}
		throw new ConfigException("M_Fixture_Section_GetTable", tableName, this);
	}

	/**
	 * 指定された名前のテーブルがあるかどうかを調べる。
	 * @param tableName テーブル名。
	 * @return 指定された名前のテーブルがある場合はtrue。
	 */
	public boolean hasTable(String tableName) {
		return tablesWithAliases.containsKey(tableName);
	}
	
	/**
	 * このセクションにテーブルが定義されているかどうかを調べる。
	 * @return テーブルが定義されていればtrue。
	 */
	public boolean hasTable() {
		return !tables.isEmpty();
	}

	@Override
	public String toString() {
		return testCase.toString() + " " + getName() ;
	}
	
	public boolean hasTable(int index) {
		return 0 <= index && index < tableNames.size();
	}
	
	public Table getTable(int index) {
		return getTable(tableNames.get(index));
	}
}
