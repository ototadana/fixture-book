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

import java.text.SimpleDateFormat
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class DynaBeanFactoryTest extends Specification {

	private FixtureBook fixtureBook = new FixtureBook();
	
	def "hasRoleはinitializeで指定されたテストケースにパラメタセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanFactory factory = getDynaBeanFactory(sheet.getCase("ロールなし"))
		
		expect:
		factory?.hasRole(null, null) == false
	}
	
	def "hasRoleは引数のクラスがDynaBeanでなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanFactory factory = getDynaBeanFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(String, null) == false
	}

	def "hasRoleは引数のクラスがDynaBeanでありかつinitializeで指定されたテストケースにパラメタセクションがあればtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanFactory factory = getDynaBeanFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(DynaBean, null) == true
	}

	def getDynaBeanFactory(Case testCase) {
		TempObjectFactory parent = new TempObjectFactory();
		parent.initialize(testCase);
		return parent.dynaBeanFactory;
	}
	
	def "getObjectは指定された名前の定義を読み込んでオブジェクト作成する"() {
		when:
		DynaBean object = fixtureBook.getObject(DynaBean, "Data")
		
		then:
		validate(object)
	}
	
	public static boolean validate(Object object) {
		validate(object["s1"], "a", String)
		validate(object["s2"], "b", String)
		validate(object["decimal1"], 1.111, BigDecimal);
		validate(object["decimal2"], 2.222, BigDecimal);
		validate(object["int1"], 3, Integer);
		validate(object["int2"], 4, Integer);
		validate(object["long1"], 5, Long);
		validate(object["short1"], 6, Short);
		validate(object["float1"], 7.77f, Float);
		validate(object["double1"], 8.88d, Double);
		validate(object["byte1"], (byte)9, Byte);
		validate(object["char1"], 'c', Character);
		validate(object["char2"], 'd', Character);
		validate(object["boolean1"], true, Boolean);
		validate(object["boolean2"], false, Boolean);
		validate(object["timestamp1"], toDate("2013-01-02 12:13:14"), java.sql.Timestamp);
		validate(object["timestamp2"], toDate("2013-01-03 12:13:15"), java.sql.Timestamp);
		validate(object["date1"], toDate("2012-12-31 00:00:00"), java.sql.Date);

		// Java の場合、時間に関しては1970-01-01の時間となるが、変換仕様としては「日付部分は不定」とする。
		validate(object["time1"], toDate("1970-01-01 10:10:11"), java.sql.Time);
		true
	}
	
	public static void validate(Object actual, Object expected, Class type) {
		assert actual == expected
		assert type.equals(actual.getClass())
	}
	
	private static Object toDate(String s) {
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s)
	}

	@Fixture(["getObjectは指定された名前の定義を読み込んでオブジェクト作成する"])
	def "getListは指定された名前の定義を読み込んでリストを作成する"() {
		when:
		List<DynaBean> list = fixtureBook.getList(DynaBean, "Data")
		
		then:
		list.size() == 2
		validate(list[0])
		validateNull(list[1])
	}
	
	def validateNull(DynaBean object) {
		for(DynaProperty p : object.getDynaClass().getDynaProperties()) {
			Object value = object.get(p.getName())
			if(p.getType().isPrimitive()) {
				assert value == TypeConverter.getNullValue(p.getType())
			} else {
				assert value == null
			}
		}
		true
	}
	
	@Fixture(["getObjectは指定された名前の定義を読み込んでオブジェクト作成する"])
	def "getArrayは指定された名前の定義を読み込んで配列を作成する"() {
		when:
		DynaBean[] array = fixtureBook.getArray(DynaBean, "Data")
		
		then:
		array.length == 2
		validate(array[0])
		validateNull(array[1])
	}
	
	def "親子構造になったデータを取得できる"() {
		when:
		DynaBean object = fixtureBook.getObject(DynaBean, "Order")
		
		then:
		validateNested(object)
	}
	
	public static boolean validateNested(Object order) {
		assert order["orderNo"] == "H001"
		assert order["customerInfo"]["code"] == "C001"
		assert order["customerInfo"]["name"] == "XX商事"
		assert order["customerInfo"]["telno"] == "045-999-9999"
		assert order["detail"].size() == 2
		assert order["detail"][0]["detailNo"] == "001"
		assert order["detail"][0]["itemCd"] == "X01"
		assert order["detail"][0]["qty"] == 10
		assert order["detail"][1]["detailNo"] == "002"
		assert order["detail"][1]["itemCd"] == "X02"
		assert order["detail"][1]["qty"] == 20
		true
	}
	
	def "バーティカルバー区切りの値で配列およびリストを表現できる"() {
		when:
		DynaBean arrayData = fixtureBook.getObject(DynaBean, "ArrayData")

		then:
		validateArrayData(arrayData)
	}
	
	public static boolean validateArrayData(Object arrayData) {
		assert arrayData["stringArray"] == ["a", "b", "c"]
		assert arrayData["intArray"] == [1, 2, 3]
		assert arrayData["stringList1"] == ["e", "f", "g"]
		assert arrayData["stringList2"] == ["h", "i", "j"]
		true
	}
}
