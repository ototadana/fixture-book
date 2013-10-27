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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaBeanMapDecorator;

import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;

/**
 * マップオブジェクト用の {@link ObjectFactory}。
 * 
 * @author Ototadana
 */
public class MapFactory extends DynaBeanFactory {
	
	public MapFactory(ObjectFactory parent) {
		super(parent);
	}

	@Override
	public boolean hasRole(Class<?> cls, String typeName) {
		return getSection() != null && Map.class.isAssignableFrom(cls);
	}

	@SuppressWarnings("unchecked")
	protected <T> T createObject(Class<? extends T> cls, Table table, Row row) {
		
		try {
			DynaBean bean = super.createObject(DynaBean.class, table, row);
			return (T)toMap(cls, bean);
		} catch(ConfigException e) {
			throw e;
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map toMap(Class<?> cls, DynaBean bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Map map = new DynaBeanMapDecorator(bean, false);
		if(cls.isAssignableFrom(map.getClass())) {
			return map;
		} else {
			Map obj = (Map)newInstance(cls);
			obj.putAll(map);
			return obj;
		}
	}

	private Object newInstance(Class<?> cls)
			throws InstantiationException, IllegalAccessException {
		if(Map.class.equals(cls)) {
			cls = HashMap.class;
		}
		return cls.newInstance();
	}
}
