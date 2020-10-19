/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Address;
import Model.Customer;
import Model.DataSource;
import static Model.DataSource.ADDRESS_TABLE;
import static Model.DataSource.CITY_TABLE;
import static Model.DataSource.COL_ADDRESS;
import static Model.DataSource.COL_ADDRESS_2;
import static Model.DataSource.COL_ADDRESS_CITY_ID;
import static Model.DataSource.COL_ADDRESS_ID;
import static Model.DataSource.COL_ADDRESS_PHONE;
import static Model.DataSource.COL_ADDRESS_POSTAL_CODE;
import static Model.DataSource.COL_CITY;
import static Model.DataSource.COL_CITY_COUNTRY_ID;
import static Model.DataSource.COL_CITY_ID;
import static Model.DataSource.COL_COUNTRY;
import static Model.DataSource.COL_COUNTRY_ID;
import static Model.DataSource.COL_CUSTOMER_ADDRESS_ID;
import static Model.DataSource.COL_CUSTOMER_ID;
import static Model.DataSource.COL_CUSTOMER_NAME;
import static Model.DataSource.COUNTRY_TABLE;
import static Model.DataSource.CUSTOMER_TABLE;
import Model.User;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class AddCustomerScreenController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField address1Field;
    @FXML
    private TextField address2Field;
    @FXML
    private TextField postalField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox countryBox;
    @FXML
    private ComboBox cityBox;
    @FXML
    private Button saveButton;
    @FXML
    private Label headerLabel;

    private String selectedCountry;
    private String address1;
    private String address2;
    private String postalCode;
    private String phone;

    private boolean nameFieldChanged;
    private boolean address1FieldChanged;
    private boolean address2FieldChanged;
    private boolean countryBoxChanged;
    private boolean cityBoxChanged;
    private boolean postalFieldChanged;
    private boolean phoneFieldChanged;

    private static final String QUERY_ALL_COUNTRIES = "SELECT " + COL_COUNTRY + " FROM " + COUNTRY_TABLE + ";";
    private static final String QUERY_COUNTRY_ID_BY_NAME = "SELECT " + COL_COUNTRY_ID + ", " + COL_COUNTRY + " FROM " + COUNTRY_TABLE + " WHERE " + COL_COUNTRY + " = ?;";
    private static final String QUERY_CITIES = "SELECT " + COL_CITY + " FROM " + CITY_TABLE + " WHERE " + COL_CITY_COUNTRY_ID + " = ?;";
    private static final String QUERY_ADDRESS_BY_ID = "SELECT " + COL_ADDRESS_ID + ", " + COL_ADDRESS + ", " + COL_ADDRESS_2 + ", " + COL_ADDRESS_POSTAL_CODE + ", " + COL_ADDRESS_PHONE + " FROM " + ADDRESS_TABLE
            + " WHERE " + COL_ADDRESS_ID + " = ?;";
    private static final String QUERY_CUSTOMER_COUNTRY = "SELECT " + COUNTRY_TABLE + "." + COL_COUNTRY_ID + ", " + COUNTRY_TABLE + "." + COL_COUNTRY + " FROM " + COUNTRY_TABLE
            + " INNER JOIN " + CITY_TABLE + " ON " + COUNTRY_TABLE + "." + COL_COUNTRY_ID + " = " + CITY_TABLE + "." + COL_CITY_COUNTRY_ID + " INNER JOIN " + ADDRESS_TABLE
            + " ON " + CITY_TABLE + "." + COL_CITY_ID + " = " + ADDRESS_TABLE + "." + COL_ADDRESS_CITY_ID + " INNER JOIN " + CUSTOMER_TABLE + " ON " + ADDRESS_TABLE + "."
            + COL_ADDRESS_ID + " = " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ADDRESS_ID + " WHERE " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ID + "= ?;";
    private static final String UPDATE_CUSTOMER_PART_1 = "UPDATE " + CUSTOMER_TABLE + " SET ";
    private static final String UPDATE_CUSTOMER_PART_2 = " = ? WHERE " + COL_CUSTOMER_ID + " = ?;";
    private static final String UPDATE_ADDRESS_PART_1 = "UPDATE " + ADDRESS_TABLE + " SET ";
    private static final String UPDATE_ADDRESS_PART_2 = " = ? WHERE " + COL_ADDRESS_ID + " = ?;";
    private static final String QUERY_CUSTOMER_ADDRESS_ID = "SELECT " + COL_ADDRESS_ID + ", " + COL_ADDRESS + ", " + COL_ADDRESS_2
            + " FROM " + ADDRESS_TABLE + " WHERE " + COL_ADDRESS + " = ? AND " + COL_ADDRESS_2 + " = ? AND " + COL_ADDRESS_CITY_ID + " = ?;";
    private static final String INSERT_NEW_ADDRESS = "INSERT INTO " + ADDRESS_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_NEW_CUSTOMER = " INSERT INTO " + CUSTOMER_TABLE + " VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String QUERY_CITY_ID_BY_NAME = "SELECT " + COL_CITY_ID + ", " + COL_CITY + " FROM " + CITY_TABLE + " WHERE " + COL_CITY + " = ?;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerLabel.setText(DataSource.getAddOrModify());
        populateCountries();
        if (DataSource.getAddOrModify().equals("Modify")) {
            populateFields();
        }

        countryBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    selectedCountry = newValue.toString();
                    populateCities();
                }
        );
    }

    public void populateCountries() {
        Connection conn;
        PreparedStatement statement = null;
        ObservableList<String> countryList = FXCollections.observableArrayList();

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_ALL_COUNTRIES);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                countryList.add(results.getString(COL_COUNTRY));
            }

        } catch (SQLException e) {
            System.out.println("SQLException in populateCountries: " + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement");
            }
        }
        countryBox.setItems(countryList);
    }

    public int queryCountryIdByName(String name) {
        Connection conn;
        PreparedStatement statement = null;
        int id = -1;

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_COUNTRY_ID_BY_NAME);
            statement.setString(1, countryBox.getSelectionModel().getSelectedItem().toString());
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                id = results.getInt(COL_COUNTRY_ID);
            }
        } catch (SQLException e) {
            System.out.println("SQLException in queryCountryIdByName :" + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement");
            }
        }

        return id;
    }

    public void populateCities() {
        Connection conn;
        PreparedStatement statement = null;
        ResultSet results = null;
        ObservableList<String> cityList = FXCollections.observableArrayList();
        int id = queryCountryIdByName(selectedCountry);

        try {
            conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_CITIES);
            statement.setInt(1, id);
            results = statement.executeQuery();
            while (results.next()) {
                cityList.add(results.getString(COL_CITY));
            }
            cityBox.setItems(cityList);
        } catch (SQLException e) {
            System.out.println("SQLException in populateCities :" + e.getMessage());
        } finally {
            try {
                results.close();
                results.close();
            } catch (SQLException e) {
                System.out.println("Error closing statement and results in populateCities");
            }
        }

    }

    public void populateFields() {
        queryAddress();
       
        idField.setText(Integer.toString(DataSource.getCustomerToModify().getCustomerId()));
        nameField.setText(DataSource.getCustomerToModify().getCustomerName());
        address1Field.setText(address1);
        address2Field.setText(address2);
        countryBox.getSelectionModel().select(queryCustomerCountry());
        cityBox.getSelectionModel().select(DataSource.getCustomerToModify().getCity());
        postalField.setText(postalCode);
        phoneField.setText(phone);
        populateCities();

        nameField.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        nameFieldChanged = true;
                    }
                }
        );
        address1Field.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        address1FieldChanged = true;
                    }
                }
        );
        address2Field.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        address2FieldChanged = true;
                    }
                }
        );
        countryBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    selectedCountry = newValue.toString();
                    if (oldValue != newValue) {
                        countryBoxChanged = true;
                    }
                    populateCities();

                }
        );

        cityBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> {
                    if (oldValue != newValue) {
                        cityBoxChanged = true;
                    }
                }
        );

        postalField.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        postalFieldChanged = true;
                    }
                }
        );
        phoneField.textProperty().addListener(
                (options, oldValue, newValue) -> {

                    if (!oldValue.equals(newValue)) {
                        phoneFieldChanged = true;
                    }
                }
        );
    }

    public void queryAddress() {
        PreparedStatement statement = null;
        ResultSet results = null;
        Connection conn = DataSource.getConnection();
        try {
            statement = conn.prepareStatement(QUERY_ADDRESS_BY_ID);
            statement.setInt(1, DataSource.getCustomerToModify().getCustomerAddressId());
            results = statement.executeQuery();

            if (results.next()) {
                address1 = results.getString(COL_ADDRESS);
                address2 = results.getString(COL_ADDRESS_2);
                postalCode = results.getString(COL_ADDRESS_POSTAL_CODE);
                phone = results.getString(COL_ADDRESS_PHONE);
            }
        } catch (SQLException e) {
            System.out.println("SQLException in queryAddress: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error closing results/statement in queryAddress");
            }
        }
    }

    public String queryCustomerCountry() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        String country = null;

        try {
            statement = conn.prepareStatement(QUERY_CUSTOMER_COUNTRY);
            statement.setInt(1, DataSource.getCustomerToModify().getCustomerId());
            results = statement.executeQuery();
            if (results.next()) {
                country = results.getString(COUNTRY_TABLE + "." + COL_COUNTRY);
            }

        } catch (SQLException e) {
            System.out.println("SQLException in queryCustomerCountry: " + e.getMessage());
        } finally {
            if (results != null && statement != null) {
                try {
                    results.close();
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing statement/results in queryCustomerCountry");
                }
            }
        }
        return country;
    }

    public void handleSave() throws IOException {
        if (isValid()) {
            PreparedStatement statement = null;
            ResultSet results = null;
            Connection conn = DataSource.getConnection();

            try {
                if (DataSource.getAddOrModify().equals("Add")) {
                    Customer customer = new Customer();
                    customer.setCustomerId();
                    customer.setCustomerName(nameField.getText());
                    customer.setCustomerAddressId(queryCustomerAddressId());
                    customer.setActive(0);
                    customer.setCity(cityBox.getSelectionModel().getSelectedItem().toString());
                    customer.setCreateDate(LocalDateTime.now());
                    customer.setCreatedBy(User.getCurrentUser());
                    customer.setLastUpdate(LocalDateTime.now());
                    customer.setLastUpdateBy(User.getCurrentUser());
                    DataSource.setNewCustomer(customer);

                    statement = conn.prepareStatement(INSERT_NEW_CUSTOMER);
                    statement.setInt(1, customer.getCustomerId());
                    statement.setString(2, customer.getCustomerName());
                    statement.setInt(3, customer.getCustomerAddressId());
                    statement.setInt(4, customer.getActive());
                    statement.setString(5, customer.getCreateDate().toString().replace("T", " "));
                    statement.setString(6, customer.getCreatedBy());
                    statement.setString(7, customer.getLastUpdate().toString().replace("T", " "));
                    statement.setString(8, customer.getLastUpdateBy());
                    statement.executeUpdate();
                    DataSource.setAddedCustomer(true);

                } else if (DataSource.getAddOrModify().equals("Modify")) {
                    if (nameFieldChanged) {
                        statement = conn.prepareStatement(UPDATE_CUSTOMER_PART_1 + COL_CUSTOMER_NAME + UPDATE_CUSTOMER_PART_2);
                        statement.setString(1, nameField.getText());
                        statement.setInt(2, DataSource.getCustomerToModify().getCustomerId());
                        Alert alert = new Alert(AlertType.CONFIRMATION, "Update Customer information?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.NO) {
                            alert.close();
                        } else if (alert.getResult() == ButtonType.YES) {
                            DataSource.getCustomerToModify().setCustomerName(nameField.getText());
                            statement.executeUpdate();
                        }

                    }
                    if (address1FieldChanged || cityBoxChanged || countryBoxChanged) {
                        int id = queryCustomerAddressId();
                        statement = conn.prepareStatement(UPDATE_CUSTOMER_PART_1 + COL_CUSTOMER_ADDRESS_ID + UPDATE_CUSTOMER_PART_2);
                        statement.setInt(1, id);
                        statement.setInt(2, DataSource.getCustomerToModify().getCustomerId());
                        Alert alert = new Alert(AlertType.CONFIRMATION, "Add new address for customer : " + nameField.getText() + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.NO) {
                            alert.close();
                        } else if (alert.getResult() == ButtonType.YES) {
                            DataSource.getCustomerToModify().setCustomerAddressId(id);
                            statement.executeUpdate();
                        }

                    }
                    if (!(address1FieldChanged || cityBoxChanged) && (address2FieldChanged || phoneFieldChanged || postalFieldChanged)) {
                        Alert alert = new Alert(AlertType.CONFIRMATION, "Update address for customer : " + nameField.getText() + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.NO) {
                            alert.close();
                        } else if (alert.getResult() == ButtonType.YES) {
                            updateAddress();
                        }
                    }
                }

            } catch (SQLException e) {
                System.out.println("SQLException in handleSave: " + e.getMessage());
            } finally {
                try {
                    results.close();
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing results/statement in queryAddress");
                } catch (NullPointerException ex) {
                    System.out.println("No resultSet to close");
                }
            }
            displayMainScreen();

        }
    }

    public int queryCustomerAddressId() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        int id = -1;

        try {
            statement = conn.prepareStatement(QUERY_CUSTOMER_ADDRESS_ID);
            statement.setString(1, address1Field.getText());
            statement.setString(2, address2Field.getText());
            statement.setInt(3, queryCityId());
            results = statement.executeQuery();
            if (results.next()) {
                id = results.getInt(COL_ADDRESS_ID);
            } else {
                Address address = new Address();
                address.setAddressId(0);
                address.setAddress(address1Field.getText());
                address.setAddress2(address2Field.getText());
                address.setCityId(queryCityId());
                address.setPostalCode(postalField.getText());
                address.setPhone(phoneField.getText());
                address.setCreateDate(LocalDateTime.now());
                address.setCreatedBy(User.getCurrentUser());
                address.setLastUpdate(LocalDateTime.now());
                address.setLastUpdateBy(User.getCurrentUser());

                statement = conn.prepareStatement(INSERT_NEW_ADDRESS);
                statement.setInt(1, address.getAddressId());
                statement.setString(2, address.getAddress());
                statement.setString(3, address.getAddress2());
                statement.setInt(4, address.getCityId());
                statement.setString(5, address.getPostalCode());
                statement.setString(6, address.getPhone());
                statement.setString(7, address.getCreateDate().toString().replace("T", " "));
                statement.setString(8, address.getCreatedBy());
                statement.setString(9, address.getLastUpdate().toString().replace("T", " "));
                statement.setString(10, address.getLastUpdateBy());
                statement.executeUpdate();

                id = address.getAddressId();
            }

        } catch (SQLException e) {
            System.out.println("SQLException in queryCustomerAddressId: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("Error closing results/statement in queryAddress");
            }
        }
        return id;
    }

    public int queryCityId() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        int id = -1;

        try {
            statement = conn.prepareStatement(QUERY_CITY_ID_BY_NAME);
            statement.setString(1, cityBox.getSelectionModel().getSelectedItem().toString());
            results = statement.executeQuery();
            if (results.next()) {
                id = results.getInt(COL_CITY_ID);
            }

        } catch (SQLException e) {
            System.out.println("SQLException in queryCityId: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
        return id;
    }

    public void updateAddress() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        try {
            if (address2FieldChanged) {
                statement = conn.prepareStatement(UPDATE_ADDRESS_PART_1 + COL_ADDRESS_2 + UPDATE_ADDRESS_PART_2);
                statement.setString(1, address2Field.getText());
                statement.setInt(2, DataSource.getCustomerToModify().getCustomerAddressId());
                statement.executeUpdate();
            }
            if (phoneFieldChanged) {
                statement = conn.prepareStatement(UPDATE_ADDRESS_PART_1 + COL_ADDRESS_PHONE + UPDATE_ADDRESS_PART_2);
                statement.setString(1, phoneField.getText());
                statement.setInt(2, DataSource.getCustomerToModify().getCustomerAddressId());
                statement.executeUpdate();
            }
            if (postalFieldChanged) {
                statement = conn.prepareStatement(UPDATE_ADDRESS_PART_1 + COL_ADDRESS_POSTAL_CODE + UPDATE_ADDRESS_PART_2);
                statement.setString(1, postalField.getText());
                statement.setInt(2, DataSource.getCustomerToModify().getCustomerAddressId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("SQLExcetpion in updateAddress: " + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
    }

    public void displayMainScreen() throws IOException {
        Stage stg = (Stage) saveButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/HomeScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
    }

    private boolean isValid() {
        StringBuilder sb = new StringBuilder();
        boolean valid = false;
        if (nameField.getText().length() == 0) {
            sb.append("name cannot be blank \n");
        }
        if (address1Field.getText().length() == 0 || address2Field.getText().length() == 0) {
            sb.append("address1 and address2 must be entered \n");
        }
         if (postalField.getText().length() != 6) {
            sb.append("Postal Code should be 6 digits \n");
        } else {
            try {
                Integer.parseInt(postalField.getText());
            } catch (NumberFormatException e) {
                sb.append("Postal Code must be numeric \n");
            }
        }
        if (phoneField.getText().length() == 0) {
            sb.append("phone cannot be blank \n");
        } else if (phoneField.getText().length() != 14) {
            sb.append("Invalid Phone number\n");
        } else {
           String num = phoneField.getText().replaceAll("-", "");
            try {
                
                Long.parseLong(num);

            } catch (NumberFormatException e) {
                sb.append("Phone number must be numeric\n");
                
            }
        }
        if (sb.length() != 0) {
            Alert alert = new Alert(AlertType.WARNING, sb.toString(), ButtonType.OK);
            alert.showAndWait();

        } else {
            valid = true;
        }
        return valid;
    }
}
