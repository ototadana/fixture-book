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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;

import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.Strings;


/**
 * @author Ototadana
 *
 */
class PojoUtil {
	
	private static class PropertyNames {
		private Map<String, String> propertyNames = new HashMap<String, String>();
		
		public PropertyNames(PropertyUtilsBean propertyUtil, Class<?> beanClass) {
			PropertyDescriptor[] properties = propertyUtil.getPropertyDescriptors(beanClass);
			for(PropertyDescriptor property : properties) {
				String propertyName = property.getName();
				propertyNames.put(propertyName.toLowerCase(), propertyName);
			}
		}
		
		public String find(String propertyName) {
			String s = propertyNames.get(propertyName.toLowerCase());
			if(s != null) {
				return s;
			}
			return propertyName;
		}
		
		public static String find(DynaClass dynaClass, String propertyName) {
			DynaProperty[] properties = dynaClass.getDynaProperties();
			String lowerCaseName = propertyName.toLowerCase();
			for(DynaProperty property : properties) {
				if(property.getName().toLowerCase().equals(lowerCaseName)) {
					return property.getName();
				}
			}
			return propertyName;
		}
	}

	private static Map<String, PropertyNames> propertyNamesMap = new HashMap<String, PropertyNames>();
	

	public static Object getPropertyValue(Object object, String propertyName, Table table, Row row) {
		try {
			String name = find(propertyName, object);
			return TypeConverter.getBeanUtils().getPropertyUtils().getProperty(object, name);
		} catch (NoSuchMethodException e) {
			throw createNoSuchPropertyException(object, propertyName, table, row);
		} catch (InvocationTargetException e) {
			throw new ConfigException(e.getCause());
		} catch (IllegalAccessException e) {
			throw new ConfigException(e);
		}
	}

	private static ConfigException createNoSuchPropertyException(Object object,
			String name, Table table, Row row) {
		return new ConfigException("M_Fixture_Temp_ObjectFactory_NoSuchProperty", 
				name, object.getClass().getName(), table, row);
	}
	
	public static void setPropertyValue(Object object, String propertyName, Class<?> type, Object value, Table table, Row row) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			String name = find(propertyName, object);
			TypeConverter.getBeanUtils().getPropertyUtils().setProperty(object, name, value);
		} catch (NoSuchMethodException e) {
			throw createNoSuchPropertyException(object, propertyName, table, row);
		}
	}

	public static Class<?> getPropertyType(Object object, String propertyName, Table table, Row row) {
		try {
			String name = find(propertyName, object);
			PropertyUtilsBean propertyUtils = TypeConverter.getBeanUtils().getPropertyUtils();
			if(object instanceof DynaBean) {
				return ((DynaBean) object).getDynaClass().getDynaProperty(name).getType();
			}
			Class<?> type = propertyUtils.getPropertyType(object, name);
			if(type == null) {
				throw createNoSuchPropertyException(object, name, table, row);
			}
			return type;
		} catch (NoSuchMethodException e) {
			throw createNoSuchPropertyException(object, propertyName, table, row);
		} catch (InvocationTargetException e) {
			throw new ConfigException(e.getCause());
		} catch (IllegalAccessException e) {
			throw new ConfigException(e);
		}
	}
	
	public static Class<?> getPropertyComponentType(Object object, Column column, Table table, Row row) {
		String propertyName = column.getName();
		try {
			String name = find(propertyName, object);
			PropertyDescriptor descriptor = 
					TypeConverter.getBeanUtils().getPropertyUtils().getPropertyDescriptor(object, name);
			Method method = descriptor.getWriteMethod();
			Type genericType = method.getGenericParameterTypes()[0];
			if(genericType != null) {
				if(genericType instanceof Class) {
					genericType = ((Class<?>)genericType).getGenericSuperclass();
				}
				if(genericType instanceof ParameterizedType) {
					Type[] es = ((ParameterizedType)genericType).getActualTypeArguments();
					if(es != null && es.length > 0 && es[0] instanceof Class) {
						return (Class<?>)es[0];
					}
				}
			}
			return String.class;
		} catch (NoSuchMethodException e) {
			throw createNoSuchPropertyException(object, propertyName, table, row);
		} catch (InvocationTargetException e) {
			throw new ConfigException(e.getCause());
		} catch (IllegalAccessException e) {
			throw new ConfigException(e);
		}
	}
	
	private static String find(String name, Object bean) {
		if(Strings.isEmpty(name)) {
			return name;
		}

		if(bean instanceof DynaBean) {
			DynaClass dynaClass = ((DynaBean) bean).getDynaClass();
			if(dynaClass.getDynaProperty(name) != null) {
				return name;
			}
			return PropertyNames.find(dynaClass, name);
		}

		Class<?> beanClass = bean.getClass();
		PropertyNames propertyNames = propertyNamesMap.get(beanClass.getName());
		if(propertyNames == null) {
			PropertyUtilsBean propertyUtil = TypeConverter.getBeanUtils().getPropertyUtils();
			propertyNames = new PropertyNames(propertyUtil, beanClass);
		}
		return propertyNames.find(name);
	}	
}
