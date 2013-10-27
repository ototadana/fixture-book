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

/**
 * セル処理。
 * 
 * @author Ototadana
 */
abstract class XlsxCellParser {

	private boolean isDebugEnabled;
	
	/**
	 * {@link XlsxCellParser} を作成する。
	 * @param debugEnabled デバッグ出力が有効かどうか。
	 */
	protected XlsxCellParser(boolean debugEnabled) {
		isDebugEnabled = debugEnabled;
	}

	/**
	 * デバッグ出力が有効かどうか。
	 * @return デバッグ出力が有効ならば true。
	 */
	protected boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	/**
	 * 指定されたセルの値を処理する。
	 * @param row 行番号。
	 * @param column 列番号。
	 * @param value セルの値。
	 */
	protected abstract void parse(int row, int column, String value);
}
