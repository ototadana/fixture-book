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

import com.xpfriend.junk.Loggi;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class FixtureBookPathTest extends Specification {

	def cleanup() {
		Loggi.debugEnabled = false;
	}
	
	def "明示的にパス指定をしなくても命名規約によりFixtureBookのパスを判断し読み込みができること"() {
		expect:
		"ABC" == new FixtureBook().getObject(FixtureBookPathTestData).text;
	}
	
	@FixtureBookPath("src/test/java/com/xpfriend/fixture/FixtureBookPathTest_02.xlsx")
	def "テストメソッドでFixtureBookのパスを上書きできること"() {
		expect:
		"DEF" == new FixtureBook().getObject(FixtureBookPathTestData).text;
	}
	
	public static class FixtureBookPathTestData {
		private String text;
		public String getText() {return text;}
		public void setText(String value) {text = value;}
	}
}
