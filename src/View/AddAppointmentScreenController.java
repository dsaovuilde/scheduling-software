/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.DataSource;
import static Model.DataSource.ADDRESS_TABLE;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.CITY_TABLE;
import static Model.DataSource.COL_ADDRESS_CITY_ID;
import static Model.DataSource.COL_ADDRESS_ID;
import static Model.DataSource.COL_APPOINTMENT_CONTACT;
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
import static Model.DataSource.COL_CUSTOMER_ID;
import static Model.DataSource.COL_CUSTOMER_NAME;
import static Model.DataSource.COL_USER_ID;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class AddAppointmentScreenController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private ComboBox customerBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox locationBox;
    @FXML
    private ComboBox contactBox;
    @FXML
    private ComboBox typeBox;
    @FXML
    private TextField urlField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox startTime;
    @FXML
    private ComboBox endTime;
    @FXML
    private Label headerLabel;
    @FXML
    private Button saveButton;

    private String selectedLocation;
    private Appointment appointmentToModify;
    private boolean locationBoxChanged;
    private boolean titleFieldChanged;
    private boolean descriptionFieldChanged;
    private boolean customerBoxChanged;
    private boolean contactBoxChanged;
    private boolean typeBoxChanged;
    private boolean urlFieldChanged;
    private boolean datePickerChanged;
    private boolean startTimeChanged;
    private boolean endTimeChanged;
    String zone = "US/Eastern";

    private static final String QUERY_CITIES = "SELECT " + COL_CITY + " FROM " + CITY_TABLE + ";";
    private static final String QUERY_USERS = "SELECT " + COL_USER_NAME + " FROM " + USER_TABLE + ";";
    private static final String QUERY_CUSTOMERS_BY_LOCATION = "SELECT " + COL_CUSTOMER_NAME + " FROM " + CUSTOMER_TABLE + " INNER JOIN " + ADDRESS_TABLE + " ON "
            + CUSTOMER_TABLE + "." + COL_CUSTOMER_ADDRESS_ID + " = " + ADDRESS_TABLE + "." + COL_ADDRESS_ID + " INNER JOIN " + CITY_TABLE + " ON " + ADDRESS_TABLE
            + "." + COL_ADDRESS_CITY_ID + " = " + CITY_TABLE + "." + COL_CITY_ID + " WHERE " + CITY_TABLE + "." + COL_CITY + " = ?;";
    private static final String INSERT_NEW_APPOINTMENT = "INSERT INTO " + APPOINTMENT_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String QUERY_CUSTOMER_ID_BY_NAME = "SELECT " + COL_CUSTOMER_ID + ", " + COL_CUSTOMER_NAME + " FROM " + CUSTOMER_TABLE
            + " WHERE " + COL_CUSTOMER_NAME + " = ?;";
    private static final String QUERY_USER_ID_BY_NAME = "SELECT " + COL_USER_ID + ", " + COL_USER_NAME + " FROM " + USER_TABLE + " WHERE "
            + COL_USER_NAME + " = ?;";
    private static final String UPDATE_APPOINTMENT_PART1 = "UPDATE " + APPOINTMENT_TABLE + " SET ";
    private static final String UPDATE_APPOINTMENT_PART2 = " = ? WHERE " + COL_APPOINTMENT_ID + " = ?;";
    private static final String QUERY_APPOINTMENT_FOR_OVERLAPS = " SELECT " + COL_APPOINTMENT_TITLE + ", " + COL_APPOINTMENT_LOCATION + ", " + COL_APPOINTMENT_START + ", " + COL_APPOINTMENT_END + " FROM " + APPOINTMENT_TABLE + ";";
    private static final String SET_CUSTOMER_ACTIVE = "UPDATE " + CUSTOMER_TABLE + " SET " + COL_CUSTOMER_ACTIVE + " = 1 WHERE " + COL_CUSTOMER_NAME + " = ?;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        headerLabel.setText(DataSource.getAddOrModify());
        if (DataSource.getAddOrModify().equals("Modify")) {
            populateFields();
        }
        populateTimes();
        populateLocations();
        populateContacts();
        populateTypes();

        locationBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    selectedLocation = newValue.toString();
                    populateCustomers();
                }
        );
    }

    public void populateTimes() {

        ObservableList<String> list = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse("00:00");
        for (int i = 0; i < 1440; i += 30) {
            list.add(formatter.format(time.plusMinutes(i)));

        }
        startTime.setItems(list);
        endTime.setItems(list);
    }

    public void populateLocations() {

        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ObservableList<String> cityList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CITIES);
            results = statement.executeQuery();

            while (results.next()) {
                cityList.add(results.getString(COL_CITY));
            }
            locationBox.setItems(cityList);
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

    }

    public void populateContacts() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ObservableList<String> contactList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_USERS);
            results = statement.executeQuery();

            while (results.next()) {
                contactList.add(results.getString(COL_USER_NAME));
            }
            contactBox.setItems(contactList);
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
    }

    public void populateTypes() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.add("Sales meeting");
        typeList.add("Consultation");
        typeList.add("Status Report");
        typeList.add("Web Conference");

        typeBox.setItems(typeList);
    }

    public void populateCustomers() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        ObservableList<String> customerList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CUSTOMERS_BY_LOCATION);
            statement.setString(1, selectedLocation);
            results = statement.executeQuery();

            while (results.next()) {
                customerList.add(results.getString(COL_CUSTOMER_NAME));
            }
            customerBox.setItems(customerList);
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
    }

    public void handleSave() throws IOException {
        ZonedDateTime createDate = null;
        ZonedDateTime startDate = null;
        ZonedDateTime endDate = null;
        ZonedDateTime lastUpdate = null;
        if (isValid()) {

            PreparedStatement statement = null;
            Connection conn;
            LocalDate date = datePicker.getValue();
            LocalTime start = LocalTime.parse(startTime.getSelectionModel().getSelectedItem().toString());
            LocalTime end = LocalTime.parse(endTime.getSelectionModel().getSelectedItem().toString());
            String customer = customerBox.getSelectionModel().getSelectedItem().toString();
            String location = locationBox.getSelectionModel().getSelectedItem().toString();
            String description = descriptionField.getText();
            String title = titleField.getText();
            String contact = contactBox.getSelectionModel().getSelectedItem().toString();
            String type = typeBox.getSelectionModel().getSelectedItem().toString();
            String url = urlField.getText();
            createDate = LocalDateTime.now().atZone(ZoneId.systemDefault());
            startDate = LocalDateTime.of(date, start).atZone(ZoneId.of(zone));
            endDate = LocalDateTime.of(date, end).atZone(ZoneId.of(zone));
            lastUpdate = LocalDateTime.now().atZone(ZoneId.systemDefault());
            String createdBy = User.getCurrentUser();
            String lastUpdateBy = User.getCurrentUser();

            try {

                conn = DataSource.getConnection();
                if (DataSource.getAddOrModify().equals("Add")) {
                    Appointment appointment = new Appointment();

                    appointment.setAppointmentId();
                    appointment.setCustomer(customer);
                    appointment.setLocation(location);
                    appointment.setDescription(description);
                    appointment.setTitle(title);
                    appointment.setContact(contact);
                    appointment.setType(type);
                    appointment.setUrl(url);
                    appointment.setStart(startDate.toLocalDateTime());
                    appointment.setEnd(endDate.toLocalDateTime());
                    appointment.setCreateDate(createDate.toLocalDateTime());
                    appointment.setCreatedBy(createdBy);
                    appointment.setLastUpdate(lastUpdate.toLocalDateTime());
                    appointment.setLastUpdateBy(lastUpdateBy);
                    DataSource.setNewAppointment(appointment);

                    statement = conn.prepareStatement(INSERT_NEW_APPOINTMENT);
                    statement.setString(1, Integer.toString(appointment.getAppointmentId()));
                    statement.setString(2, Integer.toString(queryCustomerIdByName()));
                    statement.setString(3, Integer.toString(queryUserIdByName()));
                    statement.setString(4, title);
                    statement.setString(5, description);
                    statement.setString(6, location);
                    statement.setString(7, contact);
                    statement.setString(8, type);
                    statement.setString(9, url);
                    statement.setString(10, startDate.toLocalDateTime().toString().replace("T", " "));
                    statement.setString(11, endDate.toLocalDateTime().toString().replace("T", " "));
                    statement.setString(12, createDate.toLocalDateTime().toString().replace("T", " "));
                    statement.setString(13, createdBy);
                    statement.setString(14, lastUpdate.toLocalDateTime().toString().replace("T", " "));
                    statement.setString(15, lastUpdateBy);
                    statement.executeUpdate();
                    DataSource.setAddedAppointment(true);

                } else if (DataSource.getAddOrModify().equals("Modify")) {
                    if (locationBoxChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_LOCATION + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, locationBox.getSelectionModel().getSelectedItem().toString());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setLocation(locationBox.getSelectionModel().getSelectedItem().toString());
                        locationBoxChanged = false;
                    }
                    if (titleFieldChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_TITLE + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, titleField.getText());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setTitle(titleField.getText());
                        titleFieldChanged = false;

                    }
                    if (descriptionFieldChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_DESCRIPTION + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, descriptionField.getText());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setDescription(descriptionField.getText());
                        descriptionFieldChanged = false;
                    }
                    if (customerBoxChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_CUSTOMER_ID + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, Integer.toString(queryCustomerIdByName()));
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setCustomer(customerBox.getSelectionModel().getSelectedItem().toString());
                        customerBoxChanged = false;
                    }
                    if (contactBoxChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_CONTACT + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, contactBox.getSelectionModel().getSelectedItem().toString());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setContact(contactBox.getSelectionModel().getSelectedItem().toString());
                        contactBoxChanged = false;
                    }
                    if (typeBoxChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_TYPE + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, typeBox.getSelectionModel().getSelectedItem().toString());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setType(typeBox.getSelectionModel().getSelectedItem().toString());
                        typeBoxChanged = false;
                    }
                    if (urlFieldChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_URL + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, urlField.getText());
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setUrl(urlField.getText());
                        urlFieldChanged = false;
                    }
                    if (datePickerChanged || startTimeChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_START + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, startDate.toLocalDateTime().toString().replace("T", " "));
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setStart(startDate.toLocalDateTime());
                        startTimeChanged = false;
                    }
                    if (datePickerChanged || endTimeChanged) {
                        statement = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_END + UPDATE_APPOINTMENT_PART2);
                        statement.setString(1, endDate.toLocalDateTime().toString().replace("T", " "));
                        statement.setString(2, idField.getText());
                        statement.executeUpdate();
                        DataSource.getAppointmentToModify().setEnd(endDate.toLocalDateTime());
                        datePickerChanged = false;
                        endTimeChanged = false;
                    }
                    PreparedStatement stmt = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_LAST_UPDATE + UPDATE_APPOINTMENT_PART2);
                    stmt.setString(1, lastUpdate.toLocalDateTime().toString().replace("T", " "));
                    stmt.setString(2, idField.getText());
                    stmt.executeUpdate();
                    DataSource.getAppointmentToModify().setLastUpdate(lastUpdate.toLocalDateTime());

                    PreparedStatement stmt2 = conn.prepareStatement(UPDATE_APPOINTMENT_PART1 + COL_APPOINTMENT_LAST_UPDATE_BY + UPDATE_APPOINTMENT_PART2);
                    stmt2.setString(1, User.getCurrentUser());
                    stmt2.setString(2, idField.getText());
                    stmt2.executeUpdate();
                    DataSource.getAppointmentToModify().setLastUpdateBy(User.getCurrentUser());

                    DataSource.setModifiedAppointment(true);
                }

            } catch (SQLException e) {
                System.out.println("SQLException: " + e.getMessage());
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    System.out.println("error closing statement");
                }
            }
            setCustomerActive();
            displayMainScreen();
        }
    }

    private int queryCustomerIdByName() {
        int customerId = -1;
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        String customer = customerBox.getSelectionModel().getSelectedItem().toString();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CUSTOMER_ID_BY_NAME);
            statement.setString(1, customer);
            results = statement.executeQuery();
            if (!results.next()) {
                System.out.println("Query customer id by name returned no results");
            }
            customerId = results.getInt(COL_CUSTOMER_ID);
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
        return customerId;
    }

    public int queryUserIdByName() {
        int userId = -1;
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_USER_ID_BY_NAME);
            statement.setString(1, User.getCurrentUser());
            results = statement.executeQuery();
            if (!results.next()) {
                System.out.println("Query user id by name returned no results");
            }
            userId = results.getInt(COL_USER_ID);
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
        return userId;
    }

    private boolean isValid() {
        boolean valid = true;

        StringBuilder message = new StringBuilder();
        String location = null;
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn;
        LocalDateTime businessHourStart = LocalDateTime.of(datePicker.getValue(), LocalTime.parse("08:00"));
        LocalDateTime businessHourEnd = LocalDateTime.of(datePicker.getValue(), LocalTime.parse("17:00"));
        LocalTime start = LocalTime.parse(startTime.getSelectionModel().getSelectedItem().toString());
        LocalTime end = LocalTime.parse(endTime.getSelectionModel().getSelectedItem().toString());
        LocalDate startDate = datePicker.getValue();
        LocalDateTime selectedStartTime = LocalDateTime.of(startDate, start);
        LocalDateTime selectedEndTime = LocalDateTime.of(startDate, end);
        ZonedDateTime startHour = null;
        ZonedDateTime endHour = null;
        ZonedDateTime businessOpen = businessHourStart.atZone(ZoneId.systemDefault());
        ZonedDateTime businessClosed = businessHourEnd.atZone(ZoneId.systemDefault());
        ZonedDateTime startMyTime = null;
        ZonedDateTime endMyTime = null;
        try {
            location = locationBox.getSelectionModel().getSelectedItem().toString();

            try {

                if (selectedEndTime.isBefore(selectedStartTime) || selectedEndTime.equals(selectedStartTime)) {
                    message.append("End time must be after start time");
                    valid = false;
                }
            } catch (NullPointerException ex) {
                valid = false;
                message.append("Start and end times cannot be blank \n");
            }

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
            startHour = selectedStartTime.atZone(ZoneId.of(zone));
            endHour = selectedEndTime.atZone(ZoneId.of(zone));
            startMyTime = startHour.withZoneSameInstant(ZoneId.systemDefault());
            endMyTime = endHour.withZoneSameInstant(ZoneId.systemDefault());

            if ((startHour.toInstant().isBefore(businessHourStart.atZone(ZoneId.of(zone)).toInstant()) || startHour.toInstant().isAfter(businessHourEnd.atZone(ZoneId.of(zone)).toInstant())) || (endHour.toInstant().isBefore(businessHourStart.atZone(ZoneId.of(zone)).toInstant()) || endHour.toInstant().isAfter(businessHourEnd.atZone(ZoneId.of(zone)).toInstant()))) {
                valid = false;
                System.out.println(businessHourStart.atZone(ZoneId.of(zone)));
                System.out.println(startHour);
                message.append("Meeting scheduled between ").append(selectedStartTime).append(" and ").append(selectedEndTime).append(" is outside of business hours in ").append(location).append(" \n");
            }
        } catch (NullPointerException e) {
            message.append("Please select a location");
        }

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_APPOINTMENT_FOR_OVERLAPS);

            results = statement.executeQuery();

            while (results.next()) {
                String appointmentZone = "US/Eastern";
                LocalDateTime s = results.getObject(COL_APPOINTMENT_START, LocalDateTime.class);
                LocalDateTime e = results.getObject(COL_APPOINTMENT_END, LocalDateTime.class);

                String appointmentLocation = results.getString(COL_APPOINTMENT_LOCATION);
                switch (appointmentLocation) {
                    case "Phoenix":
                        appointmentZone = "US/Arizona";
                        break;
                    case "London":
                        appointmentZone = "Europe/London";
                        break;
                    case "New York":
                        appointmentZone = "US/Eastern";

                }
                ZonedDateTime startTimeAtLocation = s.atZone(ZoneId.of(appointmentZone));
                ZonedDateTime endTimeAtLocation = e.atZone(ZoneId.of(appointmentZone));
                ZonedDateTime startTimeAtHome = startTimeAtLocation.withZoneSameInstant(ZoneId.systemDefault());
                ZonedDateTime endTimeAtHome = endTimeAtLocation.withZoneSameInstant(ZoneId.systemDefault());
                
                

                if ((endHour.toInstant().isAfter(startTimeAtHome.toInstant()) || endHour.toInstant().equals(startTimeAtHome.toInstant())) && (startHour.toInstant().equals(endTimeAtHome.toInstant()) || startHour.toInstant().isBefore(endTimeAtHome.toInstant()))) {
                    valid = false;
                    System.out.println(endHour.toLocalDateTime());
                    System.out.println(startTimeAtHome.toLocalDateTime());
                    String overlappingAppointment = results.getString(COL_APPOINTMENT_TITLE);
                    if(!overlappingAppointment.equals(titleField.getText())){

                    message.append("Selected times overlap with Appointment: ").append(overlappingAppointment).append("\n");
                    }

                }

            }
        } catch (SQLException ex) {
            System.out.println("SQLException in isValid : " + ex.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }

        if (titleField.getText().length() == 0 || descriptionField.getText().length() == 0 || urlField.getText().length() == 0) {
            valid = false;
            message.append("Title, Description and URL must be entered \n");
        }
        if (customerBox.getSelectionModel().getSelectedItem() == null || contactBox.getSelectionModel().getSelectedItem() == null || typeBox.getSelectionModel().getSelectedItem() == null) {
            valid = false;
            message.append("Please select Customer, Contact and Type \n");
        }
        if (datePicker.getValue() == null || startTime.getSelectionModel().getSelectedItem() == null || endTime.getSelectionModel().getSelectedItem() == null) {
            valid = false;
            message.append("Please select a valid start and end time \n");
        }
        if (startHour.toInstant().isBefore(businessOpen.toInstant()) || startHour.toInstant().isAfter(businessClosed.toInstant()) || endHour.toInstant().isBefore(businessOpen.toInstant()) || endHour.toInstant().isAfter(businessClosed.toInstant())) {
            message.append("Meeting is scheduled outside of local office hours");
            valid = false;
        }
        if (message.length() != 0) {
            Alert alert = new Alert(AlertType.ERROR, message.toString(), ButtonType.OK);
            alert.showAndWait();
        }

        if (valid) {
            message.append("Appointment scheduled from ").append(startHour.toLocalTime()).append(" - ").append(endHour.toLocalTime()).append(" in ").append(location).append(" is ").append(startMyTime.toLocalTime()).append(" - ").append(endMyTime.toLocalTime()).append("Local Time. Is this the correct time for this appointment?");
            Alert alert = new Alert(AlertType.CONFIRMATION, message.toString(), ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.NO) {
                valid = false;
                alert.close();
            } else {
                alert.close();
            }
        }

        return valid;
    }

    public void displayMainScreen() throws IOException {
        Stage stg = (Stage) saveButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/HomeScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    public void populateFields() {
        populateLocations();

        appointmentToModify = DataSource.getAppointmentToModify();
        idField.setText(Integer.toString(appointmentToModify.getAppointmentId()));
        locationBox.getSelectionModel().select(appointmentToModify.getLocation());
        titleField.setText(appointmentToModify.getTitle());
        descriptionField.setText(appointmentToModify.getDescription());
        customerBox.getSelectionModel().select(appointmentToModify.getCustomer());
        contactBox.getSelectionModel().select(appointmentToModify.getContact());
        typeBox.getSelectionModel().select(appointmentToModify.getType());
        urlField.setText(appointmentToModify.getUrl());
        datePicker.setValue(appointmentToModify.getStart().toLocalDate());
        startTime.getSelectionModel().select(appointmentToModify.getStart().toLocalTime());
        endTime.getSelectionModel().select(appointmentToModify.getEnd().toLocalTime());

        locationBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    selectedLocation = newValue.toString();
                    if (oldValue != newValue) {
                        locationBoxChanged = true;
                    }
                    populateCustomers();
                }
        );
        titleField.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        titleFieldChanged = true;
                    }
                }
        );
        descriptionField.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        descriptionFieldChanged = true;
                    }
                }
        );
        customerBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (oldValue != newValue) {
                        customerBoxChanged = true;
                    }
                }
        );
        contactBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (oldValue != newValue) {
                        contactBoxChanged = true;
                    }
                }
        );
        typeBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (oldValue != newValue) {
                        typeBoxChanged = true;
                    }
                }
        );
        urlField.textProperty().addListener(
                (options, oldValue, newValue) -> {
                    if (!oldValue.equals(newValue)) {
                        urlFieldChanged = true;
                    }
                }
        );
        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (oldDate != newDate) {
                datePickerChanged = true;
            }
        }
        );
        startTime.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (oldValue != newValue) {
                        startTimeChanged = true;
                    }
                }
        );
        endTime.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (oldValue != newValue) {
                        endTimeChanged = true;
                    }
                }
        );
    }

    public void setCustomerActive() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(SET_CUSTOMER_ACTIVE);
            statement.setString(1, (customerBox.getSelectionModel().toString()));
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQLException in setCustomerActive: " + e.getMessage());
        } finally {
            try {
                statement.close();

            } catch (SQLException e) {
                System.out.println("error closing statement");
            }
        }
    }

}
