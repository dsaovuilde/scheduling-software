/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.DataSource;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.COL_APPOINTMENT_CONTACT;
import static Model.DataSource.COL_APPOINTMENT_LOCATION;
import static Model.DataSource.COL_APPOINTMENT_START;
import static Model.DataSource.COL_APPOINTMENT_TITLE;
import static Model.DataSource.COL_USER_ACTIVE;
import static Model.DataSource.COL_USER_NAME;
import static Model.DataSource.COL_USER_PASSWORD;
import static Model.DataSource.USER_TABLE;
import Model.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class LoginScreenController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label headerLabel;
    @FXML
    private Label loginHeaderLabel;

    ResourceBundle rb = ResourceBundle.getBundle("Resources/Login", Locale.getDefault());

    private static final String QUERY_DB_USER_AND_PASS = "SELECT " + COL_USER_NAME + ", " + COL_USER_PASSWORD + " FROM " + USER_TABLE
            + " WHERE " + COL_USER_NAME + " = ?" + " AND " + COL_USER_PASSWORD + " = ?;";

    private static final String UPDATE_USER_ACTIVE = "UPDATE " + USER_TABLE + " SET " + COL_USER_ACTIVE + " = 1 WHERE " + COL_USER_NAME + " = ?;";
    private static final String QUERY_ALL_APPOINTMENTS = "SELECT * FROM " + APPOINTMENT_TABLE + ";";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateFields();

    }

    public void handleCancel() {
        DataSource.closeConnection();
        Platform.exit();
    }

    public void handleLogin() throws IOException {

        ResultSet results = null;
        PreparedStatement statement = null;
        String successful;

        try {
            Connection conn = DataSource.getConnection();
            statement = conn.prepareStatement(QUERY_DB_USER_AND_PASS);
            statement.setString(1, usernameField.getText());
            statement.setString(2, passwordField.getText());
            results = statement.executeQuery();

            if (results.next()) {
                User.setCurrentUser(usernameField.getText());
                showUserActive();
                checkForAppointment();
                displayMainScreen();
                System.out.println("Loggin in...");
                successful = "SUCCESS";
            } else {
                errorLabel.setText(rb.getString("error"));
                successful = "FAILED";
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("log.txt", true))) {
                bw.write("Login attempt by user: " + usernameField.getText() + " " + successful + " " + LocalDateTime.now()
                        + System.getProperty("line.separator"));
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
        };

    }

    public void displayMainScreen() throws IOException {
        Stage stg = (Stage) loginButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/HomeScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();

    }

    public void populateFields() {
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        headerLabel.setText(rb.getString("header"));
        loginHeaderLabel.setText(rb.getString("login"));
        loginButton.setText(rb.getString("login"));
        cancelButton.setText(rb.getString("cancel"));
    }

    public void showUserActive() {
        PreparedStatement statement = null;

        try {
            Connection conn = DataSource.getConnection();
            statement = conn.prepareStatement(UPDATE_USER_ACTIVE);
            statement.setString(1, usernameField.getText());
            statement.executeUpdate();
            System.out.println("User " + usernameField.getText() + " is active.");
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
     public void checkForAppointment() { // warning of appointment within 15 minutes when loading home screen
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        String zone = "US/Eastern";

        try {
            statement = conn.prepareStatement(QUERY_ALL_APPOINTMENTS);
            results = statement.executeQuery();
            while (results.next()) {
                LocalDateTime apptStart = results.getObject(COL_APPOINTMENT_START, LocalDateTime.class);
                String location = results.getString(COL_APPOINTMENT_LOCATION);
                String contact = results.getString(COL_APPOINTMENT_CONTACT);
                String title = results.getString(COL_APPOINTMENT_TITLE);
                switch (location) {
                    case "Phoenix":
                        zone = "US/Arizona";
                        break;
                    case "London":
                        zone = "Europe/London";
                        break;
                    case "New York":
                        zone = "US/Eastern";
                        break;
                }

                ZonedDateTime currentTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
                ZonedDateTime appointmentStart = apptStart.atZone(ZoneId.of(zone));
                ZonedDateTime appointmentStartLocalTime = appointmentStart.withZoneSameInstant(ZoneId.systemDefault());

                if (currentTime.toInstant().isBefore(appointmentStartLocalTime.toInstant()) && (currentTime.plusMinutes(15).toInstant().isAfter(appointmentStartLocalTime.toInstant()) || currentTime.plusMinutes(15).toInstant().equals(appointmentStartLocalTime.toInstant()) && contact.equals(User.getCurrentUser())))  {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "You have an appointment \'" + title + "\' scheduled to begin within 15 minutes");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException in checkForAppointment: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
    }
}
