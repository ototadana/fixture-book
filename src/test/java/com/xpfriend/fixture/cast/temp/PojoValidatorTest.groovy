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

import org.junit.Rule;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBookPath;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.cast.temp.data.ArrayData;
import com.xpfriend.fixture.cast.temp.data.Data;
import com.xpfriend.fixture.cast.temp.data.Order;
import com.xpfriend.fixture.cast.temp.PojoValidator;
import com.xpfriend.fixture.cast.temp.TempObjectValidator;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class PojoValidatorTest extends Specification {
	
	private FixtureBook fixtureBook = new FixtureBook();

	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがなければfalseを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoValidator validator = getPojoValidator(sheet.getCase("ロールなし"))
		
		expect:
		validator?.hasRole(null, null) == false
	}

	def "hasRoleはinitializeで指定されたテストケースに取得データセクションがあればtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoValidator validator = getPojoValidator(sheet.getCase("ロールあり"))
		
		expect:
		validator?.hasRole(null, null) == true
	}
	
	def "hasRoleは引数のクラスがMapでもtrueを返す"() {
		setup:
		Sheet sheet = TempActors.getBook().getSheet("HasRole")
		PojoValidator validator = getPojoValidator(sheet.getCase("ロールあり"))
		
		expect:
		validator?.hasRole(new HashMap<String, String>(), null) == true
	}
	
	def "例外のvalidate実行時に例外が発生しない場合はAssertionErrorが発生する"() {
		when:
		new FixtureBook().validate(Exception, {true})
		
		then:
		AssertionError e = thrown()
		println e
	}

	def getPojoValidator(Case testCase) {
		TempObjectValidator parent = new TempObjectValidator();
		parent.initialize(testCase);
		return parent.pojoValidator;
	}
}
