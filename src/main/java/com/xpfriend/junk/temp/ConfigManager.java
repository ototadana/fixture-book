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
import com.xpfriend.junk.Config;
import com.xpfriend.junk.ExceptionHandler;

/**
 * @author Ototadana
 */
public class ConfigManager {
	
	private Map<String, String> config = new HashMap<String, String>();

	public static ConfigManager getInstance() {
		return Classes.newInstance(ConfigManager.class);
	}
	
	public ConfigManager() {
		initialize();
	}
	
	public void initialize() {
		config.clear();
		ResourceBundle.clearCache();
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(Config.class.getSimpleName());
			for(Enumeration<String> en = bundle.getKeys(); en.hasMoreElements(); ) {
				String key = en.nextElement();
				config.put(key, bundle.getString(key));
			}
		} catch (MissingResourceException e) {
			ExceptionHandler.ignore(e);
		}
	}

	public String get(String key, String defaultValue) {
		String value = config.get(key);
		if(value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	public void put(String key, String value) {
		if(value == null) {
			config.remove(key);
		} else {
			config.put(key, value);
		}
	}
}
