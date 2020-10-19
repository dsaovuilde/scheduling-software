/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.DataSource;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.COL_APPOINTMENT_CUSTOMER_ID;
import static Model.DataSource.COL_APPOINTMENT_ID;
import static Model.DataSource.COL_APPOINTMENT_LOCATION;
import static Model.DataSource.COL_APPOINTMENT_START;
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
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.time.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class MonthlyCalendarController implements Initializable {

    @FXML
    ComboBox monthSelector;
    @FXML
    ComboBox yearSelector;
    @FXML
    Label dateLabel_10;
    @FXML
    Label dateLabel_11;
    @FXML
    Label dateLabel_12;
    @FXML
    Label dateLabel_13;
    @FXML
    Label dateLabel_14;
    @FXML
    Label dateLabel_15;
    @FXML
    Label dateLabel_16;
    @FXML
    Label dateLabel_20;
    @FXML
    Label dateLabel_21;
    @FXML
    Label dateLabel_22;
    @FXML
    Label dateLabel_23;
    @FXML
    Label dateLabel_24;
    @FXML
    Label dateLabel_25;
    @FXML
    Label dateLabel_26;
    @FXML
    Label dateLabel_30;
    @FXML
    Label dateLabel_31;
    @FXML
    Label dateLabel_32;
    @FXML
    Label dateLabel_33;
    @FXML
    Label dateLabel_34;
    @FXML
    Label dateLabel_35;
    @FXML
    Label dateLabel_36;
    @FXML
    Label dateLabel_40;
    @FXML
    Label dateLabel_41;
    @FXML
    Label dateLabel_42;
    @FXML
    Label dateLabel_43;
    @FXML
    Label dateLabel_44;
    @FXML
    Label dateLabel_45;
    @FXML
    Label dateLabel_46;
    @FXML
    Label dateLabel_50;
    @FXML
    Label dateLabel_51;
    @FXML
    Label dateLabel_52;
    @FXML
    Label dateLabel_53;
    @FXML
    Label dateLabel_54;
    @FXML
    Label dateLabel_55;
    @FXML
    Label dateLabel_56;
    @FXML
    Label dateLabel_60;
    @FXML
    Label dateLabel_61;
    @FXML
    Label dateLabel_62;
    @FXML
    Label dateLabel_63;
    @FXML
    Label dateLabel_64;
    @FXML
    Label dateLabel_65;
    @FXML
    Label dateLabel_66;
    @FXML
    Label Label_10;
    @FXML
    Label Label_11;
    @FXML
    Label Label_12;
    @FXML
    Label Label_13;
    @FXML
    Label Label_14;
    @FXML
    Label Label_15;
    @FXML
    Label Label_16;
    @FXML
    Label Label_20;
    @FXML
    Label Label_21;
    @FXML
    Label Label_22;
    @FXML
    Label Label_23;
    @FXML
    Label Label_24;
    @FXML
    Label Label_25;
    @FXML
    Label Label_26;
    @FXML
    Label Label_30;
    @FXML
    Label Label_31;
    @FXML
    Label Label_32;
    @FXML
    Label Label_33;
    @FXML
    Label Label_34;
    @FXML
    Label Label_35;
    @FXML
    Label Label_36;
    @FXML
    Label Label_40;
    @FXML
    Label Label_41;
    @FXML
    Label Label_42;
    @FXML
    Label Label_43;
    @FXML
    Label Label_44;
    @FXML
    Label Label_45;
    @FXML
    Label Label_46;
    @FXML
    Label Label_50;
    @FXML
    Label Label_51;
    @FXML
    Label Label_52;
    @FXML
    Label Label_53;
    @FXML
    Label Label_54;
    @FXML
    Label Label_55;
    @FXML
    Label Label_56;
    @FXML
    Label Label_60;
    @FXML
    Label Label_61;
    @FXML
    Label Label_62;
    @FXML
    Label Label_63;
    @FXML
    Label Label_64;
    @FXML
    Label Label_65;
    @FXML
    Label Label_66;
    @FXML
    Label userLabel;
    @FXML
    private Button homeButton;

    Month month;
    String selectedYear;
    LocalDate selectedDate;
    int index;
    int stop;
    String selectedMonth;
    ObservableList<Label> dateLabels;
    ObservableList<Label> labels;

    private static final String QUERY_ALL_APPOINTMENTS = "SELECT " + COL_APPOINTMENT_ID + ", " + COL_APPOINTMENT_START + "," + COL_APPOINTMENT_LOCATION + ", " +  COL_CUSTOMER_NAME + " FROM " + APPOINTMENT_TABLE
            + " INNER JOIN " + CUSTOMER_TABLE + " ON " + APPOINTMENT_TABLE + "." + COL_APPOINTMENT_CUSTOMER_ID + " = " + CUSTOMER_TABLE + "." + COL_CUSTOMER_ID + ";";
    private static final String UPDATE_USER_INACTIVE = "UPDATE " + USER_TABLE + " SET " + COL_USER_ACTIVE + " = 0 WHERE " + COL_USER_NAME + " = ?;";

    private static final String QUERY_APPOINTMENT_DATE = "SELECT * FROM " + APPOINTMENT_TABLE + " WHERE DATE(start) = DATE(?) AND " + COL_APPOINTMENT_ID + " != ?;";
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userLabel.setText(User.getCurrentUser());

        ObservableList<String> months = FXCollections.observableArrayList(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        );
        ObservableList<String> years = FXCollections.observableArrayList(
                "2019",
                "2020",
                "2021"
        );
        yearSelector.setItems(years);
        monthSelector.setItems(months);
        monthSelector.getSelectionModel().select(LocalDate.now().getMonth().getValue() - 1);
        yearSelector.getSelectionModel().select(Integer.toString(LocalDate.now().getYear()));
        dateLabels = FXCollections.observableArrayList(
                dateLabel_10,
                dateLabel_11,
                dateLabel_12,
                dateLabel_13,
                dateLabel_14,
                dateLabel_15,
                dateLabel_16,
                dateLabel_20,
                dateLabel_21,
                dateLabel_22,
                dateLabel_23,
                dateLabel_24,
                dateLabel_25,
                dateLabel_26,
                dateLabel_30,
                dateLabel_31,
                dateLabel_32,
                dateLabel_33,
                dateLabel_34,
                dateLabel_35,
                dateLabel_36,
                dateLabel_40,
                dateLabel_41,
                dateLabel_42,
                dateLabel_43,
                dateLabel_44,
                dateLabel_45,
                dateLabel_46,
                dateLabel_50,
                dateLabel_51,
                dateLabel_52,
                dateLabel_53,
                dateLabel_54,
                dateLabel_55,
                dateLabel_56,
                dateLabel_60,
                dateLabel_61,
                dateLabel_62,
                dateLabel_63,
                dateLabel_64,
                dateLabel_65,
                dateLabel_66
        );
        labels = FXCollections.observableArrayList(
                Label_10,
                Label_11,
                Label_12,
                Label_13,
                Label_14,
                Label_15,
                Label_16,
                Label_20,
                Label_21,
                Label_22,
                Label_23,
                Label_24,
                Label_25,
                Label_26,
                Label_30,
                Label_31,
                Label_32,
                Label_33,
                Label_34,
                Label_35,
                Label_36,
                Label_40,
                Label_41,
                Label_42,
                Label_43,
                Label_44,
                Label_45,
                Label_46,
                Label_50,
                Label_51,
                Label_52,
                Label_53,
                Label_54,
                Label_55,
                Label_56,
                Label_60,
                Label_61,
                Label_62,
                Label_63,
                Label_64,
                Label_65,
                Label_66
        );
        selectedMonth = monthSelector.getSelectionModel().getSelectedItem().toString();
        monthSelector.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> { // use of lambda to avoid need to use inner class
                    selectedMonth = newValue.toString();
                    getSelectedMonth();
                }
        );
        selectedYear = yearSelector.getSelectionModel().getSelectedItem().toString();
        yearSelector.getSelectionModel().selectedItemProperty().addListener(
                (options, oldValue, newValue) -> { // use of lambda to avoid need to use inner class
                    selectedYear = newValue.toString();
                    getSelectedMonth();
                }
        );

        getSelectedMonth();

    }

    public void getSelectedMonth() {
        switch (selectedMonth) {
            case ("January"):
                month = Month.JANUARY;
                break;
            case ("February"):
                month = Month.FEBRUARY;
                break;
            case ("March"):
                month = Month.MARCH;
                break;
            case ("April"):
                month = Month.APRIL;
                break;
            case ("May"):
                month = Month.MAY;
                break;
            case ("June"):
                month = Month.JUNE;
                break;
            case ("July"):
                month = Month.JULY;
                break;
            case ("August"):
                month = Month.AUGUST;
                break;
            case ("September"):
                month = Month.SEPTEMBER;
                break;
            case ("October"):
                month = Month.OCTOBER;
                break;
            case ("November"):
                month = Month.NOVEMBER;
                break;
            case ("December"):
                month = Month.DECEMBER;
                break;

        }
        populateDays();
    }

    public void populateDays() {
        stop = -1;
        YearMonth daysInPreviousMonth;

        selectedDate = LocalDate.of(Integer.parseInt(selectedYear), month.getValue(), 1);
        YearMonth daysInMonth = YearMonth.of(Integer.parseInt(selectedYear), month.getValue());
        if (month != Month.JANUARY) {
            daysInPreviousMonth = YearMonth.of(Integer.parseInt(selectedYear), month.getValue() - 1);
        } else {
            daysInPreviousMonth = YearMonth.of(Integer.parseInt(selectedYear), month.getValue() + 11);
        }

        switch (selectedDate.getDayOfWeek()) {
            case SUNDAY:
                dateLabel_10.setText("1");
                index = 0;
                break;
            case MONDAY:
                dateLabel_11.setText("1");
                index = 1;
                break;
            case TUESDAY:
                dateLabel_12.setText("1");
                index = 2;
                break;
            case WEDNESDAY:
                dateLabel_13.setText("1");
                index = 3;
                break;
            case THURSDAY:
                dateLabel_14.setText("1");
                index = 4;
                break;
            case FRIDAY:
                dateLabel_15.setText("1");
                index = 5;
                break;
            case SATURDAY:
                dateLabel_16.setText("1");
                index = 6;
                break;
        }

        if (index != 0) {
            for (int i = index - 1, j = daysInPreviousMonth.lengthOfMonth(); i >= 0; --i, --j) {
                dateLabels.get(i).setText(Integer.toString(j));
            }
        }
        for (int i = index + 1, j = 2; i < dateLabels.size(); ++i, ++j) {
            if (j <= daysInMonth.lengthOfMonth()) {
                dateLabels.get(i).setText(Integer.toString(j));
            } else {
                stop = i;
                break;
            }

        }
        for (int i = stop, j = 1; i < dateLabels.size(); ++i, ++j) {
            dateLabels.get(i).setText(Integer.toString(j));
        }
        populateAppointments();
    }

    public void populateAppointments() {
        for (Label label : labels) {
            label.setText("");
        }
        Month previousMonth = month.minus(1);
        Month nextMonth = month.plus(1);
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        StringBuilder sb = new StringBuilder();
        String zone = "US/Eastern";
        String location = null;

        try { 
            statement = conn.prepareStatement(QUERY_ALL_APPOINTMENTS);
            results = statement.executeQuery();
            while (results.next()) {
                int id = results.getInt(COL_APPOINTMENT_ID);
                String name = results.getString(COL_CUSTOMER_NAME);
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
                for (int i = index; i < stop; ++i) {
                    if ((Integer.parseInt(dateLabels.get(i).getText()) == start.getDayOfMonth()) && (start.getMonth() == month)) {
                        sb.append(name).append(" - ").append(timeAtHome.toLocalTime()).append("\n");
                        labels.get(i).setText(sb.toString());
                        if(!sameDate(start.toLocalDate(), id)){
                            sb.setLength(0);
                        }
                    }
                }
                for (int i = 0; i < index; i++) {
                    if ((Integer.parseInt(dateLabels.get(i).getText()) == start.getDayOfMonth()) && (start.getMonth() == previousMonth)) {
                        sb.append(name).append(" - ").append(timeAtHome.toLocalTime()).append("\n");
                        labels.get(i).setText(sb.toString());
                        if(!sameDate(start.toLocalDate(), id)){
                            sb.setLength(0);
                        }
                    }
                }
                for (int i = stop; i < dateLabels.size(); ++i) {
                    if ((Integer.parseInt(dateLabels.get(i).getText()) == start.getDayOfMonth()) && (start.getMonth() == nextMonth)) {
                        sb.append(name).append(" - ").append(timeAtHome.toLocalTime()).append("\n");
                        labels.get(i).setText(sb.toString());
                        if(!sameDate(start.toLocalDate(), id)){
                            sb.setLength(0);
                        }
                        
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException in queryAllAppointments: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
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

    public void displayWeeklyCalendar() throws IOException {
        Stage stg = (Stage) homeButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/WeeklyCalendar.fxml"));
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

        } catch (SQLException e) {
            System.out.println("SQLException :" + e.getMessage());
        } finally {
            try {
                statement.close();
                DataSource.closeConnection();
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
            if(results.next()) {
                same = true;
            }
            
        }catch(SQLException e) {
            System.out.println("SQLException in sameDate: " + e.getMessage());
       
        
}
        return same;
    }
}

