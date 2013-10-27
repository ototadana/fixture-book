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

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class ClassesTest extends Specification {
	
	public static class BaseClass {
	}
	
	public static class ImplClass extends BaseClass {
	}

	public static abstract class ErrorClass {
	}
	
	def "Classes.properties が存在する場合、その内容で設定が行われる"() {
		setup:
		String text = BaseClass.name + "=" + ImplClass.name
		File file = createPropertiesFile(text)
		Classes.initialize()
		
		expect:
		Classes.get(BaseClass) is ImplClass
		
		cleanup:
		assert file.delete()
		Classes.initialize()
	}

	def "Classes.properties の内容が間違っている場合、初期化時に ConfigException が発生する"() {
		setup:
		String text = BaseClass.name + "XXX=" + ImplClass.name
		File file = createPropertiesFile(text)

		when:
		Classes.initialize()
		
		then:
		thrown(ConfigException)
		
		cleanup:
		assert file.delete()
		Classes.initialize()
	}

	private File createPropertiesFile(String text) {
		File file = new File("target/test-classes/Classes.properties")
		file.write(text)
		assert file.length() == text.length()
		return file
	}

	def "put で設定した実装クラスは get で取得できる"() {
		when:
		Classes.put(BaseClass, ImplClass)
		
		then:
		Classes.get(BaseClass) is ImplClass
		
		cleanup:
		Classes.initialize()
	}
	
	def "put で設定していない場合、get を呼び出すと引数のクラスをそのまま返す"() {
		setup:
		Classes.initialize()

		expect:
		Classes.get(BaseClass) is BaseClass
	}
	
	def "newInstance は put で設定したクラスをインスタンス化する"() {
		when:
		Classes.put(BaseClass, ImplClass)

		then:
		Classes.newInstance(BaseClass).getClass() is ImplClass
	}

	def "newInstance は put で設定されていない場合、引数のクラスをインスタンス化する"() {
		setup:
		Classes.initialize()

		expect:
		Classes.newInstance(BaseClass).getClass() is BaseClass
	}
		
	def "newInstance は 、インスタンス作成失敗時に ConfigException をスローする"() {
		setup:
		Classes.initialize()

		when:
		Classes.newInstance(ErrorClass)

		then:
		thrown(ConfigException)
	}
}
