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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.xpfriend.fixture.FixtureBook;
import com.xpfriend.fixture.FixtureBookPath;

/**
 * @author Ototadana
 *
 */
@FixtureBookPath("com/xpfriend/fixture/cast/temp/PojoFactoryTest.xlsx")
public class PojoFactoryExtraTest {
	
	private FixtureBook fixtureBook = new FixtureBook();

	@Test
	public void PojoFactoryTest__Stringを作成できる() {
		//when
		String object = fixtureBook.getObject(String.class);
		//then
		assertEquals("a", object);
		
		//when
		List<String> list = fixtureBook.getList(String.class);
		//then
		assertEquals(2, list.size());
		assertEquals("a", list.get(0));
		assertEquals("b", list.get(1));
		
		//when
		String[] array = (String[])fixtureBook.getArray(String.class);
		//then
		assertEquals(2, array.length);
		assertEquals("a", array[0]);
		assertEquals("b", array[1]);
	}

	@Test
	public void PojoFactoryTest__intを作成できる() {
		//when
		int object = fixtureBook.getObject(int.class);
		//then
		assertEquals(1, object);
		
		//when
		List<Integer> list = fixtureBook.getList(Integer.class);
		//then
		assertEquals(2, list.size());
		assertEquals(Integer.valueOf(1), list.get(0));
		assertEquals(Integer.valueOf(2), list.get(1));
		
		//when
		int[] array = (int[])fixtureBook.getArray(int.class);
		//then
		assertEquals(2, array.length);
		assertEquals(1, array[0]);
		assertEquals(2, array[1]);
	}
}
