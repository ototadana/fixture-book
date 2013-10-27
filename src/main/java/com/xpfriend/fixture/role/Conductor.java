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
package com.xpfriend.fixture.role;

/**
 * フィクスチャ操作のコンダクター。
 * 
 * @author Ototadana
 */
public interface Conductor extends Actor {

	void expect(Object action, Class<?>... cls);

	void expectReturn(Object action, Class<?>... cls);

	void expectThrown(Class<? extends Throwable> exceptionClass, Object action, Class<?>... cls);

	<T> T getParameterAt(int index);

	void validateParameterAt(int index);

	void validateParameterAt(int index, String name);

	void expect(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass);

	void expectReturn(Class<?> targetClass, String targetMethod,
			Class<?>[] parameterClass);

	void expectThrown(Class<? extends Throwable> exceptionClass,
			Class<?> targetClass, String targetMethod, Class<?>[] parameterClass);
}
