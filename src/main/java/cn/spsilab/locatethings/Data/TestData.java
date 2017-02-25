package cn.spsilab.locatethings.Data;

import java.util.Random;

import cn.spsilab.locatethings.tag.TagModule;

/**
 * Created by changrq on 17-2-17.
 */

public class TestData {
    public final static long fakeUserId = 33;
    public final static String[] usableMac = {"36EDC308004B1200", "41EDC308004B1200"};
    public final static String[] itemNames = {"裤子", "书包", "CPU", "袜子", "主板", "内存", "梳子"};

    public static LittleItem[] getTestItems() {
        LittleItem[] items = new LittleItem[itemNames.length];

        for(int i = 0; i < itemNames.length; i++) {
            items[i] = new LittleItem();
            TagModule bindTag = new TagModule();

            bindTag.setModuleMAC(usableMac[i % usableMac.length]);

            items[i].setItemName(itemNames[i]);
            items[i].setUserId(fakeUserId);
            items[i].setBindTagModule(bindTag);
        }

        return items;
    }

}
