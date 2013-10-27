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

import com.xpfriend.junk.temp.ClassManager;

/**
 * クラス管理。
 * 
 * @author Ototadana
 */
public final class Classes {

	private static ClassManager instance = new ClassManager();
	
	static {
		new Classes(); // for coverage
	}
	
	private Classes() {
	}

	/**
	 * ある基底クラスの実装クラスを設定する。
	 * @param baseClass 基底クラス。
	 * @param implClass 実装クラス。
	 */
	public static <T> void put(Class<T> baseClass, Class<? extends T> implClass) {
		instance.put(baseClass, implClass);
	}

	/**
	 * {@link #put(Class, Class)} で設定した実装クラスを取得する。
	 * {@link #put(Class, Class)} で設定していない場合は、引数の基底クラスをそのまま返す。
	 * @param baseClass 基底クラス。
	 * @return 実装クラス。
	 */
	public static <T> Class<? extends T> get(Class<T> baseClass) {
		Class<? extends T> cls = instance.get(baseClass);
		if(cls == null) {
			cls = baseClass;
		}
		return cls;
	}

	/**
	 * {@link #get(Class)} で取得できる実装クラスのインスタンスを作成する。
	 * @param baseClass 基底クラス。
	 * @return 実装クラスのインスタンス。
	 */
	public static <T> T newInstance(Class<T> baseClass) {
		Class<? extends T> cls = get(baseClass);
		try {
			return cls.newInstance();
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}
	
	static void initialize() {
		instance.initialize();
	}
}
