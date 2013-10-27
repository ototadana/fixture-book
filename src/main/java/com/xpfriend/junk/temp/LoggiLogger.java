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
package com.xpfriend.junk.temp;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xpfriend.junk.Classes;
import com.xpfriend.junk.Loggi;


/**
 * {@link Loggi} 用のロガー。
 * 
 * @author Ototadana
 */
public class LoggiLogger {
	
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static LoggiLogger getInstance() {
		return Classes.newInstance(LoggiLogger.class);
	}
	
	/**
	 * デバッグレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public void debug(String message) {
		logger.log(Level.CONFIG, message);
	}

	/**
	 * デバッグレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public void debug(String message, Throwable t) {
		logger.log(Level.CONFIG, message, t);
	}

	/**
	 * デバッグレベルログ出力を行なうかどうかを調べる。
	 * @return デバッグレベルログ出力を行なう場合は true。
	 */
	public boolean isDebugEnabled() {
		return logger.isLoggable(Level.CONFIG);
	}

    /**
     * デバッグ出力可不可設定を行う。
     * @param enabled デバッグ出力可にする場合は true。
     */
    public void setDebugEnabled(boolean enabled) {
    	if(enabled) {
    		setLevel(Level.CONFIG);
    	} else {
    		setLevel(Level.INFO);
    	}
    }
    
    private void setLevel(Level level) {
    	setLevel(logger, level);
    }
    
    private static void setLevel(Logger logger, Level level) {
    	logger.setLevel(level);
    	Handler[] handlers = logger.getHandlers();
    	for(int i = 0; i < handlers.length; i++) {
    		handlers[i].setLevel(level);
    	}
    	Logger parent = logger.getParent();
    	if(parent != null) {
			setLevel(parent, level);
    	}
    }

	/**
	 * 情報レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public void info(String message) {
		logger.log(Level.INFO, message);
	}

	/**
	 * 情報レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public void info(String message, Throwable t) {
		logger.log(Level.INFO, message, t);
	}

	/**
	 * 警告レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public void warn(String message) {
		logger.log(Level.WARNING, message);
	}

	/**
	 * 警告レベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public void warn(String message, Throwable t) {
		logger.log(Level.WARNING, message, t);
	}

	/**
	 * エラーレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 */
	public void error(String message) {
		logger.log(Level.SEVERE, message);
	}

	/**
	 * エラーレベルのログ出力を行なう。
	 * @param message 出力メッセージ。
	 * @param t 例外情報。
	 */
	public void error(String message, Throwable t) {
		logger.log(Level.SEVERE, message, t);
	}
}
