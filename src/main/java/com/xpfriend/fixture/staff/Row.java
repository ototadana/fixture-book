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
import java.util.Map;

/**
 * 行データ。
 * 
 * @author Ototadana
 */
public class Row {
	private Map<String, String> values = new HashMap<String, String>();
	private int index;
	private boolean deleted;

	/**
	 * 行を作成する。
	 * @param index　行番号。
	 * @param deleted 削除指定行かどうか。
	 */
	Row(int index, boolean deleted) {
		this.index = index;
		this.deleted = deleted;
	}

	/**
	 * 列データを取得する。
	 * @return 列データ。
	 */
	public Map<String, String> getValues() {
		return values;
	}
	
	/**
	 * 行番号を取得する。
	 * @return 行番号。
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * 削除指定行かどうかを取得する。
	 * @return 削除指定行ならば true。
	 */
	public boolean isDeleted() {
		return deleted;
	}
	
	@Override
	public String toString() {
		return String.valueOf(index);
	}
}