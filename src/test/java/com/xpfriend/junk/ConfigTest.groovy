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
package com.xpfriend.junk

import java.io.File;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class ConfigTest extends Specification {
	
	private File configFile = new File("target/test-classes/Config.properties")
	
	def setup() {
		Config.initialize()
	}
	
	def cleanup() {
		deleteConfigFile()
		Config.initialize()
	}

	private void prepareConfigFile() {
		def src = new File("src/test/resources/com/xpfriend/junk/test01/Config.properties")
		configFile << src.readBytes()
	}
	
	private void deleteConfigFile() {
		if(configFile.exists()) {
			assert configFile.delete()
		}
	}

	def "get（key,String defaultValue）は設定ファイルから設定値を取得する"() {
		when:
		prepareConfigFile()
		Config.initialize()
		
		then:
		Config.get("text01", "") == "aaa"
	}

	def "get（key,boolean defaultValue）は設定ファイルから設定値を取得する"() {
		when:
		prepareConfigFile()
		Config.initialize()
		
		then:
		Config.get("boolean01", false) == true
	}

	def "get（key,int defaultValue）は設定ファイルから設定値を取得する"() {
		when:
		prepareConfigFile()
		Config.initialize()
		
		then:
		Config.get("int01", 0) == 123
	}

	def "get（key,String defaultValue）は設定されていないキーが指定された場合デフォルト値を返す"() {
		expect:
		Config.get("text01", "xxx") == "xxx"
	}

	def "get（key,boolean defaultValue）は設定されていないキーが指定された場合デフォルト値を返す"() {
		expect:
		Config.get("boolean01", false) == false
	}

	def "get（key,int defaultValue）は設定されていないキーが指定された場合デフォルト値を返す"() {
		expect:
		Config.get("int01", 0) == 0
	}
	
	def "putで設定した値はgetで取得できる"() {
		when:
		Config.put("aaa", "xxx")
		
		then:
		Config.get("aaa", null) == "xxx"
	}

	def "putでnullを設定すると値がクリアされる"() {
		setup:
		Config.put("aaa", "xxx")
		Config.get("aaa", null) == "xxx"
		
		when:
		Config.put("aaa", null)
		
		then:
		Config.get("aaa", null) == null
	}
	
	def "get（key）は設定されていないキーが指定された場合にConfigExceptionをスローする"() {
		when:
		Config.get("xxxxxxxxx")
		
		then:
		thrown(ConfigException)
	}
}
