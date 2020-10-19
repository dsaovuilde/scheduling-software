/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author dwsou
 */
public class User {
    private int userId;
    private static AtomicInteger userIdIncrement = new AtomicInteger(0);
    private String userName;
    private String password;
    private int active = 0;
    private LocalDate createDate;
    private LocalTime createTime;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    private static String currentUser;

    public User(int userId, String userName, String password, LocalDate createDate, LocalTime createTime, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.userId = userIdIncrement.incrementAndGet();
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
        this.createTime = createTime;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userIdIncrement.incrementAndGet();
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalTime createTime) {
        this.createTime = createTime;
    }
    

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
       return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    public static void setCurrentUser(String currentUser)
    {
        User.currentUser = currentUser;
    }
    
    public static String getCurrentUser() 
    {
        return currentUser;
    }
    
    
}
