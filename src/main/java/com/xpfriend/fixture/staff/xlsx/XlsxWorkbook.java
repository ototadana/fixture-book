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
package com.xpfriend.fixture.staff.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * .xlsx ファイル中のワークブック情報（xl/wookbook.xml）。
 * 
 * @author Ototadana
 */
class XlsxWorkbook {

	private Map<String, String> sheetNameMap = new LinkedHashMap<String, String>();

	/**
	 * {@link XlsxWorkbook} を作成する。
	 * @param is .xlsx ファイル中のワークブック情報を格納したファイル（xl/wookbook.xml）の入力ストリーム。
	 */
	public XlsxWorkbook(InputStream is) throws IOException, SAXException,
			ParserConfigurationException {
		XlsxUtil.load(is, new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				if ("sheet".equals(qName)) {
					String name = attributes.getValue("name");
					String rid = attributes.getValue("r:id");
					String sheetName = "xl/worksheets/sheet" + rid.substring(3)
							+ ".xml";
					sheetNameMap.put(name, sheetName);
				}
			}
		});
	}

	/**
	 * シート名のマップを取得する。
	 * @return シート名のマップ。
	 */
	public Map<String, String> getSheetNameMap() {
		return sheetNameMap;
	}
}
