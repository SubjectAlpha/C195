package exam.controllers;

import exam.entities.Appointment;
import exam.utility.AlertHelper;
import exam.utility.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class ReportController extends ControllerBase implements Initializable {
    @FXML TableView<Report1> report1Table;
    @FXML TableColumn<Report1, Integer> r1YearColumn;
    @FXML TableColumn<Report1, Integer> r1MonthColumn;
    @FXML TableColumn<Report1, String> r1TypeColumn;
    @FXML TableColumn<Report1, Integer> r1CountColumn;
    @FXML TableView<Appointment> report2Table;
    @FXML TableColumn<Appointment, Integer> aptId;
    @FXML TableColumn<Appointment, String> aptTitle;
    @FXML TableColumn<Appointment, String> aptDesc;
    @FXML TableColumn<Appointment, String> aptType;
    @FXML TableColumn<Appointment, Timestamp> aptStart;
    @FXML TableColumn<Appointment, Timestamp> aptEnd;
    @FXML TableColumn<Appointment, Integer> aptCustId;
    @FXML Label newCustomerCountLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        r1YearColumn.setCellValueFactory(new PropertyValueFactory("Year"));
        r1MonthColumn.setCellValueFactory(new PropertyValueFactory("Month"));
        r1TypeColumn.setCellValueFactory(new PropertyValueFactory("AppointmentType"));
        r1CountColumn.setCellValueFactory(new PropertyValueFactory("Count"));

        aptId.setCellValueFactory(new PropertyValueFactory("Appointment_ID"));
        aptTitle.setCellValueFactory(new PropertyValueFactory("Title"));
        aptDesc.setCellValueFactory(new PropertyValueFactory("Description"));
        aptType.setCellValueFactory(new PropertyValueFactory("Type"));
        aptStart.setCellValueFactory(new PropertyValueFactory("Start"));
        aptEnd.setCellValueFactory(new PropertyValueFactory("End"));
        aptCustId.setCellValueFactory(new PropertyValueFactory("Customer_ID"));

        var report1Query = """
                SELECT YEAR(Start) AS Year, MONTH(Start) AS Month, Type, COUNT(Appointment_ID) AS TOTALCOUNT
                FROM client_schedule.appointments
                GROUP BY YEAR(Start), MONTH(Start)
                ORDER BY YEAR(Start), MONTH(Start), Type""";
        var report2Query = "SELECT Contact_ID, Contact_Name FROM client_schedule.contacts";
        var report3Query = "SELECT COUNT(*) as New_Customers FROM client_schedule.customers WHERE Create_Date BETWEEN DATE_FORMAT(NOW() - INTERVAL 1 MONTH, '%Y-%m-01 00:00:00')\n" +
                "AND DATE_FORMAT(LAST_DAY(NOW() - INTERVAL 1 MONTH), '%Y-%m-%d 23:59:59');";

        try{
            var report1 = JDBC.getConnection().prepareStatement(report1Query);
            var report2 = JDBC.getConnection().prepareStatement(report2Query);
            var report3 = JDBC.getConnection().prepareStatement(report3Query);

            var report1Result = report1.executeQuery();
            var report3Result = report3.executeQuery();
            var report2Results = report2.executeQuery();

            while(report1Result.next()) {
                var year =  report1Result.getInt(1);
                var month = report1Result.getInt(2);
                var count = report1Result.getInt(4);
                var type = report1Result.getString(3);
                report1Table.getItems().add(
                        new Report1(
                            year,month,count, type
                        )
                );
            }
            report1Table.refresh();
            while(report2Results.next()) {
                int contactId = report2Results.getInt("Contact_ID");
                System.out.println(contactId);
            }

            if(report3Result.next()) {
                newCustomerCountLabel.setText(String.valueOf(report3Result.getInt(1)));
            }
        } catch (Exception ex) {
            AlertHelper.CreateError(ex.getMessage());
        }
    }

    public class Report1 {
        private int Year;
        private int Month;
        private int Count;
        private String AppointmentType;

        public Report1(int year, int month, int count, String appointmentType) {
            this.Year = year;
            this.Month = month;
            this.Count = count;
            this.AppointmentType = appointmentType;
        }

        public String getAppointmentType() {
            return AppointmentType;
        }

        public void setAppointments(String appointmentType) {
            AppointmentType = appointmentType;
        }

        public int getMonth() {
            return Month;
        }

        public void setMonth(int month) {
            Month = month;
        }

        public int getYear() {
            return Year;
        }

        public void setYear(int year) {
            Year = year;
        }

        public int getCount() {
            return Count;
        }

        public void setCount(int count) {
            Count = count;
        }
    }
}