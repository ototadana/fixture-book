FixtureBook
===========

A class library for unit testing with fixtures described by .xlsx file.


1. FixtureBook とは
-------------------

FixtureBook とは単体テストで使用するデータを .xlsx ファイルに記述できるようにするための仕組みです。

### 1.1 機能

FixtureBook を使うと、.xlsx ファイル上に記述した以下のようなデータを JUnit や Spock で簡単に利用することができます。

*   テスト前にDBテーブルに登録しておきたいデータ内容。
*   テスト対象メソッドの引数等として利用するオブジェクトのプロパティ値。
*   テスト対象メソッドを実行して取得できた結果と照合するための予想結果。
*   テスト後のDBテーブルのあるべき状態を表すデータ。


### 1.2 どんなアプリに使える？

Webアプリケーションのバックエンドロジック等、
データベースの入出力を中心としたクラスのテストに向いています。


### 1.3 特徴

#### テストコードが非常にシンプル

テストコードが非常にシンプルになります。

例えば、JUnit の場合、

    @Test
    public void getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる() {
        FixtureBook.expectReturn();
    }

あるいは、Spock の場合、

```java
    def "getAllEmployees__データベーステーブルEMPLOYEE上の全データが取得できる"() {
        expect: FixtureBook.expectReturn()
    }
```

というコードだけで、

1.  データベースに必要なデータをセットアップし、
2.  テスト対象の getAllEmployees メソッドに引数として渡すオブジェクトを作成し、
3.  テスト対象の getAllEmployees メソッドを実行し、
4.  その戻り値を予想結果と比較する

という処理が行えます。



#### 導入が簡単

1.  pom.xml や build.gradle に dependency 設定をして
2.  db.properties に データベースへの接続設定をする

だけで使い始められます。



#### APIがシンプル

利用するのは `FixtureBook` クラスだけです。



2. セットアップ
---------------

### 2.1 ダウンロードとインストール

Maven セントラルリポジトリに登録されていますので、
pom.xml や build.gradle に dependency の記述をすれば利用可能になります。


#### Maven (pom.xml) での設定例

```xml
    <dependency>
      <groupId>com.xpfriend</groupId>
      <artifactId>fixture-book</artifactId>
      <version>[5,)</version>
      <scope>test</scope>
    </dependency>
```

#### Gradle (build.gradle) での設定例

    ...
    repositories {
        mavenCentral()
    }
    dependencies {
        ...
        testCompile group: 'com.xpfriend', name: 'fixture-book', version: '5.+'
        ...
    }


*   Java コンパイラは JDK1.6 (Java6) 以上を使用してください。


### 2.2 データベース接続設定

データベース接続設定はクラスパス上に（例えば、Maven のディレクトリ構成だと src/test/resources フォルダに）
置いた db.properties ファイルに以下のように記述します。

    driverClassName=oracle.jdbc.OracleDriver
    url=jdbc:oracle:thin:@localhost:1521:xe
    username=scott
    password=tiger



##### 参考 : 複数の接続先を利用する場合
>複数の接続先を利用する場合は、上記と同様の内容のファイルを名前を変えて同じフォルダに複数作成してください。
>この場合、db.properties ファイルはデフォルトの接続先情報として使用され、
>それ以外の名前のファイルは FixtureBook (.xlsxファイル) で明示的に指定した場合に使用されます。
>
>例えば、SQLServer.properties というファイルを作成した場合、
>FixtureBook のテーブル名指定箇所に `[テーブル名]@SQLServer` という形式で接続名の指定を行えば、
>db.properties の代わりに、SQLServer.properties に記述されている接続情報が使用されます。



3. 利用方法
-----------

以下のドキュメントを参照してみてください。

*   [FixtureBook の使い方 (JUnit編)](./doc/IntroductionWithJUnit.md)
*   [FixtureBook の使い方 (Spock編)](./doc/IntroductionWithSpock.md)
