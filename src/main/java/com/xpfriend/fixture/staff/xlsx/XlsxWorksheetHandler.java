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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.xpfriend.junk.Convi;
import com.xpfriend.junk.Loggi;

/**
 * .xlsx 中のワークシート（xl/worksheets/sheet*.xml）処理。
 * 
 * @author Ototadana
 */
class XlsxWorksheetHandler extends DefaultHandler {

	enum DataType {
		BOOLEAN, SST_STRING, NUMBER,
	}

	private XlsxStyles styles;
	private XlsxSharedStrings sharedStrings;
	private XlsxCellParser cellParser;
	private StringBuffer value = new StringBuffer();

	private boolean vIsOpen;
	private DataType dataType;

	private String formatString;
	private int formatIndex;
	private String cellRef;

	/**
	 * {@link XlsxWorksheetHandler} を作成する。
	 * @param styles　書式情報。
	 * @param strings 共有文字列情報。
	 * @param cellParser セル処理。
	 */
	public XlsxWorksheetHandler(XlsxStyles styles, XlsxSharedStrings strings,
			XlsxCellParser cellParser) {
		this.styles = styles;
		this.sharedStrings = strings;
		this.cellParser = cellParser;
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		if (isTextTag(name)) {
			vIsOpen = true;
			value.setLength(0);
		} else if ("c".equals(name)) {
			cellRef = attributes.getValue("r");

			String cellType = attributes.getValue("t");
			if ("b".equals(cellType)) {
				dataType = DataType.BOOLEAN;
			} else if ("s".equals(cellType)) {
				dataType = DataType.SST_STRING;
			} else {
				dataType = DataType.NUMBER;
			}

			String cellStyle = attributes.getValue("s");
			if (cellStyle != null) {
				int index = Integer.parseInt(cellStyle);
				formatString = styles.getFormatString(index);
				formatIndex = styles.getFormatId(index);
			} else {
				formatString = null;
				formatIndex = -1;
			}
		}
	}

	private boolean isTextTag(String name) {
		return "v".equals(name) || "t".equals(name);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (isTextTag(name)) {
			vIsOpen = false;
			output(cellRef, getValue());
		}
	}

	private void output(String cellReference, String formattedValue) {
		if (cellParser.isDebugEnabled()) {
			Loggi.debug("[" + cellReference + "]" + formattedValue);
		}
		
		cellParser.parse(toRow(cellReference), toColumn(cellReference), formattedValue);
	}

	private int toRow(String ref) {
		StringBuilder sb = new StringBuilder(ref.length() - 1);
		for(int i = 0; i < ref.length(); i++) {
			char c = ref.charAt(i);
			if(c >= '0' && c <= '9') {
				sb.append(c);
			}
		}
		return Integer.parseInt(sb.toString());
	}
	
	private int toColumn(String ref) {
		int position = 0;
		int value = 0;
		for(int i = ref.length() - 1; i >= 0; i--) {
			char c = ref.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				int shift = (int)Math.pow(26, position);
                value += (c - 'A' + 1) * shift;
				position++;
			}
		}
		return value - 1;
	}

	private String getValue() {
		if (dataType == DataType.BOOLEAN) {
			return (value.charAt(0) == '0') ? "false" : "true";
		} else if (dataType == DataType.SST_STRING) {
			int index = Integer.parseInt(value.toString());
			return sharedStrings.get(index);
		} else {
			String s = value.toString();
			if (formatString != null) {
				try {
					return format(Double.parseDouble(s));
				} catch (NumberFormatException e) {
					return s;
				}
			} else {
				return s;
			}
		}
	}

	private String format(double value) {
		if (XlsxUtil.isDateFormat(formatIndex, formatString)) {
			return Convi.toString(XlsxUtil.getJavaDate(value));
		} else {
			return XlsxUtil.getFormatter(formatString).format(value);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (vIsOpen) {
			value.append(ch, start, length);
		}
	}
}
