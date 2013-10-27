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

import org.junit.Rule

import spock.lang.Specification

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBookPath
import com.xpfriend.fixture.FixtureBook
import com.xpfriend.fixture.staff.Case
import com.xpfriend.fixture.staff.Sheet
import com.xpfriend.junk.Formi

/**
 * @author Ototadana
 *
 */
@FixtureBookPath("com/xpfriend/fixture/cast/temp/DynaBeanValidatorTest.xlsx")
class MapValidatorTest extends Specification {

	private FixtureBook fixtureBook = new FixtureBook();
	
	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapValidator validator = getMapValidator(sheet.getCase("ロールなし"))
		
		expect:
		validator?.hasRole(null, null) == false
	}

	def "hasRoleは引数のクラスがMapでなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapValidator validator = getMapValidator(sheet.getCase("ロールあり"))
		
		expect:
		validator?.hasRole(String, null) == false
	}

	def getMapValidator(Case testCase) {
		TempObjectValidator parent = new TempObjectValidator();
		parent.initialize(testCase);
		return parent.mapValidator;
	}
	
	@Fixture(["DynaBeanValidatorTest", "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"])
	def "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"() {
		setup:
		Map actual = fixtureBook.getObject(Map, "Data")
		actual["timestamp3"] = Formi.toTimestamp(new Date())
		
		expect:
		fixtureBook.validate(actual, "Data")
	}
	
	@Fixture(["DynaBeanValidatorTest", "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"])
	def "validateは指定されたリストが予想結果と等しいかどうかを調べる"() {
		setup:
		List<Map> list = fixtureBook.getList(Map, "Data")
		list[0]["timestamp3"] = Formi.toTimestamp(new Date())
		
		expect:
		fixtureBook.validate(list, "Data")
	}
	
	@Fixture(["DynaBeanValidatorTest", "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"])
	def "validateは指定された配列が予想結果と等しいかどうかを調べる"() {
		setup:
		Map[] array = fixtureBook.getArray(Map, "Data")
		array[0]["timestamp3"] = Formi.toTimestamp(new Date())
		
		expect:
		fixtureBook.validate(array, "Data")
	}
	
	def "MapValidatorはvalidate_Exceptionメソッドの実行はできない"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		MapValidator validator = getMapValidator(sheet.getCase("ロールなし"));
		
		when:
		validator.validate(Exception, null, "")
		
		then:
		UnsupportedOperationException e = thrown()
		println e
	}
}
