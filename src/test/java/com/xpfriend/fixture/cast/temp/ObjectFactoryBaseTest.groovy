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

import com.xpfriend.fixture.FixtureBook
import com.xpfriend.fixture.cast.temp.data.Data;
import com.xpfriend.junk.ConfigException;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class ObjectFactoryBaseTest extends Specification {

	def "NULLおよびEMPTYの指定ができること"() {
		when:
		List<Data> list = new FixtureBook().getList(Data)
		
		then:
		list[0].text1 == null
		list[1].text1 == null
		list[2].text1 == ""
	}
	
	def "セル値を変換できない場合は例外が発生すること"() {
		when:
		new FixtureBook().getObject(Data)

		then:
		ConfigException e = thrown()
		e.printStackTrace()
		e.getMessage() == "M_Fixture_Temp_ObjectFactory_ConvertError"
	}
}
