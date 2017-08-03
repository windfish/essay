package com.demon.netty.chapter7;

import java.io.Serializable;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 上午10:15:01
 *
 */
public class SubscribeResp implements Serializable {

    private static final long serialVersionUID = 1091187281720358854L;

    private int subReqID;
    private String desc;
    
    public int getSubReqID() {
        return subReqID;
    }
    public void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return "Resp [subReqID="+subReqID+", desc="+desc+"]";
    }
    
}
