/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Address;
import Model.Appointment;
import Model.Customer;
import Model.DataSource;
import static Model.DataSource.ADDRESS_TABLE;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.CITY_TABLE;
import static Model.DataSource.COL_ADDRESS;
import static Model.DataSource.COL_ADDRESS_2;
import static Model.DataSource.COL_ADDRESS_CITY_ID;
import static Model.DataSource.COL_ADDRESS_CREATED_BY;
import static Model.DataSource.COL_ADDRESS_CREATE_DATE;
import static Model.DataSource.COL_ADDRESS_ID;
import static Model.DataSource.COL_ADDRESS_LAST_UPDATE;
import static Model.DataSource.COL_ADDRESS_LAST_UPDATE_BY;
import static Model.DataSource.COL_ADDRESS_PHONE;
import static Model.DataSource.COL_ADDRESS_POSTAL_CODE;
import static Model.DataSource.COL_APPOINTMENT_CONTACT;
import static Model.DataSource.COL_APPOINTMENT_CREATED_BY;
import static Model.DataSource.COL_APPOINTMENT_CREATE_DATE;
import static Model.DataSource.COL_APPOINTMENT_CUSTOMER_ID;
import static Model.DataSource.COL_APPOINTMENT_DESCRIPTION;
import static Model.DataSource.COL_APPOINTMENT_END;
import static Model.DataSource.COL_APPOINTMENT_ID;
import static Model.DataSource.COL_APPOINTMENT_LAST_UPDATE;
import static Model.DataSource.COL_APPOINTMENT_LAST_UPDATE_BY;
import static Model.DataSource.COL_APPOINTMENT_LOCATION;
import static Model.DataSource.COL_APPOINTMENT_START;
import static Model.DataSource.COL_APPOINTMENT_TITLE;
import static Model.DataSource.COL_APPOINTMENT_TYPE;
import static Model.DataSource.COL_APPOINTMENT_URL;
import static Model.DataSource.COL_CITY;
import static Model.DataSource.COL_CITY_ID;
import static Model.DataSource.COL_CUSTOMER_ACTIVE;
import static Model.DataSource.COL_CUSTOMER_ADDRESS_ID;
import static Model.DataSource.COL_CUSTOMER_CREATED_BY;
import static Model.DataSource.COL_CUSTOMER_CREATE_DATE;
import static Model.DataSource.COL_CUSTOMER_ID;
import static Model.DataSource.COL_CUSTOMER_LAST_UPDATE;
import static Model.DataSource.COL_CUSTOMER_LAST_UPDATE_BY;
import static Model.DataSource.COL_CUSTOMER_NAME;
import static Model.DataSource.COL_USER_ACTIVE;
import static Model.DataSource.COL_USER_NAME;
import static Model.DataSource.CUSTOMER_TABLE;
import static Model.DataSource.USER_TABLE;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class HomeScreenController implements Initializable {

    @FXML
    private Label userLabel;
    @FXML
    private TextField appointmentSearchField;
    @FXML
    private TextField customerSearchField;
    @FXML
    private Button logoutButton;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn;
    @FXML
    private TableColumn<Customer, String> customerCityColumn;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button modifyAppointmentButton;
    @FXML
    private Button monthlyCalendarButton;
    @FXML
    private Button modifyCustomerButton;
    @FXML
    private Button addCustomerButton;

    private ObservableList<Appointment> appointmentArrayList = FXCollections.observableArrayList();
    private ObservableList<Customer> customerArrayList = FXCollections.observableArrayList();
    private Appointment appointmentToDelete;
    private Customer customerToDelete;
    private int deletedCustomerId;
    private int deletedAppointmentId;
    private static final String QUERY_ALL_APPOINTMENTS = "SELECT * FROM " + APPOINTMENT_TABLE + ";";

    private static final String QUERY_ALL_CUSTOMERS = "SELECT " + COL_CUSTOMER_ID + ", " + COL_CUSTOMER_NAME + ", " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ADDRESS_ID + ", "
            + COL_CUSTOMER_ACTIVE + ", " + CUSTOMER_TABLE + "." + COL_CUSTOMER_CREATE_DATE + ", " + CUSTOMER_TABLE + "." + COL_CUSTOMER_CREATED_BY + ", " + CUSTOMER_TABLE + "." + COL_CUSTOMER_LAST_UPDATE + ", "
            + CUSTOMER_TABLE + "." + COL_CUSTOMER_LAST_UPDATE_BY + ", " + CITY_TABLE + "." + COL_CITY + " AS city FROM " + CUSTOMER_TABLE + " INNER JOIN " + ADDRESS_TABLE + " ON " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ADDRESS_ID + " = " + ADDRESS_TABLE + "." + COL_ADDRESS_ID + " INNER JOIN " + CITY_TABLE + " ON " + ADDRESS_TABLE + "."
            + COL_ADDRESS_CITY_ID + " = " + CITY_TABLE + "." + COL_CITY_ID + ";";

    private static final String QUERY_APPOINTMENT = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE " + COL_APPOINTMENT_ID + " = ? OR " + COL_APPOINTMENT_TITLE + " = ?;";

    private static final String QUERY_CUSTOMER = "SELECT " + COL_CUSTOMER_ID + ", " + COL_CUSTOMER_NAME + "," + CITY_TABLE + "." + COL_CITY + " FROM " + CUSTOMER_TABLE + " INNER JOIN "
            + ADDRESS_TABLE + " ON " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ADDRESS_ID + " = " + ADDRESS_TABLE + "." + COL_ADDRESS_CITY_ID + " INNER JOIN " + CITY_TABLE + " ON " + ADDRESS_TABLE + "."
            + COL_ADDRESS_CITY_ID + " = " + CITY_TABLE + "." + COL_CITY_ID + " WHERE " + COL_CUSTOMER_ID + " = ? OR " + COL_CUSTOMER_NAME + " = ?;";

    private static final String UPDATE_USER_INACTIVE = "UPDATE " + USER_TABLE + " SET " + COL_USER_ACTIVE + " = 0 WHERE " + COL_USER_NAME + " = ?;";
    private static final String QUERY_CUSTOMER_NAME_BY_ID = "SELECT " + COL_CUSTOMER_ID + ", " + COL_CUSTOMER_NAME + " FROM " + CUSTOMER_TABLE
            + " WHERE " + COL_CUSTOMER_ID + " = ?;";
    private static final String DELETE_APPOINTMENT = "DELETE FROM " + APPOINTMENT_TABLE + " WHERE " + COL_APPOINTMENT_ID + " = ?;";
    private static final String DELETE_CUSTOMER = "DELETE FROM " + CUSTOMER_TABLE + " WHERE " + COL_CUSTOMER_ID + " = ?;";
    private static final String QUERY_ALL_ADDRESSES = "SELECT * FROM " + ADDRESS_TABLE + ";";
    private static final String QUERY_CUSTOMER_APPOINTMENTS = "SELECT " + COL_CUSTOMER_NAME + ", " + COL_APPOINTMENT_ID + " FROM " + CUSTOMER_TABLE + " INNER JOIN " + 
            APPOINTMENT_TABLE + " ON " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ID + " = " + APPOINTMENT_TABLE + "." + COL_CUSTOMER_ID + " WHERE " + COL_CUSTOMER_NAME + " = ?;";
    private static final String ADJUST_APPOINTMENT_IDS = "UPDATE " + APPOINTMENT_TABLE + " SET " + COL_APPOINTMENT_ID + " = ? WHERE " + COL_APPOINTMENT_ID + " = ?;";
  
    @Override

    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText(User.getCurrentUser());
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));

        customerIdColumn.setCellValueFactory(new PropertyValueFactory("CustomerId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory("customerName"));
        customerCityColumn.setCellValueFactory(new PropertyValueFactory("city"));

        populateAppointmentTable();
        populateCustomerTable();
        queryAddresses();
    }

    public void handleAppointmentSearch() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ObservableList<Appointment> searchList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_APPOINTMENT);
            statement.setString(1, appointmentSearchField.getText());
            statement.setString(2, appointmentSearchField.getText());
            results = statement.executeQuery();
            if (results.next()) {

                int id = results.getInt(COL_APPOINTMENT_ID);
                for (Appointment appt : appointmentArrayList) {
                    if (id == appt.getAppointmentId()) {
                        searchList.add(appt);
                    }
                }
                appointmentTable.setItems(searchList);
            } else {
                Alert alert = new Alert(AlertType.ERROR, "No appointment found", ButtonType.OK);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    alert.close();
                }
            }

        } catch (SQLException e) {
            System.out.println("SQLException :" + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
    }

    public void handleLogout() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logout?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }

        if (alert.getResult() == ButtonType.YES) {
            showUserInactive();
            displayLoginScreen();
        }
    }

    public void displayLoginScreen() throws IOException {
        Stage stg = (Stage) logoutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/LoginScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void showUserInactive() {

        PreparedStatement statement = null;
        Connection conn;

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(UPDATE_USER_INACTIVE);
            statement.setString(1, userLabel.getText());
            statement.executeUpdate();
            System.out.println("User " + userLabel.getText() + " is inactive.");
            DataSource.closeConnection();
        } catch (SQLException e) {
            System.out.println("SQLException :" + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }

    }

    public void populateAppointmentTable() {

        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ZonedDateTime start = null;
        ZonedDateTime startConverted = null;
        ZonedDateTime end = null;
        ZonedDateTime endConverted = null;

        if (DataSource.isAddedAppointment()) {
            appointmentArrayList = Appointment.getAllAppointments();
            appointmentArrayList.add(DataSource.getNewAppointment());
            Appointment.setAllAppointments(appointmentArrayList);
            DataSource.setAddedAppointment(false);
        } else if (DataSource.isModifiedAppointment()) {
            appointmentArrayList = Appointment.getAllAppointments();
            for (Appointment appt : appointmentArrayList) {
                if (appt.getAppointmentId() == DataSource.getAppointmentToModify().getAppointmentId()) {
                    System.out.println("modified appointment id :" + appt.getAppointmentId());
                    appt.setCustomer(DataSource.getAppointmentToModify().getCustomer());
                    appt.setTitle(DataSource.getAppointmentToModify().getTitle());
                    appt.setDescription(DataSource.getAppointmentToModify().getDescription());
                    appt.setLocation(DataSource.getAppointmentToModify().getLocation());
                    appt.setContact(DataSource.getAppointmentToModify().getContact());
                    appt.setType(DataSource.getAppointmentToModify().getType());
                    appt.setUrl(DataSource.getAppointmentToModify().getUrl());
                    appt.setStart(DataSource.getAppointmentToModify().getStart());
                    appt.setEnd(DataSource.getAppointmentToModify().getEnd());
                    appt.setLastUpdate(DataSource.getAppointmentToModify().getLastUpdate());
                    appt.setLastUpdateBy(DataSource.getAppointmentToModify().getLastUpdateBy());
                }
            }
            Appointment.setAllAppointments(appointmentArrayList);
            DataSource.setModifiedAppointment(false);
        } else if (Appointment.getAllAppointments().isEmpty()) {

            try {
                conn = DataSource.getConnection();
                statement = conn.prepareStatement(QUERY_ALL_APPOINTMENTS);
                results = statement.executeQuery();

                while (results.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setCustomer(queryCustomerNameById(results.getInt(COL_APPOINTMENT_CUSTOMER_ID)));
                    appointment.setAppointmentId();
                    appointment.setTitle(results.getString(COL_APPOINTMENT_TITLE));
                    appointment.setDescription(results.getString(COL_APPOINTMENT_DESCRIPTION));
                    appointment.setLocation(results.getString(COL_APPOINTMENT_LOCATION));
                    appointment.setContact(results.getString(COL_APPOINTMENT_CONTACT));
                    appointment.setType(results.getString(COL_APPOINTMENT_TYPE));
                    appointment.setUrl(results.getString(COL_APPOINTMENT_URL));
                    appointment.setStart(results.getObject(COL_APPOINTMENT_START, LocalDateTime.class));
                    appointment.setEnd(results.getObject(COL_APPOINTMENT_END, LocalDateTime.class));
                    appointment.setCreateDate(results.getObject(COL_APPOINTMENT_CREATE_DATE, LocalDateTime.class));
                    appointment.setCreatedBy(results.getString(COL_APPOINTMENT_CREATED_BY));
                    appointment.setLastUpdate(results.getObject(COL_APPOINTMENT_LAST_UPDATE, LocalDateTime.class));
                    appointment.setLastUpdateBy(results.getString(COL_APPOINTMENT_LAST_UPDATE_BY));

                    appointmentArrayList.add(appointment);
                }
                Appointment.setAllAppointments(appointmentArrayList);

            } catch (SQLException e) {
                System.out.println("SQLException populateAppointmentTable");
            } finally {
                try {
                    results.close();
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("error closing statement/results");
                }

            }

        } else {
            appointmentArrayList = Appointment.getAllAppointments();
        }
        String location = null;
        String zone = null;
        ObservableList<Appointment> adjustedAppointmentList = FXCollections.observableArrayList();
        adjustedAppointmentList = appointmentArrayList;
        for(Appointment appt : adjustedAppointmentList){
            location = appt.getLocation();
            switch (location) {
                case "Phoenix":
                    zone = "US/Arizona";
                    break;
                case "London":
                    zone = "Europe/London";
                    break;
                case "New York":
                    zone = "US/Eastern";

            }
            start = appt.getStart().atZone(ZoneId.of(zone));
            end = appt.getEnd().atZone(ZoneId.of(zone));
            startConverted = start.withZoneSameInstant(ZoneId.systemDefault());
            endConverted = end.withZoneSameInstant(ZoneId.systemDefault());
            if(!appt.isAdjusted()){
            appt.setStart(startConverted.toLocalDateTime());
            appt.setEnd(endConverted.toLocalDateTime());
            appt.setAdjusted(true);
            }
            
        }
        appointmentTable.setItems(adjustedAppointmentList);
    }

    public void populateCustomerTable() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;

        if (DataSource.isAddedCustomer()) {
            customerArrayList = Customer.getAllCustomers();
            customerArrayList.add(DataSource.getNewCustomer());
            Customer.setAllCustomers(customerArrayList);
            DataSource.setAddedCustomer(false);
        } else if (DataSource.isModifiedCustomer()) {
            customerArrayList = Customer.getAllCustomers();
            for (Customer customer : customerArrayList) {
                if (customer.getCustomerId() == DataSource.getCustomerToModify().getCustomerId());
                customer.setCustomerName(DataSource.getCustomerToModify().getCustomerName());
                customer.setCustomerAddressId(DataSource.getCustomerToModify().getCustomerAddressId());
                customer.setCity(DataSource.getCustomerToModify().getCity());
                customer.setActive(DataSource.getCustomerToModify().getActive());
                customer.setCreateDate(DataSource.getCustomerToModify().getCreateDate());
                customer.setCreatedBy(DataSource.getCustomerToModify().getCreatedBy());
                customer.setLastUpdate(DataSource.getCustomerToModify().getLastUpdate());
                customer.setLastUpdateBy(DataSource.getCustomerToModify().getLastUpdateBy());

                Customer.setAllCustomers(customerArrayList);
                DataSource.setModifiedCustomer(false);
            }
        } else if (Customer.getAllCustomers().isEmpty()) {

            try {
                conn = DataSource.getConnection();
                statement = conn.prepareStatement(QUERY_ALL_CUSTOMERS);
                results = statement.executeQuery();
                while (results.next()) {
                    Customer customer = new Customer();
                    customer.setId(results.getInt(COL_CUSTOMER_ID));
                    customer.setCustomerName(results.getString(COL_CUSTOMER_NAME));
                    customer.setCustomerAddressId(results.getInt(COL_CUSTOMER_ADDRESS_ID));
                    customer.setActive(results.getInt(COL_CUSTOMER_ACTIVE));
                    customer.setCreateDate(results.getObject(COL_CUSTOMER_CREATE_DATE, LocalDateTime.class));
                    customer.setCreatedBy(results.getString(COL_CUSTOMER_CREATED_BY));
                    customer.setLastUpdate(results.getObject(COL_CUSTOMER_LAST_UPDATE, LocalDateTime.class));
                    customer.setLastUpdateBy(results.getString(COL_CUSTOMER_LAST_UPDATE_BY));
                    customer.setCity(results.getString("city"));

                    customerArrayList.add(customer);

                }
                // when a customer is deleted, the unique customerId will not be reused.
                List<Integer> idList = new ArrayList<Integer>();
                for(int i = 0; i < customerArrayList.size(); ++i) {
                    idList.add(customerArrayList.get(i).getCustomerId());
                }
                Integer maxValue = Collections.max(idList);
                Customer.setIdIncrement(maxValue);
                        
                
                Customer.setAllCustomers(customerArrayList);
            } catch (SQLException e) {
                System.out.println("SQLException :" + e.getMessage());
            } finally {
                try {
                    results.close();
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("error closing statement/results");
                }
            }
        } else {
            customerArrayList = Customer.getAllCustomers();
        }
        customerTable.setItems(customerArrayList);
    }

    public void handleCustomerSearch() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ObservableList<Customer> searchList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CUSTOMER);
            statement.setString(1, customerSearchField.getText());
            statement.setString(2, customerSearchField.getText());
            results = statement.executeQuery();
            if (results.next()) {

                int id = results.getInt(COL_CUSTOMER_ID);
                for (Customer cus : customerArrayList) {
                    if (id == cus.getCustomerId()) {
                        searchList.add(cus);
                    }
                }
                customerTable.setItems(searchList);
            } else {
                Alert alert = new Alert(AlertType.ERROR, "No matching customer found", ButtonType.OK);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    alert.close();
                }
            }

        } catch (SQLException e) {
            System.out.println("SQLException :" + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
    }

    public void handleAddAppointment() throws IOException {
        DataSource.setAddOrModify("Add");
        Stage stg = (Stage) addAppointmentButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/AddAppointmentScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void handleModifyAppointment() throws IOException {
        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(AlertType.ERROR, "Select an appointment to modify", ButtonType.OK);
            alert.showAndWait();

        } else {
            Stage stg = (Stage) modifyAppointmentButton.getScene().getWindow();
            DataSource.setAddOrModify("Modify");
            DataSource.setAppointmentToModify(appointmentTable.getSelectionModel().getSelectedItem());
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/AddAppointmentScreen.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stg.close();
        }

    }

    public void handleDeleteAppointment() {
        
    deletedAppointmentId = appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId();
        PreparedStatement statement = null;

        if (appointmentTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(AlertType.ERROR, "Select an appointment to delete", ButtonType.OK);
            alert.showAndWait();

        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this Appointment?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.NO) {
                alert.close();
            }
            if (alert.getResult() == ButtonType.YES) {
                try {
                    Connection conn = DataSource.getConnection();
                    statement = conn.prepareStatement(DELETE_APPOINTMENT);
                    statement.setInt(1,deletedAppointmentId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("SQLException: " + e.getMessage());
                } finally {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing statement");
                    }
                }
                        adjustAppointmentIds();
            }

        }
        appointmentArrayList.stream().filter((appt) -> (appt.getAppointmentId() == appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId())).forEachOrdered((appt) -> {
      appointmentToDelete = appt;
        });
        appointmentArrayList.remove(appointmentToDelete);
        
        Appointment.setAllAppointments(appointmentArrayList);

        populateAppointmentTable();
    }
    
    public void handleDeleteCustomer() {
        deletedCustomerId = customerTable.getSelectionModel().getSelectedItem().getCustomerId();
    
        PreparedStatement statement = null;
        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(AlertType.ERROR, "Select an customer to delete", ButtonType.OK);
            alert.showAndWait();
        } else if (customerHasAppointment()) {
            Alert alert = new Alert(AlertType.ERROR, "Customer cannot be deleted while scheduled for an appointment", ButtonType.OK);
            alert.showAndWait();

        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this customer?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.NO) {
                alert.close();
            }
            if (alert.getResult() == ButtonType.YES) {
                try {
                    Connection conn = DataSource.getConnection();
                    statement = conn.prepareStatement(DELETE_CUSTOMER);
                    statement.setInt(1, deletedCustomerId);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("SQLException: " + e.getMessage());
                } finally {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing statement");
                    }
                }
            }
            customerArrayList.stream().filter((cus) -> (cus.getCustomerId() == customerTable.getSelectionModel().getSelectedItem().getCustomerId())).forEachOrdered((cus) -> {
      customerToDelete = cus;
        });
        customerArrayList.remove(customerToDelete);
        Customer.setAllCustomers(customerArrayList);
        populateCustomerTable();

        }
        
        
    }
    private boolean customerHasAppointment() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        boolean hasAppointment = false;
        
        try{
           statement = conn.prepareStatement(QUERY_CUSTOMER_APPOINTMENTS);
           statement.setString(1, customerTable.getSelectionModel().getSelectedItem().getCustomerName());
           results = statement.executeQuery();
           if(results.next()) {
               hasAppointment = true;
           }
        }catch(SQLException e) {
            System.out.println("SQLException in hasAppointment: " + e.getMessage());
        } finally {
                    try {
                        results.close();
                        statement.close();
                    } catch (SQLException e) {
                        System.out.println("Error closing statement");
                    }
                }
        return hasAppointment;
    }

    private String queryCustomerNameById(int id) {
        String cusName = "";
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CUSTOMER_NAME_BY_ID);
            statement.setString(1, Integer.toString(id));
            results = statement.executeQuery();
            if (!results.next()) {
                System.out.println("Query customer id by name returned no results");
            }
            cusName = results.getString(COL_CUSTOMER_NAME);
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
        return cusName;
    }

    public void handleMonthlyCalendar() throws IOException {
        Stage stg = (Stage) monthlyCalendarButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/MonthlyCalendar.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void handleModifyCustomer() throws IOException {
        if (customerTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(AlertType.ERROR, "Select a customer to modify", ButtonType.OK);
            alert.showAndWait();

        } else {
            Stage stg = (Stage) modifyCustomerButton.getScene().getWindow();
            DataSource.setAddOrModify("Modify");
            DataSource.setCustomerToModify(customerTable.getSelectionModel().getSelectedItem());
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/AddCustomerScreen.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stg.close();
        }
    }

    public void handleAddCustomer() throws IOException {
        DataSource.setAddOrModify("Add");
        Stage stg = (Stage) addCustomerButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/AddCustomerScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void queryAddresses() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            statement = conn.prepareStatement(QUERY_ALL_ADDRESSES);
            results = statement.executeQuery();
            while (results.next()) {
                Address address = new Address();
                address.setAddressId(results.getInt(COL_ADDRESS_ID));
                address.setAddress(results.getString(COL_ADDRESS));
                address.setAddress2(results.getString(COL_ADDRESS_2));
                address.setCityId(results.getInt(COL_ADDRESS_CITY_ID));
                address.setPostalCode(results.getString(COL_ADDRESS_POSTAL_CODE));
                address.setPhone(results.getString(COL_ADDRESS_PHONE));
                address.setCreateDate(results.getObject(COL_ADDRESS_CREATE_DATE, LocalDateTime.class));
                address.setCreatedBy(results.getString(COL_ADDRESS_CREATED_BY));
                address.setLastUpdate(results.getObject(COL_ADDRESS_LAST_UPDATE, LocalDateTime.class));
                address.setLastUpdateBy(results.getString(COL_ADDRESS_LAST_UPDATE_BY));
            }
        } catch (SQLException e) {
            System.out.println("SQLException in queryAddresses: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
    }

    public void handleWeeklyCalendar() throws IOException {
        Stage stg = (Stage) monthlyCalendarButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/WeeklyCalendar.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void handleExit() {
        showUserInactive();
        DataSource.closeConnection();
        Platform.exit();
    }

    public void handleReports() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/ReportsScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    // when deleting appointments, old appointment id's will be reused.
    public void adjustAppointmentIds() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        
        try {
            
        
            statement = conn.prepareStatement(ADJUST_APPOINTMENT_IDS);
            for(int i = deletedAppointmentId; i < Appointment.getAllAppointments().size(); ++i) {
              statement.setInt(1, i);
              statement.setInt(2, i + 1);
              statement.executeUpdate();
            }
            
        }catch(SQLException e) {
            System.out.println("SQLException in updateId's: " + e.getMessage());
    }
        appointmentArrayList.clear();
        Appointment.getAllAppointments().clear();
        Appointment.resetIdIncrement();
    }
   
}
