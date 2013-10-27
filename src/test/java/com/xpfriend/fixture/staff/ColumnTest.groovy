/*
 * Copyright 2013 XPFriend Community.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.xpfriend.fixture.staff

import com.xpfriend.fixture.staff.Column;

import spock.lang.Specification

/**
 * @author Ototadana
 *
 */
class ColumnTest extends Specification {

	def "getNameはコンストラクタで指定された列名を返す"() {
		when:
		Column column = new Column("test1", null, null)
		
		then:
		column.getName() == "test1"
	}
	
	def "getTypeはコンストラクタで指定された列タイプを返す"() {
		when:
		Column column = new Column(null, "type1", null)
		
		then:
		column.getType() == "type1"
	}

	def "getComponentTypeはコンストラクタで指定された列要素タイプを返す"() {
		when:
		Column column = new Column(null, null, "ctype1")
		
		then:
		column.getComponentType() == "ctype1"
	}
	
	def "isArrayは列タイプがnullで列要素タイプがnullでない場合にtrueを返す"(type, ctype, expected) {
		when:
		Column column = new Column(null, type, ctype)
		
		then:
		column.isArray() == expected
		
		where:
		type | ctype | expected
		null | "x"   | true
		null | null  | false
		"x"  | null  | false
		"x"  | "x"   | false
	}
	
	def "toStringは列タイプも列要素タイプも指定されていない場合、列名を表す文字列を返す"() {
		when:
		Column column = new Column("x", null, null)
		
		then:
		column.toString() == "x"
	}
	
	def "toStringは列名と列タイプのみが指定されている場合、「列名:列タイプ」型式の文字列を返す"() {
		when:
		Column column = new Column("x", "t", null)
		
		then:
		column.toString() == "x:t"
	}

	def "toStringは列名と列タイプと列要素タイプが指定されている場合、「列名:列タイプ<列要素タイプ>」型式の文字列を返す"() {
		when:
		Column column = new Column("x", "t", "ct")
		
		then:
		column.toString() == "x:t<ct>"
	}

	def "toStringは列名と列要素タイプのみが指定されている場合、「列名:列要素タイプ[]」型式の文字列を返す"() {
		when:
		Column column = new Column("x", null, "ct")
		
		then:
		column.toString() == "x:ct[]"
	}
	
	def "setTypeはColumnの型を指定できる"(Class type, String typeText, String componentType) {
		setup:
		Column column = new Column("x", null, null)
		
		when:
		column.setType(type)
		
		then:
		println column
		column.getType() == typeText
		column.getComponentType() == componentType
		
		where:
		type   | typeText | componentType
		String | "java.lang.String" | null
		String[] | null | "java.lang.String"
	}
}
