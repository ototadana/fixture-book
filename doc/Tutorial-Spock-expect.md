
### FixtureBook チュートリアル (Spock編)

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


    class EmployeeStoreTest extends Specification {
        
        def "save__データベーステーブルEMPLOYEEに従業員データを新規追加できる"() {
            expect: FixtureBook.expect()
        }
        
        def "delete__指定した従業員データのIDをキーにしてデータベーステーブルEMPLOYEE上のデータが削除される"() {
            expect: FixtureBook.expect()
        }
        
        def "getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる"() {
            expect: FixtureBook.expectReturn()
        }
        
        @Fixture(["getEmployees", "引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる"])
        def "getEmployees__引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる"() {
            expect: FixtureBook.expectReturn()
        }

        def "getEmployees__引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる"() {
            expect: FixtureBook.expectReturn()
        }
        
        def "delete__指定した従業員データのIDが未設定ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"() {
            expect: FixtureBook.expectThrown()
        }
    }

上記のように記述した場合、以下のルールに従ってテスト対象メソッドが呼び出されます。

*   テストクラス名（例：EmployeeStoreTest）から "Test" を外した名前のクラス（例：EmployeeStore）をテスト対象クラスとする。
*   シート名（例：save, delete 等）と同じ名前のメソッドをテスト対象メソッドとする。

このルールに従えない場合、expect / expectReturn / expectThrown メソッドの引数で、
テスト対象クラスやテスト対象メソッドを明示的に指定することも可能です。


テスト対象処理をクロージャとして実装する
----------------------------------------

テスト対象メソッドを直接呼び出すかわりに、
任意の処理をテスト対象としたい場合には、
以下のようにテスト対象処理をクロージャの形で指定することが可能です。


    class EmployeeStoreTest2 extends Specification {
        
        EmployeeStore dao = new EmployeeStore()
        
        def "save__データベーステーブルEMPLOYEEに従業員データを新規追加できる"() {
            expect:
            FixtureBook.expect({List p -> dao.save(p)}, Employee)
        }
        
        def "delete__指定した従業員データのIDをキーにしてデータベーステーブルEMPLOYEE上のデータが削除される"() {
            expect:
            FixtureBook.expect({List p -> dao.delete(p)}, Employee)
        }
        
        def "getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる"() {
            expect:
            FixtureBook.expectReturn({dao.getAllEmployees()})
        }
        
        @Fixture(["getEmployees", "引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる"])    
        def "getEmployees__引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる"() {
            expect:
            FixtureBook.expectReturn{Employee p -> dao.getEmployees(p)}
        }
        
        def "getEmployees__引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる"() {
            expect:
            FixtureBook.expectReturn({Employee p -> dao.getEmployees(p)})
        }
        
        def "delete__指定した従業員データのIDが未設定ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"() {
            expect:
            FixtureBook.expectThrown(IllegalArgumentException, {List p -> dao.delete(p)}, Employee)
        }
    }

クロージャのパラメタが List や配列 (FixtureBook.getList や FixtureBook.getArray で作成する必要があるもの) の場合、
以下のように expect メソッドの引数で要素クラス（この例では `Employee`）を指定する必要があります。

    FixtureBook.expect({List p -> dao.save(p)}, Employee)


------------------------

*   [FixtureBook チュートリアル (Spock編)](./Tutorial-Spock.md)
    *   [#01 データ登録メソッド save のテスト](./Tutorial-Spock-save.md)
    *   [#02 データ削除メソッド delete のテスト](./Tutorial-Spock-delete.md)
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-Spock-getAllEmployees.md)
    *   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-Spock-getEmployees.md)
    *   [#05 例外発生のテスト](./Tutorial-Spock-Exception.md)
    *   #06 テストメソッドの簡略化
