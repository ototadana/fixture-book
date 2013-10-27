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

import com.xpfriend.junk.temp.LoggiLogger;

/**
 * ロギング。
 * 
 * @author Ototadana
 */
public final class Loggi {

	private static LoggiLogger logger = LoggiLogger.getInstance();
	
	static {
		new Loggi(); // for coverage
	}
	
	private Loggi() {
	}
	
	/**
	 * デバッグレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public static void debug(String message) {
		logger.debug(message);
	}

	/**
	 * デバッグレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public static void debug(String message, Throwable t) {
		logger.debug(message, t);
	}

	/**
	 * デバッグレベルのログ出力を行なう。
	 * @param t 例外情報。
	 */
	public static void debug(Throwable t) {
		logger.debug(t.toString(), t);
	}

	/**
	 * デバッグレベルログ出力を行なうかどうかを調べる。
	 * @return デバッグレベルログ出力を行なう場合は true。
	 */
	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

    /**
     * デバッグ出力可不可設定を行う。
     * @param enabled デバッグ出力可にする場合は true。
     */
    public static void setDebugEnabled(boolean enabled) {
    	logger.setDebugEnabled(enabled);
    }

	/**
	 * 情報レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public static void info(String message) {
		logger.info(message);
	}

	/**
	 * 情報レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public static void info(String message, Throwable t) {
		logger.info(message, t);
	}

	/**
	 * 情報レベルのログ出力を行なう。
	 * @param t 例外情報。
	 */
	public static void info(Throwable t) {
		logger.info(t.toString(), t);
	}

	/**
	 * 警告レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public static void warn(String message) {
		logger.warn(message);
	}

	/**
	 * 警告レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public static void warn(String message, Throwable t) {
		logger.warn(message, t);
	}

	/**
	 * 警告レベルのログ出力を行なう。
	 * @param t 例外情報。
	 */
	public static void warn(Throwable t) {
		logger.warn(t.toString(), t);
	}

	/**
	 * エラーレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public static void error(String message) {
		logger.error(message);
	}

	/**
	 * エラーレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public static void error(String message, Throwable t) {
		logger.error(message, t);
	}

	/**
	 * エラーレベルのログ出力を行なう。
	 * @param t 例外情報。
	 */
	public static void error(Throwable t) {
		logger.error(t.toString(), t);
	}
}
