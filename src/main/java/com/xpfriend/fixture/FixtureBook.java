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
package com.xpfriend.fixture;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;

import com.xpfriend.fixture.staff.Book;
import com.xpfriend.fixture.staff.Case;
import com.xpfriend.fixture.staff.Sheet;
import com.xpfriend.fixture.toolkit.MethodFinder;
import com.xpfriend.fixture.toolkit.PathUtil;
import com.xpfriend.fixture.toolkit.StackFrameFinder;
import com.xpfriend.junk.Config;
import com.xpfriend.junk.ConfigException;
import com.xpfriend.junk.ExceptionHandler;
import com.xpfriend.junk.Loggi;
import com.xpfriend.junk.Resi;

/**
 * FixtureBook 操作ツール。
 * 
 * @author Ototadana
 */
public class FixtureBook {
	
	private static class FeatureNameFinder {
		private Class<?> specInfoBuilderClass;
		private Method buildMethod;
		private Method toFeatureNameMethod;
		
		public FeatureNameFinder() {
			try {
				specInfoBuilderClass = Class.forName("org.spockframework.runtime.SpecInfoBuilder");
			} catch(Exception e) {
				ExceptionHandler.ignore(e);
			}
			
			if(specInfoBuilderClass == null) {
				return;
			}
			
			try {
				buildMethod = specInfoBuilderClass.getMethod("build");
				toFeatureNameMethod = Class.forName("org.spockframework.runtime.model.SpecInfo")
						.getMethod("toFeatureName", String.class);
			} catch (Exception e) {
				throw new ConfigException(e); // ここにはこないはず
			}
		}

		public String find(Method method) {
			if(specInfoBuilderClass == null) {
				return method.getName();
			}

			try {
				Class<?> type = method.getDeclaringClass();
				Object specInfoBuilder = specInfoBuilderClass.getConstructor(Class.class).newInstance(type);
				Object specInfo = buildMethod.invoke(specInfoBuilder);
				return (String)toFeatureNameMethod.invoke(specInfo, method.getName());
			} catch (Exception e) {
				return method.getName();
			}
		}
		
	}

	private static class FixtureInfo {
		Class<?> testClass;
		String filePath;
		String sheetName;
		String testCaseName;

		public FixtureInfo(Class<?> testClass, String filePath, String sheetName, String testCaseName) {
			this.testClass = testClass;
			this.filePath = filePath;
			this.sheetName = sheetName;
			this.testCaseName = testCaseName;
		}
	}
	
	private static final String NAME_SEPARATOR_KEY = "FixtureBook.nameSeparator";
	private static final String DEFAULT_NAME_SEPARATOR = "__";
	private static String nameSeparator;
	private static FeatureNameFinder featureNameFinder = new FeatureNameFinder();
	static {
		Resi.add(FixtureBook.class.getPackage().getName());
		initNameSeparator();
	}

	private static void initNameSeparator() {
		nameSeparator = Config.get(NAME_SEPARATOR_KEY, DEFAULT_NAME_SEPARATOR);
	}
	
	private static String[] getNamesByClassAndFeatureName(Class<?> type, String featureName) {
		return new String[] { type.getSimpleName(), featureName };
	}
	
	private static String[] getNamesByAnnotation(Method method) {
		Fixture annotation = method.getAnnotation(Fixture.class);
		if(annotation == null) {
			return null;
		}
		return annotation.value();
	}
	
	private static String[] getNamesByFeatureName(String name) {
		return name.split(nameSeparator);
	}
	
	private static String getFilePath(StackTraceElement stackTraceElement, Class<?> type, Method method) {
		FixtureBookPath annotation = method.getAnnotation(FixtureBookPath.class);
		if(annotation == null) {
			annotation = type.getAnnotation(FixtureBookPath.class);
		}
		if(annotation != null) {
			String path = annotation.value();
			if(path != null) {
				return path;
			}
		}
		return getDefaultFilePath(stackTraceElement);
	}

