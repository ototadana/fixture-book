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
package com.xpfriend.fixture.staff.xlsx

import com.xpfriend.fixture.staff.Column;

import spock.lang.Specification;

/**
 * @author Ototadana
 *
 */
class XlsxAuthorTest extends Specification {

	def "createColumnはtextで指定された列名を含むColumnを返す"() {
		when:
		Column column = XlsxAuthor.Parser.createColumn("apple")
		
		then:
		column.getName() == "apple"
		column.getType() == null
		column.getComponentType() == null
	}

	def "createColumnのtextに「:」が含まれる場合、列名と列タイプとして解釈する"() {
		when:
		Column column = XlsxAuthor.Parser.createColumn("apple:String")
		
		then:
		column.getName() == "apple"
		column.getType() == "String"
		column.getComponentType() == null
	}
	
	def "createColumnのtextに「:」と「[」が含まれる場合、列名と列要素タイプとして解釈する"(text) {
		when:
		Column column = XlsxAuthor.Parser.createColumn(text)
		
		then:
		column.getName() == "apple"
		column.getType() == null
		column.getComponentType() == "String"
		
		where:
		text << ["apple:String[]", "apple:String["]
	}

	def "createColumnのtextに「:」と「<」が含まれる場合、列名、列タイプ、列要素タイプとして解釈する"(text) {
		when:
		Column column = XlsxAuthor.Parser.createColumn(text)
		
		then:
		column.getName() == "apple"
		column.getType() == "List"
		column.getComponentType() == "String"
		
		where:
		text << ["apple:List<String>", "apple:List<String"]
	}

}
