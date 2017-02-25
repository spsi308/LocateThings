package cn.spsilab.locatethings.module;

/**
 * Created by Feng on 2/19/2017.
 * http request response module
 */

public class ResponseResult<T> {

    private int status;
    private String msg;
    private T data;

    public ResponseResult(){}

    public ResponseResult(int status, String msg) {
        this(status, msg, null);
    }

    public ResponseResult(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseResult build(int status, String msg) {
        return new ResponseResult(status, msg);
    }

    public static <T> ResponseResult<T> build(int status, String msg, T data) {
        return new ResponseResult<T>(status, msg, data);
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    // test time output
    @Override
    public String toString() {
        return "ResponseResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

}
