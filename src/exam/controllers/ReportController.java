package exam.controllers;

import exam.entities.Appointment;
import exam.entities.Contact;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReportController extends ControllerBase implements Initializable {
    @FXML TableView<Report1> report1Table;
    @FXML TableColumn<Report1, Integer> r1YearColumn;
    @FXML TableColumn<Report1, Integer> r1MonthColumn;
    @FXML TableColumn<Report1, String> r1TypeColumn;
    @FXML TableColumn<Report1, Integer> r1CountColumn;
    @FXML TableView<Report2> report2Table;
    @FXML TableColumn<Report2, String> contactName;
    @FXML TableColumn<Report2, Integer> aptId;
    @FXML TableColumn<Report2, String> aptTitle;
    @FXML TableColumn<Report2, String> aptDesc;
    @FXML TableColumn<Report2, String> aptType;
    @FXML TableColumn<Report2, Timestamp> aptStart;
    @FXML TableColumn<Report2, Timestamp> aptEnd;
    @FXML TableColumn<Report2, Integer> aptCustId;
    @FXML Label newCustomerCountLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        r1YearColumn.setCellValueFactory(new PropertyValueFactory("Year"));
        r1MonthColumn.setCellValueFactory(new PropertyValueFactory("Month"));
        r1TypeColumn.setCellValueFactory(new PropertyValueFactory("AppointmentType"));
        r1CountColumn.setCellValueFactory(new PropertyValueFactory("Count"));

        contactName.setCellValueFactory(new PropertyValueFactory("contactName"));
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

        var report3Query = """
                SELECT count(*) FROM client_schedule.customers
                       WHERE MONTH(Create_Date) = MONTH(now())
                       AND YEAR(Create_Date) = YEAR(now())
                       GROUP BY DATE(Create_Date)""";

        try{
            var report1 = JDBC.getConnection().prepareStatement(report1Query);

            var report3 = JDBC.getConnection().prepareStatement(report3Query);

            var report1Result = report1.executeQuery();
            var report3Result = report3.executeQuery();


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

            Contact.getAll().forEach(c -> {
                report2Table.getItems().addAll(new Report2().findByContact(c.ID, c.Name));
            });
            report2Table.refresh();

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

    public class Report2 extends Appointment {
        private String contactName;

        Report2() {}

        Report2(String contactName,
                int id, int customer_id, int user_id,
                int contact_id, String title, String type,
                String description, String location, String created_by, String last_updated_by,
                Timestamp start, Timestamp end, Timestamp create, Timestamp last_update
            ){
            super(id, customer_id, user_id, contact_id, title, type, description, location, created_by, last_updated_by, start, end, create, last_update);
            this.setContactName(contactName);
        }

        /**
         * @param contactId ID of the user whose appointments you are trying to see.
         * @return List of user appointments, or null if an error occurred.
         */
        public ArrayList<Report2> findByContact(int contactId, String contactName){
            var getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE Contact_ID=? ORDER BY Create_Date";
            try(var stmt = JDBC.getConnection().prepareStatement(getAppointmentsQuery)) {
                stmt.setInt(1, contactId);
                var results = stmt.executeQuery();
                var report2Results = new ArrayList<Report2>();
                while(results.next()) {
                    var id = results.getInt("Appointment_ID");
                    var usrId = results.getInt("User_ID");
                    var custId = results.getInt("Customer_ID");
                    var contId = results.getInt("Contact_ID");
                    var title = results.getString("Title");
                    var description = results.getString("Description");
                    var location = results.getString("Location");
                    var type = results.getString("Type");
                    var start = results.getTimestamp("Start");
                    var end = results.getTimestamp("End");
                    var createdBy = results.getString("Created_By");
                    var createDate = results.getTimestamp("Create_Date");
                    var updatedBy = results.getString("Last_Updated_By");
                    var lastUpdate = results.getTimestamp("Last_Update");

                    report2Results.add(new Report2(contactName, id, custId, usrId, contId, title, type, description, location, createdBy, updatedBy, start, end, createDate, lastUpdate));
                }
                return report2Results;
            } catch (SQLException ex) {
                AlertHelper.CreateError(ex.getMessage()).show();
            }
            return null;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }
    }
}