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

import java.sql.Timestamp

import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class FormiTest extends Specification {
	
	private static final String TIMESTAMP_FORMAT_CONFIG_KEY = "Junk.Formi.defaultTimestampFormat";
	private TimeZone timeZone;
	
	def setup() {
		initalize(null)
		timeZone = TimeZone.getDefault()
		TimeZone.setDefault(TimeZone.getTimeZone(TimeZone.GMT_ID))
	}
	
	def cleanup() {
		TimeZone.setDefault(timeZone)
	}
	
	private void initalize(String timestampFormat) {
		Config.put(TIMESTAMP_FORMAT_CONFIG_KEY, timestampFormat)
		Formi.initialize();
	}

	def "getDefaultTimestampFormatは、Junk.Formi.defaultTimestampFormatで設定した日付書式を取得する"() {
		when:
		initalize("yyyy/MM/dd")
		
		then:
		Formi.getDefaultTimestampFormat() == "yyyy/MM/dd"
		
		cleanup:
		initalize(null)
	}
	
	def "getDefaultTimestampFormatは、Junk.Formi.defaultTimestampFormatの設定がないときyyyy-MM-dd HH:mm:ssを返す"() {
		expect:
		Formi.getDefaultTimestampFormat() == "yyyy-MM-dd HH:mm:ss"
	}
	
	def "formatTimestamp（Date）はgetDefaultTimestampFormatで取得できる書式で日時を文字列に変換する"() {
		setup:
		Date date = TestUtil.getCalendar(2012, 0, 1, 1, 1, 2).getTime()
		
		expect:
		Formi.formatTimestamp(date) == "2012-01-01 01:01:02"
	}

	def "formatTimestamp（Canendar）はgetDefaultTimestampFormatで取得できる書式で日時を文字列に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 1, 1, 2)
		
		expect:
		Formi.formatTimestamp(cal) == "2012-01-01 01:01:02"
	}

	def "formatTimestamp（XMLGregorianCalendar）はgetDefaultTimestampFormatで取得できる書式で日時を文字列に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 1, 1, 2)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)
		
		expect:
		Formi.formatTimestamp(xmlCal) == "2012-01-01 01:01:02"
	}
	
	def "format（Date,String）は指定された書式で日時を文字列変換する"() {
		setup:
		Date date = TestUtil.getCalendar(2012, 0, 1, 1, 1, 2).getTime()
		
		expect:
		Formi.format(date, "yyyyMMddHHmmss") == "20120101010102"
	}
	
	def "format（Calendar,String）は指定された書式で日時を文字列変換する"() {
		setup:
		Calendar cal1 = TestUtil.getCalendar(2012,  0, 1, 1, 1, 2)
		Calendar cal2 = TestUtil.getCalendar(2013, 11, 2, 2, 2, 3)

		expect:
		Formi.format(cal1, "yyyyMMddHHmmss") == "20120101010102"
		Formi.format(cal2, "yyyyMMddHHmmss") == "20131202020203"
	}

	def "format（XMLGregorianCalendar,String）は指定された書式で日時を文字列変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 1, 1, 2)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)
		
		expect:
		Formi.format(xmlCal, "yyyyMMddHHmmss") == "20120101010102"
	}
	
	def "format（Date,String,TimeZone）は指定された書式で日時を文字列変換する"() {
		setup:
		Date date = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2).getTime()
		TimeZone tz = TimeZone.getTimeZone("JST")
		
		expect:
		Formi.format(date, "yyyyMMddHHmmss", tz) == "20120101090102"
	}

	def "format（Calendar,String,TimeZone）は指定された書式で日時を文字列変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		TimeZone tz = TimeZone.getTimeZone("JST")
		
		expect:
		Formi.format(cal, "yyyyMMddHHmmss", tz) == "20120101090102"
	}
	
	def "format（XMLGregorianCalendar,String,TimeZone）は指定された書式で日時を文字列変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)
		TimeZone tz = TimeZone.getTimeZone("JST")
		
		expect:
		Formi.format(xmlCal, "yyyyMMddHHmmss", tz) == "20120101090102"
	}

	def "toTimestamp（Calendar）はTimestamp型に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		
		expect:
		Formi.toTimestamp(cal) == new Timestamp(cal.getTime().getTime())
	}

	def "toTimestamp（Date）はTimestamp型に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		Date date = cal.getTime()
		
		expect:
		Formi.toTimestamp(date) == new Timestamp(cal.getTime().getTime())
	}

	def "toTimestamp（XMLGregorianCalendar）はTimestamp型に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)
		
		expect:
		Formi.toTimestamp(xmlCal) == new Timestamp(cal.getTime().getTime())
	}
	
	def "toCalendar（Date）はCalendar型に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		Date date = cal.getTime()
		
		expect:
		Formi.toCalendar(date) == cal
	}

	def "toCalendar（XMLGregorianCalendar）はCalendar型に変換する"() {
		setup:
		Calendar cal = TestUtil.getCalendar(2012, 0, 1, 0, 1, 2)
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal)
		
		expect:
		Formi.toCalendar(xmlCal) == cal
	}
}
