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

import java.util.Locale;

/**
 * 設定に関連する例外。
 * 
 * @author Ototadana
 */
public class ConfigException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Object[] args;
	private Locale locale;

	/**
	 * ConfigException　をメッセージ付きで作成する。
	 * @param message メッセージ。
	 * @param args メッセージパラメタ。
	 */
	public ConfigException(String message, Object... args) {
		super(message);
		this.args = args;
	}

	/**
	 * ConfigException　を原因例外付きで作成する。
	 * @param exception この例外が発生する原因となった例外。
	 */
	public ConfigException(Throwable exception) {
		super(exception);
		this.args = new Object[]{};
	}

	/**
	 * ConfigException　を原因例外とメッセージ付きで作成する。
	 * @param exception この例外が発生する原因となった例外。
	 * @param message メッセージ。
	 * @param args メッセージパラメタ。
	 */
	public ConfigException(Throwable exception, String message, Object... args) {
		super(message, exception);
		this.args = args;
	}

	/**
	 * メッセージのロケールを取得する。
	 * @return メッセージのロケール。
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * メッセージのロケールを設定する。
	 * @param locale メッセージのロケール。
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	@Override
	public String getLocalizedMessage() {
		String key = getMessage();
		if(key == null) {
			return null;
		}
		String messageFormat = Resi.get(locale, key, key);
		try {
			return String.format(locale, messageFormat, args);
		} catch(Exception e){
			return messageFormat;
		}
	}
}
