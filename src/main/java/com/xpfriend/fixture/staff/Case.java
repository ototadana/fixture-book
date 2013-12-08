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
package com.xpfriend.fixture.staff;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.xpfriend.fixture.FixtureBook.ExceptionEditor;
import com.xpfriend.fixture.cast.DressingRoom;
import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.role.StorageUpdater;
import com.xpfriend.fixture.role.StorageValidator;
import com.xpfriend.junk.ConfigException;

/**
 * テストケース定義。
 * 
 * @author Ototadana
 */
public class Case {
	/**
	 * 名前のついていないテストケースの名前。
	 */
	public static final String ANONYMOUS = " ";
	
	private Sheet sheet;
	private String caseName;
	private DressingRoom dressingRoom;
	private Section[] sections = new Section[Section.getMaxNumber() + 1];
	private boolean notYet = true;
	private Map<Class<?>, ExceptionEditor> exceptionEditors = new HashMap<Class<?>, ExceptionEditor>();
	
	/**
	 * テストケース定義を作成する。
	 * @param sheet このテストケースが属すシート。
	 * @param caseName テストケース名。
	 * @param director ディレクタ。
	 */
	Case(Sheet sheet, String caseName, Director director) {
		this.sheet = sheet;
		this.caseName = caseName;
		this.dressingRoom = director.assignActors(this);
	}
	
	/**
	 * このテストケースの名前を取得する。
	 * @return テストケース名。
	 */
	public String getName() {
		return caseName;
	}

	/**
	 * このテストケースが記述されているシートを取得する。
	 * @return シート。
	 */
	public Sheet getSheet() {
		return sheet;
	}
	
	Section createSection(String sectionName) {
		Section temp = new Section(this, sectionName);
		int number = temp.getNumber();
		Section section = sections[number];
		if(section == null) {
			sections[number] = temp;
			section = temp;
		}
		return section;
	}

	/**
	 * 指定された名前のセクションを取得する。
	 * @param sectionName セクション名。
	 * @return セクション。
	 */
	public Section getSection(String sectionName) {
		return getSection(new Section(this, sectionName).getNumber());
	}
	
	/**
	 * 指定されたタイプのセクションを取得する。
	 * @param sectionType セクションタイプ。
	 * @return　セクション。
	 */
	public Section getSection(Section.SectionType sectionType) {
		return getSection(sectionType.getValue());
	}
	
	/**
	 * 指定された番号のセクションを取得する。
	 * @param sectionNumber セクション番号。
	 * @return　セクション。
	 */
	Section getSection(int sectionNumber) {
		if(sectionNumber >= sections.length || sectionNumber < 0) {
			throw new ConfigException("M_Fixture_Case_GetSection", sectionNumber, this);
		}
		return sections[sectionNumber];
	}
	
	public <O> O getObject(Class<? extends O> cls, String typeName) {
		setupIfNotYet();
		for(ObjectFactory actor : dressingRoom.getObjectFactories()) {
			if(actor.hasRole(cls, typeName)) {
				return actor.getObject(cls, typeName);
			}
		}
		throw new ConfigException("M_Fixture_Case_GetObject", this);
	}

	public <O> List<O> getList(Class<? extends O> cls, String typeName) {
		setupIfNotYet();
		for(ObjectFactory actor : dressingRoom.getObjectFactories()) {
			if(actor.hasRole(cls, typeName)) {
				return actor.getList(cls, typeName);
			}
		}
		throw new ConfigException("M_Fixture_Case_GetList", this);
	}

	public <O> Object getArray(Class<? extends O> cls, String typeName) {
		setupIfNotYet();
		for(ObjectFactory actor : dressingRoom.getObjectFactories()) {
			if(actor.hasRole(cls, typeName)) {
				return actor.getArray(cls, typeName);
			}
		}
		throw new ConfigException("M_Fixture_Case_GetArray", this);
	}

	public void validateStorage() {
		if(!validateStorageInternal()) {
			throw new ConfigException("M_Fixture_Case_Validate_Storage", this);
		}
	}
	
	public boolean validateStorageInternal() {
		boolean validated = false;
		for(StorageValidator actor : dressingRoom.getStorageValidators()) {
			if(actor.hasRole()) {
				validated = true;
				actor.validate();
			}
		}
		return validated;
	}

	public void validate(Object object, String typeName) {
		boolean validated = false;
		for(ObjectValidator actor : dressingRoom.getObjectValidators()) {
			if(actor.hasRole(object, typeName)) {
				validated = true;
				actor.validate(object, typeName);
			}
		}
		if(!validated) {
			throw new ConfigException("M_Fixture_Case_Validate_Object", this);
		}
	}

	public void setup() {
		sheet.setupIfNotYet();
		for(StorageUpdater actor : dressingRoom.getStorageUpdaters()) {
			if(actor.hasRole()) {
				actor.setup();
			}
		}
	}
	
	private void setupIfNotYet() {
		if(notYet) {
			setup();
			notYet = false;
		}
	}
	
	@Override
	public String toString() {
		return sheet.toString() + "[" + getName() + "]";
	}

	public void validate(Class<? extends Throwable> exceptionClass,
			Callable<?> action, String typeName) {
		boolean validated = false;
		for(ObjectValidator actor : dressingRoom.getObjectValidators()) {
			if(actor.hasRole(action, typeName)) {
				validated = true;
				actor.validate(exceptionClass, action, typeName);
			}
		}
		if(!validated) {
			throw new ConfigException("M_Fixture_Case_Validate_Object", this);
		}
	}

	public void expect(Object action, Class<?>... cls) {
		dressingRoom.getConductor().expect(action, cls);
	}

	public void expectReturn(Object action, Class<?>... cls) {
		dressingRoom.getConductor().expectReturn(action, cls);
	}

	public void expectThrown(Class<? extends Throwable> exceptionClass,
			Object action, Class<?>... cls) {
		dressingRoom.getConductor().expectThrown(exceptionClass, action, cls);
	}

	public <T> T getParameterAt(int index) {
		return dressingRoom.getConductor().getParameterAt(index);
	}

	public void validateParameterAt(int index) {
		dressingRoom.getConductor().validateParameterAt(index);
	}

	public void validateParameterAt(int index, String name) {
		dressingRoom.getConductor().validateParameterAt(index, name);
	}

	public void expect(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) {
		dressingRoom.getConductor().expect(targetClass, targetMethod, parameterClass);
	}

	public void expectReturn(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) {
		dressingRoom.getConductor().expectReturn(targetClass, targetMethod, parameterClass);
	}

	public void expectThrown(Class<? extends Throwable> exceptionClass,
			Class<?> targetClass, String targetMethod, Class<?>[] parameterClass) {
		dressingRoom.getConductor().expectThrown(exceptionClass, targetClass, targetMethod, parameterClass);
	}

	public void registerExceptionEditor(Class<?> exceptionType, ExceptionEditor exceptionEditor) {
		exceptionEditors.put(exceptionType, exceptionEditor);
	}

	public ExceptionEditor getExceptionEditor(Class<?> type) {
		return exceptionEditors.get(type);
	}
}
