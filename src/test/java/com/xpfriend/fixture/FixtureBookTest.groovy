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
package com.xpfriend.fixture

import spock.lang.Specification

import com.xpfriend.fixture.FixtureBookTest.FixtureBookTestData;
import com.xpfriend.fixture.cast.temp.Database
import com.xpfriend.junk.ConfigException
import com.xpfriend.junk.Loggi

/**
 * @author Ototadana
 *
 */
class FixtureBookTest extends Specification {
	
	public static class FixtureBookTestData {
		private String text;
		public String getText() {return text;}
		public void setText(String value) {text = value;}
	}
	
	private boolean calledSetup = false
	private static boolean calledSetupSpec = false
	
	def cleanup() {
		Loggi.debugEnabled = false
		assert calledSetup == true
	}
	
	def cleanupSpec() {
		assert calledSetupSpec == true
	}
	
	def "コンストラクタで true を指定すると、FixtureBookの初期化ができる"() {
		when:
		FixtureBook fixtureBook = new FixtureBook(true)
		
		then:
		fixtureBook.book != null
		fixtureBook.sheet.name == "FixtureBookTest" 
		fixtureBook.testCase.caseName == "コンストラクタで true を指定すると、FixtureBookの初期化ができる"
	}
	
	def "FixtureBookPathアノテーション指定をしなくても参照可能なこと"() {
		when:
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData)
		
