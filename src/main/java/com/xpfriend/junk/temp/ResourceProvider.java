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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.ExceptionHandler;

/**
 * リソースのプロバイダ。
 * 
 * @author Ototadana
 */
public class ResourceProvider {

	private List<String> resourceNames = new ArrayList<String>();
	
	public static ResourceProvider getInstance() {
		return Classes.newInstance(ResourceProvider.class);
	}
	
	public void add(String packageName) {
		String name = packageName + ".Resources";
		if(resourceNames.contains(name)) {
			resourceNames.remove(name);
		}
		resourceNames.add(name);
	}
	
	public String get(Locale locale, String key, String defaultValue) {
		if(locale == null) {
			locale = Locale.getDefault();
		}
		
		for(int i = resourceNames.size() - 1; i >= 0; i--) {
			try {
				ResourceBundle resourceBundle = 
						ResourceBundle.getBundle(resourceNames.get(i), locale);
				return resourceBundle.getString(key);
			} catch(Exception e) {
				ExceptionHandler.ignore(e);
			}
		}
		return defaultValue;
	}
	
	public void initialize() {
		ResourceBundle.clearCache();
		resourceNames.clear();
	}
}
