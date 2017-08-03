package com.demon.netty.chapter7;

import java.io.Serializable;

/**
 * 
 * @author xuliang
 * @since 2017年8月3日 上午10:11:35
 *
 */
public class SubscribeReq implements Serializable {

    private static final long serialVersionUID = -2889778156283710956L;
    
    private int subReqID;
    private String username;
    private String productName;
    private String phoneNumber;
    private String address;
    
    public int getSubReqID() {
        return subReqID;
    }
    public void setSubReqID(int subReqID) {
        this.subReqID = subReqID;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString() {
        return "Req [subReqID="+subReqID+", username="+username+", productName="+productName+", phoneNumber="+phoneNumber+"]";
    }

}
