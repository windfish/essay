package com.demon.shiro.study._5;

import java.util.Set;

public interface UserService {

    public User createUser(User user);
    public void changePassword(Long userId, String newPassword);  
    public void correlationRoles(Long userId, Long... roleIds);  
    public void uncorrelationRoles(Long userId, Long... roleIds);
    public User findByUsername(String username);  
    public Set<String> findRoles(String username);  
    public Set<String> findPermissions(String username);  
    
}
