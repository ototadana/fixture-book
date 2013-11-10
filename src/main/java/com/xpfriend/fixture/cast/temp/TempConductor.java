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
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import com.xpfriend.fixture.role.Conductor;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Section;
import com.xpfriend.fixture.staff.Section.SectionType;
import com.xpfriend.fixture.staff.Table;
import com.xpfriend.fixture.toolkit.MethodFinder;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;

/**
 * @author Ototadana
 *
 */
public class TempConductor implements Conductor {
	
	private static class Result {
		public String name;
		public Object value;
		public Result(String name, Object value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private static final int SYNTHETIC = 0x00001000;
	
	
	private static boolean isSynthetic(int mod) {
		return (mod & SYNTHETIC) != 0;
	}
	
	private Case testCase;
	private Object[] parameters;
	private String[] parameterNames;

	@Override
	public void initialize(Case testCase) {
		this.testCase = testCase;
	}

	@Override
	public void expect(Object action, Class<?>... cls) {
		testCase.setup();
		dynamicInvoke(action, cls);
		testCase.validateStorageInternal();
	}

	private Object dynamicInvoke(Object action, Class<?>[] cls) {
		Object result = null;
		try {
			result = dynamicInvokeInternal(action, cls);
		} catch(Throwable t) {
			ExceptionHandler.throwRuntimeException(t);
		}
		return result;
	}
	
	private Object dynamicInvokeInternal(Object action, Class<?>[] cls) throws Throwable {
		if(GroovySupport.isClosure(action)) {
			return callClosure(action, cls);
		}
		Method method = getMethod(action);
		if(method != null) {
			return invokeMethod(method, action, cls);
		}
		throw new ConfigException("M_Fixture_Temp_Conductor_CannotFindExecutable", action.getClass().getName());
	}
	
	private Object invokeMethod(Method method, Object action, Class<?>[] cls) throws Throwable {
		try {
			Class<?>[] elementTypes = getElementTypes(method, cls);
			Object[] parameters = getParameters(method.getParameterTypes(), elementTypes);
			method.setAccessible(true);
			return method.invoke(action, parameters);
		} catch(InvocationTargetException e) {
			throw e.getCause();
		}
	}

	protected Class<?>[] getElementTypes(Method method, Class<?>[] cls) {
		Type[] types = method.getGenericParameterTypes();
		Class<?>[] elementTypes = new Class<?>[types.length];
		if(cls != null) {
			for(int i = 0; i < cls.length; i++) {
				elementTypes[i] = cls[i];
			}
		}
		for(int i = 0; i < types.length; i++) {
			if(elementTypes[i] == null && types[i] instanceof ParameterizedType) {
				elementTypes[i] = (Class<?>) ((ParameterizedType)types[i]).getActualTypeArguments()[0];
			}
		}
		return elementTypes;
	}


	private Method getMethod(Object action) {
		Class<?> cls = action.getClass();
		Method[] method = cls.getDeclaredMethods();
		for(int i = 0; i < method.length; i++) {
			int mod = method[i].getModifiers();
			if(Modifier.isPublic(mod) && !isSynthetic(mod) && 
					!Modifier.isStatic(mod) && !Modifier.isAbstract(mod) ) {
				return method[i];
			}
		}
		return null;
	}

	private Object callClosure(Object action, Class<?>[] cls) throws Exception {
		Class<?>[] parameterTypes = GroovySupport.getParameterTypess(action);
		Object[] parameters;
		if(mayBeItParameter(parameterTypes)) {
			parameters = getNoParameters();
		} else {
			parameters = getParameters(parameterTypes, cls);
		}
		return GroovySupport.invoke(action, new Object[]{parameters});
	}

	private Object[] getNoParameters() {
		parameters = new Object[0]; 
		parameterNames = new String[0];
		return parameters;
	}

	// 暗黙的に it パラメータが指定されている状況かどうかを判断する
	private boolean mayBeItParameter(Class<?>[] parameterTypes) {
		if(parameterTypes.length > 1) {
			return false;
		}
		if(!Object.class.equals(parameterTypes[0])) {
			return false;
		}
		Section section = testCase.getSection(Section.SectionType.OBJECT_FOR_EXEC);
		if(section != null && section.hasTable(0)) {
			return false;
		}
		return true;
	}

	private Object[] getParameters(Class<?>[] types, Class<?>[] elementTypes) {
		Object[] parameters = new Object[types.length];
		this.parameters = parameters;
		this.parameterNames = new String[parameters.length];
		
		for(int i = 0; i < parameters.length; i++) {
			parameters[i] = getParameter(types[i], get(elementTypes, i), i);
		}
		return parameters;
	}

	private Class<?> get(Class<?>[] types, int index) {
		if(index < types.length) {
			return types[index];
		}
		return null;
	}

	private Object getParameter(Class<?> type, Class<?> elementType, int index) {
		String tableName = getTableName(index, Section.SectionType.OBJECT_FOR_EXEC);
		parameterNames[index] = tableName;
		return getParameter(type, elementType, tableName);
	}

	private String getTableName(int index, SectionType sectionType) {
		Section section = testCase.getSection(sectionType);
		if(section == null) {
			throw new ConfigException("M_Fixture_FixtureBook_GetSection_" + sectionType, testCase);
		}
		
		if(!section.hasTable(index)) {
			throw new ConfigException("M_Fixture_FixtureBook_GetTable_" + sectionType, index + 1, section);
		}
		
		Table table = section.getTable(index);
		return table.getName();
	}

	private Object getParameter(Class<?> type, Class<?> elementType, String tableName) {
		if(tableName == null) {
			return null;
		}
		
		if(Object.class.equals(type) && elementType != null) {
			type = elementType;
		}
		
		if(elementType == null) {
			elementType = HashMap.class;
		}
		
		if(type.isArray()) {
			Class<?> objectType = type.getComponentType();
			return testCase.getArray(objectType, tableName);
		}
		if(type.isAssignableFrom(ArrayList.class)) {
			return testCase.getList(elementType, tableName);
		}
		return testCase.getObject(type, tableName);
	}

	@Override
	public void expectReturn(Object action, Class<?>... cls) {
		testCase.setup();
		Object result = dynamicInvoke(action, cls);
		validate(toResult(result));
		testCase.validateStorageInternal();
	}

	private void validate(Result result) {
		Section section = testCase.getSection(SectionType.EXPECTED_RESULT);
		if(section != null && section.hasTable(result.name)) {
			testCase.validate(result.value, result.name);
		}
	}

	private Result toResult(Object result) {
		String tableName = getTableName(0, SectionType.EXPECTED_RESULT);
		return new Result(tableName, result);
	}

	@Override
	public void expectThrown(Class<? extends Throwable> exceptionClass,
			final Object action, final Class<?>... cls) {
		testCase.setup();
		testCase.validate(exceptionClass, 
				new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						try {
							dynamicInvokeInternal(action, cls);
						} catch(Throwable t) {
							ExceptionHandler.throwException(t);
						}
						return null;
					}
				}, null);
		testCase.validateStorageInternal();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParameterAt(int index) {
		assertParameterIndex(index, "getParameterAt");
		return (T)parameters[index];
	}

