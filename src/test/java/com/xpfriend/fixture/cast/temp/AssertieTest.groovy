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

/**
 * @author Ototadana
 *
 */
class AssertieTest extends Specification {
	
	def "assertEqualsはオブジェクトが等しいかどうかを調べる"(expected, actual, invalid) {
		expect: "エラーにならないケース"
		Assertie.assertEquals(expected, actual, "XXXX")
		
		when: "エラーになるケース"
		Assertie.assertEquals(expected, invalid, "xxxx")
		then:
		AssertionError e = thrown()
		println e
		e.getMessage().indexOf("xxxx") > -1
		
		where:
		expected       | actual          | invalid
		"a"            | "a"             | "b"
		new Long(1)    | new Long(1)     | new Long(2)
	}
	
	def "assertEqualsIntはint値が等しいかどうかを調べる"() {
		expect: "エラーにならないケース"
		Assertie.assertEqualsInt(1, 1, "XXXX")
		
		when: "エラーになるケース"
		Assertie.assertEqualsInt(1, 2, "xxxx")
		then:
		AssertionError e = thrown()
		println e
		e.getMessage().indexOf("xxxx") > -1
	}

}
