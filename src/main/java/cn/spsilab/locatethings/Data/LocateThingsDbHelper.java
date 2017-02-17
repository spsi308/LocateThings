package cn.spsilab.locatethings.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.spsilab.locatethings.Data.LocateThingsDbContract.ItemEntity;

/**
 * Created by changrq on 17-2-17.
 */

public class LocateThingsDbHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "findThings.db";
    private final static int VERSION_NUM = 1;

    public LocateThingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DATABASE_SQL = "CREATE TABLE " +  ItemEntity.TABLE_NAME + " ("+
                ItemEntity._ID +                     " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntity.COLUMN_ITEM_NAME +        " TEXT NOT NULL, " +
                ItemEntity.COLUMN_OWN_BY_USER +      " INTEGER NOT NULL, " +
                ItemEntity.COLUMN_BIND_MODULE +      " INTEGER NOT NULL, " +
                ItemEntity.COLUMN_CREATE_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                ItemEntity.COLUMN_MODIFY_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(CREATE_DATABASE_SQL);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemEntity.TABLE_NAME);
        onCreate(db);
    }

}
