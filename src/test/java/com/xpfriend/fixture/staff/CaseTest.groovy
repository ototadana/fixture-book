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

import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * {@link Case} の仕様。
 * 
 * @author Ototadana
 */
class CaseTest extends Specification {
	
	Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet01")
	FixtureBook fixtureBook = new FixtureBook()

	def "getName はテストケースの名前を返す"(testCaseName) {
		setup:
		Case testCase = sheet.getCase(testCaseName)
		
		expect:
		testCase?.getName() == testCaseName
		
		where:
		testCaseName << ["テストケース001", "テストケース002"]
	}
	
	def "getSheet はこのテストケースが属すシートを返す"() {
		setup:
		Case testCase = sheet.getCase("テストケース001")

		expect:
		testCase.getSheet().is(sheet);
	}
	
	def "getSectionByNumber（int）と getSection（String）は同一インスタンスのセクションを返す"(int number, String name) {
		setup:
		Case testCase = sheet.getCase("テストケース001")
		
		when:
		def section1 = testCase.getSection(number)
		def section2 = testCase.getSection(name)
		
		then:
		section1.is(section2)
		
		where:
		number | name
		1      | "A. test case"
		2      | "C. test data"
		3      | "B. clear condition"
		4      | "F. updated"
		5      | "D. parameters"
		6      | "E. expected result"
		7      | "G. file check"
	}

	def "getSection（int）で不正なセクション番号を指定すると、例外が発生する"(number) {
		setup:
		Case testCase = sheet.getCase("テストケース001")

		when:
		testCase.getSection(number)
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Case_GetSection"
		e.getLocalizedMessage().indexOf("テストケース001") > -1

		where:
		number << [-1, 8]
	}

	def "toString はシートの情報とテストケース名を表す文字列を返す"() {
		setup:
		Case testCase = sheet.getCase("テストケース001")

		expect:
		println testCase.toString()
		testCase.toString() == "BookTest.xlsx#Sheet01[テストケース001]"
	}
	
	def "getObject を「D.パラメタ」に何も定義されていない場合に呼び出すと例外が発生する"() {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet01")
		Case testCase = sheet.getCase("テストケース003")

		when:
		testCase.getObject(null, "")
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Case_GetObject"
		e.getLocalizedMessage().indexOf("テストケース003") > -1
	}
	
	def "getList を「D.パラメタ」に何も定義されていない場合に呼び出すと例外が発生する"() {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet01")
		Case testCase = sheet.getCase("テストケース003")

		when:
		testCase.getList(null, "")
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Case_GetList"
		e.getLocalizedMessage().indexOf("テストケース003") > -1
	}

	def "getArray を「D.パラメタ」に何も定義されていない場合に呼び出すと例外が発生する"() {
		setup:
		Sheet sheet = Book.getInstance(BookTest.BOOK_FILE_PATH).getSheet("Sheet01")
		Case testCase = sheet.getCase("テストケース003")

		when:
		testCase.getArray(null, "")
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Case_GetArray"
		e.getLocalizedMessage().indexOf("テストケース003") > -1
	}

	def "validate（storage）を「F.更新後データ」に何も定義されていない場合に呼び出すと例外が発生する"() {
		setup:
		Case testCase = sheet.getCase("テストケース001")

		when:
		testCase.validateStorage()
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Case_Validate_Storage"
		e.getLocalizedMessage().indexOf("テストケース001") > -1
	}

	def "validate（object）を「E.取得データ」が存在しない場合に呼び出すと例外が発生する"() {
		setup:
		Case testCase = sheet.getCase("テストケース001")

		when:
		testCase.validate("xxx", "")
		
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Case_Validate_Object"
		e.getLocalizedMessage().indexOf("テストケース001") > -1
	}
}
