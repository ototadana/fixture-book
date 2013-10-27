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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;

/**
 * @author Ototadana
 *
 */
public class DynaBeanFactory extends ObjectFactoryBase {

	private static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	static {
		initializeClassMap();
	}
	
	private static void initializeClassMap() {
		classMap.put("string", String.class);

		classMap.put("boolean", boolean.class);
		classMap.put("bool", boolean.class);
		
		classMap.put("bigdecimal", BigDecimal.class);
		classMap.put("decimal", BigDecimal.class);
		
		classMap.put("int", int.class);
		classMap.put("integer", int.class);
		classMap.put("long", long.class);
		classMap.put("short", short.class);
		classMap.put("float", float.class);
		classMap.put("double", double.class);
		classMap.put("byte", byte.class);
		
		classMap.put("char", char.class);
		classMap.put("character", char.class);
		
		classMap.put("date", Date.class);
		classMap.put("time", Time.class);
		classMap.put("datetime", Timestamp.class);
		classMap.put("timestamp", Timestamp.class);
		
		classMap.put("map", Map.class);
		classMap.put("list", List.class);
	}
	
	protected DynaBeanFactory(ObjectFactory parent) {
		super(parent);
	}

	@Override
	public boolean hasRole(Class<?> cls, String typeName) {
		return getSection() != null && DynaBean.class.isAssignableFrom(cls);
	}

	@SuppressWarnings("unchecked")
	protected <T> T createObject(Class<? extends T> cls, Table table, Row row) {
		try {
			BasicDynaClass dynaClass = getDynaClass(table);
			DynaBean bean = dynaClass.newInstance();
			setProperties(table, row, bean);
			return (T)bean;
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}

	private BasicDynaClass getDynaClass(Table table) throws ClassNotFoundException {
		return new TempDynaClass(table);
	}

	@Override
	protected Class<?> getPropertyComponentType(Object bean, Column column, Table table, Row row) {
		String typeName = column.getComponentType();
		try {
			return toClass(typeName);
		} catch(ClassNotFoundException e) {
			throw new ConfigException(e, "M_Fixture_Temp_ObjectFactory_ClassNotFound", column.getName(), typeName, table, row);
		}
	}
	
	static Class<?> toClass(String typeName) throws ClassNotFoundException {
		if(typeName == null) {
			return String.class;
		}
		Class<?> cls = classMap.get(typeName.toLowerCase(Locale.ENGLISH));
		if(cls != null) {
			return cls;
		}
		return Class.forName(typeName);
	}
}
