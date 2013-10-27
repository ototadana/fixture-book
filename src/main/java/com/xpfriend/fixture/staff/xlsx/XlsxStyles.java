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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * .xlsx ファイル中の書式情報（xl/styles.xml）。
 * 
 * @author Ototadana
 */
class XlsxStyles {

	private List<String> formatString = new ArrayList<String>();
	private List<Integer> formatId = new ArrayList<Integer>();

	/**
	 * {@link XlsxStyles} を作成する。
	 * @param is .xlsx ファイル中の書式情報を格納したファイル（xl/styles.xml）の入力ストリーム。
	 */
	public XlsxStyles(InputStream is) throws IOException, SAXException,
			ParserConfigurationException {
		XlsxUtil.load(is, new XlsxStylesHandler());
	}

	/**
	 * 指定した番号の書式文字列を取得する。
	 * @param index 書式番号。
	 * @return 書式文字列。
	 */
	public String getFormatString(int index) {
		return formatString.get(index);
	}

	/**
	 * 指定した番号の書式IDを取得する。
	 * @param index 書式番号。
	 * @return 書式ID。
	 */
	public int getFormatId(int index) {
		return formatId.get(index);
	}

	private class XlsxStylesHandler extends DefaultHandler {
		
		private Map<String, String> numFmts = new HashMap<String, String>();
		private boolean cellXfsIsOpen = false;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("cellXfs".equals(qName)) {
				cellXfsIsOpen = true;
			} else if ("numFmt".equals(qName)) {
				numFmts.put(attributes.getValue("numFmtId"),
						attributes.getValue("formatCode"));
			} else if (cellXfsIsOpen && "xf".equals(qName)) {
				String numFmtId = attributes.getValue("numFmtId");
				int id = Integer.parseInt(numFmtId);
				String format = XlsxUtil.get(id);
				if (format == null) {
					format = numFmts.get(numFmtId);
				}
				formatString.add(format);
				formatId.add(id);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("cellXfs".equals(qName)) {
				cellXfsIsOpen = false;
			}
		}
	}
}
