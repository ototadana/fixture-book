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

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.cast.temp.Database.DbCommand;
import com.xpfriend.junk.Loggi;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class DatabaseValidatorTest extends Specification {
	
	FixtureBook fixtureBook = new FixtureBook()
	
	def cleanup() {
		Loggi.debugEnabled = false
	}

	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_SQLServer"])
	def "検索列指定をしてデータベーステーブルの状態が予想結果と同じであることをチェックできる_SQLServer"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		
		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_Oracle"])
	def "検索列指定をしてデータベーステーブルの状態がチェックできる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		
		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_SQLServer"])
	def "指定した検索列が存在しない場合はエラー_SQLServer"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery(null, "delete from TypesTable")

		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TypesTable.*TypesTable.*")
		e.getLocalizedMessage().indexOf('*') == -1
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_Oracle"])
	def "指定した検索列が存在しない場合はエラー_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery("Oracle", "delete from TYPES_TABLE")

		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TYPES_TABLE.*TYPES_TABLE.*")
		e.getLocalizedMessage().indexOf('*') == -1
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_SQLServer"])
	def "検索列指定をしてデータベーステーブルの状態が予想結果と異なることをチェックできる_SQLServer"(String column, Object value, String message) {
		setup:
		fixtureBook.setup()
		Database database = new Database()
		try {
			DbCommand command = database.createCommand("update TypesTable set " + column + "=? where Id=2")
			database.addParameter(command, value.getClass(), value);
			database.executeNonQuery(command)
			database.commit()
		} finally {
			database.dispose()
		}
		
		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf(column) > -1
		e.getLocalizedMessage().indexOf(message) > -1
		
		where:
		column				| value						| message
		"bigint1"			| 2 						| "<[2]>"
		"bit1"				| true						| "<[tru]e>"
		"date1"				| "2012-12-02"				| "<2012-12-0[2]>"
		"datetime1"			| "2012-12-02 12:34:57"		| "<2012-12-02 12:34:5[7]>"
		"datetime2"			| "2012-12-03 12:34:56.124"	| "<...12-12-03 12:34:56.12[4]>"
		"decimal"			| "123.457"					| "<123.45[7]>"
		"float1"			| "12.4"					| "<12.[4]>"
		"int1"				| "5"						| "<[5]>"
		"money1"			| "1234.0001"				| "<1234[.0001]>"
		"mumeric1"			| "124"						| "<12[4]>"
		"real1"				| "32.2"					| "<32.[2]>"
		"smalldatetime1"	| "2012-12-05 12:34:00"		| "<2012-12-05 12:3[4]:00>"
		"smallint1"			| "6"						| "<[6]>"
		"smallmoney1"		| "7.0000"					| "<[7].0000>"
		"text1"				| "b"						| "<[b]>"
		"time1"				| "12:34:57"				| "<12:34:5[7]>"
		"tinyint1"			| "9"						| "<[9]>"
		"varchar1"			| "c"						| "<[c]>"
		"xml1"				| "b"						| "<[b]>"
		"ntext1"			| "う"						| "<[う]>"
		"nvarchar1"			| "え"						| "<[え]>"
		"char1"				| "b         "				| "<[b]         >"
		"nchar1"			| "い         "					| "<[い]         >"
		"binary1"			| [2, 2, 0, 0] as byte[]	| "<2|[2]|0|0>"
		"image1"			| [1, 2, 3] as byte[]		| "<1|2[|3]>"
		"varbinary1"		| [2] as byte[]				| "<[2]>"
		"uniqueidentifier1"	| [2] as byte[] 			| "<0000000[2]-0000-0000-0000-0000...>"
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をしてデータベーステーブルの状態がチェックできる_Oracle"])
	def "検索列指定をしてデータベーステーブルの状態が予想結果と異なることをチェックできる_Oracle"(String column, Object value, String message) {
		setup:
		fixtureBook.setup()
		Database database = new Database()
		try {
			database.use("Oracle")
			DbCommand command = database.createCommand("update TYPES_TABLE set " + column + "=? where Id=2")
			database.addParameter(command, value.getClass(), value);
			database.executeNonQuery(command)
			database.commit()
		} finally {
			database.dispose()
		}
		
		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf(column) > -1
		e.getLocalizedMessage().indexOf(message) > -1
		
		where:
		column				| value						| message
		"CHAR1"				| "b"						| "<[b]         >"
		"VARCHAR1"			| "c"						| "<[c]>"
		"NCHAR1"			| "う         "					| "<[う]         >"
		"NVARCHAR1"			| "え"						| "<[え]>"
		"INT1"				| 2							| "<[2]>"
		"DECIMAL1"			| 123.457					| "<123.45[7]>"
		"CLOB1"				| "b"						| "<[b]>"
		"BLOB1"				| "3"						| "<[3]>"
		"DATE1"				| TypeConverter.changeType("2012-12-02 12:34:57", java.sql.Timestamp) | "<2012-12-02 12:34:5[7]>"
		"TIMESTAMP1"		| "2012-12-02 12:34:56.788"	| "<...12-12-02 12:34:56.78[8]>"
		"TIMESTAMPTZ"		| "2012-12-03 01:00:01"		| "<20[20-12-12 03:01]:00>"
		"TIMESTAMPLTZ"		| "2012-12-03 01:00:01"		| "<2012-12-03 01:00:0[1]>"
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をせずにデータベーステーブルの状態がチェックできる_SQLServer"])
	def "検索列指定をせずにデータベーステーブルの状態が予想結果と同じであることをチェックできる_SQLServer"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をせずにデータベーステーブルの状態がチェックできる_Oracle"])
	def "検索列指定をせずにデータベーステーブルの状態が予想結果と同じであることをチェックできる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		expect:
		fixtureBook.validateStorage()
	}

	@Fixture(["DatabaseValidatorTest", "検索列指定をせずにデータベーステーブルの状態がチェックできる_SQLServer"])
	def "検索列指定をせずにデータベーステーブルの状態が予想結果と異なることをチェックできる_SQLServer"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery(null, "update TypesTable set bigint1=2 where Id=2")

		when:
		fixtureBook.validateStorage()
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TypesTable.*TypesTable.*")
		e.getLocalizedMessage().indexOf('*') > -1
	}
	
	@Fixture(["DatabaseValidatorTest", "検索列指定をせずにデータベーステーブルの状態がチェックできる_Oracle"])
	def "検索列指定をせずにデータベーステーブルの状態が予想結果と異なることをチェックできる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery("Oracle", "update TYPES_TABLE set VARCHAR1='c' where Id=2")

		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TYPES_TABLE.*TYPES_TABLE.*")
		e.getLocalizedMessage().indexOf('*') > -1
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在しない場合は正常終了となる"])
	def "削除済み指定のデータが存在しない場合は正常終了となる"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在しない場合は正常終了となる_Oracle"])
	def "削除済み指定のデータが存在しない場合は正常終了となる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータとD指定のないデータの両方を同時に検証できる"])
	def "D指定のデータとD指定のないデータの両方を同時に検証できる"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在する場合はエラーとなる"])
	def "削除済み指定のデータが存在しない場合は正常終了となるその2"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery(null, "delete from TypesTable where Id = 2")

		expect:
		fixtureBook.validateStorage()
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在する場合はエラーとなる_Oracle"])
	def "削除済み指定のデータが存在しない場合は正常終了となるその2_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()
		Database.executeNonQuery("Oracle", "delete from TYPES_TABLE where ID = 2")

		expect:
		fixtureBook.validateStorage()
	}

	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在する場合はエラーとなる"])
	def "削除済み指定のデータが存在する場合はエラーとなる"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		when:
		fixtureBook.validateStorage()
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TypesTable.*TypesTable.*")
	}
	
	@Fixture(["DatabaseValidatorTest", "削除済み指定（D）のデータが存在する場合はエラーとなる_Oracle"])
	def "削除済み指定のデータが存在する場合はエラーとなる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		when:
		fixtureBook.validateStorage()
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TYPES_TABLE.*TYPES_TABLE.*")
	}

	def "指定した検索列条件で複数ヒットしてしまう場合はエラーとなる"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		when:
		fixtureBook.validateStorage()
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TypesTable.*TypesTable.*")
		e.getLocalizedMessage().indexOf("{bigint1=1}") > -1
	}

	def "指定した検索列条件で複数ヒットしてしまう場合はエラーとなる_Oracle"() {
		setup:
		Loggi.debugEnabled = true
		fixtureBook.setup()

		when:
		fixtureBook.validateStorage()
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().matches(".*TYPES_TABLE.*TYPES_TABLE.*")
		e.getLocalizedMessage().indexOf("{VARCHAR1=b") > -1
	}
	
	def "複数の接続先に対して同時に操作できる"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()

		then:
		fixtureBook.validateStorage()
	}
	
	def "BLOB項目の値をチェックできる"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()

		then:
		fixtureBook.validateStorage()
	}
	
	def "image項目の値をチェックできる"() {
		setup:
		Loggi.debugEnabled = true
		
		when:
		fixtureBook.setup()

		then:
		fixtureBook.validateStorage()
	}
}
