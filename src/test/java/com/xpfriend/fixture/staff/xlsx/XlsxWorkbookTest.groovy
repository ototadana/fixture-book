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

import com.xpfriend.fixture.staff.xlsx.XlsxWorkbook;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class XlsxWorkbookTest extends Specification {

	def "getSheetNameMapはworkbookに格納されるシート名とファイル名のマップを取得する"() {
		setup:
		InputStream stream = new FileInputStream(getWorkbookFile())
		XlsxWorkbook workbook = new XlsxWorkbook(stream)
		
		expect:
		2 == workbook.getSheetNameMap().size()
		workbook.getSheetNameMap() == ["test1":"xl/worksheets/sheet1.xml", "テスト2":"xl/worksheets/sheet2.xml"]
		
		cleanup:
		stream.close()
	}
	private static File getWorkbookFile() {
		return XlsxTestUtil.getXMLFile("workbook.xml")
	}
}
