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
package com.xpfriend.fixture.toolkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xpfriend.junk.Config;
import com.xpfriend.junk.Strings;

/**
 * @author Ototadana
 *
 */
public class PathUtil {

	private static final String SOURCE_PATH_KEY = "FixtureBook.sourcePath";
	private static final String DEFAULT_SOURCE_PATH = "./src/test/resources;./src/test/java;./src/test/groovy;./src;.";
	private static List<String> sourcePath;

	static {
		initSourcePath();
	}

	private static void initSourcePath() {
		String path = Config.get(SOURCE_PATH_KEY, null);
		if(Strings.isEmpty(path)) {
			path = DEFAULT_SOURCE_PATH;
		}
		String[] paths = path.split("[;|,| |:]");
		sourcePath = new ArrayList<String>(paths.length);
		for(int i = 0; i < paths.length; i++) {
			if(paths[i].length() > 0) {
				sourcePath.add(paths[i]);
			}
		}
	}

	
	public static String editFilePath(String filePath) {
		String path = getFilePath(filePath);
		if(path != null) {
			return path;
		}
		return new File(sourcePath.get(0), filePath).getAbsolutePath();
	}


	public static String getFilePath(String filePath) {
		if(new File(filePath).exists()) {
			return filePath;
		}
		
		for(String directory : sourcePath) {
			File file = new File(directory, filePath);
			if(file.exists()) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
}
