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

import org.apache.commons.beanutils.DynaBean;

import com.xpfriend.fixture.role.StorageValidator;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Table;

/**
 * データベース用の {@link StorageValidator}。
 * 
 * @author Ototadana
 */
public class DatabaseValidator extends DatabaseOperator implements StorageValidator {
	
	private TempObjectValidator tempObjectValidator;
	
	protected Section getSection() {
		return testCase.getSection(Section.SectionType.DATA_AS_EXPECTED);
	}
	
	@Override
	public void initialize(Case testCase) {
		super.initialize(testCase);
		tempObjectValidator = new TempObjectValidator();
		tempObjectValidator.initialize(testCase);
		tempObjectValidator.setSectionType(Section.SectionType.DATA_AS_EXPECTED);
	}

	@Override
	public boolean hasRole() {
		return getSection() != null && getSection().hasTable();
	}

	@Override
	public void validate() {
		Database database = new Database();
		try {
			Section section = getSection();
			List<TempDynaSet> dynaSet = getDynaSet(Section.SectionType.DATA_AS_EXPECTED);
			for(TempDynaSet keyTable : dynaSet) {
				List<String> keyColumns = getKeyColumns(section, keyTable);
				Table expectedTable = section.getTable(keyTable.getName());
				List<DynaBean> actualTable = database.select(keyColumns, keyTable, expectedTable);
				validate(actualTable, keyTable.getName());
			}
		} finally {
			database.dispose();
		}
	}

	private void validate(List<DynaBean> actualTable, String tableName) {
		tempObjectValidator.validate(actualTable, tableName);
	}

	private List<String> getKeyColumns(Section section, TempDynaSet table) {
		return section.getTable(table.getName()).getKeyColumnNames();
	}
}
