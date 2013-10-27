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
package com.xpfriend.fixture.staff;

import java.util.ArrayList;
import java.util.List;

/**
 * テーブル。
 * 
 * @author Ototadana
 */
public class Table {
	
	private Section section;
	private String tableName;
	private List<Column> columns = new ArrayList<Column>();
	private List<Row> rows = new ArrayList<Row>();
	private List<String> keyColumnNames;
	private Boolean hasKeyColumn;

	/**
	 * テーブルを作成する。
	 * @param section このテーブルが属するセクション。
	 * @param tableName テーブル名。
	 */
	Table(Section section, String tableName) {
		this.section = section;
		this.tableName = tableName;
	}

	/**
	 * このテーブルが属するセクションを取得する。
	 * @return セクション。
	 */
	public Section getSection() {
		return section;
	}

	/**
	 * テーブル名を取得する。
	 * @return テーブル名。
	 */
	public String getName() {
		return tableName;
	}

	/**
	 * 列データを取得する。
	 * @return 列データ。
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * 行データを取得する。
	 * @return 行データ。
	 */
	public List<Row> getRows() {
		return rows;
	}

	/**
	 * 行データを追加する。
	 * @param index 行番号
	 * @param deleted　削除指定行かどうか。
	 * @return 追加した行データ。
	 */
	public Row addRow(int index, boolean deleted) {
		Row row = new Row(index, deleted);
		rows.add(row);
		return row;
	}
	
	@Override
	public String toString() {
		return section.toString() + " - " + getName();
	}
	
	public boolean hasKeyColumn() {
		if(hasKeyColumn == null) {
			hasKeyColumn = initHasKeyColumn();
		}
		return hasKeyColumn.booleanValue();
	}

	private boolean initHasKeyColumn() {
		for(Column column : columns) {
			if(column != null && column.isSearchKey()) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getKeyColumnNames() {
		if(keyColumnNames == null) {
			keyColumnNames = createKeyColumnNames();
		}
		return keyColumnNames;
	}

	private List<String> createKeyColumnNames() {
		List<String> keyColumns = new ArrayList<String>();
		for(Column column : columns) {
			if(column != null && column.isSearchKey()) {
				keyColumns.add(column.getName());
			}
		}
		
		if(keyColumns.size() > 0) {
			return keyColumns;
		}
		
		for(Column column : columns) {
			if(column != null) {
				keyColumns.add(column.getName());
			}
		}
		return keyColumns;
	}
}
