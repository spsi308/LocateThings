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
import cn.spsilab.locatethings.Data.LocateThingsDbContract.TagModuleEntity;
import cn.spsilab.locatethings.tag.TagModule;

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
        //mDbHelper.onUpgrade(mDb, 3, 3);
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

        TagModule bindTag = new TagModule();

        bindTag.setModuleMAC(cursor.getString(cursor.getColumnIndex(TagModuleEntity.COLUMN_MAC_ADDR)));
        bindTag.setModuleId(cursor.getLong(cursor.getColumnIndex(ItemEntity.COLUMN_BIND_MODULE)));

        item.setBindTagModule(bindTag);

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
     * query database using specify user id and return a cursor, query use outer join with
     * TagModule table.
     * @param uid
     * @return
     */
    private Cursor getItemsCursorByUserId(long uid) {
        final String rawQuery = "SELECT * FROM " + ItemEntity.TABLE_NAME + " LEFT OUTER JOIN "
                + TagModuleEntity.TABLE_NAME + " ON " + ItemEntity.TABLE_NAME + "."
                + ItemEntity.COLUMN_BIND_MODULE + "=" + TagModuleEntity.TABLE_NAME+"."
                + TagModuleEntity._ID + " WHERE " + ItemEntity.TABLE_NAME + "."
                + ItemEntity.COLUMN_OWN_BY_USER + "=?";

        return mDb.rawQuery(rawQuery, new String[]{String.valueOf(uid)});
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
     * get tag module by specify tag mac.
     * @param mac
     * @return if exists, return object, otherwise return null.
     */
    public TagModule getTagModuleIdByMac(String mac) {
        String selection = TagModuleEntity.COLUMN_MAC_ADDR + " = ?";
        String[] selectionArgs = { mac };

        Cursor queryTagCursor = mDb.query(TagModuleEntity.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (queryTagCursor.moveToFirst()) {
            TagModule tag = new TagModule();
            tag.setModuleId(queryTagCursor.getLong(
                    queryTagCursor.getColumnIndex(TagModuleEntity._ID)));

            tag.setModuleMAC(queryTagCursor.getString(
                    queryTagCursor.getColumnIndex(TagModuleEntity.COLUMN_MAC_ADDR)));
            return tag;
        } else {
            return null;
        }
    }

    /**
     * insert a new tag to database.
     * @param tag
     * @return the row id that newly inserted.
     */
    public long insertNewTagModule(TagModule tag) {

        ContentValues tagCV = new ContentValues();

        tagCV.put(TagModuleEntity.COLUMN_MAC_ADDR, tag.getModuleMAC());

        return mDb.insert(TagModuleEntity.TABLE_NAME, null, tagCV);
    }

    /**
     * Add item to database, createTime timestamp will be auto added by sqlite.
     * @param g
     * @return newly inserted row id.
     */
    public synchronized long addItem(LittleItem g) {
        long userId = g.getUserId();
        long moduleId;
        String itemName = g.getItemName();
        String bindTagMac  = g.getBindTagModule().getModuleMAC();

        // get tag id.
        TagModule bindTag = getTagModuleIdByMac(bindTagMac);
        if (bindTag != null) {
            moduleId = bindTag.getModuleId();
        } else {
            moduleId = insertNewTagModule(g.getBindTagModule());
        }

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

        final String rawQuery = "SELECT * FROM " + ItemEntity.TABLE_NAME + " LEFT OUTER JOIN "
                + TagModuleEntity.TABLE_NAME + " ON " + ItemEntity.TABLE_NAME + "."
                + ItemEntity.COLUMN_BIND_MODULE + "=" + TagModuleEntity.TABLE_NAME+"."
                + TagModuleEntity._ID + " WHERE " + ItemEntity.TABLE_NAME + "."
                + ItemEntity._ID + "=?";

        Cursor cursor  = mDb.rawQuery(rawQuery, new String[]{String.valueOf(itemId)});

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
    public synchronized int updateItemById(long itemId, LittleItem newItem) {
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

        // if set bind module, get id.  if tag is new, insert it.
        if (newItem.getBindTagModule() != null) {
            TagModule newTag = newItem.getBindTagModule();
            long moduleId;

            if (newTag.getModuleId() != -1) {
                moduleId = newTag.getModuleId();
            } else {
                TagModule existsTag;
                existsTag = getTagModuleIdByMac(newItem.getBindTagModule().getModuleMAC());
                if (existsTag != null) {
                    moduleId = existsTag.getModuleId();
                } else {
                    moduleId = insertNewTagModule(newTag);
                }

            }
            cv.put(ItemEntity.COLUMN_BIND_MODULE, moduleId);
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
