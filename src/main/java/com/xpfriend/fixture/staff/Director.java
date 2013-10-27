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

import java.util.Collections;
import java.util.List;

import com.xpfriend.fixture.cast.DressingRoom;
import com.xpfriend.fixture.cast.temp.DatabaseUpdater;
import com.xpfriend.fixture.cast.temp.DatabaseValidator;
import com.xpfriend.fixture.cast.temp.TempConductor;
import com.xpfriend.fixture.cast.temp.TempObjectFactory;
import com.xpfriend.fixture.cast.temp.TempObjectValidator;
import com.xpfriend.fixture.role.Actor;
import com.xpfriend.fixture.role.StorageUpdater;
import com.xpfriend.fixture.staff.xlsx.XlsxAuthor;

/**
 * ディレクタ。
 * 
 * {@link Author} および {@link Actor} のファクトリ。
 * 
 * @author Ototadana
 */
public class Director {
	
	/**
	 * ディレクタを作成する。
	 */
	public Director() {	
	}
	
	/**
	 * ブックの作者をアサインする。
	 * @param book ブック。
	 * @return　ブックの作者。
	 */
	protected Author assignAuthor(Book book) {
		return new XlsxAuthor();
	}
	
	/**
	 * 指定されたシートにふさわしい {@link StorageUpdater} をアサインする。
	 * @param sheet シート。
	 */
	public List<StorageUpdater> assignStorageUpdaters(Sheet sheet) {
		StorageUpdater databaseUpdater = new DatabaseUpdater();
		databaseUpdater.initialize(sheet);
		return Collections.singletonList(databaseUpdater);
	}
	
	/**
	 * 指定されたテストケースにふさわしいアクターをアサインする。
	 * @param testCase テストケース。
	 * @return アクター。
	 */
	public DressingRoom assignActors(Case testCase) {
		DressingRoom dressingRoom = new DressingRoom();

		dressingRoom.getStorageUpdaters().add(initialize(new DatabaseUpdater(), testCase));
		dressingRoom.getStorageValidators().add(initialize(new DatabaseValidator(), testCase));
		dressingRoom.getObjectFactories().add(initialize(new TempObjectFactory(), testCase));
		dressingRoom.getObjectValidators().add(initialize(new TempObjectValidator(), testCase));
		dressingRoom.setConductor(initialize(new TempConductor(), testCase));
		
		return dressingRoom;
	}
	
	private <T extends Actor> T initialize(T actor, Case testCase) {
		actor.initialize(testCase);
		return actor;
	}
}
