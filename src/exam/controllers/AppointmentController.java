package exam.controllers;

import exam.entities.Appointment;
import exam.entities.Contact;
import exam.entities.Customer;
import exam.utility.AlertHelper;
import exam.utility.DateHelper;
import exam.utility.JDBC;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AppointmentController extends ControllerBase {
    @FXML TableView<Appointment> appointmentTable;
    @FXML TableColumn<Appointment, Integer> Appointment_ID;
    @FXML TableColumn<Appointment, String> Type;
    @FXML TableColumn<Appointment, String> Title;
    @FXML TableColumn<Appointment, String> Description;
    @FXML TableColumn<Appointment, String> Location;
    @FXML TableColumn<Appointment, String> Start;
    @FXML TableColumn<Appointment, String> End;
    @FXML TableColumn<Appointment, Integer> User_ID;
    @FXML TableColumn<Appointment, Integer> Contact_ID;
    @FXML TableColumn<Appointment, String> Created_By;
    @FXML TableColumn<Appointment, String> Create_Date;
    @FXML TableColumn<Appointment, String> Last_Updated_By;
    @FXML TableColumn<Appointment, String> Last_Update;

    @FXML TextField appointmentId;
    @FXML TextField appointmentTitle;
    @FXML TextField appointmentDescription;
    @FXML TextField appointmentType;
    @FXML TextField appointmentLocation;
    @FXML TextField appointmentUserId;
    @FXML DatePicker appointmentStartDate;
    @FXML DatePicker appointmentEndDate;
    @FXML TextField appointmentStartTime;
    @FXML TextField appointmentEndTime;
    @FXML ComboBox<Contact> appointmentContactSelect;
    @FXML ComboBox<Customer> appointmentCustomerSelect;
    @FXML Button saveButton;
    @FXML RadioButton weekRadio;
    @FXML RadioButton monthRadio;

    /**
     * Takes the values from the form and determines whether it is a new appointment or one that is modified and then saves the changes to the database.
     * @param ev ActionEvent
     */
    @FXML
    public void saveAppointment(ActionEvent ev) {
        try {
            var parsedStartDate = LocalDate.parse(this.appointmentStartDate.getValue().toString());
            var parsedStartTime = LocalTime.parse(this.appointmentStartTime.getText());
            var parsedEndDate = LocalDate.parse(this.appointmentEndDate.getValue().toString());
            var parsedEndTime = LocalTime.parse(this.appointmentEndTime.getText());
            var aptTitle = this.appointmentTitle.getText();
            var aptDesc = this.appointmentDescription.getText();
            var aptLoc = this.appointmentLocation.getText();
            var aptType = this.appointmentType.getText();
            var aptStart = DateHelper.localToUTC(parsedStartDate, parsedStartTime);
            var aptEnd = DateHelper.localToUTC(parsedEndDate, parsedEndTime);
            var aptUserId = Integer.parseInt(this.appointmentUserId.getText());
            var inputsValid = (isValidText(aptTitle) && isValidText(aptDesc) && isValidText(aptLoc) && isValidText(aptTitle));

            if (DateHelper.isBetweenBusinessHoursEST(parsedStartTime, parsedEndTime) && DateHelper.isNotDuringWeekend(parsedStartDate, parsedEndDate) && inputsValid) {
                var localStartDateTime = DateHelper.utctoLocal(aptStart.toInstant());
                var localEndDateTime = DateHelper.utctoLocal(aptEnd.toInstant());
                var appointments = this.appointmentTable.getItems();
                for(var apt : appointments){
                    var occursDuring = ((this.appointmentContactSelect.getSelectionModel().getSelectedItem().ID == apt.getContact_ID())
                            || (aptUserId == apt.getUser_ID()))
                            && ((localStartDateTime.after(apt.getStart()) && localEndDateTime.before(apt.getEnd()))
                            || (localStartDateTime.before(apt.getStart()) && localEndDateTime.before(apt.getEnd()))
                            || (localStartDateTime.before(apt.getStart()) && localEndDateTime.after(apt.getEnd()))
                            || (localStartDateTime.equals(apt.getStart()) && localEndDateTime.equals(apt.getEnd())));
                    if(occursDuring){
                        AlertHelper.CreateError("There is already a meeting scheduled at your selected time").show();
                        return;
                    }
                }

                try {
                    var aptId = Integer.parseInt(this.appointmentId.getText());
                    var query = "UPDATE client_schedule.appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Last_Update=?, Customer_ID=?, Contact_ID=?, Last_Updated_By=? WHERE Appointment_ID=?";
                    try (var stmt = JDBC.getConnection().prepareStatement(query)) {
                        stmt.setString(1, aptTitle);
                        stmt.setString(2, aptDesc);
                        stmt.setString(3, aptLoc);
                        stmt.setString(4, aptType);
                        stmt.setTimestamp(5, aptStart);
                        stmt.setTimestamp(6, aptEnd);
                        stmt.setTimestamp(7, Timestamp.from(Instant.now()));
                        stmt.setInt(8, Integer.parseInt(this.dataObject.toString()));
                        stmt.setInt(9, this.appointmentContactSelect.getSelectionModel().getSelectedItem().ID);
                        stmt.setString(10, this.Username);
                        stmt.setInt(11, aptId);

                        var result = stmt.execute();
                        if (!result) {
                            refreshAppointmentTable(this.monthRadio.selectedProperty().get());
                        } else {
                            AlertHelper.CreateError("Failed to update appointment").show();
                        }
                    } catch (SQLException ex) {
                        AlertHelper.CreateError(ex.getMessage()).show();
                    }
                } catch (NumberFormatException numEx) {
                    //create new appointment
                    if (this.appointmentTitle.getText().length() > 0 && this.appointmentDescription.getText().length() > 0) {
                        var query = "INSERT INTO client_schedule.appointments (Title, Description, Location, Type, Start, End, User_ID, Customer_ID, Contact_ID, Create_Date, Created_By, Last_Update, Last_Updated_By) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        try (var stmt = JDBC.getConnection().prepareStatement(query)) {
                            stmt.setString(1, aptTitle);
                            stmt.setString(2, aptDesc);
                            stmt.setString(3, aptLoc);
                            stmt.setString(4, aptType);
                            stmt.setTimestamp(5, aptStart);
                            stmt.setTimestamp(6, aptEnd);
                            stmt.setInt(7, Integer.parseInt(this.appointmentUserId.getText()));
                            stmt.setInt(8, Integer.parseInt(this.dataObject.toString()));
                            stmt.setInt(9, this.appointmentContactSelect.getSelectionModel().getSelectedItem().ID);
                            stmt.setTimestamp(10, Timestamp.from(Instant.now()));
                            stmt.setString(11, this.Username);
                            stmt.setTimestamp(12, Timestamp.from(Instant.now()));
                            stmt.setString(13, this.Username);

                            var result = stmt.execute();
                            if (!result) {
                                refreshAppointmentTable(this.monthRadio.selectedProperty().get());
                            } else {
                                AlertHelper.CreateError("Failed to create appointment").show();
                            }
                        } catch (SQLException ex) {
                            AlertHelper.CreateError(ex.getMessage()).show();
                        }
                    } else {
                        AlertHelper.CreateError("You must have a title and description set.").show();
                    }

                } catch (Exception ex) {
                    AlertHelper.CreateError(ex.getMessage()).show();
                }
            }
            else{
                AlertHelper.CreateError("Your appointment must be scheduled between 08:00 and 22:00 EST on weekdays only").show();
            }
        }catch(DateTimeParseException ex) {
            AlertHelper.CreateError("Failed to parse either the start or end time.").show();
        }
    }

    /**
     * This function will clear the on screen form.
     * @param e Action Event
     */
    @FXML
    public void clearForm(ActionEvent e) {
        this.appointmentId.setText("");
        this.appointmentStartTime.setText("");
        this.appointmentTitle.setText("");
        this.appointmentDescription.setText("");
        this.appointmentType.setText("");
        this.appointmentLocation.setText("");
        this.appointmentEndDate.setValue(null);
        this.appointmentStartDate.setValue(null);
        this.appointmentStartTime.setText("");
        this.appointmentEndTime.setText("");
        this.appointmentUserId.setText("");
    }

    @FXML
    public void cancelAppointment(ActionEvent e) {
        if(this.appointmentId.getText().isBlank())
        {
            return;
        }


    }

    /**
     * Set the data object for the controller and run page initialization
     * @param o Object to be used on the page.
     */
    @Override
    public void setDataObject(Object o) {
        this.dataObject = o;
        appointmentTable.setRowFactory(v -> {
            TableRow<Appointment> r = new TableRow<>();
            r.setOnMouseClicked(clickHandler);
            return r;
        });
        Appointment_ID.setCellValueFactory(new PropertyValueFactory<>("Appointment_ID"));
        Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        Title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        Description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        Location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        Start.setCellValueFactory(col -> new ReadOnlyStringWrapper(DateHelper.utctoLocal(col.getValue().getStart().toInstant()).toString()));
        End.setCellValueFactory(col -> new ReadOnlyStringWrapper(DateHelper.utctoLocal(col.getValue().getEnd().toInstant()).toString()));
        User_ID.setCellValueFactory(new PropertyValueFactory<>("User_ID"));
        Contact_ID.setCellValueFactory(new PropertyValueFactory<>("Contact_ID"));
        Created_By.setCellValueFactory(new PropertyValueFactory<>("Created_By"));
        Create_Date.setCellValueFactory(new PropertyValueFactory<>("Create_Date"));
        Last_Updated_By.setCellValueFactory(new PropertyValueFactory<>("Last_Updated_By"));
        Last_Update.setCellValueFactory(new PropertyValueFactory<>("Last_Update"));
        appointmentCustomerSelect.getItems().addAll(Customer.findAll());
        appointmentCustomerSelect.setValue(Customer.get((int)o));
        appointmentCustomerSelect.setOnAction(changeHandler);

        var query = "SELECT Contact_ID, Contact_Name FROM client_schedule.contacts";
        try(var stmt = JDBC.getConnection().prepareStatement(query)){
            var rs = stmt.executeQuery();
            while(rs.next()){
                appointmentContactSelect.getItems().add(new Contact(rs.getInt("Contact_ID"), rs.getString("Contact_Name")));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }

        final ToggleGroup group = new ToggleGroup();
        weekRadio.setToggleGroup(group);
        monthRadio.setToggleGroup(group);

        /**
         * C195 Task 1: Requirement B: Write at least two different lambda expressions to improve your code.
         * "Using lambda expressions also improves the collection libraries", Oracle claims. This would make using a lambda expression the faster option for iterating, filtering, and extracting data from a collection.
         * Also, lambda expressions have concurrency features that improve performance in multi-core environments.
         */
        weekRadio.setOnAction(e -> {
            refreshAppointmentTable(false);
        });
        monthRadio.setOnAction(e -> {
            refreshAppointmentTable(true);
        });
        refreshAppointmentTable(false);
        weekRadio.setSelected(true);
    }

    /**
     * C195 Task 1: Requirement B: Write at least two different lambda expressions to improve your code.
     * "Using lambda expressions also improves the collection libraries", Oracle claims. This would make using a lambda expression the faster option for iterating, filtering, and extracting data from a collection.
     * Also, lambda expressions have concurrency features that improve performance in multi-core environments.
     */
    private final EventHandler<MouseEvent> clickHandler = e -> {
        var apt = this.appointmentTable.getSelectionModel().getSelectedItem();
        this.appointmentId.setText(Integer.valueOf(apt.getAppointment_ID()).toString());
        this.appointmentUserId.setText(Integer.valueOf(apt.getUser_ID()).toString());
        this.appointmentType.setText(apt.getType());
        this.appointmentLocation.setText(apt.getLocation());
        this.appointmentTitle.setText(apt.getTitle());
        this.appointmentDescription.setText(apt.getDescription());
        this.appointmentStartDate.setValue(apt.getStart().toLocalDateTime().toLocalDate());
        this.appointmentStartTime.setText(apt.getStart().toLocalDateTime().toLocalTime().toString());
        this.appointmentEndDate.setValue(apt.getEnd().toLocalDateTime().toLocalDate());
        this.appointmentEndTime.setText(apt.getEnd().toLocalDateTime().toLocalTime().toString());
        this.appointmentCustomerSelect.setValue(Customer.get(apt.getUser_ID()));
        this.appointmentContactSelect.setValue(Contact.getById(apt.getUser_ID()));
    };

    private final EventHandler<ActionEvent> changeHandler = e -> {
        refreshAppointmentTable(this.monthRadio.isSelected());
    };

    private void refreshAppointmentTable(boolean month) {
        this.appointmentTable.getItems().removeAll(this.appointmentTable.getItems());
        int customerId = -1;

        if(this.appointmentCustomerSelect.getSelectionModel().getSelectedItem() != null){
            customerId = this.appointmentCustomerSelect.getSelectionModel().getSelectedItem().getCustomer_ID();
        }

        if(month){
            this.appointmentTable.getItems().addAll(Appointment.findByCustomerMonth(customerId));
        } else {
            this.appointmentTable.getItems().addAll(Appointment.findByCustomerWeek(customerId));
        }

        this.appointmentTable.refresh();
    }

    private boolean isValidText(String text) {
        if(text.length() > 0 && !text.isBlank() && !text.isEmpty()){
            return true;
        }
        return false;
    }
}
