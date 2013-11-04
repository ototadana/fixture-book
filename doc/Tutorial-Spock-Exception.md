
### FixtureBook チュートリアル (Spock編)

#05 例外発生のテスト
====================

ここでは、メソッドの中で例外が発生することを想定したテストの例を紹介します。


テスト対象のメソッド
--------------------

[Delete メソッド](./Tutorial-Spock-delete.md) を以下のように修正し、
parameter の id が未設定 (0) の場合に例外が発生するようにしました。

```java
    public class EmployeeStore {

        (...)

        def delete(List<Employee> employees) {
            def sql = createSql()
            employees.each {
                if(it.id == 0) {
                    throw new IllegalArgumentException("Invalid ID")
                }
                sql.execute("DELETE FROM EMPLOYEE WHERE ID = :id", it)
            }
        }

        (...)
    }
```


テストケース
------------

上記の変更に伴い、以下のテストケースを追加します。

*   <b>指定した従業員データのIDが未設定ならば "Invalid ID" というメッセージを持つ IllegalArgumentException が発生する</b>


FixtureBook の記述
------------------

FixtureBook では以下の記述を行います。

*   Delete メソッドの引数として渡すデータを `D.パラメタ` に記述する。
    ID列には何も入力しない。
    ただし、全列に値が入力されていない場合、 Excel ファイルの読み込み時に「行そのものが存在しない」と解釈されてしまうため、
    左端の列に `C` と入力しておく（データが存在することを示す印です）。
*   キャッチした例外の内容をチェックするために `E.取得データ` の記述を行う。
    ここでは、例外メッセージの内容が想定通りかどうかをチェックしている。


![FixtureBook記述](./images/Tutorial-Exception-01.png?raw=true)


テストメソッドの記述
--------------------

validate メソッドの引数には、発生が予想される例外と、例外を発生させるテストコードとを指定します。

    def "delete__指定した従業員データのIDが未設定ならば \"Invalid ID\" というメッセージを持つ IllegalArgumentException が発生する"() {
        setup:
        List<Employee> employees = fixtureBook.getList(Employee)
        
        expect:
        fixtureBook.validate(IllegalArgumentException, {new EmployeeStore().delete(employees)})
    }


------------------------

*   [FixtureBook チュートリアル (Spock編)](./Tutorial-Spock.md)
    *   [#01 データ登録メソッド save のテスト](./Tutorial-Spock-save.md)
    *   [#02 データ削除メソッド delete のテスト](./Tutorial-Spock-delete.md)
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-Spock-getAllEmployees.md)
    *   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-Spock-getEmployees.md)
    *   #05 例外発生のテスト
    *   [#06 テストメソッドの簡略化](./Tutorial-Spock-expect.md)
