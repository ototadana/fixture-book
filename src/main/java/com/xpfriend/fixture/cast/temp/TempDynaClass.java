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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Table;

class TempDynaClass extends BasicDynaClass {
	private static final long serialVersionUID = 1L;
	
	private String tableName;
	private String databaseName;
	
	public TempDynaClass(Table table) throws ClassNotFoundException {
		super(table.getName(), null, createDynaProperties(table));
		splitTableNameAndDatabaseName(getName());
	}
	
	public TempDynaClass(String tableName) {
		name = tableName;
		splitTableNameAndDatabaseName(tableName);
	}

	private static DynaProperty[] createDynaProperties(Table table) throws ClassNotFoundException {
		List<Column> columns = table.getColumns();
		List<DynaProperty> list = new ArrayList<DynaProperty>(columns.size());
		for(Column column : columns) {
			if(column != null) {
				list.add(new TempDynaProperty(column));
			}
		}
		DynaProperty[] properties = new DynaProperty[list.size()];
		return list.toArray(properties);
	}
	
	private void splitTableNameAndDatabaseName(String name) {
		if(name.indexOf('@') > -1) {
			String[] s = name.split("@");
			tableName = s[0];
			databaseName = s[1];
		} else {
			tableName = name;
		}
	}

	public String getTableName() {
		return tableName;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void update(DynaClass metaData) {
		setProperties(metaData.getDynaProperties());
	}
}