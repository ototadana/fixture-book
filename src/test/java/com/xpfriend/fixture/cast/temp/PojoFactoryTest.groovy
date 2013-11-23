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

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Rule;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBookPath;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.cast.temp.data.ArrayData;
import com.xpfriend.fixture.cast.temp.data.Data;
import com.xpfriend.fixture.cast.temp.data.Order;
import com.xpfriend.fixture.cast.temp.PojoFactory;
import com.xpfriend.fixture.cast.temp.TempObjectFactory;
import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class PojoFactoryTest extends Specification {
	
	private FixtureBook fixtureBook = new FixtureBook();

	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoFactory factory = getPojoFactory(sheet.getCase("ロールなし"))
		
		expect:
		factory?.hasRole(null, null) == false
	}

	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがあればtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoFactory factory = getPojoFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(Data, null) == true
	}
	
	def "hasRole引数のクラスがMapでもtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoFactory factory = getPojoFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(Map, null) == true
	}

	@Fixture(["データあり"])
	def "getObjectは指定された名前の定義を読み込み、オブジェクト作成する"(name, value) {
		when:
		Data object = fixtureBook.getObject(Data, name)
		
		then:
		object.toString() == value
		
		where:
		name    | value
		null    | "a,1"
		"Data1" | "c,3"
	}
	
	@Fixture(["データなし"])
	def "getObjectはデータ行がない場合nullを返す"() {
		when:
		Data object = fixtureBook.getObject(Data)
		
		then:
		object == null
	}
	
	@Fixture(["データあり"])
	def "getListは指定された名前の定義を読み込み、リスト作成する"(String name, List value) {
		when:
		List<Data> list = fixtureBook.getList(Data, name)
		
		then:
		list.size() == value.size()
		int i = 0
		list.every {it.toString() == value[i++]}
		
		where:
		name    | value
		null    | ["a,1", "b,2", "null,0"]
		"Data1" | ["c,3"]
	}
	
	@Fixture(["データなし"])
	def "getListはデータ行がない場合に要素数ゼロのリストを返す"() {
		when:
		List list = fixtureBook.getList(Data)
		
		then:
		list.size() == 0
	}

	@Fixture(["データあり"])
	def "getArrayは指定された名前の定義を読み込み、配列作成する"(String name, List value) {
		when:
		Data[] array = fixtureBook.getArray(Data, name)
		
		then:
		array.length == value.size()
		int i = 0
		array.every {it.toString() == value[i++]}
		
		where:
		name    | value
		null    | ["a,1", "b,2", "null,0"]
		"Data1" | ["c,3"]
	}
	
	@Fixture(["データなし"])
	def "getArrayはデータ行がない場合に要素数ゼロの配列を返す"() {
		when:
		Data[] array = fixtureBook.getArray(Data)
		
		then:
		array.length == 0
	}

	@Fixture(["親子データ"])
	def "親子構造になったデータを取得できる"() {
		when:
		Order order = fixtureBook.getObject(Order)
		
		then:
		order.orderNo == "H001"
		order.customerInfo.code == "C001"
		order.customerInfo.name == "XX商事"
		order.customerInfo.telno == "045-999-9999"
		order.detail.size() == 2
		order.detail[0].detailNo == "001"
		order.detail[0].itemCd == "X01"
		order.detail[0].qty == 10
		order.detail[1].detailNo == "002"
		order.detail[1].itemCd == "X02"
		order.detail[1].qty == 20
	}

	@Fixture(["配列データ"])
	def "バーティカルバー区切りの値で配列およびリストを表現できる" () {
		when:
		ArrayData arrayData = fixtureBook.getObject(ArrayData)

		then:
		arrayData.stringArray == ["a", "b", "c"]
		arrayData.intArray == [1, 2, 3]
		arrayData.stringList == ["e", "f", "g"]
	}

	def getPojoFactory(Case testCase) {
		TempObjectFactory parent = new TempObjectFactory();
		parent.initialize(testCase);
		return parent.pojoFactory;
	}
	
	def "指定されたプロパティが存在しない場合は例外が発生する"() {
		when:
		fixtureBook.getObject(Data)
		
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Temp_ObjectFactory_NoSuchProperty"
		e.getLocalizedMessage().indexOf("zzz") > -1
	}

	def "Stringを作成できる"() {
		when: String object = fixtureBook.getObject(String)
		then: object == "a"
		when: String[] array = fixtureBook.getArray(String)
		then: array == ["a", "b"]

		when: List<String> list = fixtureBook.getList(String)
		then: list == ["a", "b"]
		
		expect:
		FixtureBook.expect({String s -> assert s == "a"})
		FixtureBook.expect({List list2 -> assert list2 == ["a", "b"]}, String)
		FixtureBook.expect({String[] array2 -> assert array2 == ["a", "b"]}, String)
	}

	def "intを作成できる"() {
		when: int object = fixtureBook.getObject(int)
		then: object == 1

		when: int[] array = fixtureBook.getArray(int)
		then: array == [1, 2]

		when: List<Integer> list = fixtureBook.getList(Integer)
		then: list == [1, 2]
		
		expect:
		FixtureBook.expect({int object2 -> assert object2 == 1})
		FixtureBook.expect({List list2 -> assert list2 == [1, 2]}, int)
		FixtureBook.expect({int[] array2 -> assert array2 == [1, 2]}, int)
	}
}
