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
package com.xpfriend.fixture;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

/**
 * @author Ototadana
 *
 */
public class ExceptionEditors {

	public void dummy1(ParseException e) {}
	static void dummy2(ParseException e) {}
	public static Object dummy3(Object o) {return null;}
	public static Object dummy4() {return null;}
	public static Object dummy5(ParseException e, Object o) {return null;}
	
	public static Object editParseException(ParseException e) {
		System.out.println(e);
		Assert.assertEquals("app", e.getMessage());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "zzz");
		return map;
	}

	public static Object editIllegalArgumentException(IllegalArgumentException e) {
		System.out.println(e);
		Assert.assertEquals("sys", e.getMessage());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "zzz");
		return map;
	}
}
