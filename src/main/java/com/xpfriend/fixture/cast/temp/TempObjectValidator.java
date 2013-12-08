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

import java.util.concurrent.Callable;

import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section.SectionType;
import com.xpfriend.junk.Loggi;

/**
 * @author Ototadana
 *
 */
public class TempObjectValidator implements ObjectValidator {

	private MapValidator mapValidator = new MapValidator(this);
	private PojoValidator pojoValidator = new PojoValidator(this);
	private DynaBeanValidator dynaBeanValidator = new DynaBeanValidator(this);
	private Case testCase;

	@Override
	public void initialize(Case testCase) {
		mapValidator.initialize(testCase);
		pojoValidator.initialize(testCase);
		dynaBeanValidator.initialize(testCase);
		this.testCase = testCase;
	}

	@Override
	public boolean hasRole(Object object, String typeName) {
		return pojoValidator.hasRole(object, typeName) ||
				mapValidator.hasRole(object, typeName) ||
				dynaBeanValidator.hasRole(object, typeName);
	}

	@Override
	public void validate(Object object, String typeName) {
		if(mapValidator.hasRole(object, typeName)) {
			mapValidator.validate(object, typeName);
		} else if(dynaBeanValidator.hasRole(object, typeName)){
			dynaBeanValidator.validate(object, typeName);
		} else {
			pojoValidator.validate(object, typeName);
		}
	}

	@Override
	public void validate(Class<? extends Throwable> exceptionClass,
			Callable<?> action, String typeName) {
		boolean isNormalEnd = false;
		try {
			action.call();
			isNormalEnd = true;
		} catch(Throwable t) {
			Loggi.debug(t);
			if(GroovySupport.isInvokerInvocationException(t)) {
				t = t.getCause();
			}
			Object obj = t;
			FixtureBook.ExceptionEditor editor = getExceptionEditor(t.getClass());
			if(editor != null) {
				obj = editor.edit(t);
			}
			validate(obj, typeName);
		}
		
		if(isNormalEnd) {
			Assertie.fail("M_Fixture_Temp_ObjectValidator_Exception", exceptionClass.getName());
		}
	}

	private FixtureBook.ExceptionEditor getExceptionEditor(Class<?> type) {
		FixtureBook.ExceptionEditor editor = testCase.getExceptionEditor(type);
		if(editor != null) {
			return editor;
		}
		if(Throwable.class.isAssignableFrom(type.getSuperclass())) {
			return getExceptionEditor(type.getSuperclass());
		}
		return null;
	}
	
	public void setSectionType(SectionType sectionType) {
		mapValidator.sectionType = sectionType;
		pojoValidator.sectionType = sectionType;
		dynaBeanValidator.sectionType = sectionType;
	}
}
