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

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.cast.temp.data.Data;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class EmptyValidateTest extends Specification {

	private FixtureBook fixtureBook = new FixtureBook();

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのListを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({List list ->
			assert list.size() == 0
			return list
		}, Data)
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのListを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({List list ->
			list.add(new Data())
			return list
		}, Data)

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのListを検証できる_正常() {
		setup:
		List<Data> list = fixtureBook.getList(Data)

		expect:
		fixtureBook.validate(list)
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのListを検証できる_エラー() {
		setup:
		List<Data> list = fixtureBook.getList(Data)
		list.add(new Data())

		when:
		fixtureBook.validate(list)

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロの配列を検証できる_正常() {
		expect:
		FixtureBook.expectReturn({Data[] array ->
			assert array.length == 0
			return array
		})
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロの配列を検証できる_エラー() {
		when:
		FixtureBook.expectReturn({[new Data()]})

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロの配列を検証できる_正常() {
		setup:
		Data[] array = fixtureBook.getArray(Data)

		expect:
		array.length == 0
		fixtureBook.validate(array)
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロの配列を検証できる_エラー() {
		when:
		fixtureBook.validate([new Data()])

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのMapListを検証できる_正常() {
		expect:
		FixtureBook.expectReturn({List list ->
			assert list.size() == 0
			return list
		}, Map)
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのMapListを検証できる_エラー() {
		when:
		FixtureBook.expectReturn({List list ->
			list.add(new HashMap<String, Object>())
			return list
		}, Map)

		then:
		AssertionError e = thrown()
		println e
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのMapListを検証できる_正常() {
		setup:
		List list = fixtureBook.getList(Map)

		expect:
		assert list.size() == 0
		fixtureBook.validate(list)
	}

	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのMapListを検証できる_エラー() {
		setup:
		List list = fixtureBook.getList(Map)
		list.add(new HashMap())

		when:
		fixtureBook.validate(list)

		then:
		AssertionError e = thrown()
		println e
	}
	
	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのMap配列を検証できる_正常() {
		expect:
		FixtureBook.expectReturn({Map[] array ->
			assert array.length == 0
			return array
		})
	}
	
	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def expectReturnで要素数ゼロのMap配列を検証できる_エラー() {
		when:
		FixtureBook.expectReturn({[new HashMap()]})

		then: 
		AssertionError e = thrown()
		println e
	}
	
	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのMap配列を検証できる_正常() {
		setup:
		Map[] array = fixtureBook.getArray(Map)

		expect:
		assert array.length == 0
		fixtureBook.validate(array)
	}
	
	@Fixture(["要素数ゼロのコレクションを検証できる"])
	def 要素数ゼロのMap配列を検証できる_エラー() {
		when:
		fixtureBook.validate([new HashMap()])
	
		then:
		AssertionError e = thrown()
		println e
	}
}
