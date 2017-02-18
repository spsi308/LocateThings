package cn.spsilab.locatethings;

import cn.spsilab.locatethings.Data.LittleItem;

/**
 * Created by changrq on 17-2-18.
 */

public interface ItemOperateHandler {
    void onItemChange(int inAdapterPosi, LittleItem item);

    void onAddItem(LittleItem newItem);

    void onItemRemove(int inAdapterPosi);
}
