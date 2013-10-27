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
package com.xpfriend.junk.temp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;

/**
 * @author Ototadana
 *
 */
public class ClassManager {

	private Map<Class<?>, Class<?>> classes = new HashMap<Class<?>, Class<?>>();

	public ClassManager() {
		initialize();
	}
	
	public <T> void put(Class<T> baseClass, Class<? extends T> implClass) {
		classes.put(baseClass, implClass);
	}

	@SuppressWarnings("unchecked")
	public <T> Class<? extends T> get(Class<T> baseClass) {
		return (Class<? extends T>) classes.get(baseClass);
	}
	
	public void initialize() {
		classes.clear();
		ResourceBundle.clearCache();
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(Classes.class.getSimpleName());
			for(Enumeration<String> en = bundle.getKeys(); en.hasMoreElements(); ) {
				String key = en.nextElement();
				Class<?> baseClass = Class.forName(key);
				Class<?> implClass = Class.forName(bundle.getString(key));
				classes.put(baseClass, implClass);
			}
		} catch (MissingResourceException e) {
			ExceptionHandler.ignore(e);
		} catch(ClassNotFoundException e) {
			throw new ConfigException(e);
		}
	}
}
