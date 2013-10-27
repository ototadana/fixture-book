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

import com.xpfriend.junk.temp.ConfigManager;

/**
 * アプリケーション設定情報管理。
 * 
 * @author Ototadana
 */
public final class Config {

	private static ConfigManager instance = ConfigManager.getInstance();
	
	static {
		new Config(); // for coverage
	}
	
	private Config() {
	}
	
	/**
	 * 指定された設定項目名で値を設定する。
	 * @param key 設定項目名。
	 * @param value 登録する値。
	 */
	public static void put(String key, String value) {
		instance.put(key, value);
	}
	
	/**
	 * 指定された設定項目の値を取得する。
	 * @param key 設定項目名。
	 * @param defaultValue 取得できなかった場合のデフォルト値。
	 * @return 設定値。
	 */
	public static String get(String key, String defaultValue) {
		return instance.get(key, defaultValue);
	}

	/**
	 * 指定された設定項目の値を取得する。
	 * @param key 設定項目名。
	 * @param defaultValue 取得できなかった場合のデフォルト値。
	 * @return 設定値。
	 */
	public static int get(String key, int defaultValue) {
		String s = get(key, (String)null);
		if(s == null) {
			return defaultValue;
		}
		return Integer.parseInt(s);
	}
	
	/**
	 * 指定された設定項目の値を取得する。
	 * @param key 設定項目名。
	 * @param defaultValue 取得できなかった場合のデフォルト値。
	 * @return 設定値。
	 */
	public static boolean get(String key, boolean defaultValue) {
		String s = get(key, (String)null);
		if(s == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(s);
	}
	
	/**
	 * 指定された設定項目の値を取得する。
	 * 値が取得できなかった場合は {@link ConfigException} が発生する。
	 * @param key 設定項目名。
	 * @return 設定値。
	 */
	public static String get(String key) {
		String value = get(key, null);
		if(value == null) {
			throw new ConfigException("M_Junk_Config_Get", key);
		}
		return value;
	}

	static void initialize() {
		instance.initialize();
	}
}
