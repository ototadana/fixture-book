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
package com.xpfriend.junk;

/**
 * 文字列関連ユーティリティ。
 * 
 * @author Ototadana
 */
public final class Strings {
	
	static {
		new Strings(); // for coverage
	}
	private Strings() {
	}

	/**
	 * 指定された文字列が null または長さゼロであれば true を返す。
	 * @param string 調べる文字列。
	 * @return null または長さゼロであれば true。
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
}
