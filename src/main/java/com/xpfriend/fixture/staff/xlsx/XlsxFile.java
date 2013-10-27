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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;

/**
 * .xlsx ファイル。
 * 
 * @author Ototadana
 */
class XlsxFile {

	private File file;
	private ZipFile zipFile;
	private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	private XlsxCellParser xlsxCellParser;
	private XlsxWorkbook workbook;
	private XlsxStyles styles;
	private XlsxSharedStrings sharedStrings;
	private List<String> worksheetNames;

	/**
	 * {@link XlsxFile} を作成する。
	 * @param file 読み込むファイル。
	 * @param xlsxCellParser ワークシート処理。
	 */
	public XlsxFile(File file, XlsxCellParser xlsxCellParser)
			throws IOException, SAXException, ParserConfigurationException {
		this.file = file;
		this.zipFile = new ZipFile(file);
		this.xlsxCellParser = xlsxCellParser;
		readWorkbookInformation();
	}

	private InputStream getInputStream(String name) throws IOException {
		ZipEntry entry = zipFile.getEntry(name);
		if (entry != null) {
			return zipFile.getInputStream(entry);
		} else {
			return null;
		}
	}

	private void readWorkbookInformation() throws IOException, SAXException,
			ParserConfigurationException {
		try {
			workbook = new XlsxWorkbook(getInputStream("xl/workbook.xml"));
			styles = new XlsxStyles(getInputStream("xl/styles.xml"));
			sharedStrings = new XlsxSharedStrings(getInputStream("xl/sharedStrings.xml"));
			init();
		} finally {
			close();
		}
	}

	private void init() {
		Map<String, String> sheetNameMap = workbook.getSheetNameMap();
		worksheetNames = new ArrayList<String>(sheetNameMap.keySet());
	}

	/**
	 * 指定された名前のワークシートを読み込む。
	 * @param sheetName ワークシート名。
	 */
	public void read(String sheetName) throws SAXException,
			ParserConfigurationException, IOException {
		try {
			XMLReader reader = createReader();
			String fileName = workbook.getSheetNameMap().get(sheetName);
			if (fileName == null) {
				throw new ConfigException("M_Fixture_XlsxFile_Read", sheetName);
			}
			parse(reader, getInputStream(fileName), sheetName);
		} finally {
			close();
		}
	}

	private void parse(XMLReader reader, InputStream sheet, String sheetName)
			throws IOException, SAXException {
		InputSource sheetSource = new InputSource(sheet);
		reader.parse(sheetSource);
	}

	private XMLReader createReader() throws SAXException,
			ParserConfigurationException, IOException {
		zipFile = new ZipFile(file);
		SAXParser saxParser = saxParserFactory.newSAXParser();
		XMLReader reader = saxParser.getXMLReader();
		XlsxWorksheetHandler handler = new XlsxWorksheetHandler(styles,
				sharedStrings, xlsxCellParser);
		reader.setContentHandler(handler);
		return reader;
	}

	public List<String> getWorksheetNames() {
		return worksheetNames;
	}

	private void close() {
		try {
			if (zipFile != null) {
				zipFile.close();
			}
		} catch (IOException e) {
			ExceptionHandler.ignore(e);
		} finally {
			zipFile = null;
		}
	}
}
