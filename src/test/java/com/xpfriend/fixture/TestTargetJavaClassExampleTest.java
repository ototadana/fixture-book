package com.xpfriend.fixture;

import org.junit.Test;

public class TestTargetJavaClassExampleTest {

	@Test
	public void save__引数なしのexpectはテストクラス名とテストカテゴリからテスト対象メソッドを類推して実行する() {
		FixtureBook.expect();
	}
	
	@Test
	public void getEmployees__引数なしのexpectReturnはテストクラス名とテストカテゴリからテスト対象メソッドを類推して実行する() {
		FixtureBook.expectReturn();
	}
	
	@Test
	public void delete__引数なしのexpectThrownはテストクラス名とテストカテゴリからテスト対象メソッドを類推して実行する() {
		FixtureBook.expectThrown();
	}
}
