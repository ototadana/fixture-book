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
package com.xpfriend.fixture.toolkit;

import java.util.ArrayList;
import java.util.List;


/**
 * 現在のメソッドを呼び出しているクラス・メソッドを取得する為のユーティリティ。
 * 
 * @author Ototadana
 */
public class StackFrameFinder {

	private static final String MY_CLASS_NAME = StackFrameFinder.class.getName();
	private static final String[] EXCLUDE_PACKAGES = 
			new String[]{"sun.", "java.", "org.codehaus.groovy.", "groovy.", "org.spockframework."};
	
	/**
	 * 指定されたクラスの {@link StackTraceElement} を取得する。
	 * @param className 取得するクラス。
	 * @return StackTraceElement。
	 */
	public static List<StackTraceElement> find(String className) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		List<StackTraceElement> list = new ArrayList<StackTraceElement>(stackTrace.length);
		for(int i = 0; i < stackTrace.length; i++) {
			String stackFrameClassName = stackTrace[i].getClassName();
			if(stackFrameClassName.equals(className)) {
				list.add(stackTrace[i]);
			} else if(!list.isEmpty()) {
				break;
			}
		}
		if(list.isEmpty()) {
			throw new IllegalStateException("cannot find a StackTraceElement of " + className);
		}
		return list;
	}
	
	/**
	 * このメソッドの呼び出し元の呼び出し元のStackTraceElementを取得する。
	 */
	public static List<StackTraceElement> findCaller(Class<?> calleeClass) {
		String calleeClassName = calleeClass.getName();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

//		for(int i = 1; i < stackTrace.length; i++) {
//			System.out.println(stackTrace[i].getClassName() + ", " + stackTrace[i].getMethodName() + "," + 
//					stackTrace[i].getFileName() + ":" + stackTrace[i].getLineNumber());
//		}
		
		for(int i = 1; i < stackTrace.length; i++) {
			String className = stackTrace[i].getClassName();
			if(!MY_CLASS_NAME.equals(className) && !calleeClassName.equals(className) && 
					!isExcludePackage(className) && 
					!isExcludeClass(className, stackTrace[i].getMethodName()) && 
					stackTrace[i].getFileName() != null) {
//				System.out.println("find:" + className);
				return find(className);
			}
		}
		throw new IllegalStateException("cannot find caller of " + calleeClassName);
	}

	private static boolean isExcludeClass(String className, String methodName) {
		return ("doCall".equals(methodName) && className.indexOf("closure") > -1) ||
				(className.startsWith("com.xpfriend.fixture.") && 
						!(className.endsWith("Test") || className.endsWith("Tst")));
	}

	private static boolean isExcludePackage(String className) {
		for(int i = 0; i < EXCLUDE_PACKAGES.length; i++) {
			if(className.startsWith(EXCLUDE_PACKAGES[i])) {
				return true;
			}
		}
		return false;
	}
}
