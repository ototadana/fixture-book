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
package com.xpfriend.fixture.staff.xlsx

import spock.lang.Specification

import com.xpfriend.fixture.FixtureBook
import com.xpfriend.fixture.staff.Book
import com.xpfriend.junk.Formi;
import com.xpfriend.junk.Loggi

/**
 * @author Ototadana
 *
 */
class XlsxWorksheetHandlerTest extends Specification {

	def setup() {
		Book.debugEnabled = true
		Loggi.debugEnabled = true
	}
	
	def cleanup() {
		Book.debugEnabled = false
		Loggi.debugEnabled = false
	}
	
	def "Boolean項目が読み込めること"() {
		expect:
		FixtureBook.expect({List list -> 
			assert list.size() == 4
			assert list[0]["bool1"] == true
			assert list[1]["bool1"] == false
			assert list[2]["bool1"] == false
			assert list[3]["bool1"] == true
		}, Data)
	}
	
	def "数値項目が読み込めること"() {
		expect:
		FixtureBook.expect({List list ->
			assert list.size() == 4
			assert list[0]["decimal1"] == 1000.00
			assert list[1]["decimal1"] == 1000.01
			assert list[2]["decimal1"] == 1000.02
			assert list[3]["decimal1"] == 1000.03
		}, Data)
	}
	
	def "日時項目が読み込めること"() {
		expect:
		FixtureBook.expect({List list ->
			assert list.size() == 6
			assert list[0]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:01");
			assert list[1]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:02");
			assert list[2]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:03");
			assert list[3]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:04");
			assert list[4]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:05");
			assert list[5]["dateTime1"] == Date.parse("yyyy/MM/dd HH:mm:ss", "2013/12/11 00:01:06");
		}, Data)
	}
	
	public static class Data {
		private boolean bool1;
		private BigDecimal decimal1;
		private Date dateTime1;
		
		public boolean isBool1() {
			return bool1;
		}
		public void setBool1(boolean bool1) {
			this.bool1 = bool1;
		}
		public BigDecimal getDecimal1() {
			return decimal1;
		}
		public void setDecimal1(BigDecimal decimal1) {
			this.decimal1 = decimal1;
		}
		public Date getDateTime1() {
			return dateTime1;
		}
		public void setDateTime1(Date dateTime1) {
			this.dateTime1 = dateTime1;
		}
	}

}
