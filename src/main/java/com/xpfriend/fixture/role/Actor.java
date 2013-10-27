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

import com.xpfriend.fixture.staff.Case;

/**
 * フィクスチャ操作のアクター。
 * 
 * @author Ototadana
 */
public interface Actor {

	/**
	 * 指定されたテストケースの内容を使ってこのアクターを初期化する。
	 * @param sheet 初期化に使用するテストケース。
	 */
	void initialize(Case testCase);
}
