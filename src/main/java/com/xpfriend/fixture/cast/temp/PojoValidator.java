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
import java.util.concurrent.Callable;

import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.Loggi;

/**
 * Java Beans 用の {@link ObjectValidator}。
 * @author Ototadana
 */
public class PojoValidator extends ObjectValidatorBase {

	public PojoValidator(ObjectValidator parent) {
		super(parent);
	}

	@Override
	public boolean hasRole(Object object, String typeName) {
		return getSection() != null && getSection().hasTable();
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
			validate(t, typeName);
		}
		
		if(isNormalEnd) {
			Assertie.fail("M_Fixture_Temp_ObjectValidator_Exception", exceptionClass.getName());
		}
	}

	@Override
	protected boolean isValidatableObject(Object object) {
		return true;
	}

	@Override
	protected Object getPropertyValue(Object object, String name, Table table, Row row) {
		if(isSimpleType(object, row)) {
			return object;
		}
		return PojoUtil.getPropertyValue(object, name, table, row);
	}

	private boolean isSimpleType(Object value, Row row) {
		Map<String, String> values = row.getValues();
		return values.size() == 1 && values.containsKey(OWN) &&
				(value == null || TypeConverter.isConvertible(value.getClass()));
	}
}
