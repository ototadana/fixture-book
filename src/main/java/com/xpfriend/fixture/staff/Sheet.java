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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xpfriend.fixture.role.StorageUpdater;
import com.xpfriend.junk.ConfigException;

/**
 * フィクスチャを定義したシート。
 * 
 * @author Ototadana
 */
public class Sheet {
	private Book book;
	private String sheetName;
	private Director director;
	private Map<String, Case> caseMap = new HashMap<String, Case>();
	private boolean written;
	private boolean notYet = true;
	
	Sheet(Book book, String sheetName, Director director) {
		this.book = book;
		this.sheetName = sheetName;
		this.director = director;
	}
	
	/**
	 * このシートの名前を取得する。
	 * @return シート名。
	 */
	public String getName() {
		return sheetName;
	}
	
	/**
	 * このシートを格納しているブックを取得する。
	 * @return ブック。
	 */
	public Book getBook() {
		return book;
	}
	
	/**
	 * ストレージに対する更新を行う。
	 */
	public void setup() {
		write(this);
		List<StorageUpdater> storageUpdaters = director.assignStorageUpdaters(this);
		for(StorageUpdater storageUpdater : storageUpdaters) {
			storageUpdater.setup();
		}
	}
	
	/**
	 * セットアップをまだ一度もおこなっていない場合にはセットアップ実行する。
	 */
	public void setupIfNotYet() {
		if(notYet) {
			setup();
			notYet = false;
		}
	}

	/**
	 * テストケース定義を取得する。
	 * @param caseName テストケース名。
	 * @return テストケース定義。
	 */
	public Case getCase(String caseName) {
		write(this);
		Case testCase = caseMap.get(caseName);
		if(testCase == null) {
			throw new ConfigException("M_Fixture_Sheet_GetCase", caseName, this);
		}
		return testCase.copy();
	}
	
	private void write(Sheet sheet) {
		if(written) {
			return;
		}
		book.getAuthor().write(this);
		written = true;
	}

	/**
	 * テストケース定義を取得する。
	 * @param caseName テストケース名。
	 * @return テストケース定義。
	 */
	Case createCase(String caseName) {
		Case testCase = caseMap.get(caseName);
		if(testCase == null) {
			testCase = new Case(this, caseName, director);
			caseMap.put(caseName, testCase);
		}
		return testCase;
	}

	@Override
	public String toString() {
		return book.toString() + "#" + getName();
	}
}
