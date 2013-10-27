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
package com.xpfriend.fixture.cast.temp

import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.Loggi;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class DatabaseUpdaterTest extends Specification {
	
	FixtureBook fixtureBook = new FixtureBook()
	
	def cleanup() {
		Loggi.debugEnabled = false
	}
	
	def "指定されたデータをデータベーステーブルに投入できる_SQLServer"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()
		
		then:
		List<DynaBean> table = Database.executeQuery(null, "select * from TypesTable order by Id")
		fixtureBook.validate(table, "TypesTable")
	}
	
	def "指定されたデータをデータベーステーブルに投入できる_Oracle"() {
		setup:
		Loggi.debugEnabled = true

		when:
		fixtureBook.setup()
		
		then:
		List<DynaBean> table = Database.executeQuery("Oracle", "select * from TYPES_TABLE order by ID")
		fixtureBook.validate(table, "TYPES_TABLE")
	}
	
	def "BLOB項目にbyte配列の読み込みができる"() {
		setup:
		Loggi.debugEnabled = true

		when:
		fixtureBook.setup()
		
		then:
		fixtureBook.validateStorage()
	}
	
	def "image項目にbyte配列の読み込みができる"() {
		setup:
		Loggi.debugEnabled = true

		when:
		fixtureBook.setup()
		
		then:
		fixtureBook.validateStorage()
	}
	
	def "Bテストデータクリア条件で存在しないテーブル名を指定した場合は例外が発生する"() {
		when:
		fixtureBook.setup()
		
		then:
		ConfigException e = thrown()
		println e
		println e.getCause()
		e.getMessage() == "M_Fixture_Temp_Database_DeleteRow"
		e.getLocalizedMessage().indexOf("xxxx") > -1
	}
	
	def "Cテストデータで存在しないテーブル名を指定した場合は例外が発生する"() {
		when:
		fixtureBook.setup()
		
		then:
		ConfigException e = thrown()
		println e
		println e.getCause()
		e.getMessage() == "M_Fixture_Temp_Database_GetMetaData"
		e.getLocalizedMessage().indexOf("xxxx") > -1
	}
	
	def "重複データ登録時には例外が発生する"() {
		when:
		fixtureBook.setup()
		
		then:
		ConfigException e = thrown()
		println e
		println e.getCause()
		e.getMessage() == "M_Fixture_Temp_Database_InsertRow"
		e.getLocalizedMessage().indexOf("TypesTable") > -1
	}
	
	def "Bテストデータクリア条件で存在しない列名を指定した場合例外が発生する"() {
		when:
		fixtureBook.setup()
		
		then:
		ConfigException e = thrown()
		println e
		println e.getCause()
		e.getMessage() == "M_Fixture_Temp_Database_DeleteRow"
		e.getLocalizedMessage().indexOf("TypesTable") > -1
	}
	
	def "Bテストデータクリア条件で不正な列値を指定した場合例外が発生する"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()
		
		then:
		ConfigException e = thrown()
		println e
		println e.getCause()
		e.getMessage() == "M_Fixture_Temp_Database_DeleteRow"
		e.getLocalizedMessage().indexOf("TypesTable") > -1
	}
	
	def "Bテストデータクリア条件では部分一致指定が可能"() {
		setup:
		Loggi.debugEnabled = true
		DatabaseUpdaterTest__Bテストデータクリア条件では部分一致指定が可能_Setup()

		when:
		fixtureBook.setup()
		
		then:
		fixtureBook.validateStorage()
	}

	def "DatabaseUpdaterTest__Bテストデータクリア条件では部分一致指定が可能_Setup"() {
		new FixtureBook().setup()
	}
	
	def "Bテストデータクリア条件では複数条件指定が可能"() {
		setup:
		Loggi.debugEnabled = true
		DatabaseUpdaterTest__Bテストデータクリア条件では複数条件指定が可能_Setup()
		
		when:
		fixtureBook.setup()
		
		then:
		fixtureBook.validateStorage()
	}
	
	def "DatabaseUpdaterTest__Bテストデータクリア条件では複数条件指定が可能_Setup"() {
		new FixtureBook().setup()
	}
	
	def "IDENTITY列に対して明示的に値を設定できる"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()
		
		then:
		fixtureBook.validateStorage()
	}
}
