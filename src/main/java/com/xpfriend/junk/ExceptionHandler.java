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

/**
 * 例外処理ユーティリティ。
 * 
 * @author Ototadana
 */
public final class ExceptionHandler {

	static {
		new ExceptionHandler(); // for coverage
	}
	
	private ExceptionHandler() {
	}
	
	/**
	 * 指定された例外を無視する。
	 * @param e 無視する例外。
	 */
	public static void ignore(Throwable e) {
	}
	
	/**
	 * 指定された例外を Exception または Error に変換してスローする。
	 * @param t 例外。
	 */
	public static void throwException(Throwable t) throws Exception {
		if(t instanceof Error) {
			throw (Error)t;
		} else if(t instanceof Exception) {
			throw (Exception)t;
		} else if(t != null) {
			throw new ConfigException(t);
		}
	}

	/**
	 * 指定された例外を RuntimeException または Error に変換してスローする。
	 * @param t 例外。
	 */
	public static void throwRuntimeException(Throwable t) {
		if(t instanceof Error) {
			throw (Error)t;
		} else if(t instanceof RuntimeException) {
			throw (RuntimeException)t;
		} else if(t != null) {
			throw new ConfigException(t);
		}
	}
}
