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

import spock.lang.Specification

import com.xpfriend.fixture.Fixture
import com.xpfriend.fixture.FixtureBook
import com.xpfriend.fixture.FixtureBookPath
import com.xpfriend.fixture.staff.Case
import com.xpfriend.fixture.staff.Sheet

/**
 * @author Ototadana
 *
 */
class MapFactoryTest extends Specification {

	private FixtureBook fixtureBook = new FixtureBook();

	def "hasRoleはinitializeで指定されたテストケースにパラメタセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapFactory factory = getMapFactory(sheet.getCase("ロールなし"))
		
		expect:
		factory?.hasRole(null, null) == false
	}

	def "hasRoleは引数のクラスがMapでなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapFactory factory = getMapFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(String, null) == false
	}

	def "hasRoleは引数のクラスがMapでありかつinitializeで指定されたテストケースにパラメタセクションがあればtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapFactory factory = getMapFactory(sheet.getCase("ロールあり"))
		
		expect:
		factory?.hasRole(Map, null) == true
	}

	def getMapFactory(Case testCase) {
		TempObjectFactory parent = new TempObjectFactory();
		parent.initialize(testCase);
		return parent.mapFactory;
	}
	
	@FixtureBookPath("com/xpfriend/fixture/cast/temp/DynaBeanFactoryTest.xlsx")
	@Fixture(["DynaBeanFactoryTest", "getObjectは指定された名前の定義を読み込んでオブジェクト作成する"])
	def "getObjectは指定された名前の定義を読み込んでオブジェクト作成する"() {
		when:
		Map object = fixtureBook.getObject(Map, "Data")
		
		then:
		DynaBeanFactoryTest.validate(object)
	}
	
	@FixtureBookPath("com/xpfriend/fixture/cast/temp/DynaBeanFactoryTest.xlsx")
	@Fixture(["DynaBeanFactoryTest", "getObjectは指定された名前の定義を読み込んでオブジェクト作成する"])
	def "getListは指定された名前の定義を読み込んでリストを作成する"() {
		when:
		List<Map> list = fixtureBook.getList(Map, "Data")
		
		then:
		list.size() == 2
		DynaBeanFactoryTest.validate(list[0])
		validateNull(list[1])
	}
	
	def validateNull(Map object) {
		for(Object value : object.values()) {
			if(value != null) {
				if(value instanceof Number) {
					assert value == 0
				} else if(value instanceof Boolean){
					assert value == false
				} else if(value instanceof Character){
					assert value == TypeConverter.getNullValue(Character)
				} else {
					assert false
				}
			}
		}
		true
	}
	
	@FixtureBookPath("com/xpfriend/fixture/cast/temp/DynaBeanFactoryTest.xlsx")
	@Fixture(["DynaBeanFactoryTest", "getObjectは指定された名前の定義を読み込んでオブジェクト作成する"])
	def "getArrayは指定された名前の定義を読み込んで配列を作成する"() {
		when:
		Map[] array = fixtureBook.getArray(Map, "Data")
		
		then:
		array.length == 2
		DynaBeanFactoryTest.validate(array[0])
		validateNull(array[1])
	}
	
	def "親子構造になったデータを取得できる"() {
		when:
		Map object = fixtureBook.getObject(Map, "Order")
		
		then:
		DynaBeanFactoryTest.validateNested(object)
	}

	def "親子構造になったデータを取得できる2"() {
		when:
		Map order = fixtureBook.getObject(Map, "Order")
		
		then:
		order["orderNo"] == "H001"
		order["customerInfo"]["code"] == "C001"
		order["detail"].size() == 2
		order["detail"][0]["detailNo"] == "001"
		order["detail"][1]["detailNo"] == "002"
	}
	
	@FixtureBookPath("com/xpfriend/fixture/cast/temp/DynaBeanFactoryTest.xlsx")
	@Fixture(["DynaBeanFactoryTest", "バーティカルバー区切りの値で配列およびリストを表現できる"])
	def "バーティカルバー区切りの値で配列およびリストを表現できる"() {
		when:
		Map arrayData = fixtureBook.getObject(Map, "ArrayData")

		then:
		DynaBeanFactoryTest.validateArrayData(arrayData)
	}
}
