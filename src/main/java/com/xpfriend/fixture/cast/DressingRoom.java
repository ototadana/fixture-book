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
package com.xpfriend.fixture.cast;

import java.util.ArrayList;
import java.util.List;

import com.xpfriend.fixture.role.Conductor;
import com.xpfriend.fixture.role.ObjectFactory;
import com.xpfriend.fixture.role.ObjectValidator;
import com.xpfriend.fixture.role.StorageUpdater;
import com.xpfriend.fixture.role.StorageValidator;

/**
 * アクターの待機場所。
 * 
 * @author Ototadana
 */
public class DressingRoom {

	private List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
	private List<ObjectValidator> objectValidators = new ArrayList<ObjectValidator>();
	private List<StorageUpdater> storageUpdaters = new ArrayList<StorageUpdater>();
	private List<StorageValidator> storageValidators = new ArrayList<StorageValidator>();
	private Conductor conductor;

	/**
	 * {@link ObjectFactory} のリストを取得する。
	 * @return {@link ObjectFactory} のリスト。
	 */
	public List<ObjectFactory> getObjectFactories() {
		return objectFactories;
	}

	/**
	 * {@link ObjectValidator} のリストを取得する。
	 * @return {@link ObjectValidator} のリスト。
	 */
	public List<ObjectValidator> getObjectValidators() {
		return objectValidators;
	}

	/**
	 * {@link StorageUpdater} のリストを取得する。
	 * @return {@link StorageUpdater} のリスト。
	 */
	public List<StorageUpdater> getStorageUpdaters() {
		return storageUpdaters;
	}

	/**
	 * {@link StorageValidator} のリストを取得する。
	 * @return {@link StorageValidator} のリスト。
	 */
	public List<StorageValidator> getStorageValidators() {
		return storageValidators;
	}

	/**
	 * {@link Conductor} を取得する。
	 * @return {@link Conductor}。
	 */
	public Conductor getConductor() {
		return conductor;
	}

	/**
	 * {@link Conductor} をセットする。
	 * @param conductor {@link Conductor}。
	 */
	public void setConductor(Conductor conductor) {
		this.conductor = conductor;
	}
}
