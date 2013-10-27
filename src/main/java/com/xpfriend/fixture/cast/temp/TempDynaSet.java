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
package com.xpfriend.fixture.cast.temp;

import java.util.List;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

/**
 * @author Ototadana
 *
 */
public class TempDynaSet {
	
	private TempDynaClass tempDynaClass;
	private List<DynaBean> rows;

	public TempDynaSet(List<DynaBean> table) {
		this.tempDynaClass = getTempDynaClass(table);
		this.rows = table;
	}
	
	private static TempDynaClass getTempDynaClass(List<DynaBean> table) {
		if(table.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return (TempDynaClass)table.get(0).getDynaClass();
	}
	
	public String getDatabaseName() {
		return tempDynaClass.getDatabaseName();
	}
	
	public String getTableName() {
		return tempDynaClass.getTableName();
	}
	
	public String getName() {
		return tempDynaClass.getName();
	}
	
	public List<DynaBean> getRows() {
		return rows;
	}
	
	public DynaProperty[] getColumns() {
		return tempDynaClass.getDynaProperties();
	}
	
	public DynaProperty getColumn(String name) {
		return tempDynaClass.getDynaProperty(name);
	}
}
