package cn.spsilab.locatethings.module;

/**
 * Created by Feng on 2/20/2017.
 * user info
 */

public class User {

    private long id;
    private String name;
    private String phone;
    private String password;
    private String photo; //optional
    private String token;
    private int networkNo; //网络号
    private int channelNo; //频段号

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(int channelNo) {
        this.channelNo = channelNo;
    }

    public int getNetworkNo() {
        return networkNo;
    }

    public void setNetworkNo(int networkNo) {
        this.networkNo = networkNo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", token='" + token + '\'' +
                ", networkNo=" + networkNo +
                ", channelNo=" + channelNo +
                '}';
    }
}
