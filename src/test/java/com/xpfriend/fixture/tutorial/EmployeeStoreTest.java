package com.xpfriend.fixture.tutorial;

import org.junit.Test;

import com.xpfriend.fixture.Fixture;
import com.xpfriend.fixture.FixtureBook;

public class EmployeeStoreTest {

	@Test
	public void save__データベーステーブルEMPLOYEEに従業員データを新規追加できる() {
		FixtureBook.expect();
	}

	@Test
	public void delete__指定した従業員データのIDをキーにしてデータベーステーブルEMPLOYEE上のデータが削除される() {
		FixtureBook.expect();
	}

	@Test
	public void getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる() {
		FixtureBook.expectReturn();
	}

	@Test
	public void getEmployees__引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる() {
		FixtureBook.expectReturn();
	}

	@Test
	public void getEmployees__引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる() {
		FixtureBook.expectReturn();
	}

	@Test
	@Fixture({"delete", "指定した従業員データのIDが null ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"})
	public void delete__指定した従業員データのIDがnullならばInvalid_IDというメッセージを持つIllegalArgumentExceptionが発生する() {
		FixtureBook.expectThrown();
	}
}
