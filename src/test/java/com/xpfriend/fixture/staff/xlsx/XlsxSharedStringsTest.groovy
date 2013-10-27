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

import com.xpfriend.fixture.staff.xlsx.XlsxSharedStrings;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class XlsxSharedStringsTest extends Specification {

	def "コンストラクタは指定されたstreamから共有文字列を読み込む"() {
		setup:
		InputStream stream = new FileInputStream(getSharedStringsFile())
		XlsxSharedStrings strings = new XlsxSharedStrings(stream)
		
		expect:
		strings.size() == 6
		"aiueo" == strings.get(0)
		"A1: aiueo" == strings.get(1)
		"B1: あいうえお" == strings.get(2)
		"A2: kakiku" == strings.get(3)
		"B2: かきく" == strings.get(4)
		"B3:今日は" == strings.get(5)
		
		cleanup:
		stream.close()
	}
	
	def "コンストラクタにnullが指定されると何も読み込まない"() {
		setup:
		XlsxSharedStrings strings = new XlsxSharedStrings(null)

		expect:
		strings.size() == 0
	}
	
	private static File getSharedStringsFile() {
		return XlsxTestUtil.getXMLFile("sharedStrings.xml")
	}
}
