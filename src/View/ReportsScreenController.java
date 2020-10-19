/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.DataSource;
import static Model.DataSource.APPOINTMENT_TABLE;
import static Model.DataSource.COL_APPOINTMENT_TYPE;
import Model.Report;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dwsou
 */
public class ReportsScreenController implements Initializable {

    @FXML
    private TableView appointmentTypeTable;
    @FXML
    private TableColumn<Report, String> typeTableMonthColumn;
    @FXML
    private TableColumn<Report, String> typeTableTypeColumn;
    @FXML
    private TableColumn<Report, Integer> typeTableNumberColumn;
    @FXML
    private TableView appointmentLengthTable;
    @FXML
    private TableColumn<Appointment, String> lengthTableMonthColumn;
    @FXML
    private TableColumn<Appointment, String> lengthTableLengthColumn;
    @FXML
    private Button closeButton;

    private ObservableList<Report> appointmentTypes = FXCollections.observableArrayList();
    private ObservableList<Report> appointmentLengths = FXCollections.observableArrayList();

    private static final String QUERY_AVERAGE_APPOINTMENT_LENGTHS = "SELECT AVG(TIME_TO_SEC(TIMEDIFF(end, start))/60) as average, MONTH(start) as Month FROM "
            + APPOINTMENT_TABLE + " GROUP BY Month;";
    private static final String QUERY_APPOINTMENT_TYPE_BY_MONTH = "SELECT MONTH(start) AS Month, " + COL_APPOINTMENT_TYPE + ", " + " COUNT(*) AS Number FROM "
            + APPOINTMENT_TABLE + " GROUP BY " + COL_APPOINTMENT_TYPE + ", MONTH(start) ORDER BY MONTH(start);";

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        typeTableMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        typeTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeTableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        lengthTableMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        lengthTableLengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        populateAppointmentTypeTable();
        populateAppointmentLengthTable();
    }

    public void populateAppointmentTypeTable() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            statement = conn.prepareStatement(QUERY_APPOINTMENT_TYPE_BY_MONTH);
            results = statement.executeQuery();
            while (results.next()) {
                int monthNumber = results.getInt("Month");
                String month = Month.of(monthNumber).name();
                month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();
                String type = results.getString(COL_APPOINTMENT_TYPE);
                int num = results.getInt("number");
                Report report = new Report();
                report.setMonth(month);
                report.setType(type);
                report.setNumber(num);
                appointmentTypes.add(report);
            }
        } catch (SQLException e) {
            System.out.println("SQLException in populateAppointmentTable: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
        appointmentTypeTable.setItems(appointmentTypes);
    }

    public void populateAppointmentLengthTable() {
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            statement = conn.prepareStatement(QUERY_AVERAGE_APPOINTMENT_LENGTHS);
            results = statement.executeQuery();
            while (results.next()) {
                int monthNumber = results.getInt("Month");
                String month = Month.of(monthNumber).name();
                month = month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();
                Long length = results.getLong("average");
                Report report = new Report();
                report.setMonth(month);
                report.setLength(length);
                appointmentLengths.add(report);
            }

        } catch (SQLException e) {
            System.out.println("SQLException in populateAppointmentTable: " + e.getMessage());
        } finally {
            try {
                results.close();
                statement.close();
            } catch (SQLException e) {
                System.out.println("error closing statement/results");
            }
        }
        appointmentLengthTable.setItems(appointmentLengths);
    }

    public void handleClose() throws IOException {
        Stage stg = (Stage) closeButton.getScene().getWindow();
        stg.close();
    }
}
