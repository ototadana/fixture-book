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

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class ConviTest extends Specification {

	def "toStringにnullを渡すと空文字列を返す"() {
		expect:
		Convi.toString(null) == ""
	}
	
	def "toStringに文字列を渡すとそのまま返す"() {
		setup:
		String s = "aaa"
		
		expect:
		Convi.toString(s) is s
	}
	
	def "toStringに日時を渡すとデフォルト書式で文字列変換する"() {
		setup:
		Date date = getCalendar(2012, 0, 2, 13, 12, 11).getTime()
		
		expect:
		Convi.toString(date) == "2012-01-02 13:12:11"
	}
	
	def "toStringにBigDecimal値を渡すと文字列変換する"() {
		expect:
		Convi.toString(new BigDecimal("0.00000001")) == "0.00000001"
	}
	
	def "toStringに数値を渡すと文字列変換する"() {
		expect:
		Convi.toString(123) == "123"
	}
	
	def "toStringにbooleanを渡すと文字列変換する"(boolean value, String expected) {
		expect:
		Convi.toString(value) == expected
		
		where:
		value | expected
		true  | "true"
		false | "false"
	}
	
	def "toStringにCalendarを渡すと文字列変換する"() {
		setup:
		Calendar cal = getCalendar(2012, 0, 2, 22, 12, 11)
		
		expect:
		Convi.toString(cal) == "2012-01-02 22:12:11"
	}
	
	def "toStringにXMLGregorianCalendarを渡すと文字列変換する"() {
		setup:
		GregorianCalendar cal = getCalendar(2013, 0, 2, 22, 12, 11)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)

		expect:
		Convi.toString(xmlCal) == "2013-01-02 22:12:11"
	}

	def "toStringにListを渡すとバーティカルバー区切りの文字列を返す"() {
		setup:
		List list = [1, 2, 3]
		
		expect:
		Convi.toString(list) == "1|2|3"
	}
	
	def "toStringに配列を渡すとバーティカルバー区切りの文字列を返す"() {
		setup:
		int[] array = [1, 2, 3]
		
		expect:
		Convi.toString(array) == "1|2|3"
	}
	
	def "toStringにその他のオブジェクトを渡すとtoStringして返す"() {
		expect:
		Convi.toString(new TestObject()) == "object..."
	}

	private static class TestObject {
		public String toString() {
			return "object..."
		}
	}
	
	private Calendar getCalendar(int year, int month, int date, int hourOfDay, int minute, int second) {
		return TestUtil.getCalendar(year, month, date, hourOfDay, minute, second)
	}
}
