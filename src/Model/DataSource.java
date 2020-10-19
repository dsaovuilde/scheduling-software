/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author dwsou
 */
public class DataSource {

    private static Connection conn = null;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB = "U05P9v";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + DB;
    private static final String USER = "U05P9v";
    private static final String PASS = "53688566389";
    private static String addOrModify;
    private static Customer customerToModify;
    private static Appointment appointmentToModify;
    private static Appointment newAppointment = null;
    private static Customer newCustomer = null;
    private static boolean addedAppointment = false;
    private static boolean modifiedAppointment = false;
    private static boolean addedCustomer = false;
    private static boolean modifiedCustomer = false;
    
    public static final String APPOINTMENT_TABLE = "appointment";
    public static final String COL_APPOINTMENT_ID = "appointmentId";
    public static final String COL_APPOINTMENT_CUSTOMER_ID = "customerId";
    public static final String COL_APPOINTMENT_USER_ID = "userId";
    public static final String COL_APPOINTMENT_NAME = "appointmentName";
    public static final String COL_APPOINTMENT_TITLE = "title";
    public static final String COL_APPOINTMENT_DESCRIPTION = "description";
    public static final String COL_APPOINTMENT_LOCATION = "location";
    public static final String COL_APPOINTMENT_CONTACT = "contact";
    public static final String COL_APPOINTMENT_TYPE = "type";
    public static final String COL_APPOINTMENT_URL = "url";
    public static final String COL_APPOINTMENT_START = "start";
    public static final String COL_APPOINTMENT_END = "end";
    public static final String COL_APPOINTMENT_CREATE_DATE = "createDate";
    public static final String COL_APPOINTMENT_CREATED_BY = "createdBY";
    public static final String COL_APPOINTMENT_LAST_UPDATE = "lastUpdate";
    public static final String COL_APPOINTMENT_LAST_UPDATE_BY = "lastUpdateBy";

    public static final String CUSTOMER_TABLE = "customer";
    public static final String COL_CUSTOMER_ID = "customerId";
    public static final String COL_CUSTOMER_NAME = "customerName";
    public static final String COL_CUSTOMER_ADDRESS_ID = "addressId";
    public static final String COL_CUSTOMER_ACTIVE = "active";
    public static final String COL_CUSTOMER_CREATE_DATE = "createDate";
    public static final String COL_CUSTOMER_CREATED_BY = "createdBY";
    public static final String COL_CUSTOMER_LAST_UPDATE = "lastUpdate";
    public static final String COL_CUSTOMER_LAST_UPDATE_BY = "lastUpdateBy";

    public static final String USER_TABLE = "user";
    public static final String COL_USER_ID = "userId";
    public static final String COL_USER_NAME = "userName";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ACTIVE = "active";
    public static final String COL_USER_CREATE_DATE = "createDate";
    public static final String COL_USER_CREATED_BY = "createdBY";
    public static final String COL_USER_LAST_UPDATE = "lastUpdate";
    public static final String COL_USER_LAST_UPDATE_BY = "lastUpdateBy";

    public static final String ADDRESS_TABLE = "address";
    public static final String COL_ADDRESS_ID = "addressId";
    public static final String COL_ADDRESS = "address";
    public static final String COL_ADDRESS_2 = "address2";
    public static final String COL_ADDRESS_CITY_ID = "cityId";
    public static final String COL_ADDRESS_POSTAL_CODE = "postalCode";
    public static final String COL_ADDRESS_PHONE = "phone";
    public static final String COL_ADDRESS_CREATE_DATE = "createDate";
    public static final String COL_ADDRESS_CREATED_BY = "createdBY";
    public static final String COL_ADDRESS_LAST_UPDATE = "lastUpdate";
    public static final String COL_ADDRESS_LAST_UPDATE_BY = "lastUpdateBy";

    public static final String CITY_TABLE = "city";
    public static final String COL_CITY_ID = "cityId";
    public static final String COL_CITY = "city";
    public static final String COL_CITY_COUNTRY_ID = "countryId";
    public static final String COL_CITY_CREATE_DATE = "createDate";
    public static final String COL_CITY_CREATED_BY = "createdBY";
    public static final String COL_CITY_LAST_UPDATE = "lastUpdate";
    public static final String COL_CITY_LAST_UPDATE_BY = "lastUpdateBy";

    public static final String COUNTRY_TABLE = "country";
    public static final String COL_COUNTRY_ID = "countryId";
    public static final String COL_COUNTRY = "country";
    public static final String COL_COUNTRY_CREATE_DATE = "createDate";
    public static final String COL_COUNTRY_CREATED_BY = "createdBY";
    public static final String COL_COUNTRY_LAST_UPDATE = "lastUpdate";
    public static final String COL_COUNTRY_LAST_UPDATE_BY = "lastUpdateBy";

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName(DRIVER);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected to Database");

            } catch (SQLException e) {
                System.out.println("SQLException:" + e.getMessage());

            } catch (ClassNotFoundException e) {
                
                System.out.println("Class not found " + e.getMessage());
            }
        }
        return conn;
       

    }
    
    public static void closeConnection() 
    {
        
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Closing DB connection");
                conn = null;
            } catch (SQLException e) {
                System.out.println("Error closing conn");
                e.printStackTrace();
            }
        };
    }
    public static void setAddOrModify(String addOrModify)
    {
        DataSource.addOrModify = addOrModify;
    }
    public static String getAddOrModify()
    {
        return addOrModify;
    }

    public static Customer getCustomerToModify() {
        return customerToModify;
    }

    public static void setCustomerToModify(Customer customerToModify) {
        DataSource.customerToModify = customerToModify;
    }

    public static Appointment getAppointmentToModify() {
        return appointmentToModify;
    }

    public static void setAppointmentToModify(Appointment appointmentToModify) {
        DataSource.appointmentToModify = appointmentToModify;
    }

    public static Appointment getNewAppointment() {
        return newAppointment;
    }

    public static void setNewAppointment(Appointment newAppointment) {
        DataSource.newAppointment = newAppointment;
    }

    public static Customer getNewCustomer() {
        return newCustomer;
    }

    public static void setNewCustomer(Customer newCustomer) {
        DataSource.newCustomer = newCustomer;
    }

    public static boolean isAddedAppointment() {
        return addedAppointment;
    }

    public static void setAddedAppointment(boolean addedAppointment) {
        DataSource.addedAppointment = addedAppointment;
    }

    public static boolean isModifiedAppointment() {
        return modifiedAppointment;
    }

    public static void setModifiedAppointment(boolean modifiedAppointment) {
        DataSource.modifiedAppointment = modifiedAppointment;
    }
    
    public static boolean isAddedCustomer() {
        return addedCustomer;
    }
    
    public static boolean isModifiedCustomer() {
        return modifiedCustomer;
    }
    
    public static void setAddedCustomer( boolean addedCustomer) {
        DataSource.addedCustomer = addedCustomer;
    }
    
    public static void setModifiedCustomer(boolean modifiedCustomer) {
        DataSource.modifiedCustomer = modifiedCustomer;
    }


}
