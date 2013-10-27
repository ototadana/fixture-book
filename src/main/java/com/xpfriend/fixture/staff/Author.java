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



/**
 * ブックの作者。
 * 
 * @author Ototadana
 */
public abstract class Author {
	
	/**
	 * ブックを作成する。
	 * @param book 作成するブック。
	 */
	public abstract void write(Book book);
	
	/**
	 * シートを作成する。
	 * @param sheet 作成するシート。
	 */
	public abstract void write(Sheet sheet);
	
	/**
	 * 指定されたブックに指定された名前でシートを作成する。
	 * @param book　ブック。
	 * @param sheetName シート名。
	 * @return 作成したシート。
	 */
	public static Sheet createSheet(Book book, String sheetName) {
		return book.createSheet(sheetName);
	}
	
	/**
	 * 指定されたシートに指定された名前のテストケースを作成する。
	 * @param sheet シート。
	 * @param caseName テストケース名。
	 * @return 作成したテストケース。
	 */
	public static Case createCase(Sheet sheet, String caseName) {
		return sheet.createCase(caseName);
	}

	/**
	 * 指定されたテストケースに指定された名前のセクションを作成する。
	 * @param testCase テストケース。
	 * @param sectionName セクション名。
	 * @return 作成したセクション。
	 */
	public static Section createSection(Case testCase, String sectionName) {
		return testCase.createSection(sectionName);
	}

	/**
	 * 指定されたセクションに指定された名前のテーブルを作成する。
	 * @param section セクション。
	 * @param tableName テーブル名。
	 * @return 作成したテーブル。
	 */
	public static Table createTable(Section section, String tableName) {
		return section.createTable(tableName);
	}
}
