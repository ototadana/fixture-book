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
class ConfigExceptionTest extends Specification {

	private static ResiTest resiTest = new ResiTest();
	
	def setupSpec() {
		resiTest.setupSpec()
	}
	
	def cleanupSpec() {
		resiTest.cleanupSpec()
	}
	
	def setup() {
		resiTest.setup()
	}
	
	def cleanup() {
		resiTest.cleanup()
	}
	
	def "setLocale で設定したロケールは getLocale で取得できる"(Locale locale) {
		setup:
		ConfigException exception = new ConfigException("")
		
		when:
		exception.setLocale(locale)
		
		then:
		exception.getLocale() is locale
		
		where:
		locale << [ null, Locale.CANADA, Locale.CHINA ]
	}
	
	def "コンストラクタ（message, args）で設定したメッセージは getLocalizedMessage で取得できる"(String message) {
		when:
		ConfigException exception = new ConfigException(message)
		
		then:
		exception.getLocalizedMessage() == message
		
		where:
		message << ["message1", "message2"]
	}
	
	def "コンストラクタ（message, args）で設定したメッセージが null の場合、getLocalizedMessage も null を返す"() {
		when:
		ConfigException exception = new ConfigException((String)null)

		then:
		exception.getLocalizedMessage() == null
	}
	
	def "コンストラクタ（exception）で設定した例外は getCause で取得できる"(Exception cause) {
		when:
		ConfigException exception = new ConfigException(cause)
		
		then:
		exception.getCause() is cause
		
		where:
		cause << [new Exception(), new RuntimeException(), null]
	}

	def "コンストラクタ（exception, message, args）で設定したメッセージは getLocalizedMessage で取得できる"(String message) {
		when:
		ConfigException exception = new ConfigException(new Exception(), message)
		
		then:
		exception.getLocalizedMessage() == message
		
		where:
		message << ["message1", "message2"]
	}
	
	def "コンストラクタ（exception, message, args）で設定したメッセージが null の場合、getLocalizedMessage も null を返す"() {
		when:
		ConfigException exception = new ConfigException(new Exception(), null)

		then:
		exception.getLocalizedMessage() == null
	}
	
	def "コンストラクタ（exception, message, args）で設定した例外は getCause で取得できる"(Exception cause) {
		when:
		ConfigException exception = new ConfigException(cause, null)
		
		then:
		exception.getCause() is cause
		
		where:
		cause << [new Exception(), new RuntimeException(), null]
	}
	
	def "コンストラクタ（message, args）で設定したリソースキーは getLocalizedMessage でメッセージに変換される"(String key, String result) {
		when:
		Resi.add(getClass().getPackage().getName() + ".test01")
		ConfigException exception = new ConfigException(key)
		
		then:
		exception.getLocalizedMessage() == result
		
		where:
		key   | result
		"aaa" | "あああ"
		"bbb" | "いいい"
	}

	def "コンストラクタ（exception, message, args）で設定したリソースキーは getLocalizedMessage でメッセージに変換される"(String key, String result) {
		when:
		Resi.add(getClass().getPackage().getName() + ".test01")
		ConfigException exception = new ConfigException(new Exception(), key)
		
		then:
		exception.getLocalizedMessage() == result
		
		where:
		key   | result
		"aaa" | "あああ"
		"bbb" | "いいい"
	}
	
	def "setLocaleで設定されたロケールで、getLocalizedMessage はメッセージに変換する"(Locale locale, String result) {
		setup:
		Resi.add(getClass().getPackage().getName() + ".test01")
		
		when:
		ConfigException exception = new ConfigException("aaa")
		exception.setLocale(locale)
		
		then:
		exception.getLocalizedMessage() == result
		
		where:
		locale          | result
		Locale.ENGLISH  | "AAA"
		Locale.JAPANESE | "あああ"
	}
	
	def "コンストラクタ（message, args）で設定したメッセージ引数を使ってgetLocalizedMessage はメッセージに変換する"(String args, String result) {
		setup:
		Resi.add(getClass().getPackage().getName() + ".test01")
		
		when:
		ConfigException exception = new ConfigException("ddd", args)
		
		then:
		exception.getLocalizedMessage() == result
		
		where:
		args   | result
		"X"    | "aXc"
		"Y"    | "aYc"
	}

	def "コンストラクタ（exception, message, args）で設定したメッセージ引数を使ってgetLocalizedMessage はメッセージに変換する"(String args, String result) {
		setup:
		Resi.add(getClass().getPackage().getName() + ".test01")
		
		when:
		ConfigException exception = new ConfigException(new Exception(), "ddd", args)
		
		then:
		exception.getLocalizedMessage() == result
		
		where:
		args   | result
		"X"    | "aXc"
		"Y"    | "aYc"
	}
}
