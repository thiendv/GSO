package net.keyring.bookend.db;

import net.keyring.bookend.Logput;
import net.keyring.bookend.constant.ConstDB;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
/**
 * contentsテーブルAccessクラス
 * @author Hamaji
 *
 */
public class ContentsDBAccess extends SQLiteEngine implements ConstDB {
	
	public ContentsDBAccess(Context con){
		super(con);
	}
	
	/**
	 * データベースが存在しない状態でデータベースをオープンしようとすると呼ばれる
	 *
	 * @param 新規作成されたデータベースのインスタンス
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try{
			// テーブルの作成
			db.execSQL(CREATE_CONTENTS_TABLE);
			db.execSQL(CREATE_UP_CON_TABLE);
			db.execSQL(CREATE_UP_LIC_TABLE);
			db.execSQL(CREATE_NAVI_SALES_TABLE);
			db.execSQL(CREATE_PURCHASE_TABLE);
			db.setTransactionSuccessful();
		}finally{
			db.endTransaction();
			Logput.i(">>CREATE contents.cb : ver." + DB_VERSION);
		}
	}

	
	/**
	 * テーブル構造の再構成などの処理を行う(DBのバージョンアップ時など)
	 * 
	 * UPDATE DB Ver
	 * -2	salesテーブル追加
	 * -3	contentsテーブルのKRPDF_FORMAT_VERカラム追加
	 * -4	purchaseテーブル追加
	 * -5	contentsテーブルにDL_STATUS,DL_PROGRESSカラム追加
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logput.i("DB Upgrade = " + oldVersion + " => " + newVersion);
		
		try{
			db.beginTransaction();
			if(newVersion == DB_VERSION){
				switch(oldVersion){
				case 1:
					db.execSQL(CREATE_NAVI_SALES_TABLE);
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + KRPDF_FORMAT_VER + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_STATUS + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_PROGRESS + " INTEGER;");
					db.execSQL(CREATE_PURCHASE_TABLE);
					db.setTransactionSuccessful();
					break;
				case 2:
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + KRPDF_FORMAT_VER + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_STATUS + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_PROGRESS + " INTEGER;");
					db.execSQL(CREATE_PURCHASE_TABLE);
					db.setTransactionSuccessful();
					break;
				case 3:
					db.execSQL(CREATE_PURCHASE_TABLE);
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_STATUS + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_PROGRESS + " INTEGER;");
					db.setTransactionSuccessful();
					break;
				case 4:
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_STATUS + " INTEGER;");
					db.execSQL("ALTER TABLE " + CONTENTS_TABLE + " ADD " + DL_PROGRESS + " INTEGER;");
					db.setTransactionSuccessful();
					break;
				}
			}
		}finally{
			Logput.i("------ endTransaction");
			db.endTransaction();
		}
	}
	
	/**
	 * 複数DBアクセス用クラスインスタンスが生成されないようSingletonに
	 * @param con Context
	 * @return SQLiteEngine
	 */
	public static SQLiteEngine getInstance(Context con){
		if(sEngine == null){
			sEngine = new ContentsDBAccess(con);
		}
		return sEngine;
	}
	
	/**
	 * リセット時のDBインスタンスを初期化
	 * @param con Context
	 */
	public static void initInstance(Context con){
		sEngine = null;
	}
	
}
