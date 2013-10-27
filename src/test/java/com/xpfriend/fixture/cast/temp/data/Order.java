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

/**
 * @author Ototadana
 *
 */
public class Order {
	private String orderNo;
	private Customer customerInfo;
	private List<OrderDetail> detail;
	private OrderDetail[] detail2;
	private OrderDetails detail3;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Customer getCustomerInfo() {
		return customerInfo;
	}
	public void setCustomerInfo(Customer customerInfo) {
		this.customerInfo = customerInfo;
	}
	public List<OrderDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<OrderDetail> detail) {
		this.detail = detail;
	}
	public OrderDetail[] getDetail2() {
		return detail2;
	}
	public void setDetail2(OrderDetail[] detail2) {
		this.detail2 = detail2;
	}
	public OrderDetails getDetail3() {
		return detail3;
	}
	public void setDetail3(OrderDetails detail3) {
		this.detail3 = detail3;
	}
}
