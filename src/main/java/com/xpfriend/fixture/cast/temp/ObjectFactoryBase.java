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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;

/**
 * @author Ototadana
 *
 */
public abstract class ObjectFactoryBase extends ObjectOperatorBase implements ObjectFactory {
	
	private ObjectFactory parent;

	protected ObjectFactoryBase(ObjectFactory parent) {
		super(Section.SectionType.OBJECT_FOR_EXEC);
		this.parent = parent;
	}
	
	@Override
	public <T> List<T> getList(Class<? extends T> cls, String typeName) {
		Table table = getTable(getSection(), cls, typeName);
		List<Row> rows = table.getRows();
		List<T> list = new ArrayList<T>(rows.size());
		for(Row row : rows) {
			T bean = createObject(cls, table, row);
			list.add(bean);
		}
		return list;
	}

	protected abstract <T> T createObject(Class<? extends T> cls, Table table, Row row);

	protected <O> void setProperties(Table table, Row row, O bean) throws Exception {
		for(Column column : table.getColumns()) {
			if(column != null) {
				setProperty(table, row, column, bean);
			}
		}
	}

	private <T> void setProperty(Table table, Row row, Column column, T bean) {
		String name = column.getName();
		String textValue = row.getValues().get(name);
		Class<?> type = PojoUtil.getPropertyType(bean, column.getName(), table, row);
		Object value = toObject(bean, column, type, textValue, table, row);
		setProperty(table, row, bean, name, type, value);
	}

	private <T> void setProperty(Table table, Row row, T bean, String name,
			Class<?> type, Object value) {
		try {
			PojoUtil.setPropertyValue(bean, name, type, value, table, row);
		} catch(ConfigException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new ConfigException(e.getTargetException(), "M_Fixture_Temp_ObjectFactory_SetProperty", name, type, table, row, value);
		} catch (Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_ObjectFactory_SetProperty", name, type, table, row, value);
		}
	}

	private Object toObject(Object bean, Column column, Class<?> type, String textValue, Table table, Row row) {
		try {
			return toObjectInternal(bean, column, type, textValue, table, row);
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_ObjectFactory_ConvertError", 
					column.getName(), textValue, type, table, row);
		}
	}
	private Object toObjectInternal(Object bean, Column column, Class<?> type, String textValue, Table table, Row row) {
		if(textValue == null || NULL.equals(textValue)) {
			if(type.isPrimitive()) {
				return TypeConverter.getNullValue(type);
			}
			return null;
		}
		
		if(EMPTY.equals(textValue)) {
			return "";
		}

		if(type.isArray()) {
			if(hasArraySeparator(textValue) || !getSection().hasTable(textValue)) {
				return toArray(type.getComponentType(), textValue);
			} else {
				return parent.getArray(type.getComponentType(), textValue);
			}
		}
		if(List.class.isAssignableFrom(type)) {
			if(hasArraySeparator(textValue) || !getSection().hasTable(textValue)) {
				return toList(type, getPropertyComponentType(bean, column, table, row), textValue);
			} else {
				return parent.getList(getPropertyComponentType(bean, column, table, row), textValue);
			}
		}
		if(TypeConverter.isConvertible(type)) {
			return TypeConverter.changeType(textValue, type);
		}
		return parent.getObject(type, textValue);
	}

	private Object toList(Class<?> type, Class<?> componentType, String textValue) {
		Object values = toArray(componentType, textValue);
		int length = Array.getLength(values);
		List<Object> list = createList(type, length);
		for(int i = 0; i < length; i++) {
			list.add(Array.get(values, i));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<Object> createList(Class<?> type, int size) {
		if(List.class.equals(type)) {
			return new ArrayList<Object>(size);
		} else {
			try {
				return (List<Object>)type.newInstance();
			} catch (InstantiationException e) {
				throw new ConfigException(e);
			} catch (IllegalAccessException e) {
				throw new ConfigException(e);
			}
		}
	}

	protected abstract Class<?> getPropertyComponentType(Object bean, Column column, Table table, Row row);

	@Override
	public <O> O getObject(Class<? extends O> cls, String typeName) {
		List<O> list = getList(cls, typeName);
		if(list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public <T> Object getArray(Class<? extends T> cls, String typeName) {
		List<T> list = getList(cls, typeName);
		Object array = Array.newInstance(cls, list.size());
		for(int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		return array;
	}
}
