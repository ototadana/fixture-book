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
package com.xpfriend.fixture.staff


import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.fixture.staff.Table;

import spock.lang.Specification;

/**
 * {@link Table} の仕様。
 * 
 * @author Ototadana
 */
class TableTest extends Specification {
	
	Case testCase

	def setup() {
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet02")
		testCase = sheet.getCase("テストケース001")
	}
	
	def "toString は、「セクション情報とテーブル名」を表す文字列を返す"() {
		setup:
		Section section = testCase.getSection("B")
		Table table = section.getTable("T001")
		
		when:
		String text = table.toString()

		then:		
		text.indexOf("BookTest.xlsx") > -1
		text.indexOf("Sheet02") > -1
		text.indexOf("テストケース001") > -1
		text.indexOf("B.") > -1
		text.indexOf("T001") > -1
	}
	
	def "getSection は、このテーブルが属すセクションを返す"(sectionName, tableName) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)

		expect:
		table.getSection().is(section)

		where:
		sectionName | tableName
		"B"         | "T001"
		"C"         | "T002"
		"D"         | "P001"
		"D"         | "P002"
		"E"         | "R001"
		"F"         | "T003"
		"G"         | ".CSV"
	}
	
	def "getName は、このテーブルの名前を返す"(sectionName, tableName) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)

		expect:
		table.getName() == tableName

		where:
		sectionName | tableName
		"B"         | "T001"
		"C"         | "T002"
	}
	
	def "getRows は、テーブルに定義されている行情報を返す"(sectionName, tableName, rowCount) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)

		expect:
		table.getRows().size() == rowCount

		where:
		sectionName | tableName | rowCount
		"B"         | "T001"    | 1
		"C"         | "T002"    | 2
		"D"         | "P001"    | 1
		"D"         | "P002"    | 2
		"E"         | "R001"    | 1
		"F"         | "T003"    | 1
		"G"         | ".CSV"    | 2
	}

	def "同一セクション内に同一名のテーブルが複数定義されている場合、その情報はひとつにマージされる"(index, List columnValues) {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet03")
		Section section = sheet.getCase("テストケース001").getSection("C")
		Table table = section.getTable("T002")
		List<Row> rows = table.getRows()
		
		expect:
		rows.size() == 2
		rows[index].getValues().values().containsAll(columnValues)
		
		where:
		index | columnValues
		0     | ["1","s01","100.0","true","2013/12/12"]
		1     | ["2","s02","200.0","false","2013-12-13 00:00:00"]
	}
}
