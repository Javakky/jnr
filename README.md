# jnr-ffi Plugin
このプラグインは[Project Panama](https://openjdk.java.net/projects/panama/)で開発中(Java13で実装予定)の機能を使いやすくするものです。

特に、[JEP 191: Foreign Function Interface](https://openjdk.java.net/jeps/191)の利便化を目的としています。

JNR関連記事はこちらのQiitaにまとめたりしているのでよければどうぞ

+ [jnr-ffi関係記事リンク集](https://qiita.com/Kakky/items/bce3d6c9c47899b000a3)

紹介記事を書きました。

+ [jextractをGradleのタスクで実行するプラグイン作った](https://qiita.com/Kakky/items/56a8f5bb14ae9080fee7)

## 動作環境
システムのJDK・プロジェクトのJDKともにバージョン13以上?

動作保証のJDKは [Project Panama Early-Access Builds](http://jdk.java.net/panama/)
だけです。

JDKのバージョンが12以下の場合警告文、jextractがシステムのJDKに存在しない場合にはエラー文を出します。

## jextract

### jextractとは？

jextractとは、C言語などの.hファイルから対応するJavaのインターフェースを生成するツールです。

[early-access版のJDK](http://jdk.java.net/panama/)に付属しており、[ServiceLoader](https://docs.oracle.com/javase/jp/10/docs/api/java/util/ServiceLoader.html)を利用することでプログラムからも呼び出すことができます。

### jextractタスク
このプラグインではjextractというタスクが定義されています。

jextractタスクでは、指定されたフォルダ内にある.hファイルからjextractでjarファイル(対応するインターフェース)を生成するものです。

(入れ子になっているファイルも検索し、ディレクトリをパッケージと対応させてjarを生成します)

jextractタスクのオプションは、build.gradleで以下のように指定できます。

```groovy:build.gradle
jextract{
    // .hファイルを含むディレクトリのパス
    // 初期値は"src/main/resources/"
    sourceRoot = "head/"
    // 生成したjarを置くディレクトリのパス
    // 初期値は"libs/"
    outPath = "jar"
    // 生成するインターフェースが属するパッケージのルート名
    // <packageRoot>.<ディレクトリ名>.<ディレクトリ名>という風に命名される
    // 初期値は""
    packageRoot = "pkg"
    // パッケージ名にsourceRootディレクトリを含むかどうか
    // falseにすると、SourceRoot直下の.hファイルのパッケージはpackageRootとなる
    // 初期値はfalse
    includeRoot = false
}
```

## compileC
実装予定のタスクです。

C言語のJava処理系があればすぐに実装します。

見つかるまでは[Process](https://docs.oracle.com/javase/jp/10/docs/api/java/lang/Process.html)から既に開発環境にインストールされた[bcc](https://www.mlab.im.dendai.ac.jp/ic2/webdesign/web/tool/bcc/)や[gcc](http://gcc.gnu.org/)を呼び出すタスクを簡易的に実装しようと考えています。

# UseC4ffi4Windows
このプラグインと合わせて使えるライブラリ作りました。(なお簡易版)

+ [UseC4ffi4Windows](https://github.com/Javakky/UseC4ffi4Windows)

対応OSはWindowsだけ(コード的に別のOSでも動くかもしれませんがテストしていない)です。

resouceフォルダに置いた.dllファイルとjextractで生成したインターフェースからCのコードを呼び出す際、
dllの設定を省略できます。

詳しくはこちら
+ [jnr-ffi使った(使いやすくするなどした)](https://qiita.com/Kakky/items/a54ccc68365707765a5a)