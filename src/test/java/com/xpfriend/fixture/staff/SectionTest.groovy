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
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification;

/**
 * {@link Section} の仕様。
 * 
 * @author Ototadana
 */
class SectionTest extends Specification {
	
	Case testCase

	def setup() {
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet01")
		testCase = sheet.getCase("テストケース001")
	}
	
	def "getMaxNumber は最大のセクション番号を返す"() {
		expect:
		Section.getMaxNumber() == 7
	}
	
	def "getName はセクション名を返す"(number, name) {
		setup:
		Section section = testCase.getSection(number)

		expect:
		section.getName() == name
		
		where:
		number | name
		1      | "A. テストケース"
		2      | "C. テストデータ"
		3      | "B. テストデータクリア条件"
		4      | "F. 更新後データ"
		5      | "D. パラメタ"
		6      | "E. 取得データ"
		7      | "G. ファイルチェック"
	}
	
	def "getNumber はセクション番号を返す"(number, name) {
		setup:
		Section section = testCase.getSection(name)

		expect:
		section.getNumber() == number
		
		where:
		number | name
		1      | "A. テストケース"
		2      | "C. テストデータ"
		3      | "B. テストデータクリア条件"
		4      | "F. 更新後データ"
		5      | "D. パラメタ"
		6      | "E. 取得データ"
		7      | "G. ファイルチェック"
	}

	def "toString はテストケースの情報とをセクション名を表す文字列を返す"() {
		setup:
		Section section = testCase.getSection(1)
		
		expect:
		section.toString() == "BookTest.xlsx#Sheet01[テストケース001] A. テストケース"
	}
	
	def "getCase はこのセクションが属すテストケースを返す"() {
		setup:
		Section section = testCase.getSection(1)

		expect:
		section.getCase().is(testCase);
	}
	
	def "getTable で存在しない名前を指定した場合は例外を発生する"() {
		setup:
		Section section = testCase.getSection(2)

		when:
		section.getTable("xxx")
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Section_GetTable"
	}
	
	def "getTable では指定した名前のテーブル定義を取得する"(sectionName, tableName) {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet02")
		Section section = sheet.getCase("テストケース001").getSection(sectionName)

		expect:
		section.getTable(tableName).getName() == tableName
		
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

	def "不正なセクション名が定義されている場合、セクション番号は「0」として扱われる"() {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet03")
		
		when:
		Section section = sheet.getCase("テストケース001").getSection(0)
		
		then:
		section.getName() == "あいうえお"
	}
}
