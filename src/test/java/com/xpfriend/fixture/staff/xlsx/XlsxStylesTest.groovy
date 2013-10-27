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

import java.io.File;

import com.xpfriend.fixture.staff.xlsx.XlsxStyles;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class XlsxStylesTest extends Specification {

	def "getFormatIdは指定したインデックスの書式IDを取得できる"() {
		setup:
		InputStream stream = new FileInputStream(getStylesFile())
		XlsxStyles styles = new XlsxStyles(stream)
		
		expect:
		0 == styles.getFormatId(0)
		14 == styles.getFormatId(1)
		177 == styles.getFormatId(2)
		
		cleanup:
		stream.close()
	}
	
	def "getFormatStringは指定したインデックスの書式を取得できる"() {
		setup:
		InputStream stream = new FileInputStream(getStylesFile())
		XlsxStyles styles = new XlsxStyles(stream)
		
		expect:
		"General" == styles.getFormatString(0)
		"m/d/yy" == styles.getFormatString(1)
		"#,##0.0000_ " == styles.getFormatString(2)
		
		cleanup:
		stream.close()
	}
	
	private static File getStylesFile() {
		return XlsxTestUtil.getXMLFile("styles.xml")
	}
}
