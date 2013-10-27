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

import com.xpfriend.fixture.tutorial.Employee;
import com.xpfriend.fixture.tutorial.EmployeeStore;

/**
 * @author Ototadana
 *
 */
class TestTargetGroovyClassExample {

	def save(List<Employee> employees) {
		new EmployeeStore().save(employees)
	}
	
	List<Employee> getEmployees(Employee parameter) {
		return new EmployeeStore().getEmployees(parameter)
	}
	
	def delete(List<Employee> employees) {
		new EmployeeStore().delete(employees)
	}
}
