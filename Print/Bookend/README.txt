
bookend client

Requirements 
- Android 2.3以上

==============================
チェックアウト後に最初に行うこと
==============================
- このプロジェクトをビルドするには、MCBookViwerLib,MCComicViewerLibが必要です。 同じフォルダ内にチェックアウトして下さい。
- Antでビルドする場合、各プロジェクトの local.properties の sdk.dir をAndroidSDKのフルパスに書き換える必要があります。 

==============================
使用しているライブラリとプロジェクト
==============================
-libs/krpdflib-1.0.2.jar
	アイドック自社開発PDFライブラリ。プロジェクトKRPｄｆLibをビルドして生成します。

-MCBookViewerLib
	MCBookViewerライブラリ
		
-MCComicViewerLib
	MCComicViewerライブラリ

※.epubxコンテンツ閲覧時にMBBookライブラリが以下のエラーを出力しますが、特に問題はないそうです。
I/SqliteDatabaseCpp(30477): sqlite returned: error code = 1, msg = table common_settings has no column named _show_statusbar, db=/data/data/net.keyring.bookend/databases/mor_mcbook
E/SQLiteDatabase(30477): Error inserting _show_statusbar=false _horizontal_tap_area=[B@41549368 _instruction=true _orientation=1 _brightness=128 _show_memo_icon=true _show_gesture_guide=true _content_id= _vertical_tap_area=[B@415492c8 _sensor=1
E/SQLiteDatabase(30477): android.database.sqlite.SQLiteException: table common_settings has no column named _show_statusbar: , while compiling: INSERT INTO common_settings(_show_statusbar,_horizontal_tap_area,_instruction,_orientation,_brightness,_show_memo_icon,_show_gesture_guide,_content_id,_vertical_tap_area,_sensor) VALUES (?,?,?,?,?,?,?,?,?,?)


==============================
アプリのデバッグ用設定
==============================
※ Bookend専用フォルダ内に「log」フォルダを作成するとログが出力されます。
※ SDカード直下に「LogCat.txt」を置くとLogCatにのみデバック出力します。
※ SDカード直下に「STG.txt」を置くとステージングモードになります。
※ SDカード直下に「USB_DEBUG.txt」を作成するとUSBデバッグモードが有効でも使えるようになります。
※ SDカード直下に「version.txt」を作成すると常にバージョンチェックを行います。

==============================
 ユニットテスト
==============================
1. プロジェクト Bookend_UnitTest をチェックアウト
2. テスト対象の端末の以下の場所にディレクトリを作成
  /mnt/sdcard/tmp
2. 「Android JUnit Test」を実行して全てのテストが通ることを確認

* アクティビティやサービスなどのテストはしていません。
  純粋にコードだけのテストクラスが無いので、MainActivityのテストクラスを UnitTestBase.java に実装してそれを継承して使ってます。 
  新しいテストケースを追加する場合は DecryptUtilTest.java などをコピーして使ってください。

* テストに外部ファイルが必要な場合は Bookend_UnitTest プロジェクトの res/raw 以下にファイルを追加して、
  テストクラス内で UnitTestBase.copyResouceFile() を使って SDカード上にコピーして下さい。
 
==============================
 リリース手順(Antを使用)
==============================
1. RELEASE_NOTES.txt を更新します。

2. build.xml の version を更新します。

3. AndroidManifest.xml の android:versionName を version と同じ値に更新します。

4. AndroidManifest.xml の android:versionCode を 1加算した値に更新します。
	
5. ビルドするアプリに応じたAntのタスクを実行します。
　全てのアプリをビルドする場合は bookend-release-all を実行して下さい。
 bin以下に bookend-x.x.x.apk と  bookend-???-x.x.x.apk が生成されます。

   アプリ1つだけビルドした場合は以下を実行して下さい。
      - 標準版の場合: bookend-release を実行して下さい。
      - カスタム版の場合: bookend-release-??? を実行して下さい。
      
 ###注意###
  複数のリリースビルドを作成するためにeclipseのAntウィンドウで複数のターゲットにチェックを入れてbookend-release-xxxを実行すると正常にビルドできません。
 1回のAntタスク実行として扱われるため、ターゲットごとにロードしているプロパティの値が上書きされず、古い値が残ったままになることが原因です。
  複数のターゲットを一度に実行したい場合は bookend-release-all で実行するようにして下さい。
  
6.　他のプロジェクトにも依存しているので、Bookendプロジェクトを含む trunk の android にタグを付けます。(タグには以下のURLを使用)
 　svn+ssh://svn.keyring.net/export/krclientsvn/tags/android/Bookend-x.x.x
 
7. bookend-x.x.x.apk と RELEASE_NOTES.txt を \\tera3 の以下の場所にコピーします。
  標準版: \\TERA3\keyringDev\305_テスト用プロダクト管理\bookend\bookendクライアントAndroid版\
  カスタム版: \\TERA3\keyringDev\305_テスト用プロダクト管理\bookend\カスタム版\
  
    
==============================
 カスタム版の追加方法
==============================
* カスタム版では一意なオーナーIDとカスタム名を使用します。

1. custom 以下にカスタム名でフォルダを追加

2. 以下をオリジナルからコピーして内容を変更する
   (注意: .svn フォルダを一緒にコピーしないように。レポジトリが滅茶苦茶になります。)
   
	- アイコン画像
		drawable-hdpi/icon.png
		drawable-ldpi/icon.png
		drawable-mdpi/icon.png
		
	- スプラッシュ画像
		drawable/splash.png
		
	- 本棚の背景画像(変更する場合のみ)
		drawable-nodpi/backtile.png
		
	- アプリの挙動を変更するためのXML
		values/custom_settings.xml		日本語環境以外で使用される設定
		values-ja/custom_settings.xml	日本語環境で使用される設定
		
		*両方のファイルで以下を必ず変更すること。
			app_name: 表示されるアプリケーション名です。指定された内容に変更してください
		
	- アプリの挙動を変更するためのXML
		values/custom_settings2.xml		言語依存の無い設定
	
		*以下を必ず変更し、その他の設定は必要に応じで変更すること。
			custom_name: アプリで使用するカスタム名に変更
			service_category: "bookend"の部分をカスタム名に変更
			owner_id: アプリで使用するオーナーIDに変更	
	
	- Antビルド用プロパティファイル
		build.properties
		
		*以下を必ず変更すること。
			package: アプリで使用するアプリID
			scheme1: "behttp" の後にカスタム名を続けて指定
			scheme2: "beinfo" の後にカスタム名を続けて指定
			category_name: "bookend"の部分をカスタム名に変更
			
		*以下はアプリに署名する場合のみ指定します。
			key.store: 署名に使用するキーストアファイルパス
			key.alias: 署名に使用するキーストアのエイリアス名
			key.store.password: 署名に使用するキーストアのパスワード
			key.alias.password: 署名に使用するキーストアのパスワード
			
3. Antのビルドファイル build.xml にビルド用の設定を追加

	以下のXMLを build.xml 内にコピーして、[カスタム名]の部分をカスタム名に変更する
	------------------------------------------------------------------------
	<!-- [カスタム名]用アプリビルド -->
	<target name="bookend-release-[カスタム名]">
		<bookend-release-build-custom-app custom-name="[カスタム名]"/>
	</target>
	------------------------------------------------------------------------
	
	全部のアプリをビルドするターゲット <target name="bookend-release-all"> の一番下にビルド用の行を追加して下さい。
	------------------------------------------------------------------------
	<target name="bookend-release-all">
		...
		<ant target="bookend-release-[カスタム名]"/>
	</target>
	------------------------------------------------------------------------
	
4. bookendサーバにアプリのバージョン情報を登録
	これをしないと、アプリ起動時に「StartUp Check: ERROR」になります。
	
		
==============================
 カスタム版のデバッグ方法
==============================
1. Bookendプロジェクトをレポジトリの最新の状態にします。
	変更されているファイルが1つもない状態にして下さい。
2. bookend-debug-custom の custom-nameをデバッグしたいカスタム版のカスタム名に変更してから実行します。
	※リソースやマニフェストファイルが更新されます。この状態でコミットしないこと。
3. Bookendプロジェクトをeclipseでクリーン、ビルドしてデバッグする。
4. 終わったら作業コピーをレポジトリの最新の状態に戻して下さい。
	※カスタム版用のリソースやマニフェストファイルをコミットしないこと。
	
==============================
 ※過去の手順です。Antを使用してください
  標準版のリリース手順(手動)
==============================
1. プロジェクトの右クリックから[Android Tools]>[Export Signed Application Package...]を選択
2. プロジェクトの選択: "Bookend"を選択
3. キーストアの選択: プロジェクト内のファイル"bookend.keystore"を選択し、パスワード"bookend"を入力
4. 鍵の選択: Aliasから"bookend"を選択し、パスワード"bookend"を入力
5. 出力先の選択: .apkファイルの出力先を任意に選択して終了
  
==============================
 ※過去の手順です。Antを使用してください
  カスタム版のリリース手順(手動)
==============================  
1. リソースを変更する
	- アイコン画像
		res/drawable-hdpi/icon.png
		res/drawable-ldpi/icon.png
		res/drawable-mdpi/icon.png
	- スプラッシュ画像
		res/drawable/splash.png
		
2. カスタム内容の設定ファイルを行進する
	res/values/custom_settings.xml
	res/values/custom_settings2.xml
	res/values-ja/custom_settings.xml
	
3. AndroidManifest.xml を変更する
	- android:scheme の設定を修正
	- "category_bookend" の値を custom_settings.xml に合わせて修正
	
4. genフォルダにあるR.javaをsrc/net/keyring/bookendにコピーして元を削除

5. AndroideManifest.xmlのpackage名を変更

6. ビルド	
	