	private static String getDefaultFilePath(StackTraceElement stackTraceElement) {
		String className = stackTraceElement.getClassName().replace('.', '/');
		int dollarIndex = className.indexOf('$');
		if(dollarIndex > -1) {
			className = className.substring(0, dollarIndex);
		}
		return className + ".xlsx";
	}
	
	private Class<?> testClass;
	private Book book;
	private Sheet sheet;
	private Case testCase;
	
	/**
	 * FixtureBook を作成する。
	 */
	public FixtureBook() {
		this(false);
	}

	private FixtureBook(boolean initialize) {
		if(initialize) {
			initialize();
		}
	}

	private void initialize() {
		try {
			initialize(StackFrameFinder.findCaller(FixtureBook.class));
		} catch(Exception e) {
			throw new ConfigException(e);
		}
	}

	private void initialize(List<StackTraceElement> stackFrames) throws ClassNotFoundException, NoSuchMethodException, IOException {
		for(int i = 0; i < stackFrames.size(); i++) {
			FixtureInfo fixtureInfo = getFixtureInfo(stackFrames.get(i), false);
			if(fixtureInfo != null) {
				initialize(fixtureInfo);
				return;
			}
		}
		initialize(getFixtureInfo(stackFrames.get(0), true));
	}
	
	private FixtureInfo getFixtureInfo(StackTraceElement stackFrame, boolean force) throws ClassNotFoundException, NoSuchMethodException {
		Class<?> type = Class.forName(stackFrame.getClassName());
		Method method = findMethod(type, stackFrame.getMethodName());
		String[] name = getNamesByAnnotation(method);
		if(name != null && name.length > 0) {
			if(name.length == 1) {
				name = new String[]{type.getSimpleName(), name[0]};
			}
			return createFixtureInfo(stackFrame, method, type, name);
		}
		String featureName = featureNameFinder.find(method);
		name = getNamesByFeatureName(featureName);
		if(name.length >= 2) {
			return createFixtureInfo(stackFrame, method, type, name);
		}
		
		if(force) {
			name = getNamesByClassAndFeatureName(type, featureName);
			return createFixtureInfo(stackFrame, method, type, name);
		}
		return null;
	}
	
	private Method findMethod(Class<?> type, String methodName) throws NoSuchMethodException {
		Method method = MethodFinder.findMethod(type, methodName);
		if(method == null) {
			method = type.getDeclaredMethod(methodName);
		}
		return method;
	}

	private FixtureInfo createFixtureInfo(StackTraceElement stackFrame,
			Method method, Class<?> type, String[] name) {
		String filePath = PathUtil.editFilePath(getFilePath(stackFrame, type, method));
		return new FixtureInfo(type, filePath, name[0], name[1]);
	}

