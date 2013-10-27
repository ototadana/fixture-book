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
package com.xpfriend.junk;

import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * @author Ototadana
 *
 */
public class FormiNullTest {

	@Test
	public void toTimestampにnullを渡すとnullを返す() {
		assertNull(Formi.toTimestamp((Calendar)null));
		assertNull(Formi.toTimestamp((Date)null));
		assertNull(Formi.toTimestamp((XMLGregorianCalendar)null));
	}
	
	@Test
	public void formatTimestampにnullを渡すとnullを返す() {
		assertNull(Formi.formatTimestamp((Calendar)null));
		assertNull(Formi.formatTimestamp((Date)null));
		assertNull(Formi.formatTimestamp((XMLGregorianCalendar)null));
	}

	@Test
	public void toCalendarにnullを渡すとnullを返す() {
		assertNull(Formi.toCalendar((Date)null));
		assertNull(Formi.toCalendar((XMLGregorianCalendar)null));
	}
}
