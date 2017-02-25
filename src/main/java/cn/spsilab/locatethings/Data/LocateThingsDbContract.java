package cn.spsilab.locatethings.Data;

import android.provider.BaseColumns;

/**
 * Created by changrq on 17-2-17.
 */

public class LocateThingsDbContract {

    public final static class ItemEntity implements BaseColumns {
        public final static String TABLE_NAME = "itemInfo";

        public final static String COLUMN_ITEM_NAME = "itemName";
        public final static String COLUMN_OWN_BY_USER = "itemOwnByUserId";
        public final static String COLUMN_BIND_MODULE = "itemBindModuleId";
        public final static String COLUMN_CREATE_TIMESTAMP = "itemCreateTime";
        public final static String COLUMN_MODIFY_TIMESTAMP = "itemModifyTime";
    }

    public final static class TagModuleEntity implements BaseColumns {
        public final static String TABLE_NAME = "tagModule";

        public final static String COLUMN_MAC_ADDR = "tagModuleMacAddr";
    }
}
