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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section;

/**
 * @author Ototadana
 *
 */
public abstract class DatabaseOperator {

	protected Case testCase;
	protected TempObjectFactory tempObjectFactory;

	public void initialize(Case testCase) {
		this.testCase = testCase;
		this.tempObjectFactory = new TempObjectFactory();
		this.tempObjectFactory.initialize(testCase);
	}
	
	protected List<TempDynaSet> getDynaSet(Section.SectionType sectionType) {
		List<String> tableNames = testCase.getSection(sectionType).getTableNames();
		tempObjectFactory.setSectiontype(sectionType);
		List<TempDynaSet> list = new ArrayList<TempDynaSet>(tableNames.size());
		for(int i = 0; i < tableNames.size(); i++) {
			List<DynaBean> table = tempObjectFactory.getList(DynaBean.class, tableNames.get(i));
			if(!table.isEmpty()) {
				list.add(new TempDynaSet(table));
			}
		}
		return list;
	}
}
