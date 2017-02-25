package cn.spsilab.locatethings.Data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import cn.spsilab.locatethings.Data.LocateThingsDbContract.ItemEntity;
import cn.spsilab.locatethings.tag.TagModule;

/**
 * Created by changrq on 17-2-17.
 */

public class LittleItem implements Serializable {
    private final static String TAG = LittleItem.class.toString();

    private long itemId;
    private String itemName;
    private long userId;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private TagModule bindTagModule;

    public LittleItem() {
        itemId = -1;
        userId = -1;
        itemName = null;
        createTime = null;
        modifyTime = null;
        bindTagModule = null;
    }

    public void setBindTagModule(TagModule bindTagModule) {
        this.bindTagModule = bindTagModule;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public long getUserId() {
        return userId;
    }

    public String getItemName() {
        return itemName;
    }

    public TagModule getBindTagModule() {
        return bindTagModule;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

}
