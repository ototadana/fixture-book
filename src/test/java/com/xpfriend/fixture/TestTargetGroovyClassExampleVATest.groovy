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
package com.xpfriend.fixture

import java.util.List;

import spock.lang.Specification;

import com.xpfriend.fixture.tutorial.Employee;
import com.xpfriend.junk.ConfigException;

/**
 * @author Ototadana
 *
 */
class TestTargetGroovyClassExampleVATest extends Specification {

	static boolean save1
	static boolean save2
	static boolean getEmployees
	static boolean delete1
	static boolean delete2
	
	public static class Wrapper {
		def save(List<Employee> list) {
			new TestTargetJavaClassExample().save(list)
			TestTargetGroovyClassExampleVATest.save1 = true
		}
		
		def save(List<Employee> list, String s) {
			assert s == "a"
			new TestTargetJavaClassExample().save(list)
			TestTargetGroovyClassExampleVATest.save2 = true
		}
		
		List<Employee> getEmployees(Employee employee) {
			try {
				return new TestTargetJavaClassExample().getEmployees(employee)
			} finally {
				TestTargetGroovyClassExampleVATest.getEmployees = true
			}
		}
		
		def delete(List<Employee> employees) {
			try {
				new TestTargetJavaClassExample().delete(employees)
			} finally {
				TestTargetGroovyClassExampleVATest.delete1 = true
			}
		}

		def delete(List<Employee> employees, int i) {
			assert i == 1
			TestTargetGroovyClassExampleVATest.delete2 = true
		}
	}
	
	public static class Uncreatable {
		public Uncreatable(String s) {
		}
		def save(List<Employee> list) {
		}
	}
	
	def expect__テスト対象クラスとテストメソッドを引数指定したexpectは指定されたメソッドを実行する_引数1個の場合() {
		setup:
		save1 = false
		when:
		FixtureBook.expect(Wrapper, "save", List)
		then:
		save1 == true
	}

	@Fixture(["expect", "テスト対象クラスとテストメソッドを引数指定したexpectは指定されたメソッドを実行する_引数1個の場合"])
	def expect__指定されたクラスのインスタンスが作成できない場合は例外をスローする() {
		when:
		FixtureBook.expect(Uncreatable, "save", List)
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Temp_Conductor_CannotCreateInstance"
	}

	@Fixture(["expect", "テスト対象クラスとテストメソッドを引数指定したexpectは指定されたメソッドを実行する_引数1個の場合"])
	def expect__指定されたメソッドがみつからない場合は例外をスローする_メソッド引数指定ありの場合() {
		when:
		FixtureBook.expect(Wrapper, "save", String, String)
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Temp_Conductor_CannotFindMethod"
	}

	@Fixture(["expect", "テスト対象クラスとテストメソッドを引数指定したexpectは指定されたメソッドを実行する_引数1個の場合"])
	def expect__指定されたメソッドがみつからない場合は例外をスローする_メソッド引数指定なしの場合() {
		when:
		FixtureBook.expect(Wrapper, "ｘｘｘｘ")
		then:
		ConfigException e = thrown()
		println e
		e.getMessage() == "M_Fixture_Temp_Conductor_CannotFindMethod"
	}

	def expect__テスト対象クラスとテストメソッドを引数指定したexpectは指定されたメソッドを実行する_引数2個の場合() {
		setup:
		save2 = false
		when:
		FixtureBook.expect(Wrapper, "save", List, String)
		then:
		save2 == true
	}

	def expectReturn__テスト対象クラスとテストメソッドを引数指定したexpectReturnは指定されたメソッドを実行する() {
		setup:
		getEmployees = false
		when:
		FixtureBook.expectReturn(Wrapper, "getEmployees", Employee)
		then:
		getEmployees == true
	}

	def expectThrown__テスト対象クラスとテストメソッドを引数指定したexpectThrownは指定されたメソッドを実行する_正常() {
		setup:
		delete1 = false
		when:
		FixtureBook.expectThrown(IllegalArgumentException, Wrapper, "delete", List)
		then:
		delete1 == true
	}
	
	def expectThrown__テスト対象クラスとテストメソッドを引数指定したexpectThrownは指定されたメソッドを実行する_エラー() {
		setup:
		delete2 = false
		when:
		FixtureBook.expectThrown(IllegalArgumentException, Wrapper, "delete", List, int)
		then:
		AssertionError e = thrown()
		println e
		delete2 == true
	}
}
