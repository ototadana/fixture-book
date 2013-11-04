
### FixtureBook チュートリアル (JUnit編)

#04 データ検索メソッド getEmployees のテスト
============================================

テスト対象のメソッド
--------------------

ここでは、以下の getEmployees メソッドを対象にテストを行います。

```java
    public class EmployeeStore {

        (...)

        public List<Employee> getEmployees(Employee parameter) throws SQLException {
            try(Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement("SELECT * FROM EMPLOYEE where RETIRE = ?")) {         
                statement.setBoolean(1, parameter.isRetire());
                try(ResultSet resultSet = statement.executeQuery()) {
                    return getEmployees(resultSet);
                }
            }
        }

        (...)
    }
```

getEmployees メソッドは、parameter 引数の条件 (退職フラグ retire の true/false) に従って、
EMPLOYEE テーブル内のデータを抽出し、リスト (List<Employee>) として返します。


テストケース
------------

以下の2ケースをテストします。

*   <b>引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる</b>
*   <b>引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる</b>


FixtureBook の記述
------------------

FixtureBook では以下の記述を行います。

*   `B.テストデータクリア条件` で `*` を指定して、一旦全データを消去する。
*   `B.テストデータ` に退職者と未退職者双方のデータを登録する。
*   getEmployees メソッドの引数として渡すデータを `D.パラメタ` に記述する。
*   getEmployees でデータ取得できるはずのデータを予想結果として `E.取得データ` に記述する。


![FixtureBook記述1](./images/Tutorial-getEmployees-01.png?raw=true)

![FixtureBook記述2](./images/Tutorial-getEmployees-02.png?raw=true)



テストメソッドの記述
--------------------

テストメソッドの記述内容は以下のようになります。

1.  `fixtureBook.getObject(Employee.class)` を呼び出し、`D.パラメタ` に定義された内容で初期化された 
    従業員データを取得する。
2.  取得した DataTable を引数にしてテスト対象メソッド `getEmployees()` を呼び出して、
    戻り値 (`List<Employee> employees`) を取得する。
3.  `fixtureBook.validate(employees)` を呼び出し、戻り値 employees の内容が正しいかどうかをチェックする。

```
    @Test
    public void getEmployees__引数の退職フラグがtrueの場合データベーステーブルEMPLOYEE上の退職者のみが取得できる() throws Exception {
        //setup
        Employee parameter = fixtureBook.getObject(Employee.class);
        
        //when
        List<Employee> employees = new EmployeeStore().getEmployees(parameter);
        
        //then
        fixtureBook.validate(employees);
    }

    @Test
    public void getEmployees__引数の退職フラグがfalseの場合データベーステーブルEMPLOYEE上の未退職者のみが取得できる() throws Exception {
        //setup
        Employee parameter = fixtureBook.getObject(Employee.class);
        
        //when
        List<Employee> employees = new EmployeeStore().getEmployees(parameter);
        
        //then
        fixtureBook.validate(employees);
    }
```


------------------------

*   [FixtureBook チュートリアル (JUnit編)](./Tutorial-JUnit.md)
    *   [#01 データ登録メソッド save のテスト](./Tutorial-JUnit-save.md)
    *   [#02 データ削除メソッド delete のテスト](./Tutorial-JUnit-delete.md)
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-JUnit-getAllEmployees.md)
    *   #04 データ検索メソッド getEmployees のテスト
    *   [#05 例外発生のテスト](./Tutorial-JUnit-Exception.md)
    *   [#06 テストメソッドの簡略化](./Tutorial-JUnit-expect.md)
