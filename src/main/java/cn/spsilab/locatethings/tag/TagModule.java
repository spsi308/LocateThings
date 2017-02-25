package cn.spsilab.locatethings.tag;

import java.io.Serializable;

/**
 * Created by changrq on 17-2-20.
 */

public class TagModule  implements Serializable {
    private long moduleId;
    private String moduleMAC;
    private String networkNo;
    private String channelNo;

    public long getModuleId() {
        return moduleId;
    }

    public String getModuleMAC() {
        return moduleMAC;
    }

    public String getNetworkNo() {
        return networkNo;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public void setModuleMAC(String moduleMAC) {
        this.moduleMAC = moduleMAC;
    }

    public void setNetworkNo(String networkNo) {
        this.networkNo = networkNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }
}
