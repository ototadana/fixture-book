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

import org.apache.commons.beanutils.DynaProperty;

import com.xpfriend.fixture.role.StorageUpdater;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Column;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.junk.ConfigException;

/**
 * データベース用の {@link StorageUpdater}。
 * 
 * @author Ototadana
 */
public class DatabaseUpdater extends DatabaseOperator implements StorageUpdater {
	
	protected Section getCreateSection() {
		return testCase.getSection(Section.SectionType.DATA_TO_CREATE);
	}
	
	protected Section getDeleteSection() {
		return testCase.getSection(Section.SectionType.DATA_TO_DELETE);
	}

	@Override
	public void initialize(Sheet sheet) {
		initialize(sheet.getCase(Case.ANONYMOUS));
	}

	@Override
	public boolean hasRole() {
		return getCreateSection() != null || getDeleteSection() != null;
	}

	@Override
	public void setup() {
		Section createSection = getCreateSection();
		Section deleteSection = getDeleteSection();
		if(createSection == null && deleteSection == null) {
			return;
		}
		
		Database database = new Database();
		try {
			if(deleteSection != null && deleteSection.hasTable()) {
				List<TempDynaSet> dynaSet = getDynaSet(Section.SectionType.DATA_TO_DELETE);
				database.delete(dynaSet, deleteSection);
			}
			
			if(createSection != null && createSection.hasTable()) {
				updateColumnTypes(database, Section.SectionType.DATA_TO_CREATE);
				List<TempDynaSet> dynaSet = getDynaSet(Section.SectionType.DATA_TO_CREATE);
				database.insert(dynaSet, createSection);
			}
			database.commit();
		} finally {
			database.dispose();
		}
	}
	
	private void updateColumnTypes(Database database, Section.SectionType sectionType) {
		Section section = testCase.getSection(sectionType);
		for(String tableName : section.getTableNames()) {
			Table table = section.getTable(tableName);
			updateColumntypes(database, table);
		}
	}

	private void updateColumntypes(Database database, Table table) {
		TempDynaClass metaData = database.getMetaData(table);
		for(Column column : table.getColumns()) {
			if(column != null) {
				DynaProperty property = metaData.getDynaProperty(column.getName());
				if(property == null) {
					throw new ConfigException("M_Fixture_Temp_DatabaseOperator_Column_NotFound", 
							metaData.getTableName(), column.getName(), table);
				}
				if(column.getType() == null && column.getComponentType() == null) {
					column.setType(property.getType());
				}
			}
		}
	}
	
	
}
