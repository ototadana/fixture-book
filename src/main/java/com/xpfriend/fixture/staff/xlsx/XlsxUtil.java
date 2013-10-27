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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * .xlsx ファイルを解釈する際に使用するユーティリティメソッド集。
 * 
 * @author Ototadana
 */
final class XlsxUtil {

	private static class Format {
		String text;
		boolean date;
		public Format(String text, boolean date) {
			this.text = text;
			this.date = date;
		}
	}

	private static final long DAY_MILLISECONDS = 1000 * 60 * 60 * 24;
	private static final Format[] FORMATS = new Format[0x32];
	static {
        FORMATS[0x0] = new Format("General", false);
        FORMATS[0x1] = new Format("0", false);
        FORMATS[0x2] = new Format("0.00", false);
        FORMATS[0x3] = new Format("#,##0", false);
        FORMATS[0x4] = new Format("#,##0.00", false);
        FORMATS[0x5] = new Format("$#,##0_($#,##0)", false);
        FORMATS[0x6] = new Format("$#,##0_[Red]($#,##0)", false);
        FORMATS[0x7] = new Format("$#,##0.00_($#,##0.00)", false);
        FORMATS[0x8] = new Format("$#,##0.00_[Red]($#,##0.00)", false);
        FORMATS[0x9] = new Format("0%", false);
        FORMATS[0xa] = new Format("0.00%", false);
        FORMATS[0xb] = new Format("0.00E+00", false);
        FORMATS[0xc] = new Format("# ?/?", false);
        FORMATS[0xd] = new Format("# ??/??", false);
        FORMATS[0xe] = new Format("m/d/yy", true);
        FORMATS[0xf] = new Format("d-mmm-yy", true);
        FORMATS[0x10] = new Format("d-mmm", true);
        FORMATS[0x11] = new Format("mmm-yy", true);
        FORMATS[0x12] = new Format("h:mm AM/PM", true);
        FORMATS[0x13] = new Format("h:mm:ss AM/PM", true);
        FORMATS[0x14] = new Format("h:mm", true);
        FORMATS[0x15] = new Format("h:mm:ss", true);
        FORMATS[0x16] = new Format("m/d/yy h:mm", true);
        FORMATS[0x25] = new Format("#,##0_(#,##0)", false);
        FORMATS[0x26] = new Format("#,##0_[Red](#,##0)", false);
        FORMATS[0x27] = new Format("#,##0.00_(#,##0.00)", false);
        FORMATS[0x28] = new Format("#,##0.00_[Red](#,##0.00)", false);
        FORMATS[0x29] = new Format("_(*#,##0__(*(#,##0_(* \"-\"__(@_)", false);
        FORMATS[0x2a] = new Format("_($*#,##0__($*(#,##0_($* \"-\"__(@_)", false);
        FORMATS[0x2b] = new Format("_(*#,##0.00__(*(#,##0.00_(*\"-\"??__(@_)", false);
        FORMATS[0x2c] = new Format("_($*#,##0.00__($*(#,##0.00_($*\"-\"??__(@_)", false);
        FORMATS[0x2d] = new Format("mm:ss", true);
        FORMATS[0x2e] = new Format("[h]:mm:ss", true);
        FORMATS[0x2f] = new Format("mm:ss.0", true);
        FORMATS[0x30] = new Format("##0.0E+0", false);
        FORMATS[0x31] = new Format("@", false);
	}

	private static List<NumberFormat> numberFormatList = new ArrayList<NumberFormat>();
	
	/**
	 * 指定された書式番号の書式文字列を取得する。
	 * @param index 書式番号。
	 * @return 書式文字列。
	 */
	public static String get(int index) {
		if (index >= FORMATS.length || index < 0) {
			return null;
		}
		return FORMATS[index].text;
	}
	
	/**
	 * 指定された書式番号・書式文字列が日時書式かどうかを調べる。
	 * @param index 書式番号。
	 * @param text 書式文字列。
	 * @return 日時書式ならば true。
	 */
	public static boolean isDateFormat(int index, String text) {
		try {
			if(index < FORMATS.length && FORMATS[index].date) {
				return true;
			}
			
			if(text == null) {
				return false;
			}

			for(int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if(c == 'y' || c == 'm' || c == 'd' || c == 'h') {
					return true;
				}
			}
			return false;
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * 指定された書式文字列から {@link NumberFormat} を取得する。
	 * @param formatString　書式文字列。
	 * @return　{@link NumberFormat}。
	 */
	public static NumberFormat getFormatter(String formatString) {
		int cnt = countZero(formatString);
		if (numberFormatList.size() <= cnt) {
			for (int i = numberFormatList.size(); i < cnt; i++) {
				numberFormatList.add(createNumberFormat(i));
			}
			numberFormatList.add(createNumberFormat(cnt));
		}
		return numberFormatList.get(cnt);
	}

	private static NumberFormat createNumberFormat(int cnt) {
		StringBuilder sb = new StringBuilder();
		sb.append("0");
		if (cnt > 0) {
			sb.append(".");
			for (int i = 0; i < cnt; i++) {
				sb.append("0");
			}
		}
		return new DecimalFormat(new String(sb));
	}

	private static int countZero(String formatString) {
		if (formatString == null) {
			return 0;
		}
		int dotIndex = formatString.indexOf('.');
		if (dotIndex > -1) {
			int count = 0;
			for (int i = dotIndex + 1; i < formatString.length(); i++) {
				if (formatString.charAt(i) != '0') {
					return count;
				}
				count++;
			}
			return count;
		} else {
			return 0;
		}
	}

	/**
	 * 指定された数値を日時型データに変換する。
	 * @param date 時間を表す数値。
	 * @return 日時型データ。
	 */
	public static Date getJavaDate(double date) {
        int days = (int)Math.floor(date);
        int milliseconds = (int)((date - days) * DAY_MILLISECONDS + 0.5);
        Calendar calendar = new GregorianCalendar();
        calendar.set(1900, 0, days - 1, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, milliseconds);
        return calendar.getTime();
	}

	/**
	 * 指定されたXMLストリームの読み込みを行う。
	 * @param is XMLストリーム。
	 * @param handler XMLパーサー。
	 */
	public static void load(InputStream is, DefaultHandler handler)
			throws IOException, SAXException, ParserConfigurationException {
		InputSource sheetSource = new InputSource(is);
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		XMLReader sheetParser = saxParser.getXMLReader();
		sheetParser.setContentHandler(handler);
		sheetParser.parse(sheetSource);
	}
}
