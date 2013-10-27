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

import com.xpfriend.junk.Strings;

/**
 * 列データ。
 * 
 * @author Ototadana
 */
public class Column {

	private String name;
	private String type;
	private String componentType;
	private boolean isSearchKey;
	
	/**
	 * {@link Column} を作成する。
	 * @param name 列名。
	 * @param type 列タイプ。
	 * @param componentType 列タイプが配列またはリストの場合の要素型。
	 */
	public Column(String name, String type, String componentType) {
		if(name != null && name.startsWith("*")) {
			this.name = name.substring(1);
			this.isSearchKey = true;
		} else {
			this.name = name;
			this.isSearchKey = false;
		}
		this.type = type;
		this.componentType = componentType;
	}
	
	/**
	 * 列名を取得する。
	 * @return 列名。
	 */
	public String getName() {
		return name;
	}

	/**
	 * この項目が検索キーかどうかを調べる。
	 * @return 検索キーならば true。
	 */
	public boolean isSearchKey() {
		return isSearchKey;
	}

	/**
	 * 列タイプを取得する。
	 * @return 列タイプ。
	 */
	public String getType() {
		return type;
	}

	/**
	 * 列タイプの要素型を取得する。
	 * @return 列タイプの要素型。列タイプが配列またはリストではない場合は null。
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * この列が配列かどうかを調べる。
	 * @return 配列の場合は true。
	 */
	public boolean isArray() {
		return type == null && componentType != null;
	}

	/**
	 * この列に型設定を行う。
	 * @param type 設定する型。
	 */
	public void setType(Class<?> type) {
		if(type.isArray()) {
			this.componentType = type.getComponentType().getName();;
		} else {
			this.type = type.getName();
		}
	}
	
	@Override
	public String toString() {
		if(Strings.isEmpty(type) && Strings.isEmpty(componentType)) {
			return name;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(name).append(":");
		if(Strings.isEmpty(type)) {
			sb.append(componentType).append("[]");
			return sb.toString();
		}

		sb.append(type);
		if(Strings.isEmpty(componentType)) {
			return sb.toString();
		}

		sb.append("<").append(componentType).append(">");
		return sb.toString();
	}
}
