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
package com.xpfriend.fixture.role;

import java.util.List;

/**
 * テスト対象メソッド呼び出しの際のパラメタとして利用するオブジェクトを生成するためのアクター。
 * 
 * @author Ototadana
 */
public interface ObjectFactory extends Actor {

	/**
	 * 指定されたクラスのオブジェクトを生成可能かどうかを調べる。
	 * 
	 * @param cls 生成するオブジェクトのクラス。
	 * @param typeName 定義名。
	 * @return 生成可能な場合は true。
	 */
	boolean hasRole(Class<?> cls, String typeName);

	/**
	 * オブジェクトを生成する。
	 * 
	 * @param cls 生成するオブジェクトのクラス。
	 * @param typeName 定義名。
	 * @return 生成されたオブジェクト。
	 */
	<T> T getObject(Class<? extends T> cls, String typeName);

	/**
	 * オブジェクトのリストを生成する。
	 * 
	 * @param cls 生成するリスト要素オブジェクトクラス。
	 * @param typeName 定義名。
	 * @return 生成されたリスト。
	 */
	<T> List<T> getList(Class<? extends T> cls, String typeName);

	/**
	 * オブジェクトの配列を生成する。
	 * 
	 * @param cls 生成する配列要素オブジェクトクラス。
	 * @param typeName 定義名。
	 * @return 生成された配列。
	 */
	<T> Object getArray(Class<? extends T> cls, String typeName);
}
