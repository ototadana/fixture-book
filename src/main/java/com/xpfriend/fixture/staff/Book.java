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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.xpfriend.junk.ConfigException;

/**
 * フィクスチャブック。
 * フィクスチャを定義したシートの集まり。
 * 
 * @author Ototadana
 */
public class Book {
	
	private static Map<String, Book> bookMap = new HashMap<String, Book>();
	private static boolean debugEnabled;
	private String filePath;
	private Director director;
	private Author author;
	private Map<String, Sheet> sheetMap = new HashMap<String, Sheet>();
	
	/**
	 * 指定されたファイルを読み込み、ブックのインスタンスを取得する。
	 * 
	 * 一度読み込んだファイル内容はキャッシュに蓄えられるため、同一のテストクラスとファイルパスを指定して
	 * {@link #getInstance(Class, String)} を指定した場合、
	 * 二度目以降はファイルの読み込みは行われない。
	 * 
	 * @param testClass テストクラス。
	 * @param filePath　読み込むファイルのパス。
	 * @return ブックのインスタンス。
	 * @throws IOException ファイル読み込みでエラーが発生した場合。
	 * @see #clearCache()
	 */
	public static Book getInstance(Class<?> testClass, String filePath) throws IOException {
		return getInstance(testClass, filePath, new Director());
	}

	/**
	 * 内部テスト用簡易メソッド。
	 */
	static Book getInstance(String filePath) throws IOException {
		return getInstance(Book.class, filePath, new Director());
	}
	
	static Book getInstance(Class<?> testClass, String filePath, Director director) throws IOException {
		String canonicalPath = new File(filePath).getCanonicalPath();
		String id = testClass.getName() + "@" + canonicalPath;
		synchronized(bookMap) {
			Book book = bookMap.get(id);
			if(book == null) {
				book = new Book(canonicalPath, director);
				bookMap.put(id, book);
			}
			return book;
		}
	}
	
	/**
	 * ブックインスタンスのキャッシュをクリアする。
	 */
	public static void clearCache() {
		synchronized(bookMap) {
			bookMap.clear();
		}
	}

	/**
	 * デバッグ出力が有効かどうかを調べる。
	 * @return デバッグ出力が有効な場合は true。
	 */
	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * デバッグ出力の有効/無効を切り替える。
	 * @param debugEnabled デバッグ出力を有効にしたい場合は true。
	 */
	public static void setDebugEnabled(boolean debugEnabled) {
		Book.debugEnabled = debugEnabled;
	}

	/**
	 * 指定されたファイルを読み込みブックを作成する。
	 * @param filePath ファイルパス。
	 * @param director ディレクタ。
	 */
	private Book(String filePath, Director director) {
		this.filePath = filePath;
		this.director = director;
		Author author = director.assignAuthor(this);
		author.write(this);
		this.author = author;
	}

	/**
	 * 現在のブックのファイルパスを取得する。
	 * @return ファイルパス。
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 指定された名前のシートを取得する。
	 * @param sheetName シート名。
	 * @return シート。
	 */
	public Sheet getSheet(String sheetName) {
		String key = toMapKey(sheetName);
		Sheet sheet = sheetMap.get(key);
		if(sheet == null) {
			throw new ConfigException("M_Fixture_Book_GetSheet", sheetName, this);
		}
		return sheet;
	}

	Sheet createSheet(String sheetName) {
		String key = toMapKey(sheetName);
		synchronized(sheetMap) {
			Sheet sheet = new Sheet(this, sheetName, director);
			sheetMap.put(key, sheet);
			return sheet;
		}
	}

	private String toMapKey(String sheetName) {
		return sheetName.toLowerCase(Locale.getDefault());
	}
	
	Author getAuthor() {
		return author;
	}
	
	@Override
	public String toString() {
		return new File(getFilePath()).getName();
	}
}
