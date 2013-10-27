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

import java.util.concurrent.Callable
import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.FixtureBookTest.FixtureBookTestData;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class TempConductorTest extends Specification {

	@Fixture(["Expect", "引数なしの場合Setupとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectは引数なしの場合Setupとテスト対象メソッド呼び出しとvalidateStorageができる_エラー"() {
		when:
		FixtureBook.expect({
			validateDatabase(111)
			updateDatabase(112)
		})
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<11[1]>") > -1
		e.getLocalizedMessage().indexOf("<11[2]>") > -1
	}
	
	@Fixture(["Expect", "引数なしの場合Setupとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectは引数なしの場合Setupとテスト対象メソッド呼び出しとvalidateStorageができる_エラー_Callable"() {
		when:
		FixtureBook.expect(new Callable() {
			public Object call() throws Exception {
				TempConductorTest.validateDatabase(111)
				TempConductorTest.updateDatabase(112)
				return null
			}
		})
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<11[1]>") > -1
		e.getLocalizedMessage().indexOf("<11[2]>") > -1
	}

	@Fixture(["Expect", "引数なしの場合Setupとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectは引数なしの場合Setupとテスト対象メソッド呼び出しとvalidateStorageができる_正常終了"() {
		setup:
		boolean called = false

		when:
		FixtureBook.expect({
			validateDatabase(111)
			called = true
		})
		then:
		called == true
	}
	
	@Fixture(["Expect", "引数なしの場合Setupとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectは引数なしの場合Setupとテスト対象メソッド呼び出しとvalidateStorageができる_正常終了_Callable"() {
		setup:
		boolean[] called = new boolean[1]

		when:
		FixtureBook.expect(new Callable() {
			public Object call() throws Exception {
				TempConductorTest.validateDatabase(111)
				called[0] = true
				return null
			}
		})
		then:
		called[0] == true
	}
	
	@Fixture(["Expect", "引数なしの場合は全くテーブル定義がなくてもなんとなく動作する"])
	def "expectは引数なしの場合は全くテーブル定義がなくてもなんとなく動作する"() {
		setup:
		boolean called = false

		when:
		FixtureBook.expect({called = true})
		
		then:
		called == true
	}
	
	@Fixture(["Expect", "引数なしの場合は全くテーブル定義がなくてもなんとなく動作する"])
	def "expectは引数なしの場合は全くテーブル定義がなくてもなんとなく動作する_Callable"() {
		setup:
		boolean[] called = new boolean[1]

		when:
		FixtureBook.expect(new Callable() {
			public Object call() throws Exception {
				called[0] = true
				return null
			}
		})
		then:
		called[0] == true
	}
	
	@Fixture(["Expect", "配列やリストデータも取得できる"])
	def "expectはリストデータも取得できる"() {
		setup:
		def data = null
		
		when:
		FixtureBook.expect({List p1 -> data = p1}, FixtureBookTestData);
		
		then:
		data.size() == 2
		data instanceof List
		data[0] instanceof FixtureBookTestData
		data[1] instanceof FixtureBookTestData
		data[0]["text"] == "abc"
		data[1]["text"] == "efg"
	}
	
	@Fixture(["Expect", "配列やリストデータも取得できる"])
	def "expectはリストデータも取得できる_インターフェース実装"() {
		setup:
		List data = new ArrayList();
		
		when:
		FixtureBook.expect(new ActionWithList(){
			public Object action(List<FixtureBookTestData> p1) {
				data.addAll(p1);
				true
			}
		});
		
		then:
		data.size() == 2
		data[0] instanceof FixtureBookTestData
		data[1] instanceof FixtureBookTestData
		data[0]["text"] == "abc"
		data[1]["text"] == "efg"
	}

	@Fixture(["Expect", "配列やリストデータも取得できる"])
	def "expectは配列も取得できる"() {
		setup:
		FixtureBookTestData[] data = null
		
		when:
		FixtureBook.expect({FixtureBookTestData[] p1 -> data = p1});
		
		then:
		data.length == 2
		data[0] instanceof FixtureBookTestData
		data[1] instanceof FixtureBookTestData
		data[0].text == "abc"
		data[1].text == "efg"
	}	

	@Fixture(["Expect", "配列やリストデータも取得できる"])
	def "expectは配列も取得できる_インターフェース実装"() {
		setup:
		FixtureBookTestData[] data = new FixtureBookTestData[2]
		
		when:
		FixtureBook.expect(new ActionWithArray(){
			public void action(FixtureBookTestData[] p1) {
				data[0] = p1[0]
				data[1] = p1[1]
			}
		});
		
		then:
		data[0] instanceof FixtureBookTestData
		data[1] instanceof FixtureBookTestData
		data[0]["text"] == "abc"
		data[1]["text"] == "efg"
	}


	public interface ActionWithList {
		public Object action(List<FixtureBookTestData> list);
	}
	
	public interface ActionWithArray {
		public void action(FixtureBookTestData[] array);
	}
	
	private static void validateDatabase(int expect) {
		List<DynaBean> table = Database.executeQuery(null, "select int1 from TypesTable")
		table.size() == 1
		table[0]["int1"] == expect
	}
	
	private static void updateDatabase(int expect) {
		Database.executeNonQuery(null, "update TypesTable set int1 = " + expect)
	}
	
	@Fixture(["Expect", "引数1つの場合にGetObjectとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectで、引数がある場合にgetObjectとテスト対象メソッド呼び出しとvalidateStorageができる_正常終了"() {
		setup:
		boolean called = false;
		
		when:
		FixtureBook.expect({FixtureBookTestData data -> 
			assert data.text == "abc"
			validateDatabase(112)
			called = true
		})
		
		then:
		called == true
	}
	
	@Fixture(["Expect", "引数1つの場合にGetObjectとテスト対象メソッド呼び出しとValidateStorageができる"])
	def "expectで、引数がある場合にgetObjectとテスト対象メソッド呼び出しとvalidateStorageができる_validateStorageエラー"() {
		when:
		FixtureBook.expect({FixtureBookTestData data -> updateDatabase(113)})
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<11[2]>") > -1
		e.getLocalizedMessage().indexOf("<11[3]>") > -1
	}
	
	@Fixture(["Expect", "引数がある場合はDパラメタの定義だけで正常動作する"])
	def "expectで引数がある場合は「D.パラメタ」の定義だけで正常動作する"() {
		setup:
		boolean called = false
				
		when:
		FixtureBook.expect({FixtureBookTestData data -> 
			assert data.text == "abc"
			called = true
		})
		
		then:
		called == true
	}

	@Fixture(["Expect", "引数があるのにDパラメタが定義されていないとエラーになる"])
	def "expectで引数があるのに「D.パラメタ」が定義されていないとエラーになる"() {
		when:
		FixtureBook.expect({FixtureBookTestData data -> true})
		
		then:
		ConfigException e = thrown()
		println e
		e.message == "M_Fixture_FixtureBook_GetSection_OBJECT_FOR_EXEC"
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数なしの場合Setupとテスト対象メソッド呼び出しとValidateとValidateStorageができる"])
	def "expectReturnはパラメタ引数なしの場合setupとテスト対象メソッド呼び出しとvalidateとvalidateStorageができる_正常終了"() {
		setup:
		boolean called = false
		
		when:
		FixtureBook.expectReturn({
			validateDatabase(121)
			called = true
			new FixtureBookTestData(text:"abc")
		})
	
		then:
		called == true
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数なしの場合Setupとテスト対象メソッド呼び出しとValidateとValidateStorageができる"])
	def "expectReturnはパラメタ引数なしの場合setupとテスト対象メソッド呼び出しとvalidateとvalidateStorageができる_validateエラー"() {
		when:
		FixtureBook.expectReturn({new FixtureBookTestData(text:"ABC")})
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<[abc]>") > -1
		e.getLocalizedMessage().indexOf("<[ABC]>") > -1
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数なしの場合Setupとテスト対象メソッド呼び出しとValidateとValidateStorageができる"])
	def "expectReturnはパラメタ引数なしの場合setupとテスト対象メソッド呼び出しとvalidateとvalidateStorageができる_validateStorageエラー"() {
		when:
		FixtureBook.expectReturn({
			updateDatabase(122)
			new FixtureBookTestData(text:"abc")
		})
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<12[1]>") > -1
		e.getLocalizedMessage().indexOf("<12[2]>") > -1
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数なしの場合E取得データだけで正常動作する"])
	def "expectReturnはパラメタ引数なしの場合「E.取得データ」だけで正常動作する"() {
		expect:
		FixtureBook.expectReturn({new FixtureBookTestData(text:"abc")})
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数1つの場合はパラメタ取得と戻り値の検証ができる"])
	def "expectReturnはパラメタ引数がある場合はパラメタ取得と戻り値の検証ができる_正常終了"() {
		expect:
		FixtureBook.expectReturn({FixtureBookTestData param ->
			assert param.text == "abc"
			new FixtureBookTestData(text:"zzz")
		})
	}
	
	@Fixture(["ExpectReturn", "パラメタ引数1つの場合はパラメタ取得と戻り値の検証ができる"])
	def "expectReturnはリストをパラメタ引数として取得できる"() {
		expect:
		FixtureBook.expectReturn({List param ->
			assert param.size() == 1
			assert param[0]["text"] == "abc"
			assert param[0] instanceof FixtureBookTestData
			new FixtureBookTestData(text:"zzz")
		}, FixtureBookTestData)
	}

	@Fixture(["ExpectReturn", "パラメタ引数1つの場合はパラメタ取得と戻り値の検証ができる"])
	def "expectReturnはリストをパラメタ引数として取得できる_インターフェース実装"() {
		expect:
		FixtureBook.expectReturn(new ActionWithList(){
			public Object action(List<FixtureBookTestData> param) {
				assert param.size() == 1
				assert param[0]["text"] == "abc"
				assert param[0] instanceof FixtureBookTestData
				new FixtureBookTestData(text:"zzz")
			}
		})
	}

	@Fixture(["ExpectReturn", "パラメタ引数1つの場合はパラメタ取得と戻り値の検証ができる"])
	def "expectReturnはパラメタ引数がある場合はパラメタ取得と戻り値の検証ができる_エラー"() {
		when:
		FixtureBook.expectReturn({FixtureBookTestData param ->
			assert param.text == "abc"
			new FixtureBookTestData(text:"yyy")
		})
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<[yyy]>") > -1
		e.getLocalizedMessage().indexOf("<[zzz]>") > -1
	}
	
	@Fixture(["ExpectThrown", "Setupとテスト対象メソッド呼び出しと例外のValidateとValidateStorageができる"])
	def "expectThrownはsetupとテスト対象メソッド呼び出しと例外のvalidateとvalidateStorageができる_正常終了"() {
		setup:
		boolean called = false
		
		when:
		FixtureBook.expectThrown(Exception, {
			validateDatabase(131)
			called = true
			throw new Exception("ABC")
		})
	
		then:
		called == true
	}
	
	@Fixture(["ExpectThrown", "Setupとテスト対象メソッド呼び出しと例外のValidateとValidateStorageができる"])
	def "expectThrownはsetupとテスト対象メソッド呼び出しと例外のvalidateとvalidateStorageができる_validateエラー"() {
		when:
		FixtureBook.expectThrown(Exception, {throw new Exception("aBC")})
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<[a]BC>") > -1
		e.getLocalizedMessage().indexOf("<[A]BC>") > -1
	}

	@Fixture(["ExpectThrown", "Setupとテスト対象メソッド呼び出しと例外のValidateとValidateStorageができる"])
	def "expectThrownはsetupとテスト対象メソッド呼び出しと例外のvalidateとvalidateStorageができる_validateStorageエラー"() {
		when:
		FixtureBook.expectThrown(Exception, {
			updateDatabase(132)
			throw new Exception("ABC")
		})
	
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<13[1]>") > -1
		e.getLocalizedMessage().indexOf("<13[2]>") > -1
	}
	
	@Fixture(["ValidateParameter", "Expect系メソッドを呼ぶ前にGetParameterAtメソッドを呼ぶと例外が発生する"])
	def "getParameterAtメソッドはexpect系メソッドを呼ぶ前に呼ぶと例外発生する"() {
		expect:
		new FixtureBook().validate(ConfigException, {new FixtureBook().getParameterAt(0)})
	}
	
	@Fixture(["ValidateParameter", "Expect系メソッドを呼ぶ前にValidateParameterAtメソッドを呼ぶと例外が発生する"])
	def "validateParameterAtメソッドはexpect系メソッドを呼ぶ前に呼ぶと例外発生する"() {
		expect:
		new FixtureBook().validate(ConfigException, {new FixtureBook().validateParameterAt(0)})
		new FixtureBook().validate(ConfigException, {new FixtureBook().validateParameterAt(0, "xxx")})
	}
	
	@Fixture(["ValidateParameter", "GetParameterAtメソッドのインデックスがExpectの引数の数よりも多い場合は例外が発生する"])
	def "getParameterAtメソッドのインデックスがexpectの引数の数よりも多い場合は例外が発生する"() {
		expect:
		new FixtureBook().validate(ConfigException, {new FixtureBook().expect({}).getParameterAt(0)})
	}

	@Fixture(["ValidateParameter", "ValidateParameterAtメソッドのインデックスがExpectの引数の数よりも多い場合は例外が発生する"])
	def "validateParameterAtメソッドのインデックスがexpectの引数の数よりも多い場合は例外が発生する"() {
		expect:
		new FixtureBook().validate(ConfigException, {new FixtureBook().expect({
			FixtureBookTestData a, FixtureBookTestData b -> true}).validateParameterAt(2)})
		new FixtureBook().validate(ConfigException, {new FixtureBook().expect({
			FixtureBookTestData a -> true}).validateParameterAt(2, "xxx")})
	}

}
