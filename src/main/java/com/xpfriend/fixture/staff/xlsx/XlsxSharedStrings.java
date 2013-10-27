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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * .xlsx ファイル中の共有文字列情報（xl/sharedStrings.xml）。
 * 
 * @author Ototadana
 */
class XlsxSharedStrings {

	private List<String> stringsTable;

	/**
	 * {@link XlsxSharedStrings} を作成する。
	 * @param is　共有文字列情報を格納したファイル（xl/sharedStrings.xml）の入力ストリーム。
	 */
	public XlsxSharedStrings(InputStream is) throws IOException, SAXException,
			ParserConfigurationException {
		if (is == null) {
			stringsTable = Collections.emptyList();
		} else {
			XlsxUtil.load(is, new XlsxSharedStringsTableHandler());
		}
	}

	/**
	 * 指定された文字列番号の文字列を取得する。
	 * @param index 文字列番号。
	 * @return 文字列。
	 */
	public String get(int index) {
		return stringsTable.get(index);
	}

	int size() {
		return stringsTable.size();
	}

	private class XlsxSharedStringsTableHandler extends DefaultHandler {

		private StringBuffer value;
		private boolean rPhIsOpen;
		private boolean tIsOpen;

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {
			if ("sst".equals(name)) {
				String uniqueCount = attributes.getValue("uniqueCount");
				int capacity = 16;
				if (uniqueCount != null) {
					capacity = Integer.parseInt(uniqueCount);
				}
				stringsTable = new ArrayList<String>(capacity);
				value = new StringBuffer();
			} else if ("si".equals(name)) {
				value.setLength(0);
			} else if ("t".equals(name)) {
				tIsOpen = true;
			} else if ("rPh".equals(name)) {
				rPhIsOpen = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if ("si".equals(name)) {
				stringsTable.add(value.toString());
			} else if ("t".equals(name)) {
				tIsOpen = false;
			} else if ("rPh".equals(name)) {
				rPhIsOpen = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (tIsOpen && !rPhIsOpen) {
				value.append(ch, start, length);
			}
		}
	}
}
