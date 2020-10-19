/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import static Model.DataSource.COL_CUSTOMER_ID;
import static Model.DataSource.CUSTOMER_TABLE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author dwsou
 */
public class Customer {

    private int customerId;
    private static AtomicInteger customerIdIncrement = new AtomicInteger(0);
    private String customerName;
    private int customerAddressId;
    private int active;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;
    private String city;
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static final String CHECK_FOR_DUPLICATE_ID = "SELECT * FROM " + CUSTOMER_TABLE + " WHERE " + COL_CUSTOMER_ID + " >= ?;";
   

    public int getCustomerAddressId() {
        return customerAddressId;
    }

    public void setCustomerAddressId(int customerAddressId) {
        this.customerAddressId = customerAddressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId() {
        this.customerId = customerIdIncrement.incrementAndGet();
    }
    
    public void setId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
    
    public static void setAllCustomers(ObservableList<Customer> allCustomers) {
        Customer.allCustomers = allCustomers;
    }
    
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }
   public static void setIdIncrement(int num) {
      customerIdIncrement.set(num);
    }
}
