package cn.spsilab.locatethings.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import cn.spsilab.locatethings.Data.LocateThingsDbContract.ItemEntity;

/**
 * Created by changrq on 17-2-17.
 */

public class LocateThingsDatabase {
    private final String TAG = LocateThingsDatabase.class.toString();

    private SQLiteDatabase mDb;

    private static LocateThingsDatabase itemDataBaseInstance = null;

    private LocateThingsDatabase(Context context) {
        LocateThingsDbHelper mDbHelper = new LocateThingsDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public static LocateThingsDatabase getInstance(Context context) {
        if (itemDataBaseInstance == null) {
            itemDataBaseInstance = new LocateThingsDatabase(context);
        }

        return itemDataBaseInstance;
    }
    /**
     * Using the data that cursor now pointing to, and instantiate a item.
     * @param cursor
     * @return
     */
    private LittleItem fetchCursorDataBuildItem(Cursor cursor) {
        LittleItem item = new LittleItem();

        item.setItemId(cursor.getLong(cursor.getColumnIndex(ItemEntity._ID)));
        item.setItemName(cursor.getString(cursor.getColumnIndex(ItemEntity.COLUMN_ITEM_NAME)));
        item.setUserId(cursor.getLong(cursor.getColumnIndex(ItemEntity.COLUMN_OWN_BY_USER)));
        item.setModuleId(cursor.getLong(cursor.getColumnIndex(ItemEntity.COLUMN_BIND_MODULE)));

        item.setCreateTime(Timestamp.valueOf(cursor.getString(
                cursor.getColumnIndex(ItemEntity.COLUMN_CREATE_TIMESTAMP))));
        item.setModifyTime(Timestamp.valueOf(cursor.getString(
                cursor.getColumnIndex(ItemEntity.COLUMN_MODIFY_TIMESTAMP))));

        return item;
    }

    /**
     * if database is empty insert test data.
     */
    public void insertTestData() {
        // insert fake data.
        Cursor cursor = getItemsCursorByUserId(TestData.fakeUserId);
        if (cursor.getCount() == 0) {
            LittleItem[] itemsArray = TestData.getTestItems();
            for(LittleItem item: itemsArray) {
                addItem(item);
            }
        }
    }

    /**
     * query database using specify user id and return a cursor, soring by create time.
     * @param uid
     * @return
     */
    private Cursor getItemsCursorByUserId(long uid) {
        String selection = ItemEntity.COLUMN_OWN_BY_USER + " = ?";
        String[] selectionArgs = { String.valueOf(uid) };

        return mDb.query(ItemEntity.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                ItemEntity.COLUMN_MODIFY_TIMESTAMP + " DESC");
    }



    /**
     * Querying database and return the resulet in arraylist, soring by createTime
     * @param uid
     * @return
     */
    public ArrayList<LittleItem> getItemsByUserId(long uid) {
        Cursor cursor = getItemsCursorByUserId(uid);

        if (cursor.getCount() == 0) {
            return null;
        }

        ArrayList<LittleItem> itemsArrayList = new ArrayList<>();
        while(cursor.moveToNext()) {
            itemsArrayList.add(fetchCursorDataBuildItem(cursor));
        }
        return itemsArrayList;
    }

    /**
     * Add item to database, createTime timestamp will be auto added by sqlite.
     * @param g
     * @return
     */
    public long addItem(LittleItem g) {
        String itemName = g.getItemName();
        long userId = g.getUserId();
        long moduleId = g.getModuleId();

        ContentValues cv = new ContentValues();
        cv.put(ItemEntity.COLUMN_ITEM_NAME, itemName);
        cv.put(ItemEntity.COLUMN_OWN_BY_USER, userId);
        cv.put(ItemEntity.COLUMN_BIND_MODULE, moduleId);

        return mDb.insert(ItemEntity.TABLE_NAME, null, cv);
    }

    /**
     * Qerying database with specify itemId.
     * @param itemId
     * @return
     */
    public LittleItem getItemById(long itemId) {

        String selection = ItemEntity._ID + " = ?";
        String[] selectionArgs = { String.valueOf(itemId) };

        // start a query.
        Cursor cursor = mDb.query(ItemEntity.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // if item exists.
        if (cursor.moveToFirst()) {
            return fetchCursorDataBuildItem(cursor);
        }

        return null;
    }

    /**
     * Updating a item, method will auto set current time to the modifyTime timestamp.
     * @param itemId
     * @param newItem
     * @return
     */
    public int updateItemById(long itemId, LittleItem newItem) {
        LittleItem modifyItem = getItemById(itemId);
        modifyItem.setItemId(newItem.getItemId());
        modifyItem.setItemName(newItem.getItemName());

        ContentValues cv = new ContentValues();

        // TODO: 17-2-16 there should be using a more elegant way to get time stamp.
        Date nowDate = new Date();
        Timestamp nowTimeStamp = new Timestamp(nowDate.getTime());

        // ensure data not null.
        if (newItem.getItemName() != null && !newItem.getItemName().equals("")) {
            cv.put(ItemEntity.COLUMN_ITEM_NAME, newItem.getItemName());
            Log.d(TAG, "updateItemById: updating name.");
        }
        if (newItem.getUserId() != -1) {
            cv.put(ItemEntity.COLUMN_OWN_BY_USER, newItem.getUserId());
        }
        if (newItem.getModuleId() != -1) {
            cv.put(ItemEntity.COLUMN_BIND_MODULE, newItem.getModuleId());
        }

        cv.put(ItemEntity.COLUMN_MODIFY_TIMESTAMP, nowTimeStamp.toString());

        String selection = ItemEntity._ID + " = ?";
        String[] selectionArgs = { String.valueOf(itemId) };

        return mDb.update(ItemEntity.TABLE_NAME, cv, selection, selectionArgs);
    }

    /**
     * Removing the specify item by itemId.
     * @param itemId
     * @return
     */
    public int removeItemById(long itemId) {

        String selection = ItemEntity._ID + " = ?";
        String[] selectionArgs = { String.valueOf(itemId) };

        return mDb.delete(ItemEntity.TABLE_NAME, selection, selectionArgs);
    }
}
