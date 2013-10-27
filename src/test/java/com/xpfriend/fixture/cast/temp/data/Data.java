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
package com.xpfriend.fixture.cast.temp.data;

import java.util.List;
import java.util.Date;

/**
 * @author Ototadana
 *
 */
public class Data {
	private String text1;
	private int number1;
	private Integer number2;
	private Date date1;
	private byte[] bytes;
	private List<Data> list;
	
	public String getText1() {return text1;}
	public void setText1(String text1) {this.text1 = text1;}
	public int getNumber1() {return number1;}
	public void setNumber1(int number1) {this.number1 = number1;}
	public Integer getNumber2() {return number2;}
	public void setNumber2(Integer number2) {this.number2 = number2;}
	public String toString() {return text1 + "," + number1;}

	public Date getDate1() {
		return date1;
	}
	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public List<Data> getList() {
		return list;
	}
	public void setList(List<Data> list) {
		this.list = list;
	}
}
