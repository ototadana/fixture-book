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
package com.xpfriend.fixture

import spock.lang.Specification;

import com.xpfriend.junk.ConfigException;

/**
 * @author Ototadana
 *
 */
class NoTestTargetClassTest extends Specification {

	def 引数なしのexpectは類推したテスト対象クラスが存在しない場合は例外をスローする() {
		when:
		FixtureBook.expect()

		then:
		ConfigException e = thrown()
		println e.getLocalizedMessage()
		e.getMessage() == "M_Fixture_FixtureBook_GetDefaultTargetClass"
	}
}
