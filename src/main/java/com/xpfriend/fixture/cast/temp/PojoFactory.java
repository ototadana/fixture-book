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

import java.util.Map;

import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ExceptionHandler;

/**
 * Java Beans 用の {@link ObjectFactory}。
 * 
 * @author Ototadana
 */
public class PojoFactory extends ObjectFactoryBase {
	
	private static final String OWN = "-";

	public PojoFactory(ObjectFactory parent) {
		super(parent);
	}
	
	@Override
	public boolean hasRole(Class<?> cls, String typeName) {
		return getSection() != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T createObject(Class<? extends T> cls, Table table, Row row) {
		T bean = null;
		try {
			if(isSimpleType(cls, row)) {
				return (T)TypeConverter.changeType(row.getValues().get(OWN), cls);
			}
			bean = cls.newInstance();
			setProperties(table, row, bean);
		} catch(Exception e) {
			ExceptionHandler.throwRuntimeException(e);
		}
		return bean;
	}
	
	private boolean isSimpleType(Class<?> type, Row row) {
		Map<String, String> values = row.getValues();
		return values.size() == 1 && values.containsKey(OWN) &&
				TypeConverter.isConvertible(type);
	}

	@Override
	protected Class<?> getPropertyComponentType(Object bean, Column column, Table table, Row row) {
		return PojoUtil.getPropertyComponentType(bean, column, table, row);
	}
}
