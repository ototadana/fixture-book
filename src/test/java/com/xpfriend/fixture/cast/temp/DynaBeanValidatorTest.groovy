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

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.Formi;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class DynaBeanValidatorTest extends Specification {

	private FixtureBook fixtureBook = new FixtureBook();
	
	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanValidator validator = getDynaBeanValidator(sheet.getCase("ロールなし"))
		
		expect:
		validator?.hasRole(null, null) == false
	}

	def "hasRoleは引数のクラスがDynaBeanでなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanValidator validator = getDynaBeanValidator(sheet.getCase("ロールあり"))
		
		expect:
		validator?.hasRole(String, null) == false
	}

	def getDynaBeanValidator(Case testCase) {
		TempObjectValidator parent = new TempObjectValidator();
		parent.initialize(testCase);
		return parent.dynaBeanValidator;
	}
	
	def "validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"() {
		setup:
		DynaBean actual = fixtureBook.getObject(DynaBean, "Data")
		actual.set("timestamp3", Formi.toTimestamp(new Date()))
		
		expect:
		fixtureBook.validate(actual, "Data")
	}
	
	@Fixture(["validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"])
	def "validateは指定されたリストが予想結果と等しいかどうかを調べる"() {
		setup:
		List<DynaBean> list = fixtureBook.getList(DynaBean, "Data")
		list[0].set("timestamp3", Formi.toTimestamp(new Date()))
		
		expect:
		fixtureBook.validate(list, "Data")
	}
	
	@Fixture(["validateは指定されたオブジェクトが予想結果と等しいかどうかを調べる"])
	def "validateは指定された配列が予想結果と等しいかどうかを調べる"() {
		setup:
		DynaBean[] array = fixtureBook.getArray(DynaBean, "Data")
		array[0].set("timestamp3", Formi.toTimestamp(new Date()))
		
		expect:
		fixtureBook.validate(array, "Data")
	}
	
	def "DynaBeanValidatorはvalidate_Exceptionメソッドの実行はできない"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		DynaBeanValidator validator = getDynaBeanValidator(sheet.getCase("ロールなし"));
		
		when:
		validator.validate(Exception, null, "")
		
		then:
		UnsupportedOperationException e = thrown()
		println e
	}
}
