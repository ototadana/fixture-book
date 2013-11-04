
FixtureBook チュートリアル (Spock編)
====================================

Spock を使って単体テストを行う場合の利用例を説明します。


テスト対象のアプリケーション
----------------------------

このチュートリアルでは、以下のような構成のアプリケーションを対象にテストを行います。


### テーブル

    CREATE TABLE EMPLOYEE (
        ID              NUMBER(8)       PRIMARY KEY,
        NAME            VARCHAR2(40)    NOT NULL,
        AGE             NUMBER(3)       NOT NULL,
        RETIRE          NUMBER(1)       NOT NULL,
        LAST_UPDATED    TIMESTAMP       NOT NULL
    );

データベース環境としては Oracle を使用します。


### データベース操作を行うクラス（テスト対象のクラス）

```java
    ....

    import groovy.sql.Sql;

    class EmployeeStore {

        def save(List<Employee> employees) {
            ...
        }

        def delete(List<Employee> employees) {
            ...
        }

        List<Employee> getAllEmployees() {
            ...
        }

        List<Employee> getEmployees(Employee parameter) {
            ...
        }

        Sql createSql() {
            Sql.newInstance("jdbc:oracle:thin:@localhost:1521:xe", "scott", "tiger")
        }
    }
```

このクラスでは以下の処理を行います。

*   save は引数に指定された従業員データの保存を行う。
*   delete  は引数に指定された従業員データの削除を行う。
*   getAllEmployees はデータベーステーブル上に登録されている従業員データを全て取得する。
*   getEmployees では、引数に指定されている従業員情報の RETIRE 列の値を条件にして従業員データを抽出する。



FixtureBook の作成
------------------

まず FixtureBook (テストデータを記述する Excel ファイル) を作成します。

ファイルは `.xlsx` 形式で作成してください。
ファイルの作成場所は単体テストクラスのあるフォルダ（同一パッケージ階層のフォルダ）にして、
ファイル名は「単体テストクラス名.xlsx」とすると、
単体テストクラスでのファイルパス指定が省略できるため、おすすめです。
例えば、単体テストクラスを `EmployeeStorTest.java` というファイルに作成する場合、
FixtureBook のファイル名は `EmployeeStoreTest.xlsx` として同じフォルダに格納してください。


単体テストクラスの作成
----------------------

次に単体テストクラスを作成します。
作成した単体テストクラスで FixtureBook を利用可能にするために、
以下のような修正を加えます。

1.  `import com.xpfriend.fixture.FixtureBook` を追加する。
2.  `FixtureBook fixtureBook = new FixtureBook()` といった形で fixtureBook フィールドを追加する。

```java
    ...
    import com.xpfriend.fixture.FixtureBook
    ...

    class EmployeeStoreTest extends Specification {
        FixtureBook fixtureBook = new FixtureBook()
    }
```


実際のテストの例
----------------

これで FixtureBook の利用準備ができました。
実際のテスト方法については以下のドキュメントに記載されていますので、そちらでどうぞ。

*   [#01 データ登録メソッド save のテスト](./Tutorial-Spock-save.md)
*   [#02 データ削除メソッド delete のテスト](./Tutorial-Spock-delete.md)
*   [#03 データ取得メソッド getAllEmployees のテスト](./Tutorial-Spock-getAllEmployees.md)
*   [#04 データ検索メソッド getEmployees のテスト](./Tutorial-Spock-getEmployees.md)
*   [#05 例外発生のテスト](./Tutorial-Spock-Exception.md)
*   [#06 テストメソッドの簡略化](./Tutorial-Spock-expect.md)

