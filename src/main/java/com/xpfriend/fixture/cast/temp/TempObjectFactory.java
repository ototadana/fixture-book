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

import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section.SectionType;

/**
 * @author Ototadana
 *
 */
public class TempObjectFactory implements ObjectFactory {
	
	private MapFactory mapFactory = new MapFactory(this);
	private PojoFactory pojoFactory = new PojoFactory(this);
	private DynaBeanFactory dynaBeanFactory = new DynaBeanFactory(this);

	@Override
	public void initialize(Case testCase) {
		mapFactory.initialize(testCase);
		pojoFactory.initialize(testCase);
		dynaBeanFactory.initialize(testCase);
	}

	@Override
	public boolean hasRole(Class<?> cls, String typeName) {
		return pojoFactory.hasRole(cls, typeName) ||
				dynaBeanFactory.hasRole(cls, typeName) ||
				mapFactory.hasRole(cls, typeName);
	}

	@Override
	public <T> T getObject(Class<? extends T> cls, String typeName) {
		if(mapFactory.hasRole(cls, typeName)) {
			return mapFactory.getObject(cls, typeName);
		} else if(dynaBeanFactory.hasRole(cls, typeName)){
			return dynaBeanFactory.getObject(cls, typeName);
		} else {
			return pojoFactory.getObject(cls, typeName);
		}
	}

	@Override
	public <T> List<T> getList(Class<? extends T> cls, String typeName) {
		if(mapFactory.hasRole(cls, typeName)) {
			return mapFactory.getList(cls, typeName);
		} else if(dynaBeanFactory.hasRole(cls, typeName)){
			return dynaBeanFactory.getList(cls, typeName);
		} else {
			return pojoFactory.getList(cls, typeName);
		}
	}

	@Override
	public <T> Object getArray(Class<? extends T> cls, String typeName) {
		if(mapFactory.hasRole(cls, typeName)) {
			return mapFactory.getArray(cls, typeName);
		} else if(dynaBeanFactory.hasRole(cls, typeName)){
			return dynaBeanFactory.getArray(cls, typeName);
		} else {
			return pojoFactory.getArray(cls, typeName);
		}
	}

	public void setSectiontype(SectionType sectionType) {
		mapFactory.sectionType = sectionType;
		dynaBeanFactory.sectionType = sectionType;
		pojoFactory.sectionType = sectionType;
	}
}
