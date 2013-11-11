
### FixtureBook チュートリアル (JUnit編)

#06 テストメソッドの簡略化
==========================

ここまでの例を見てもわかるようにテストメソッドの記述は、

1.  getObject / getList を呼び出してテスト対象メソッド引数に渡すデータを作成する。
2.  テスト対象処理を呼び出す。
3.  テスト対象処理が戻り値を返すならば、validate メソッドを呼び出す。
4.  テスト対象処理がデータベースを更新するならば、validateStorage メソッドを呼び出す。

という決まりきったものになりがちです。

そこで FixtureBook クラスには、上記のような決まりきった処理を1行で書けるようにするためのメソッドが用意されています。

*   `expect` (戻り値の検証が必要ない場合に使う)
*   `expectReturn` (戻り値を検証したい場合に使う)
*   `expectThrown` (例外発生を検証したい場合に使う)


テストメソッド記述例
--------------------

expect / expectReturn / expectThrown メソッドを使用して
これまでのテストメソッドを書き直すと以下のようになります。


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
        @Fixture({"delete", "指定した従業員データのIDが未設定ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"})
        public void delete__指定した従業員データのIDが未設定ならばInvalid_IDというメッセージを持つIllegalArgumentExceptionが発生する() {
            FixtureBook.expectThrown();
        }
    }

上記のように記述した場合、以下のルールに従ってテスト対象メソッドが呼び出されます。

*   テストクラス名（例：EmployeeStoreTest）から "Test" を外した名前のクラス（例：EmployeeStore）をテスト対象クラスとする。
*   シート名（例：save, delete 等）と同じ名前のメソッドをテスト対象メソッドとする。

このルールに従えない場合、expect / expectReturn / expectThrown メソッドの引数で、
テスト対象クラスやテスト対象メソッドを明示的に指定することも可能です。


テスト対象処理の独自実装
------------------------

テスト対象メソッドを直接呼び出すのではなく、
何らかの前後処理でラップして呼び出したい場合には、
以下のように独自処理を実装したクラスを引数で指定することも可能です。


    public class EmployeeStoreTest {

        @Test
        public void save__データベーステーブルEMPLOYEEに従業員データを新規追加できる() {
            FixtureBook.expect(new Save());
        }
        
        @Test
        public void delete__指定した従業員データのIDをキーにしてデータベーステーブルEMPLOYEE上のデータが削除される() {
            FixtureBook.expect(new Delete());
        }
        
        @Test
        public void getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる() {
            FixtureBook.expectReturn(new GetAllEmployees());
        }

        @Test
        public void getEmployees__引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる() {
            FixtureBook.expectReturn(new GetEmployees());
        }
        
        @Test
        public void getEmployees__引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる() {
            FixtureBook.expectReturn(new GetEmployees());
        }
        
        @Test
        @Fixture({"delete", "指定した従業員データのIDが未設定ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"})
        public void delete__指定した従業員データのIDが未設定ならばInvalid_IDというメッセージを持つIllegalArgumentExceptionが発生する() {
            FixtureBook.expectThrown(IllegalArgumentException.class, new Delete());
        }

        // 以下テスト用独自処理の実装
        class Save {
            public void save(List<Employee> p) throws Exception {
                ...
                new EmployeeStore().save(p);
            }
        }

        class Delete {
            public void delete(List<Employee> p) throws Exception {
                ...
                new EmployeeStore().delete(p);
            }
        }
        
        class GetAllEmployees {
            public List<Employee> getAllEmployees() throws Exception {
                ...
                return new EmployeeStore().getAllEmployees();
            }
        }

        class GetEmployees {
            public List<Employee> getEmployees(Employee p) throws Exception {
                ...
                return new EmployeeStore().getEmployees(p);
            }
        }
    }


独自処理を実装したクラスは、以下のルールを守って作成する必要がります。

*   呼び出されるメソッドは、static でなく、abstract でない public メソッドとする。
*   一つのクラスの中に複数の public メソッドを作ってはならない。


------------------------

*   [FixtureBook チュートリアル (JUnit編)](./Tutorial-JUnit.md)
    *   [#01 データ登録メソッド save のテスト](./Tutorial-JUnit-save.md)
    *   [#02 データ削除メソッド delete のテスト](./Tutorial-JUnit-delete.md)
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-JUnit-getAllEmployees.md)
    *   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-JUnit-getEmployees.md)
    *   [#05 例外発生のテスト](./Tutorial-JUnit-Exception.md)
    *   #06 テストメソッドの簡略化
