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

import java.util.concurrent.Callable;

/**
 * テスト対象メソッド呼び出し後のオブジェクト状態を検証するためのアクター。
 * 
 * @author Ototadana
 */
public interface ObjectValidator extends Actor {

	/**
	 * 指定されたオブジェクトが予想結果と適合するかどうかを調べる。
	 * 予想結果と適合しない場合は {@link AssertionError} がスローされる。
	 * 
	 * @param object 調べるオブジェクト。
	 * @param typeName テーブル定義名。
	 * @exception AssertionError 予想結果と適合しない場合。
	 */
	void validate(Object object, String typeName);

	/**
	 * 指定されたオブジェクトの検証が可能かどうかを調べる。
	 * 
	 * @param object 調べるオブジェクト。
	 * @param typeName テーブル定義名。
	 * @return 検証可能な場合は true。
	 */
	boolean hasRole(Object object, String typeName);

	/**
	 * 指定された処理で発生した例外が「E.取得データ」に記述された予想結果と適合することを調べる。
	 * @param exceptionClass 発生が予想される例外。
	 * @param action テスト対象の処理。
	 * @param typeName テーブル定義名。
	 */
	void validate(Class<? extends Throwable> exceptionClass,
			Callable<?> action, String typeName);
}
