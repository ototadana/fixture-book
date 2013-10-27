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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;

/**
 * @author Ototadana
 *
 */
class GroovySupport {

	private static Class<?> closureClass;
	private static Class<?> invokerInvocationExceptionClass;
	private static Method getParameterTypesMethod;
	private static Method callMethod;
	
	static {
		initClosureClass();
	}
	
	private static void initClosureClass() {
		try {
			closureClass = Class.forName("groovy.lang.Closure");
			invokerInvocationExceptionClass = Class.forName("org.codehaus.groovy.runtime.InvokerInvocationException");
		} catch(Exception e) {
			return;
		}
		
		try {
			getParameterTypesMethod = closureClass.getMethod("getParameterTypes");
			callMethod = closureClass.getMethod("call", new Class<?>[]{Object[].class});
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}
	
	public static boolean isClosure(Object action) {
		return closureClass != null && closureClass.isInstance(action);
	}

	public static Object invoke(Object action, Object[] objects) throws Exception {
		Object result = null;
		try {
			result = callMethod.invoke(action, objects);
		} catch(InvocationTargetException e) {
			handleInvocationTargetException(e);
		}
		return result;
	}

	private static void handleInvocationTargetException(
			InvocationTargetException e) throws Exception {
		Throwable t = e.getCause();
		if(isInvokerInvocationException(t)) {
			t = t.getCause();
		}
		ExceptionHandler.throwException(t);
	}

	public static Class<?>[] getParameterTypess(Object action) throws Exception{
		Class<?>[] result = null;
		try {
			result = (Class<?>[])getParameterTypesMethod.invoke(action);
		} catch(InvocationTargetException e) {
			handleInvocationTargetException(e);
		}
		return result;
	}
	
	public static boolean isInvokerInvocationException(Throwable exception) {
		return invokerInvocationExceptionClass != null && 
				invokerInvocationExceptionClass.isInstance(exception);
	}
}
