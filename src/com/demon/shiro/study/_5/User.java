package com.demon.shiro.study._5;

public class User {

    public String username;
    public String password;
    public String salt;
    
    public boolean getLocked(){
        return false;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public String getCredentialsSalt(){
        return this.username + this.salt;
    }
    
}
