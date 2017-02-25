package cn.spsilab.locatethings.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.spsilab.locatethings.Data.LocateThingsDbContract.ItemEntity;
import cn.spsilab.locatethings.Data.LocateThingsDbContract.TagModuleEntity;

/**
 * Created by changrq on 17-2-17.
 */

public class LocateThingsDbHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "findThings.db";
    private final static int VERSION_NUM = 3;

    public LocateThingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_ITEM_TABLE_SQL = "CREATE TABLE " +  ItemEntity.TABLE_NAME + " ("+
                ItemEntity._ID +                     " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntity.COLUMN_ITEM_NAME +        " TEXT NOT NULL, " +
                ItemEntity.COLUMN_OWN_BY_USER +      " INTEGER NOT NULL, " +
                ItemEntity.COLUMN_BIND_MODULE +      " INTEGER NOT NULL, " +
                ItemEntity.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                ItemEntity.COLUMN_MODIFY_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String CREATE_TAG_MODULE_TABLE_SQL = "CREATE TABLE " + TagModuleEntity.TABLE_NAME + " (" +
                TagModuleEntity._ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TagModuleEntity.COLUMN_MAC_ADDR  + " TEXT NOT NULL " +
                ");";

        db.execSQL(CREATE_ITEM_TABLE_SQL);
        db.execSQL(CREATE_TAG_MODULE_TABLE_SQL);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemEntity.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagModuleEntity.TABLE_NAME);
        onCreate(db);
    }

}
