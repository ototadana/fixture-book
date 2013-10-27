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

import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.staff.Row;
import com.xpfriend.fixture.staff.Table;

/**
 * @author Ototadana
 *
 */
public class DynaBeanValidator extends ObjectValidatorBase implements ObjectValidator {

	public DynaBeanValidator(ObjectValidator parent) {
		super(parent);
	}

	@Override
	protected boolean isValidatableObject(Object object) {
		return object instanceof DynaBean;
	}

	@Override
	protected Object getPropertyValue(Object object, String name, Table table, Row row) {
		return ((DynaBean)object).get(name);
	}
}
