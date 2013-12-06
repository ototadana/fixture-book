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
import java.util.List;
import java.util.Map;

import com.xpfriend.fixture.staff.Author;
import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;

/**
 * .xlsx 形式ファイルからブックを作成する {@link Author}。
 * 
 * @author Ototadana
 */
public class XlsxAuthor extends Author {
	
	private XlsxFile xlsxFile;
	private Parser parser;

	@Override
	public void write(Book book) {
		try {
			parser = new Parser(Book.isDebugEnabled());
			xlsxFile = new XlsxFile(new File(book.getFilePath()), parser);
			for(String sheetName : xlsxFile.getWorksheetNames()) {
				createSheet(book, sheetName);
			}
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}

	@Override
	public void write(Sheet sheet) {
		try {
			parser.changeSheet(sheet);
			xlsxFile.read(sheet.getName());
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}


	private static class Parser extends XlsxCellParser {

		private Sheet sheet;
		private Case testCase;
		private Section section;
		private Table table;
		private List<Column> columns;
		private int tableRowIndex;
		private int columnNameRowIndex;
		private int columnValueRowIndex;
		private Map<String, String> values;
		
		protected Parser(boolean debugEnabled) {
			super(debugEnabled);
		}
		
		@Override
		protected void parse(int rowIndex, int columnIndex, String value) {
			if(columnIndex == 0) {
				return;
			}
			
			if(columnIndex == 1) {
				changeSection(value);
				return;
			}
			
			if(columnIndex == 2) {
				if(value.startsWith("[") || isTestCaseSection()) {
					changeCase(getCaseName(value));
				} else if(isFlag(value) && columns != null) {
					updateTable(rowIndex, columnIndex, value);
				} else {
					changeTable(value, rowIndex);
				}
				return;
			}
			
			updateTable(rowIndex, columnIndex, value);
		}

		private boolean isTestCaseSection() {
			return section != null && section.getNumber() == 1 && !section.getName().startsWith("1");
		}

		private void updateTable(int rowIndex, int columnIndex, String value) {
			if(table == null) {
				return;
			}
			
			if(rowIndex == tableRowIndex) {
				return;
			}
			
			if(columns == null) {
				columnNameRowIndex = rowIndex;
				columnValueRowIndex = -1;
				columns = table.getColumns();
			}

			if(columnNameRowIndex == rowIndex) {
				addColumnName(columnIndex, value);
				return;
			}

			if(columnValueRowIndex != rowIndex) {
				columnValueRowIndex = rowIndex;
				boolean deleted = columnIndex == 2 && isDeleteFlag(value);
				Row row = table.addRow(rowIndex, deleted);
				values = row.getValues();
			}
			
			String columnName = getColumnName(columnIndex);
			if(columnName == null) {
				return;
			}

			values.put(columnName, value);
		}

		private boolean isFlag(String value) {
			return value.length() == 1;
		}

		private boolean isDeleteFlag(String value) {
			return "D".equals(value);
		}

		private String getColumnName(int columnIndex) {
			if(columnIndex >= columns.size()) {
				return null;
			}
			Column column = columns.get(columnIndex);
			if(column == null) {
				return null;
			}
			return column.getName();
		}

		private void addColumnName(int columnIndex, String value) {
			for(int i = columns.size(); i < columnIndex + 1; i++) {
				columns.add(null);
			}
			columns.set(columnIndex, createColumn(value));
		}
		
		private static Column createColumn(String text) {
			int colonIndex = text.indexOf(':');
			if(colonIndex == -1) {
				return new Column(text, null, null);
			}
			
			String value = text.substring(0, colonIndex);
			int bracketIndex = text.indexOf('[', colonIndex);
			if(bracketIndex > -1) {
				return new Column(value, null, text.substring(colonIndex + 1, bracketIndex));
			}
			
			int ltIndex = text.indexOf('<', colonIndex);
			if(ltIndex == -1) {
				return new Column(value, text.substring(colonIndex + 1), null);
			}
			
			String type = text.substring(colonIndex + 1, ltIndex);
			int gtIndex = text.indexOf('>', ltIndex);
			if(gtIndex == -1) {
				return new Column(value, type, text.substring(ltIndex + 1));
			} else {
				return new Column(value, type, text.substring(ltIndex + 1, gtIndex));
			}
		}

		private String getCaseName(String value) {
			int startIndex = value.startsWith("[")? 1 : 0;
			int index = value.indexOf(']');
			if(index == -1) {
				index = value.length();
			}
			return value.substring(startIndex, index);
		}

		public void changeSheet(Sheet sheet) {
			this.sheet = sheet;
			this.section = null;
			changeCase(Case.ANONYMOUS);
			changeTable((Table)null, -1);
		}

		private void changeCase(String value) {
			this.testCase = Author.createCase(this.sheet, value);
			changeTable((Table)null, -1);
		}

		private void changeSection(String value) {
			this.section = Author.createSection(this.testCase, value);
			changeTable((Table)null, -1);
		}
		
		private void changeTable(String value, int rowIndex) {
			changeTable(Author.createTable(this.section, value), rowIndex);
		}
		
		private void changeTable(Table table, int rowIndex) {
			this.table = table;
			this.tableRowIndex = rowIndex;
			this.columns = null;
			this.columnNameRowIndex = -1;
			this.columnValueRowIndex = -1;
			this.values = null;
		}
	}
}
