
### FixtureBook チュートリアル (Spock編)

#02 データ削除メソッド delete のテスト
======================================

テスト対象のメソッド
--------------------

ここでは、以下の delete メソッドを対象にテストを行います。

```java
    public class EmployeeStore {

        (...)

        def delete(List<Employee> employees) {
            def sql = createSql()
            employees.each {
                sql.execute("DELETE FROM EMPLOYEE WHERE ID = :id", it)
            }
        }

        (...)
    }
```

delete メソッドは、引数として渡された従業員データのIDに合致する行をデータベーステーブル
(EMPLOYEE) から削除します。


テストケース
------------

以下のテストケースをテストします。

*   <b>指定した従業員データのIDをキーにしてデータベーステーブル (EMPLOYEE) 上のデータが削除される</b>


FixtureBook の記述
------------------

FixtureBook では以下の記述を行います。

*   `B.テストデータクリア条件` で `*` を指定して、一旦全データを消去する。
*   削除されるデータと削除されないデータをそれぞれ1件ずつデータベーステーブル (EMPLOYEE) に事前登録するために、
    `B.テストデータ` に記述する。
*   delete メソッドの引数として渡すデータを `D.パラメタ` に記述する。
    delete メソッドでは ID列以外は利用されないため、ID列のみ記述する。
*   delete メソッドの実行後の EMPLOYEE テーブルのデータ状態を予想して `F.更新後データ` に記述する。
    削除されるデータ行のC列には、削除済みを表す 'D' を記述する。


![FixtureBook記述](./images/Tutorial-delete-01.png?raw=true)


テストメソッドの記述
--------------------

テストメソッドは以下の内容で記述します。

1.  delete メソッドの引数として渡すリストを `fixtureBook.getList(Employee, "Employee")` で取得する。
    これにより、`D.パラメタ` の "Employee" で記述した値で初期化された DataTable が取得できる。
2.  上記で取得したリストを引数にして、テスト対象メソッド (delete) を呼び出す。
3.  `fixtureBook.validateStorage()` の呼び出しにより、データベーステーブル状態を検証する。
    これにより、`F.更新後データ`に記述した内容と、
    実際のデータベーステーブルの状態が同じかどうかのチェックが行われる。

```
    def "delete__指定した従業員データのIDをキーにしてデータベーステーブルEMPLOYEE上のデータが削除される"() {
        setup:
        List<Employee> employees = fixtureBook.getList(Employee, "Employee")
        
        when:
        new EmployeeStore().delete(employees)
        
        then:
        fixtureBook.validateStorage()
    }
```

*   今回のように `D.パラメタ` にひとつのテーブル定義しか記述されていない場合は、
    `fixtureBook.getList(Employee)` というふうに引数の "Employee" 
    を省略してメソッド呼び出しを行うこともできます。



------------------------

*   [FixtureBook チュートリアル (Spock編)](./Tutorial-Spock.md)
    *   [#01 データ登録メソッド save のテスト](./Tutorial-Spock-save.md)
    *   #02 データ削除メソッド delete のテスト
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-Spock-getAllEmployees.md)
    *   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-Spock-getEmployees.md)
    *   [#05 例外発生のテスト](./Tutorial-Spock-Exception.md)
    *   [#06 テストメソッドの簡略化](./Tutorial-Spock-expect.md)
