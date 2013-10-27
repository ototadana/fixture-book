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

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class ResiTest extends Specification {
	
	private static List<String> resourceNames
	private Locale temp
	
	def setupSpec() {
		resourceNames = new ArrayList(Resi.provider.resourceNames)
	}
	
	def cleanupSpec() {
		Resi.provider.resourceNames.addAll(resourceNames)
	}
	
	def setup() {
		temp = Locale.getDefault()
		Locale.setDefault(Locale.JAPANESE)
		Resi.initialize()
	}
	
	def cleanup() {
		Locale.setDefault(temp)
		Resi.initialize()
	}

	def "get（key）は、指定したキーに対応する文字列を add で追加したパッケージの配下にある Resources という名前のリソースから取得する"(key, result) {
		when:
		Resi.add(getClass().getPackage().getName() + ".test01");
		
		then:
		Resi.get(key) == result

		where:
		key   | result
		"aaa" | "あああ"
		"bbb" | "いいい"
	}
	
	def "get（key）は、より後で add したリソースの内容を優先する"(key, result) {
		when:
		Resi.add(getClass().getPackage().getName() + ".test01");
		Resi.add(getClass().getPackage().getName() + ".test02");
		
		then:
		Resi.get(key) == result

		where:
		key   | result
		"aaa" | "ううう"
		"bbb" | "いいい"
		"ccc" | "えええ"
	}
	
	def "get（key）は、一度設定されたものでも、再度後に add したリソースの内容を優先する"(key, result) {
		when:
		Resi.add(getClass().getPackage().getName() + ".test02");
		Resi.add(getClass().getPackage().getName() + ".test01");
		Resi.add(getClass().getPackage().getName() + ".test02");
		
		then:
		Resi.get(key) == result

		where:
		key   | result
		"aaa" | "ううう"
		"bbb" | "いいい"
		"ccc" | "えええ"
	}
	
	def "get（key）は、指定したキーに対応する文字列が登録されていない場合キーをそのまま返す"() {
		expect:
		Resi.get("xxx") == "xxx"
	}

	def "get（key, defaultValue）は、指定したキーに対応する文字列が登録されていない場合 defaultValue を返す"() {
		expect:
		Resi.get("xxx", "yyy") == "yyy"
	}
	
	def "get（locale, key, defaultValue）は、指定したロケールに対応するリソースから取得する"(locale, result) {
		setup:
		Resi.add(getClass().getPackage().getName() + ".test01");

		expect:
		Resi.get(locale, "aaa", "") == result
		
		where:
		locale          | result
		Locale.ENGLISH  | "AAA"
		Locale.JAPANESE | "あああ"
	}
}