	private void initialize(FixtureInfo fixtureInfo) throws IOException {
		testClass = fixtureInfo.testClass;
		book = Book.getInstance(fixtureInfo.testClass, fixtureInfo.filePath);
		sheet = book.getSheet(fixtureInfo.sheetName);
		testCase = sheet.getCase(fixtureInfo.testCaseName);
		Loggi.debug("FixtureBook : Case : " + testCase);
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」を用いてデータベースに対する更新を行う。
	 */
	public void setup() {
		initializeIfNotYet();
		testCase.setup();
	}

	private void initializeIfNotYet() {
		if(testCase == null) {
			initialize();
		}
	}

	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトを作成する。
	 *
	 * @param cls 作成するオブジェクトのクラス。
	 * @return 作成されたオブジェクト。
	 */
	public <O> O getObject(Class<? extends O> cls) {
		return getObject(cls, null);
	}
	
	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトを作成する。
	 *
	 * @param cls 作成するオブジェクトのクラス。
	 * @param typeName テーブル定義名。
	 * @return 作成されたオブジェクト。
	 */
	public <O> O getObject(Class<? extends O> cls, String typeName) {
		initializeIfNotYet();
		return testCase.getObject(cls, typeName);
	}

	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトのリストを作成する。
	 *
	 * @param cls 作成するリスト要素オブジェクトクラス。
	 * @return 作成されたリスト。
	 */
	public <O> List<O> getList(Class<? extends O> cls) {
		return getList(cls, null);
	}

	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトのリストを作成する。
	 *
	 * @param cls 作成するリスト要素オブジェクトクラス。
	 * @param typeName テーブル定義名。
	 * @return 作成されたリスト。
	 */
	public <O> List<O> getList(Class<? extends O> cls, String typeName) {
		initializeIfNotYet();
		return testCase.getList(cls, typeName);
	}
	
	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトの配列を作成する。
	 *
	 * @param cls 作成する配列要素オブジェクトクラス。
	 * @return 作成された配列。
	 */
	public <O> Object getArray(Class<? extends O> cls) {
		return getArray(cls, null);
	}
	
	/**
	 * 「D.パラメタ」に記述されたデータを用いてオブジェクトの配列を作成する。
	 *
	 * @param cls 作成する配列要素オブジェクトクラス。
	 * @param typeName テーブル定義名。
	 * @return 作成された配列。
	 */
	public <O> Object getArray(Class<? extends O> cls, String typeName) {
		initializeIfNotYet();
		return testCase.getArray(cls, typeName);
	}
	
	/**
	 * 指定されたオブジェクトが「E.取得データ」に定義された予想結果と適合するかどうかを調べる。
	 * 予想結果と適合しない場合は {@link AssertionError} がスローされる。
	 * 
	 * @param obj 調べるオブジェクト。
	 * @exception AssertionError 予想結果と適合しない場合。
	 */
	public void validate(Object obj) {
		validate(obj, null);
	}
	
	/**
	 * 指定されたオブジェクトが「E.取得データ」に定義された予想結果と適合するかどうかを調べる。
	 * 予想結果と適合しない場合は {@link AssertionError} がスローされる。
	 * 
	 * @param obj 調べるオブジェクト。
	 * @param typeName テーブル定義名。
	 * @exception AssertionError 予想結果と適合しない場合。
	 */
	public void validate(Object obj, String typeName) {
		initializeIfNotYet();
		testCase.validate(obj, typeName);
	}

	/**
	 * 指定された処理で発生した例外が「E.取得データ」に記述された予想結果と適合するかどうかを調べる。
	 * 
	 * @param exceptionClass　発生が予想される例外。
	 * @param action テスト対象処理。
	 */
	public void validate(Class<? extends Throwable> exceptionClass, Callable<?> action) {
		validate(exceptionClass, action, null);
	}
	
	/**
	 * 指定された処理で発生した例外が「E.取得データ」に記述された予想結果と適合するかどうかを調べる。
	 * 
	 * @param exceptionClass　発生が予想される例外。
	 * @param action テスト対象処理。
	 * @param typeName テーブル定義名。
	 */
	public void validate(Class<? extends Throwable> exceptionClass, Callable<?> action, String typeName) {
		initializeIfNotYet();
		testCase.validate(exceptionClass, action, typeName);
	}

	/**
	 * 現在のデータベース状態が「F.更新後データ」に記述された予想結果と適合するかどうかを調べる。
	 * 予想結果と適合しない場合は例外がスローされる。
	 */
	public void validateStorage() {
		initializeIfNotYet();
		testCase.validateStorage();
	}
	
	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にしてテスト対象メソッドを実行し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * テスト対象メソッドは、テストクラス名およびテストカテゴリ（シート）名からテスト対象クラスおよびメソッドを類推して実行する。
	 * 例えば、テストクラス名が 「ExampleTest」で、テストカテゴリ（シート）名が「execute」の場合、
	 * Example クラスの execute メソッドが実行される。
	 * 
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expect() {
		return expect((Class<?>)null, (String)null);
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、指定されたテスト対象メソッドを実行し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param targetClass テスト対象クラス。
	 * @param targetMethod テスト対象メソッド。
	 * @param targetMethodParameter テスト対象メソッドの引数。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expect(Class<?> targetClass, String targetMethod, Class<?>... targetMethodParameter) {
		FixtureBook fixtureBook = new FixtureBook(true);
		if(targetClass == null) {
			targetClass = fixtureBook.getDefaultTargetClass();
		}
		if(targetMethod == null) {
			targetMethod = fixtureBook.getDefaultTargetMethod(targetClass);
		}
		fixtureBook.testCase.expect(targetClass, targetMethod, targetMethodParameter);
		return fixtureBook;	
	}
	
	private String getDefaultTargetMethod(Class<?> targetClass) {
		String methodName = sheet.getName();
		Method method = MethodFinder.findMethod(targetClass, methodName);
		if(method == null) {
			throw new ConfigException("M_Fixture_FixtureBook_GetDefaultTargetMethod", methodName, targetClass.getName(), testCase);
		}
		return methodName;
	}

	private Class<?> getDefaultTargetClass() {
		String targetClassName = getTargetClassName();
		try {
			return Class.forName(targetClassName);
		} catch(Exception e) {
			throw new ConfigException(e, "M_Fixture_FixtureBook_GetDefaultTargetClass", targetClassName, testCase);
		}
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、action で指定された処理を実行し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param action 実行する処理。
	 *        ひとつの public メソッドだけを宣言したクラスのインスタンス、または、Groovy のクロージャが指定可能。
	 * @param cls 「D.パラメタ」で作成するクラス。作成するクラスが List や配列の場合は要素クラスを指定する。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expect(Object action, Class<?>... cls) {
		FixtureBook fixtureBook = new FixtureBook(true);
		fixtureBook.testCase.expect(action, cls);
		return fixtureBook;	
	}
	
	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にしてテスト対象メソッドを実行し、
	 * テスト対象メソッドの戻り値を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * テスト対象メソッドは、テストクラス名およびテストカテゴリ（シート）名からテスト対象クラスおよびメソッドを類推して実行する。
	 * 例えば、テストクラス名が 「ExampleTest」で、テストカテゴリ（シート）名が「execute」の場合、
	 * Example クラスの execute メソッドが実行される。
	 * 
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectReturn() {
		return expectReturn((Class<?>)null, (String)null);
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、指定されたテスト対象メソッドを実行し、
	 * テスト対象メソッドの戻り値を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param targetClass テスト対象クラス。
	 * @param targetMethod テスト対象メソッド。
	 * @param targetMethodParameter テスト対象メソッドの引数。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectReturn(Class<?> targetClass, String targetMethod, Class<?>... targetMethodParameter) {
		FixtureBook fixtureBook = new FixtureBook(true);
		if(targetClass == null) {
			targetClass = fixtureBook.getDefaultTargetClass();
		}
		if(targetMethod == null) {
			targetMethod = fixtureBook.getDefaultTargetMethod(targetClass);
		}
		fixtureBook.testCase.expectReturn(targetClass, targetMethod, targetMethodParameter);
		return fixtureBook;	
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、action で指定された処理を実行し、
	 * テスト対象メソッドの戻り値を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param action 実行する処理。
	 *        ひとつの public メソッドだけを宣言したクラスのインスタンス、または、Groovy のクロージャが指定可能。
	 * @param cls 「D.パラメタ」で作成するクラス。作成するクラスが List や配列の場合は要素クラスを指定する。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectReturn(Object action, Class<?>... cls) {
		FixtureBook fixtureBook = new FixtureBook(true);
		fixtureBook.testCase.expectReturn(action, cls);
		return new FixtureBook(false);
	}
	
	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にしてテスト対象メソッドを実行し、
	 * テスト対象メソッドで発生した例外を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * テスト対象メソッドは、テストクラス名およびテストカテゴリ（シート）名からテスト対象クラスおよびメソッドを類推して実行する。
	 * 例えば、テストクラス名が 「ExampleTest」で、テストカテゴリ（シート）名が「execute」の場合、
	 * Example クラスの execute メソッドが実行される。
	 * 
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectThrown() {
		return expectThrown(Throwable.class, (Class<?>)null, (String)null);
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、指定されたテスト対象メソッドを実行し、
	 * テスト対象メソッドで発生した例外を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param exceptionClass 発生が予想される例外クラス。
	 * @param targetClass テスト対象クラス。
	 * @param targetMethod テスト対象メソッド。
	 * @param targetMethodParameter テスト対象メソッドの引数。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectThrown(Class<? extends Throwable> exceptionClass, Class<?> targetClass, String targetMethod, Class<?>... targetMethodParameter) {
		FixtureBook fixtureBook = new FixtureBook(true);
		if(targetClass == null) {
			targetClass = fixtureBook.getDefaultTargetClass();
		}
		if(targetMethod == null) {
			targetMethod = fixtureBook.getDefaultTargetMethod(targetClass);
		}
		fixtureBook.testCase.expectThrown(exceptionClass, targetClass, targetMethod, targetMethodParameter);
		return fixtureBook;	
	}

	private String getTargetClassName() {
		String testClassName = testClass.getName();
		if(testClassName.endsWith("Test") || testClassName.endsWith("Spec")) {
			return testClassName.substring(0, testClassName.length() - 4);
		} else {
			throw new ConfigException("M_Fixture_FixtureBook_GetDefaultTargetClassName", testClassName, testCase);
		}
	}

	/**
	 * 「B.テストデータクリア条件」と「C.テストデータ」でデータベーステーブルの登録を行い、
	 * 「D.パラメタ」に定義されたオブジェクトを引数にして、action で指定された処理を実行し、
	 * テスト対象メソッドで発生した例外を「E.取得データ」の値で検証し、
	 * 「F.更新後データ」にテーブル定義があれば データベースの値検証を行う。
	 * 
	 * @param exceptionClass 発生が予想される例外クラス。
	 * @param action 実行する処理。
	 *        ひとつの public メソッドだけを宣言したクラスのインスタンス、または、Groovy のクロージャが指定可能。
	 * @param cls 「D.パラメタ」で作成するクラス。作成するクラスが List や配列の場合は要素クラスを指定する。
	 * @return FixtureBook のインスタンス。
	 */
	public static FixtureBook expectThrown(Class<? extends Throwable> exceptionClass, Object action, Class<?>... cls) {
		FixtureBook fixtureBook = new FixtureBook(true);
		fixtureBook.testCase.expectThrown(exceptionClass, action, cls);
		return fixtureBook;
	}
	
	/**
	 * expect, expectReturn, expectThrown メソッド実行時に、
	 * 「D.パラメタ」定義をもとに作成され、渡された引数を取得する。
	 * 
	 * @param index　取得する引数のインデックス。
	 * @return 引数の値。
	 */
	public <T> T getParameterAt(int index) {
		initializeIfNotYet();
		return testCase.getParameterAt(index);
	}

	/**
	 * expect, expectReturn, expectThrown メソッドを実行した後の引数の値が、
	 * 「E.取得データ」に記述された値と同じになっているかどうかを検証する。
	 * このメソッドでは、「E.取得データ」のテーブル定義名は「D.パラメタ」の定義名と同じものとみなされる。
	 * 
	 * @param index 検証する引数のインデックス。複数指定可能。
	 * @return FixtureBook のインスタンス。
	 */
	public FixtureBook validateParameterAt(int... index) {
		initializeIfNotYet();
		for(int i : index) {
			testCase.validateParameterAt(i);
		}
		return this;
	}

	/**
	 * expect, expectReturn, expectThrown メソッドを実行した後の引数の値が、
	 * 「E.取得データ」に記述された値と同じになっているかどうかを検証する。
	 * 
	 * @param index 検証する引数のインデックス。
	 * @param name テーブル定義名。
	 * @return FixtureBook のインスタンス。
	 */
	public FixtureBook validateParameterAt(int index, String name) {
		initializeIfNotYet();
		testCase.validateParameterAt(index, name);
		return this;
	}
}
