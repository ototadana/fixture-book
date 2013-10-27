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

import com.xpfriend.junk.ConfigException

/**
 * DatabaseConnection のテスト。
 * 
 * @author Ototadana
 */
class DatabaseConnectionTest extends Specification {

	def "間違った名前を指定するとConfigExceptionが発生する"() {
		setup:
		DatabaseConnection connection = new DatabaseConnection()
		
		when:
		connection.use("xxx")
		
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Temp_DatabaseConnection_NoSuchName"
	}
}
