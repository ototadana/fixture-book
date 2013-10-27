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

import java.util.Locale;

import com.xpfriend.junk.temp.ResourceProvider;

/**
 * リソース管理。
 * 
 * @author Ototadana
 */
public final class Resi {

	private static ResourceProvider provider = ResourceProvider.getInstance();
	
	static {
		new Resi(); // for coverage
	}

	private Resi() {
	}

	/**
	 * 指定したパッケージ名のリソースを管理対象として追加する。
	 * @param packageName パッケージ名。
	 */
	public static void add(String packageName) {
		provider.add(packageName);
	}
	
	/**
	 * 指定したキーで登録されている文字列を取得する。
	 * @param key キー。
	 * @return 文字列。
	 */
	public static String get(String key) {
		return get(key, key);
	}

	/**
	 * 指定したキーで登録されている文字列を取得する。
	 * @param key キー。
	 * @param defaultValue 指定したキーで登録されていなかった場合に使用する文字列。
	 * @return 文字列。
	 */
	public static String get(String key, String defaultValue) {
		return get(null, key, defaultValue);
	}

	/**
	 * ロケールを指定し、指定したキーで登録されている文字列を取得する。
	 * @param locale ロケール。
	 * @param key キー。
	 * @param defaultValue 指定したキーで登録されていなかった場合に使用する文字列。
	 * @return 文字列。
	 */
	public static String get(Locale locale, String key, String defaultValue) {
		return provider.get(locale, key, defaultValue);
	}
	
	static void initialize() {
		provider.initialize();
	}
}
