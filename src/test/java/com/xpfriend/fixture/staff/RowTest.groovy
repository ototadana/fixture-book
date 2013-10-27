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

import java.util.List;

import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.fixture.staff.Table;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class RowTest extends Specification {

	Case testCase

	def setup() {
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet02")
		testCase = sheet.getCase("テストケース001")
	}
	
	def "getColumns は、行中の列情報を返す"(sectionName, tableName, index, List columnNames, List columnValues) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)
		Row row = table.getRows().get(index)

		expect:
		row.getValues().size() == columnNames.size()
		row.getValues().keySet().containsAll(columnNames)
		row.getValues().values().containsAll(columnValues)
		
		where:
		sectionName | tableName | index | columnNames                            | columnValues
		"B"         | "T001"    | 0     | ["p01"]                                | ["*"]
		"C"         | "T002"    | 0     | ["p02","p03","p04","p05","p06","p07"]  | ["1","s01","100.0","true","2013/12/12","1234.0"]
		"C"         | "T002"    | 1     | ["p02","p03","p04","p05","p06","p07"]  | ["2","s02","200.0","false","2013-12-13 00:00:00","1234"]
		"G"         | ".CSV"    | 0     | ["delete","path"]                      | ["true","target/test-classes/test/fc-testA1.txt"]
		"G"         | ".CSV"    | 1     | ["delete","expectedFilePath"]          | ["false","target/test-classes/test/fc-testA2.txt"]
	}

	def "getIndex は、Excel ファイル上の行番号を返す"(sectionName, tableName, index, rowIndex) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)
		Row row = table.getRows().get(index)

		expect:
		row.getIndex() == rowIndex
		
		where:
		sectionName | tableName | index | rowIndex
		"B"         | "T001"    | 0     | 10
		"C"         | "T002"    | 0     | 16
		"C"         | "T002"    | 1     | 17
		"G"         | ".CSV"    | 0     | 49
		"G"         | ".CSV"    | 1     | 50
	}

	def "isDeleted は、削除フラグが設定されているかどうかを返す"(sectionName, tableName, index, deleted) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)
		Row row = table.getRows().get(index)

		expect:
		row.isDeleted() == deleted
		
		where:
		sectionName | tableName | index | deleted
		"C"         | "T002"    | 0     | false
		"C"         | "T002"    | 1     | true
		"D"         | "P001"    | 0     | true
		"D"         | "P002"    | 0     | true
		"D"         | "P002"    | 1     | false
	}
	
	def "toString は行番号を表す文字列を返す"(sectionName, tableName, index, rowNumber) {
		setup:
		Section section = testCase.getSection(sectionName)
		Table table = section.getTable(tableName)
		Row row = table.getRows().get(index)
		
		when:
		String s = row.toString()

		then:
		s == rowNumber.toString();

		where:
		sectionName | tableName | index | rowNumber
		"C"         | "T002"    | 0     | 16
		"C"         | "T002"    | 1     | 17
	}
}
