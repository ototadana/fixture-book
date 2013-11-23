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

	@Fixture(["stringを検証できる"])
	def stringを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({"aa"});
	}

	@Fixture(["stringを検証できる"])
	def stringを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({"bb"})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["intを検証できる"])
	def intを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({1})
	}

	@Fixture(["intを検証できる"])
	def intを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({2})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["nullを検証できる"])
	def nullを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({ String a = null; return a})
	}

	@Fixture(["nullを検証できる"])
	def nullを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({"a"})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["stringの配列を検証できる"])
	def stringの配列を検証できる_正常() {
		expect:
		FixtureBook.expectReturn({["aa", "b"] as String[]})
	}

	@Fixture(["stringの配列を検証できる"])
	def stringの配列を検証できる_エラー() {
		when:
		FixtureBook.expectReturn({"aa"})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["intの配列を検証できる"])
	def intの配列を検証できる_正常() {
		expect:
		FixtureBook.expectReturn({[1, 2].toArray(new int[2])})
	}

	@Fixture(["intの配列を検証できる"])
	def intの配列を検証できる_エラー() {
		when:
		FixtureBook.expectReturn({[1, 3].toArray(new int[2])})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["nullと空文字列の配列を検証できる"])
	def nullと空文字列の配列を検証できる_正常() {
		expect:
		FixtureBook.expectReturn({[null, ""] as String[]})
	}

	@Fixture(["nullと空文字列の配列を検証できる"])
	def nullと空文字列の配列を検証できる_エラー() {
		when:
		FixtureBook.expectReturn({["", ""] as String[]})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["stringの配列を検証できる"])
	def stringのリストを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({["aa", "b"]})
	}

	@Fixture(["stringの配列を検証できる"])
	def stringのリストを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({["aa", "bb"]})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["intの配列を検証できる"])
	def intのリストを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({[1, 2]})
	}

	@Fixture(["intの配列を検証できる"])
	def intのリストを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({[1, 3]})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["nullと空文字列の配列を検証できる"])
	def nullと空文字列のリストを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({[null, ""]})
	}

	@Fixture(["nullと空文字列の配列を検証できる"])
	def nullと空文字列のリストを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({["", ""]})

		then:
		AssertionError e = thrown()
		println e
	}
}
