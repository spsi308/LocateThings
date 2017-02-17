package cn.spsilab.locatethings.Data;

/**
 * Created by changrq on 17-2-17.
 */

public class TestData {
    public final static long fakeUserId = 33;
    public final static long fakeModuleId = 44;

    private final static String[] fakeItemsName =
            {"袜子", "裤子", "CPU", "内存条", "主板一块", "快递包", "手电筒"};

    public static LittleItem[] getTestItems() {
        LittleItem[] items = new LittleItem[fakeItemsName.length];

        for(int i = 0; i < fakeItemsName.length; i++) {
            items[i] = new LittleItem();

            items[i].setItemName(fakeItemsName[i]);
            items[i].setUserId(fakeUserId);
            items[i].setModuleId(fakeModuleId);
        }
        return items;
    }

}
