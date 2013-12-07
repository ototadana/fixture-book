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

import java.text.DateFormat;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.cast.temp.data.ArrayData;
import com.xpfriend.fixture.cast.temp.data.Data;
import com.xpfriend.fixture.cast.temp.data.Order;
import com.xpfriend.fixture.cast.temp.data.OrderDetails
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.Formi;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class ObjectValidatorBaseTest extends Specification{
	
	private FixtureBook fixtureBook = new FixtureBook()
	
	@Fixture(["データあり"])
	def "validateは指定されたリストが予想結果と等しいかどうかを調べる"() {
		setup:
		List actual = fixtureBook.getList(Data)

		expect:
		fixtureBook.validate(actual, null)
	}

	@Fixture(["データあり"])
	def "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"() {
		setup:
		Object actual = fixtureBook.getObject(Data, "Data1")

		expect:
		fixtureBook.validate(actual, "Data1")
	}

	@Fixture(["データあり"])
	def "validateは指定された配列が予想結果と等しいかどうかを調べる"() {
		setup:
		Object actual = fixtureBook.getArray(Data)

		expect:
		fixtureBook.validate(actual)
	}

	@Fixture(["データあり"])
	def "validateにnullを渡すと指定された定義が存在しなければエラーにならない"() {
		expect:
		fixtureBook.validate(null, "xxx")
	}

	@Fixture(["データなし"])
	def "validateは要素数ゼロのリストもチェックできる"() {
		setup:
		List actual = fixtureBook.getList(Data, null)
		
		expect:
		fixtureBook.validate(actual, "Data")
	}

	@Fixture(["データなし"])
	def "validateは要素数ゼロのリストも一つの定義しかなければ定義名指定なしでチェックできる"() {
		setup:
		List actual = fixtureBook.getList(Data, null)
		
		expect:
		fixtureBook.validate(actual)
	}

	@Fixture(["データあり"])
	def "validateは複数のテーブル定義がある場合には要素数ゼロのリストはチェックできない"() {
		setup:
		def actual = new ArrayList()
		
		when:
		fixtureBook.validate(actual)
		
		then:
		ConfigException e = thrown(ConfigException)
		println e
		e.message == "M_Fixture_Temp_ObjectOperator_GetTableName"
		e.getLocalizedMessage().indexOf("ObjectValidatorBaseTest.xlsx#ObjectValidatorBaseTest") > -1
	}
	
	@Fixture(["データあり"])
	def "validateでは取得データの行数が実際と異なる場合はエラーになる"() {
		setup:
		def actual = fixtureBook.getList(Data)
		
		when:
		fixtureBook.validate(actual, "行数違い")
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf("expected:<1> but was:<3>") > -1
	}

	@Fixture(["データあり"])
	def "validateでは予想結果のセル値が実際と異なる場合はエラーになる"(typeName, expected) {
		setup:
		def actual = fixtureBook.getList(Data)
		
		when:
		fixtureBook.validate(actual, typeName)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf(expected) > -1
		
		where:
		typeName   | expected
		"文字列違い"  | "expected:<a[x]> but was:<a[]>"
		"数値違い"   | "expected:<[2]> but was:<[1]>"
		"文字列違い（2行目）" | "expected:<b[x]> but was:<b[]>"
	}
	
	@Fixture(["セル内容が空の場合はnullでも空文字列でもOK"])
	def "セル内容が空の場合はnullでも空文字列でもOK"(actual) {
		setup:
		Data data = new Data()
		data.setText1(actual)
		
		expect:
		fixtureBook.validate(data)

		where:
		actual << ["", null]
	}

	@Fixture(["セル内容が空の場合はnullでも空文字列でもOK"])
	def "セル内容が空の場合はnullまたは空文字列以外はNG"() {
		setup:
		Data data = new Data()
		data.setText1("xxx")
		
		when:
		fixtureBook.validate(data)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf("text1") > -1
	}

	@Fixture(["セル内容が\${EMPTY}の場合は空文字列のみOK"])
	def "セル内容が\${EMPTY}の場合は空文字列のみOK"() {
		setup:
		Data data = new Data()
		data.setText1("")
		
		expect:
		fixtureBook.validate(data)
	}

	@Fixture(["セル内容が\${EMPTY}の場合は空文字列のみOK"])
	def "セル内容が\${EMPTY}の場合は空文字列以外はNG"(actual) {
		setup:
		Data data = new Data()
		data.setText1(actual)
		
		when:
		fixtureBook.validate(data)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf("text1") > -1
		
		where:
		actual << ["xxx", null]
	}

	@Fixture(["セル内容が\${NULL}の場合はnullのみOK"])
	def "セル内容が\${NULL}の場合はnullのみOK"() {
		setup:
		Data data = new Data()
		data.setText1(null)
		
		expect:
		fixtureBook.validate(data)
	}

	@Fixture(["セル内容が\${NULL}の場合はnullのみOK"])
	def "セル内容が\${NULL}の場合はnull以外はNG"(actual) {
		setup:
		Data data = new Data()
		data.setText1(actual)
		
		when:
		fixtureBook.validate(data)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf("text1") > -1
		
		where:
		actual << ["xxx", ""]
	}

	def "セル内容が空白スペースの場合はそのまま比較できる"() {
		setup:
		Data data = new Data(text1:" ")
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture(["セル内容が*の場合はnullまたは空文字列以外ならば何でもOK"])
	def "セル内容が*の場合はnullまたは空文字列以外ならば何でもOK"() {
		setup:
		Data data = new Data(text1:"xxx", number2:0)
		
		expect:
		fixtureBook.validate(data)
	}

	@Fixture(["セル内容が*の場合はnullまたは空文字列以外ならば何でもOK"])
	def "セル内容が*の場合はnullまたは空文字列だとNG"(text, number) {
		setup:
		Data data = new Data(text1:text, number2:number)
		
		when:
		fixtureBook.validate(data)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		println message
		if(number == null) {
			assert message.indexOf("number2") > -1
		} else {
			assert message.indexOf("text1") > -1
		}
		
		where:
		text | number
		null | 0
		""   | 0
		"xxx"| null
	}

	@Fixture(["セル内容が%を含む場合は前方一致、後方一致、部分一致で判定できる"])
	def "セル内容が%を含む場合は前方一致、後方一致、部分一致で判定できる"() {
		setup:
		List<Data> list = new ArrayList<Data>()
		for(int i = 0; i < 3; i++) {
			Data data = new Data()
			data.setText1("abcd")
			list.add(data)
		}
		
		expect:
		fixtureBook.validate(list)
	}
	
	@Fixture(["セル内容が%を含む場合は前方一致、後方一致、部分一致で判定できる"])
	def "セル内容が%を含む場合は前方一致、後方一致、部分一致で判定できる（エラーのケース）"(actual, error) {
		setup:
		List<Data> list = new ArrayList<Data>()
		for(int i = 0; i < 3; i++) {
			Data data = new Data()
			data.setText1(actual)
			list.add(data)
		}
		
		when:
		fixtureBook.validate(list)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf(error) > -1
		
		where:
		actual | error
		"abcx" | "%cd"
		"axcd" | "ab%"
		"abxcd" | "%bc%"
	}
	
	def "%記号だけを予想結果にした場合なんでもOKになる"() {
		expect:
		fixtureBook.validate(new Data(text1:"xxxx"))
	}
	
	def "正規表現を使うことで%そのものを比較できる"() {
		expect:
		fixtureBook.validate(new Data(text1:"%bc"))
		
		when:
		fixtureBook.validate(new Data(text1:"abc"))
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<[abc]>") > -1
		e.getLocalizedMessage().indexOf("<[`%bc`]>") > -1
	}
	
	@Fixture(["セル内容が``で囲まれている場合は正規表現としてのマッチングを行う"])
	def "セル内容が``で囲まれている場合は正規表現としてのマッチングを行う"(actual) {
		setup:
		Data data = new Data()
		data.setText1(actual)

		expect:
		fixtureBook.validate(data)
		
		where:
		actual << ["abcd", "abxd"]
	}

	@Fixture(["セル内容が``で囲まれている場合は正規表現としてのマッチングを行う"])
	def "セル内容が``で囲まれている場合は正規表現としてのマッチングを行う（エラーのケース）"() {
		setup:
		Data data = new Data()
		data.setText1("abccd")

		when:
		fixtureBook.validate(data, null)
		
		then:
		AssertionError e = thrown(AssertionError)
		def message = e.getMessage()
		message.indexOf("expected:<[`ab.?d`]> but was:<[abccd]>") > -1
	}
	
	@Fixture(["配列データ"])
	def "バーティカルバー区切りの値で表現された配列およびリスト項目をチェックできる" () {
		setup:
		ArrayData arrayData = fixtureBook.getObject(ArrayData)

		expect:
		fixtureBook.validate(arrayData)
	}
	
	def "親子構造になったデータをチェックできる_正常"() {
		setup:
		Order order = fixtureBook.getObject(Order)
		order.setDetail3(new OrderDetails(order.detail))
		order.getDetail()[1].setQty(21)
		
		expect:
		fixtureBook.validate(order)
	}

	def "親子構造になったデータをチェックできる_エラー"() {
		setup:
		Order order = fixtureBook.getObject(Order)
		order.setDetail3(new OrderDetails(order.detail))
		order.getDetail()[1].setQty(21)
		
		when:
		fixtureBook.validate(order)
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<2[0]>") > -1
		e.getLocalizedMessage().indexOf("<2[1]>") > -1
	}

	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK"() {
		setup:
		Data data = new Data(date1:new Date())
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_エラーのケース"() {
		setup:
		Data data = new Data(date1:new Date(0L))
		
		when:
		fixtureBook.validate(data)
		
		then:
		AssertionError e = thrown()
		println e
	}
	
	@Fixture("nullをValidateの引数に渡した場合は引数で指定された名前のテーブル定義が存在しないこと")
	def "nullをValidateの引数に渡した場合は引数で指定された名前のテーブル定義が存在しないこと_正常"() {
		expect:
		fixtureBook.validate(null, "Data2")
	}

	@Fixture("nullをValidateの引数に渡した場合は引数で指定された名前のテーブル定義が存在しないこと")
	def "nullをValidateの引数に渡した場合は引数で指定された名前のテーブル定義が存在しないこと_エラー"() {
		when:
		fixtureBook.validate(null, "Data")
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("null") > -1
		e.getLocalizedMessage().indexOf("Data") > -1
	}

	@Fixture("バイト配列の検証ができる")
	def "バイト配列の検証ができる_エラー1"() {
		setup:
		List<Data> list = fixtureBook.getList(Data)
		list[0].bytes[1] = 0
		
		when:
		fixtureBook.validate(list)
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("<49|[5]0|51>") > -1
		e.getLocalizedMessage().indexOf("<49|[]0|51>") > -1
	}
	
	@Fixture("バイト配列の検証ができる")
	def "バイト配列の検証ができる_エラー2"() {
		setup:
		List<Data> list = fixtureBook.getList(Data)
		list[1].bytes[1] = 0
		
		when:
		fixtureBook.validate(list)
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("ObjectValidatorBaseTest.txt]>") > -1
		e.getLocalizedMessage().indexOf("ObjectValidatorBaseTest.txt.tmp]>") > -1
	}
	
	@Fixture("バイト配列の検証ができる")
	def "バイト配列の検証ができる_エラー3"() {
		setup:
		List<Data> list = fixtureBook.getList(Data)
		list[2].bytes[1] = 0
		
		when:
		fixtureBook.validate(list)
		
		then:
		AssertionError e = thrown()
		println e
		e.getLocalizedMessage().indexOf("Y2Jh") > -1
		e.getLocalizedMessage().indexOf("YwBh") > -1
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_ロケール日付書式文字列"() {
		setup:
		def data = [date1: DateFormat.getDateInstance().format(new Date())];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}

	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_ロケール日時書式文字列"() {
		setup:
		def data = [date1: DateFormat.getDateTimeInstance().format(new Date())];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_ハイフン区切り日付書式文字列"() {
		setup:
		def data = [date1: Formi.format(new Date(), "yyyy-MM-dd")];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_ハイフン区切り日時書式文字列"() {
		setup:
		def data = [date1: Formi.format(new Date(), "yyyy-MM-dd HH:mm:ss")];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}

	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_区切り文字なし日付書式文字列"() {
		setup:
		def data = [date1: Formi.format(new Date(), "yyyyMMdd")];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_区切り文字なし日時分書式文字列"() {
		setup:
		def data = [date1: Formi.format(new Date(), "yyyyMMddHHmm")];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}
	
	@Fixture("セル内容が\${TODAY}の場合は本日の日付のみOK")
	def "セル内容がTODAYの場合は本日の日付のみOK_区切り文字なし日時分秒書式文字列"() {
		setup:
		def data = [date1: Formi.format(new Date(), "yyyyMMddHHmmss")];
		System.out.println(data.date1);
		
		expect:
		fixtureBook.validate(data)
	}
}