	@Override
	public void validateParameterAt(int index) {
		assertParameterIndex(index, "validateParameterAt");
		testCase.validate(parameters[index], parameterNames[index]);
	}

	@Override
	public void validateParameterAt(int index, String name) {
		assertParameterIndex(index, "validateParameterAt");
		testCase.validate(parameters[index], name);
	}
	
	private void assertParameterIndex(int index, String methodName) {
		if(parameters == null) {
			throw new ConfigException("M_Fixture_Temp_Conductor_InvalidStatus", methodName);
		}
		if(index >= parameters.length) {
			throw new ConfigException("M_Fixture_Temp_Conductor_InvalidParameterIndex", index);
		}
	}

	@Override
	public void expect(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) {
		testCase.setup();
		dynamicInvoke(targetClass, targetMethod, parameterClass);
		testCase.validateStorageInternal();
	}

	private Object dynamicInvoke(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) {
		Object result = null;
		try {
			result = dynamicInvokeInternal(targetClass, targetMethod, parameterClass);
		} catch(Throwable t) {
			ExceptionHandler.throwRuntimeException(t);
		}
		return result;
	}
	
	private Object dynamicInvokeInternal(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) throws Throwable {
		Method method = getMethod(targetClass, targetMethod, parameterClass);
		Object instance = null;
		if(!Modifier.isStatic(method.getModifiers())) {
			instance = newInstance(targetClass);
		}
		return invokeMethod(method, instance, null);
	}

	private Object newInstance(Class<?> cls) {
		try {
			return cls.newInstance();
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_Temp_Conductor_CannotCreateInstance", cls.getName());
		}
	}
	
	private Method getMethod(Class<?> cls, String targetMethod,
			Class<?>[] parameterClass) {
		if(parameterClass != null && parameterClass.length > 0) {
			try {
				return cls.getMethod(targetMethod, parameterClass);
			} catch (NoSuchMethodException e) {
				throw new ConfigException(e, "M_Fixture_Temp_Conductor_CannotFindMethod", 
						cls.getName(), getMethodName(targetMethod, parameterClass));
			}
		}

		 Method method = MethodFinder.findMethod(cls, targetMethod);
		 if(method == null) {
			 throw new ConfigException("M_Fixture_Temp_Conductor_CannotFindMethod", cls.getName(), targetMethod);
		 }
		 return method;
	}

	private Object getMethodName(String targetMethod, Class<?>[] parameterClass) {
		StringBuilder sb = new StringBuilder();
		sb.append(targetMethod).append("(");
		for(int i = 0; i < parameterClass.length; i++) {
			if(i > 0) {
				sb.append(", ");
			}
			sb.append(parameterClass[i].getName());
		}
		sb.append(")");
		return sb;
	}

	@Override
	public void expectReturn(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass) {
		testCase.setup();
		Object result = dynamicInvoke(targetClass, targetMethod, parameterClass);
		validate(toResult(result));
		testCase.validateStorageInternal();
	}

	@Override
	public void expectThrown(Class<? extends Throwable> exceptionClass,
			final Class<?> targetClass, final String targetMethod, final Class<?>[] parameterClass) {
		testCase.setup();
		testCase.validate(exceptionClass, 
				new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						try {
							dynamicInvokeInternal(targetClass, targetMethod, parameterClass);
						} catch(Throwable t) {
							ExceptionHandler.throwException(t);
						}
						return null;
					}
				}, null);
		testCase.validateStorageInternal();
	}
}
