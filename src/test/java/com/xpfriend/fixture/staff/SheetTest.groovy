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
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * {@link Sheet} の仕様。
 * 
 * @author Ototadana
 */
class SheetTest extends Specification {
	
	Book book = Book.getInstance(BookTest.BOOK_FILE_PATH)

	def "getBook は、このシートが属するブックを返す"() {
		setup:
		def sheet = book.getSheet("Sheet01")
		
		expect:
		sheet.getBook().is book
	}
	
	def "toString はBookの情報とシート名を表す文字列を返す"(sheetName) {
		setup:
		def sheet = book.getSheet(sheetName)
		
		expect:
		println sheet.toString()
		sheet.toString() == book.toString() + "#" + sheetName
		
		where:
		sheetName << ["Sheet01", "Sheet02"]
	}
	
	def "getCase は、指定された名前のテストケースを返す"(testCaseName) {
		setup:
		Sheet sheet = book.getSheet("Sheet01")

		when:
		Case testCase = sheet.getCase(testCaseName)

		then:
		testCase?.getName() == testCaseName

		where:
		testCaseName << ["テストケース001", "テストケース002"]
	}

	def "getCase で、存在しないケース名を指定すると例外が発生する"() {
		setup:
		Sheet sheet = book.getSheet("Sheet01")
		
		when:
		sheet.getCase("zzz");
		
		then:
		ConfigException e = thrown()
		e.getMessage() == "M_Fixture_Sheet_GetCase"
		e.getLocalizedMessage().indexOf("zzz") > -1
		e.getLocalizedMessage().indexOf("Sheet01") > -1
	}
}
