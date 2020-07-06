package com.demon.hadoop.custom.rpc.protocol;

/**
 * rpc 响应对象
 */
public class RpcResponse {

    private String requestId;   // 请求ID
    private Throwable throwable;    // 抛出的异常
    private Object result;  // 返回结果

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", throwable=" + throwable +
                ", result=" + result +
                '}';
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
