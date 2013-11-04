
### FixtureBook チュートリアル (Spock編)

#01 データ登録メソッド save のテスト
====================================

テスト対象のメソッド
--------------------

ここでは、以下の save メソッドを対象にテストを行います。

```java
    class EmployeeStore {

        def save(List<Employee> employees) {
            def sql = createSql()
            employees.each {
                it.lastUpdated = new java.sql.Timestamp(new Date().time)
                sql.execute(
                    "INSERT INTO EMPLOYEE(ID, NAME, AGE, RETIRE, LAST_UPDATED)" + 
                    "VALUES(:id, :name, :age, :retire, :lastUpdated)", it)
            }
        }

        (...)
    }
```

save メソッドは、引数として渡された従業員データをデータベーステーブル (EMPLOYEE) に INSERT で登録します。


テストケース
------------

以下のケースについてテストします。

*   <b>データベーステーブルEMPLOYEEに従業員データを新規追加できる</b>


テストプログラムの処理
----------------------

上記のテストケースをテストするために、テストプログラムでは以下のことを行う必要があります。

1.  事前にデータベーステーブル (EMPLOYEE) 内のデータを削除しておく
    (「saveメソッドの実行によりデータが登録された」ということを確認するため）。
2.  save メソッドの引数として渡す従業員データ (List<Employee>) を作成する。
3.  save メソッドを呼び出す。
4.  save メソッドの呼び出しの結果、データベーステーブル (EMPLOYEE) にデータ登録されたことを確認する。


上記の処理の 3 以外はいずれもテストデータを扱う処理となります。
FixtureBook には、これらのデータを記述します。



FixtureBook の記述
------------------

FixtureBook では以下の記述を行います。

*   データベーステーブル (EMPLOYEE) の行削除条件を `B.テストデータクリア条件` に記述する。
*   save メソッドの引数として渡すデータを `D.パラメタ` に記述する。
*   save メソッドの実行後の EMPLOYEE テーブルのデータ状態を予想して`F.更新後データ`に記述する。

![FixtureBook記述](./images/Tutorial-save-01.png?raw=true)


### B.テストデータクリア条件

ここでは、EMPLOYEE テーブルの行データをどういう条件で削除するかを指定します。

![B.テストデータクリア条件](./images/Tutorial-save-02.png?raw=true)

この例では、主キー項目である ID 列に `*` を指定することですべての行を削除するように指定しています。


### D.パラメタ

save メソッドの引数 (List<Employee>) に格納するデータを `D.パラメタ` に記述します。

![D.パラメタ](./images/Tutorial-save-03.png?raw=true)

LAST_UPDATE列に関しては save メソッドの中で自動的に現在時刻を設定するため、
ここでは記述していません。


### F.更新後データ

`F.更新後データ`には、save メソッド実行後の予想結果 (EMPLOYEE テーブルのあるべき状態) を記述します。

![F.更新後データ](./images/Tutorial-save-04.png?raw=true)

説明の先頭に \* が付いている項目 (この例では `*ID`) は検索条件指定列を意味します。
FixtureBook がデータベースの状態が正しいかどうかのチェックを行う際には、
まず、この列に指定された値を条件にしてデータベースを検索し、
次に、取得したデータ行の各列値について、Excel セル上の値と等しいかどうかを比較チェックします。

LAST_UPDATED列の値として指定されている `${TODAY}` は「本日の日時であればOK」を意味します。
LAST_UPDATED列に関しては save メソッドにより現在日時が自動設定されるため、正確な時間を予想することが難しいからです。


テストメソッドの記述
--------------------

次に単体テストクラスにテストメソッドを追加します。

テストメソッドの名前は、`[シート名]__[テストケース記述]` の形式で指定してください。

    def "save__データベーステーブルEMPLOYEEに従業員データを新規追加できる"() {

テストケース記述にメソッド名として利用できない文字 (空白や記号等) が含まれている場合は、
以下のように `@Fixture` アノテーションを使用してテストケースを指定してください。

    @Fixture(["save", "データベーステーブル (EMPLOYEE) に従業員データを新規追加できる"])
    def "save__データベーステーブルEMPLOYEEに従業員データを新規追加できる"() {

テストメソッドは以下の内容で記述します。

1.  save メソッドの引数として渡すデータを `fixtureBook.getList(Employee)` で取得する。
    これにより、`D.パラメタ` の "Employee" で記述した値で初期化された Employee のリスト (List<Employee>) が取得できる。
2.  上記で取得したリストを引数にして、テスト対象メソッド (save) を呼び出す。
3.  `fixtureBook.ValidateStorage()` の呼び出しにより、データベーステーブル状態を検証する。
    これにより、`F.更新後データ`に記述した内容と実際のデータベーステーブルの状態が同じかどうかのチェックが行われる。

```
    def "save__データベーステーブルEMPLOYEEに従業員データを新規追加できる"() {
        setup:
        List<Employee> employees = fixtureBook.getList(Employee, "Employee")

        when:
        new EmployeeStore().save(employees)

        then:
        fixtureBook.validateStorage()
    }
```

なお、`B.テストデータクリア条件` に記述された条件でのデータベーステーブルからの行削除は、
上記のテストプログラムでは明示的に行っていませんが、
`fixtureBook.getList()` の呼び出しの中で自動的に実行されます。

##### 参考
>今回のように `D.パラメタ` にひとつのテーブル定義しか記述されていない場合は、
>`fixtureBook.getList(Employee)` というふうに引数の "Employee" 
>を省略してメソッド呼び出しを行うこともできます。



テストの実行
------------

テスト実行の方法は、通常の単体テスト実行と同じです。
エラーが出た場合はエラーメッセージを確認して、テスト対象メソッドの内容やテストデータ記述の修正を行ってください。



------------------------

*   [FixtureBook チュートリアル (JUnit編)](./Tutorial-JUnit.md)
    *   #01 データ登録メソッド save のテスト
    *   [#02 データ削除メソッド delete のテスト](./Tutorial-JUnit-delete.md)
    *   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-JUnit-getAllEmployees.md)
    *   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-JUnit-getEmployees.md)
    *   [#05 例外発生のテスト](./Tutorial-JUnit-Exception.md)
    *   [#06 テストメソッドの簡略化](./Tutorial-JUnit-expect.md)
