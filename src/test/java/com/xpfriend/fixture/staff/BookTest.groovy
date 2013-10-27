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
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.groovy.Talkative;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * Book の仕様。
 * 
 * @author Ototadana
 */
@Talkative
class BookTest extends Specification {

	static final String BOOK_FILE_PATH = "src/test/resources/com/xpfriend/fixture/staff/BookTest.xlsx"
	
	def setupSpec() {
		FixtureBook.class.getName() // FixtureBook の static initializer を実行して Resi.add を実行させる
	}
	
	def "getInstance　は、引数で指定した Excel ファイルを読み込んで Book インスタンスを作成する"() {
		expect:
		Book.getInstance(BOOK_FILE_PATH) != null
	}

	def "getInstance　で、同一ファイル名を指定して複数回呼び出しを行うと、毎回同じ Book インスタンスを返す"() {
		setup:
		def fileName = BOOK_FILE_PATH
		
		when:
		def instance1 = Book.getInstance(fileName)
		def instance2 = Book.getInstance(fileName)
		
		then:
		instance1 == instance2
	}

	def "clearCache は Book インスタンスのキャッシュをクリアする"() {
		setup:
		def fileName = BOOK_FILE_PATH
		def instance1 = Book.getInstance(fileName)
		
		when:
		Book.clearCache()
		def instance2 = Book.getInstance(fileName)
		
		then:
		instance1 != instance2
	}
	
	def "getFilePath で取得できるファイル名は、getInstance 引数で指定された名前を File.getCanonicalPathで変換したものになる"() {
		setup:
		def fileName = BOOK_FILE_PATH
		
		expect:
		Book.getInstance(fileName).getFilePath() == new File(fileName).getCanonicalPath()
	}
	
	def "toString はフォルダ情報のないファイル名を返す"() {
		setup:
		def book = Book.getInstance(BOOK_FILE_PATH)
		
		expect:
		book.toString() == "BookTest.xlsx"
	}
	
	def "getSheet では、指定された名前の Sheet が取得できる"(sheetName) {
		setup:
		def book = Book.getInstance(BOOK_FILE_PATH)
		
		when:
		Sheet sheet = book.getSheet(sheetName)
		
		then: "例えば、シート名が #sheetName のとき、名前が #sheetName のシートが取得できる"
		sheet?.getName() == sheetName
		
		where:
		sheetName << ["Sheet01", "Sheet02"]
	}
	
	def "getSheet では、ブックに存在しないシート名を指定すると例外が発生する"() {
		setup:
		def book = Book.getInstance(BOOK_FILE_PATH)
		
		when:
		book.getSheet("xxxx")
		
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Book_GetSheet"
		e.getLocalizedMessage().indexOf("xxxx") > -1
		e.getLocalizedMessage().indexOf("BookTest.xlsx") > -1
	}
		
	def "setDebugEnabled で true を指定すると、isDebugEnabled が true を返し、false を指定すると false が返る"(debugEnabled) {
		when:
		Book.setDebugEnabled(debugEnabled)
		
		then:
		debugEnabled == Book.isDebugEnabled()
		
		where:
		debugEnabled << [true, false]
	}
	
}