		then:
		obj.text == "abc"
	}
	
	def "Sheet1__アンダーバー区切りで参照できること"() {
		when:
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData)

		then:
		obj.text == "efg"
	}
	
	@Fixture(["Sheet1", "Fixtureアノテーションで参照できること"])
	def "test_Fixtureアノテーションで参照できること"() {
		when:
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData);
		
		then:
		obj.text == "hij"
	}
	
	@Fixture(["Sheet1", "Fixtureアノテーションで参照できること"])
	def "複数のメソッドから呼び出しができること"() {
		expect:
		Sheet1__複数のメソッドから呼び出しができること()		
	}
	
	private void Sheet1__複数のメソッドから呼び出しができること() {
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData)
		assert obj.text == "xyz"
	}
	
	@Fixture(["Sheet2", "setupからの呼び出しができること"])
	def setup() {
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData)
		assert obj.text == "klm"
		calledSetup = true
	}

	@Fixture(["Sheet2", "setupSpec からの呼び出しができること"])
	def setupSpec() {
		FixtureBookTestData obj = new FixtureBook().getObject(FixtureBookTestData)
		assert obj.text == "nop"
		calledSetupSpec = true
	}
	
	def "validateメソッドはSetupメソッドの暗黙呼び出しを行わないこと"() {
		setup:
		Loggi.debugEnabled = true
		Database.executeNonQuery(null, "delete from TypesTable")
		FixtureBook fixtureBook = new FixtureBook()
		
		when:
		def obj = [Id: "1"];
		fixtureBook.validate(obj)
		then:
		Database.executeQuery(null, "select * from TypesTable").size() == 0
		
		when:
		fixtureBook.setup()
		then:
		Database.executeQuery(null, "select * from TypesTable").size() == 1
	}
	
	def "validateStorageの呼び出し時にはSetupメソッドの暗黙呼び出しはされないこと"() {
		setup:
		Loggi.debugEnabled = true
		Database.executeNonQuery(null, "delete from TypesTable")
		FixtureBook fixtureBook = new FixtureBook()

		when:
		fixtureBook.validateStorage()
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("*") == -1
	}
	
	def "validateで例外発生がテストできる"() {
		expect: "normal"
		new FixtureBook().validate(Exception, {throw new Exception("zzz")});
		new FixtureBook().validate(Exception, {throw new Exception("xxx")}, "Result");
		
		when:
		new FixtureBook().validate(Exception, {throw new Exception("ZZZ")});
		then:
		AssertionError e = thrown()
		println e.getLocalizedMessage()
		e.getLocalizedMessage().indexOf("<[zzz]>") > -1
		e.getLocalizedMessage().indexOf("<[ZZZ]>") > -1
		
		when:
		new FixtureBook().validate(Exception, {throw new Exception("XXX")}, "Result");
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<[xxx]>") > -1
		x.getLocalizedMessage().indexOf("<[XXX]>") > -1
	}
	
	@Fixture(["Expect", "ExpectおよびExpectResultのテスト"])
	def "expectは指定されたラムダ式の実行ができる"() {
		setup:
		boolean called = false;
		
		when:
		FixtureBook.expect({called = true})
		
		then:
		called == true
	}
	
	@Fixture(["Expect", "ExpectおよびExpectResultのテスト"])
	def "expectは「D.パラメタ」に定義されたオブジェクトを引数として取得できる"() {
		setup:
		boolean called = false;
		
		when:
		FixtureBook.expect({FixtureBookTestData p1, FixtureBookTestData p2, 
			FixtureBookTestData p3, FixtureBookTestData p4 -> 
			called = true
			assert p1.text == "abc"
			assert p2.text == "def"
			assert p3.text == "ghi"
			assert p4.text == "jkl"
		})
		
		then:
		called == true
	}
	
	@Fixture(["Expect", "ExpectおよびExpectResultのテスト"])
	def "expectReturnは指定されたラムダ式の戻り値が検証できる"() {
		expect: "normal"
		FixtureBook.expectReturn({new FixtureBookTestData(text:"zzz")})
		
		when:
		FixtureBook.expectReturn({new FixtureBookTestData(text:"zzp")})
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<zz[z]>") > -1
		x.getLocalizedMessage().indexOf("<zz[p]>") > -1
	}
	
	@Fixture(["Expect", "ExpectおよびExpectResultのテスト"])
	def "expectReturnは「D.パラメタ」に定義されたオブジェクトを引数として取得でき、戻り値の検証もできる"() {
		expect: "normal"
		FixtureBook.expectReturn({FixtureBookTestData p1 ->
			assert p1.text == "abc"
			new FixtureBookTestData(text:"zzz")
		})

		when:
		FixtureBook.expectReturn({FixtureBookTestData p1, FixtureBookTestData p2, 
			FixtureBookTestData p3, FixtureBookTestData p4 -> 
			assert p1.text == "abc"
			assert p2.text == "def"
			assert p3.text == "ghi"
			assert p4.text == "jkl"
			new FixtureBookTestData(text:"zzq")
		})
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<zz[z]>") > -1
		x.getLocalizedMessage().indexOf("<zz[q]>") > -1
	}
	
	@Fixture(["ExpectThrown", "ExpectThrownのテスト"])
	def "expectThrownは指定されたラムダ式の例外が検証できる"() {
		expect: "normal"
		FixtureBook.expectThrown(Exception, {throw new Exception("zzz")})
		
		when:
		FixtureBook.expectThrown(Exception, {throw new Exception("zzp")})
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<zz[z]>") > -1
		x.getLocalizedMessage().indexOf("<zz[p]>") > -1
	}

	@Fixture(["ExpectThrown", "ExpectThrownのテスト"])
	def "expectThrownは「D.パラメタ」に定義されたオブジェクトを引数として取得でき、例外の検証もできる"() {
		expect: "normal"
		FixtureBook.expectThrown(Exception, {FixtureBookTestData p1 ->
			assert p1.text == "abc"
			throw new Exception("zzz")
		})

		when:
		FixtureBook.expectThrown(Exception, {FixtureBookTestData p1, FixtureBookTestData p2, 
			FixtureBookTestData p3, FixtureBookTestData p4 -> 
			assert p1.text == "abc"
			assert p2.text == "def"
			assert p3.text == "ghi"
			assert p4.text == "jkl"
			throw new Exception("zzq")
		})
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<zz[z]>") > -1
		x.getLocalizedMessage().indexOf("<zz[q]>") > -1
	}
	
	@Fixture(["ExpectThrown", "ExpectThrownのテスト"])
	def "expectThrownは「D.パラメタ」に定義されたオブジェクトをリストとして取得できる"() {
		expect:
		FixtureBook.expectThrown(Exception, {List list1, List list2 ->
			assert list1.size() == 1
			assert list1[0]["text"] == "abc"
			assert list1[0] instanceof Map
			assert list2.size() == 1
			assert list2[0]["text"] == "def"
			assert list2[0] instanceof FixtureBookTestData
			throw new Exception("zzz")
		}, null, FixtureBookTestData)
	}
	
	@Fixture(["ExpectThrown", "ExpectThrownのテスト"])
	def "expectThrownは「D.パラメタ」に定義されたオブジェクトをリストとして取得できる_インターフェース実装"() {
		expect:
		FixtureBook.expectThrown(Exception, 
			new ActionWithParameters() {
				public Object action(List<Map> list1, List<FixtureBookTestData> list2) throws Exception {
					assert list1.size() == 1
					assert list1[0]["text"] == "abc"
					assert list1[0] instanceof Map
					assert list2.size() == 1
					assert list2[0]["text"] == "def"
					assert list2[0] instanceof FixtureBookTestData
					throw new Exception("zzz")
				}
			})
	}

	public interface ActionWithParameters {
		Object action(List<Map> list1, List<FixtureBookTestData> list2) throws Exception;
	}

	@Fixture(["ValidateParameter", "GetParameterAtおよびValidateParameterAtのテスト"])
	def "getParameterAtは指定したインデックスの引数を取得する"() {
		setup:
		FixtureBookTestData[] data = new FixtureBookTestData[4];
		FixtureBook fixtureBook = FixtureBook.expect({
			FixtureBookTestData p1, FixtureBookTestData p2, 
			FixtureBookTestData p3, FixtureBookTestData p4 -> 
			data[0] = p1
			data[1] = p2
			data[2] = p3
			data[3] = p4
		})
		
		expect:
		fixtureBook.getParameterAt(0) is data[0]
		fixtureBook.getParameterAt(1) is data[1]
		fixtureBook.getParameterAt(2) is data[2]
		fixtureBook.getParameterAt(3) is data[3]
		fixtureBook.getParameterAt(0).text == "abc"
		fixtureBook.getParameterAt(1).text == "def"
		fixtureBook.getParameterAt(2).text == "ghi"
		fixtureBook.getParameterAt(3).text == "jkl"
	}
	
	@Fixture(["ValidateParameter", "GetParameterAtおよびValidateParameterAtのテスト"])
	def "validateParameterAtは指定したインデックスの引数を検証する"() {
		setup:
		FixtureBook fixtureBook = FixtureBook.expect({
			FixtureBookTestData p1, FixtureBookTestData p2, 
			FixtureBookTestData p3, FixtureBookTestData p4 -> 
		})
		
		expect: "normal"
		fixtureBook.validateParameterAt(0)
		fixtureBook.validateParameterAt(1, 2, 3)
		fixtureBook.validateParameterAt(0, "Parameter1")
		fixtureBook.validateParameterAt(1, "Parameter2").
		validateParameterAt(2, "Parameter3").
		validateParameterAt(3, "Parameter4")
		
		when:
		fixtureBook.validateParameterAt(0, "Parameter2")
		then:
		AssertionError x = thrown()
		println x.getLocalizedMessage()
		x.getLocalizedMessage().indexOf("<[abc]>") > -1
		x.getLocalizedMessage().indexOf("<[def]>") > -1

		when:
		fixtureBook.validateParameterAt(0, "xxx")
		then:
		ConfigException e = thrown()
		println e.getLocalizedMessage()
		e.getLocalizedMessage().indexOf("xxx") > -1
	}
}
