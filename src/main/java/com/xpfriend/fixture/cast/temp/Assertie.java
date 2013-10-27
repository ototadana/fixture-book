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

import org.junit.Assert;

import com.xpfriend.junk.Resi;

/**
 * @author Ototadana
 *
 */
class Assertie {

	public static void fail(String resourceKey, Object...args){
		Assert.fail(getMessage(resourceKey, args));
	}
	
	public static void assertEqualsInt(int expected, int actual,
			String resourceKey, Object...args) {
		Assert.assertEquals(getMessage(resourceKey, args), expected, actual);
	}
	public static void assertEquals(Object expected, Object actual,
			String resourceKey, Object...args) {
		Assert.assertEquals(getMessage(resourceKey, args), expected, actual);
	}

	private static String getMessage(String key, Object...args) {
		String messageFormat = Resi.get(key, key);
		return String.format(messageFormat, args);
	}

}
