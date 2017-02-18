package cn.spsilab.locatethings.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by changrq on 17-2-17.
 */

public class LittleItem implements Serializable {
    private long itemId;
    private String itemName;
    private long userId;
    private long moduleId;
    private Timestamp createTime;
    private Timestamp modifyTime;

    public LittleItem() {

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

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getItemId() {
        return itemId;
    }

    public long getModuleId() {
        return moduleId;
    }

    public long getUserId() {
        return userId;
    }

    public String getItemName() {
        return itemName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

}
