package com.xpfriend.fixture.cast.temp;

import java.lang.reflect.Array;

import org.apache.commons.beanutils.DynaProperty;

import com.xpfriend.fixture.staff.Column;

class TempDynaProperty extends DynaProperty {
	private static final long serialVersionUID = 1L;

	public TempDynaProperty(Column column) throws ClassNotFoundException {
		super(column.getName(), toClass(column), MapFactory.toClass(column.getComponentType()));
	}
	
	private static Class<?> toClass(Column column) throws ClassNotFoundException {
		if(column.isArray()) {
			return toArrayClass(column.getComponentType());
		}
		return MapFactory.toClass(column.getType());
	}
	
	private static Class<?> toArrayClass(String type) throws ClassNotFoundException {
		Class<?> componentType = MapFactory.toClass(type);
		return Array.newInstance(componentType, 0).getClass();
	}
}