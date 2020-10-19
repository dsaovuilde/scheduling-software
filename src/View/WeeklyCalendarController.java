/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.DataSource;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.COL_APPOINTMENT_CONTACT;
import static Model.DataSource.COL_APPOINTMENT_CUSTOMER_ID;
import static Model.DataSource.COL_APPOINTMENT_ID;
import static Model.DataSource.COL_APPOINTMENT_LOCATION;
import static Model.DataSource.COL_APPOINTMENT_START;
import static Model.DataSource.COL_APPOINTMENT_TITLE;
import static Model.DataSource.COL_CUSTOMER_ID;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WeeklyCalendarController implements Initializable {

    @FXML
    private Label dateLabel1;
    @FXML
    private Label dateLabel2;
    @FXML
    private Label dateLabel3;
    @FXML
    private Label dateLabel4;
    @FXML
    private Label dateLabel5;
    @FXML
    private Label dateLabel6;
    @FXML
    private Label dateLabel7;
    @FXML
    private Label Label1;
    @FXML
    private Label Label2;
    @FXML
    private Label Label3;
    @FXML
    private Label Label4;
    @FXML
    private Label Label5;
    @FXML
    private Label Label6;
    @FXML
    private Label Label7;
    @FXML
    private Label userLabel;
    @FXML
    private Button homeButton;
    @FXML
    private ComboBox consultantBox;

    ObservableList<Label> dateLabels;
    ObservableList<Label> labels;
    ObservableList<String> consultants = FXCollections.observableArrayList();
    int index = 0;

    private static final String QUERY_ALL_APPOINTMENTS = "SELECT " + COL_APPOINTMENT_ID + ", " + COL_APPOINTMENT_TITLE + ", " + COL_APPOINTMENT_START + "," + COL_APPOINTMENT_LOCATION + ", " + COL_CUSTOMER_NAME + " FROM " + APPOINTMENT_TABLE
            + " INNER JOIN " + CUSTOMER_TABLE + " ON " + APPOINTMENT_TABLE + "." + COL_APPOINTMENT_CUSTOMER_ID + " = " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ID
            + " WHERE " + COL_APPOINTMENT_CONTACT + " = ?;";
    private static final String UPDATE_USER_INACTIVE = "UPDATE " + USER_TABLE + " SET " + COL_USER_ACTIVE + " = 0 WHERE " + COL_USER_NAME + " = ?;";
    private static final String QUERY_APPOINTMENTS_BY_CONTACT = "SELECT DISTINCT " + COL_APPOINTMENT_CONTACT + " FROM " + APPOINTMENT_TABLE + ";";
    private static final String QUERY_APPOINTMENT_DATE = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE DATE(start) = Date(?) AND " + COL_APPOINTMENT_ID + " != ?;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText(User.getCurrentUser());
        dateLabels = FXCollections.observableArrayList(
                dateLabel1,
                dateLabel2,
                dateLabel3,
                dateLabel4,
                dateLabel5,
                dateLabel6,
                dateLabel7
        );
        labels = FXCollections.observableArrayList(
                Label1,
                Label2,
                Label3,
                Label4,
                Label5,
                Label6,
                Label7
        );

        populateConsultants();
        consultantBox.getSelectionModel().select(User.getCurrentUser());
        populateDays();

        consultantBox.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> { // used a lambda here because it is more readable and easier to use than an anonymous inner class.
                    populateAppointments();
                }
        );
    }

    public void populateDays() {
        YearMonth daysInPreviousMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth().getValue() - 1);
        switch (LocalDate.now().getDayOfWeek()) {
            case SUNDAY:
                dateLabel1.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                break;
            case MONDAY:
                dateLabel2.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 1;
                break;
            case TUESDAY:
                dateLabel3.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 2;
                break;
            case WEDNESDAY:
                dateLabel4.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 3;
                break;
            case THURSDAY:
                dateLabel5.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 4;
                break;
            case FRIDAY:
                dateLabel6.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 5;
                break;
            case SATURDAY:
                dateLabel7.setText(Integer.toString(LocalDate.now().getDayOfMonth()));
                index = 6;
                break;
        }
        if (index != 0) {
            for (int i = index - 1, j = daysInPreviousMonth.lengthOfMonth(); i >= 0; --i, j--) {
                dateLabels.get(i).setText(Integer.toString(j));
            }
        }
        for (int i = index, j = LocalDate.now().getDayOfMonth(); i < dateLabels.size(); ++i, ++j) {
            dateLabels.get(i).setText(Integer.toString(j));
        }
        populateAppointments();
    }

    public void populateAppointments() {
        for (Label label : labels) { // chose not to use functional programming because I do not belive a lambda is more effieienct or readable than a simple for each used in this case.
            label.setText("");
        }
        String zone = "US/Eastern";
        String location = null;
        
        
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        StringBuilder sb = new StringBuilder();
        try {
            statement = conn.prepareStatement(QUERY_ALL_APPOINTMENTS);
            statement.setString(1, consultantBox.getSelectionModel().getSelectedItem().toString());
            results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt(COL_APPOINTMENT_ID);
                String title = results.getString(COL_APPOINTMENT_TITLE);
                LocalDateTime start = results.getObject(COL_APPOINTMENT_START, LocalDateTime.class);
                location = results.getString(COL_APPOINTMENT_LOCATION);
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
                ZonedDateTime timeAtLocation = start.atZone(ZoneId.of(zone));
                ZonedDateTime timeAtHome = timeAtLocation.withZoneSameInstant(ZoneId.systemDefault());
                for (int i = 0; i < dateLabels.size(); ++i) {
                    if (Integer.parseInt(dateLabels.get(i).getText()) == start.getDayOfMonth() && start.getMonth() == LocalDate.now().getMonth()) {
                        if (!sameDate(start.toLocalDate(), id)) {
                            sb.setLength(0);
                        }
                        sb.append(title).append(": ").append(timeAtHome.toLocalTime()).append("\n");
                        labels.get(i).setText("");
                        labels.get(i).setText(sb.toString());
                    }
                }
                for (int i = 0; i < index; i++) {
                    if ((Integer.parseInt(dateLabels.get(i).getText()) == start.getDayOfMonth()) && (start.getMonth() == LocalDate.now().getMonth().minus(1))) {
                        if (!sameDate(start.toLocalDate(), id)) {
                            sb.setLength(0);
                        }
                        sb.append(title).append(": ").append(timeAtHome.toLocalTime()).append("\n");
                        labels.get(i).setText(sb.toString());
                    }
                }
            }
                    
                }catch (SQLException e) {
            System.out.println("SQLException in populateAppointments: " + e.getMessage());
        }
        }
            

    

    public void displayHomeScreen() throws IOException {
        Stage stg = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/HomeScreen.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stg.close();
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
        Stage stg = (Stage) homeButton.getScene().getWindow();
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

    public void handleExit() {
        showUserInactive();
        DataSource.closeConnection();
        Platform.exit();
    }

    public void populateConsultants() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            statement = conn.prepareStatement(QUERY_APPOINTMENTS_BY_CONTACT);
            results = statement.executeQuery();
            while (results.next()) {
                consultants.add(results.getString(COL_APPOINTMENT_CONTACT));
            }
        } catch (SQLException e) {
            System.out.println("SQLException in populateConsultants: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
        consultantBox.setItems(consultants);

    }

    public boolean sameDate(LocalDate date, int id) {
        boolean same = false;
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            statement = conn.prepareStatement(QUERY_APPOINTMENT_DATE);
            statement.setString(1, date.toString());
            statement.setInt(2, id);
            results = statement.executeQuery();
            if (results.next()) {
                same = true;
            }

        } catch (SQLException e) {
            System.out.println("SQLException in sameDate: " + e.getMessage());

        }
        return same;
    }

}
