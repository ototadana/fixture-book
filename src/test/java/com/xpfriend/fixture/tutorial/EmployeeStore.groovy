package com.xpfriend.fixture.tutorial

import groovy.sql.Sql;

class EmployeeStore {
	
	def save(List<Employee> employees) {
		def sql = createSql()
		employees.each {
			it.lastUpdated = new java.sql.Timestamp(new Date().time)
			sql.execute(
				"INSERT INTO EMPLOYEE(ID, NAME, AGE, RETIRE, LAST_UPDATED)" + 
				"VALUES(:id, :name, :age, :retire, :lastUpdated)", it)
		}
	}
	
	def delete(List<Employee> employees) {
		def sql = createSql()
		employees.each {
			if(it.id == 0) {
				throw new IllegalArgumentException("Invalid ID")
			}
			sql.execute("DELETE FROM EMPLOYEE WHERE ID = :id", it)
		}
	}
	
	List<Employee> getAllEmployees() {
		def sql = createSql()
		def employees = []
		sql.eachRow("SELECT * FROM EMPLOYEE") {
			def employee = new Employee(
				id:it.ID, name:it.NAME, age:it.AGE, retire:it.RETIRE, 
				lastUpdated:it.LAST_UPDATED.timestampValue())
			employees.add(employee)
		}
		return employees
	}
	
	List<Employee> getEmployees(Employee parameter) {
		def sql = createSql()
		def employees = []
		sql.eachRow("SELECT * FROM EMPLOYEE where RETIRE = :retire", [parameter]) {
			def employee = new Employee(
				id:it.ID, name:it.NAME, age:it.AGE, retire:it.RETIRE,
				lastUpdated:it.LAST_UPDATED.timestampValue())
			employees.add(employee)
		}
		return employees
	}
	
	Sql createSql() {
		Sql.newInstance("jdbc:oracle:thin:@localhost:1521:xe", "system", "manager")
	}
}
