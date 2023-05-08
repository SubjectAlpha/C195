package exam.controllers;

import exam.entities.Appointment;
import exam.entities.Customer;
import exam.utility.AlertHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalTime;

public class CustomerController extends ControllerBase {

    @FXML TableView<Customer> customerTable;
    @FXML TableColumn<Customer, Integer> idColumn;
    @FXML TableColumn<Customer, String> nameColumn;
    @FXML TableColumn<Customer, String> addressColumn;
    @FXML TableColumn<Customer, String> postalColumn;
    @FXML TableColumn<Customer, String> createDateColumn;
    @FXML TableColumn<Customer, String> createdByColumn;
    @FXML TableColumn<Customer, Timestamp> updateDateColumn;
    @FXML TableColumn<Customer, String> updatedByColumn;
    @FXML TableColumn<Customer, Integer> divisionIdColumn;
    @FXML TextField reportDirectory;
    @FXML Button createCustomerButton;

    /**
     * Set the data object used in the controller then initialize the page with default values
     * @param o Object to be used on the page.
     */
    @Override
    public void setDataObject(Object o) {
        try{
            customerTable.setRowFactory(customerTableView -> {
                TableRow<Customer> row = new TableRow<>();
                row.setOnMouseClicked(clickHandler);
                return row;
            });
            idColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("Customer_ID"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Customer_Name"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Address"));
            postalColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Postal_Code"));
            createDateColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Create_Date"));
            createdByColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Created_By"));
            updateDateColumn.setCellValueFactory(new PropertyValueFactory<Customer, Timestamp>("Last_Update"));
            updatedByColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Last_Updated_By"));
            divisionIdColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("Division_ID"));
            customerTable.getItems().addAll(Customer.findAll());
            var userAppointments = Appointment.findByUser(this.UserId);
            var meetingStartingSoon = false;
            for(var apt : userAppointments){
                var aptStart = apt.getStart().toLocalDateTime().toLocalTime();
                var triggerTime = aptStart.minusMinutes(15);
                if(LocalTime.now().isAfter(triggerTime) && LocalTime.now().isBefore(aptStart)){
                    AlertHelper.CreateInformation("Meeting " + apt.getAppointment_ID() + " is in less than 15 minutes. The meeting will start at " + aptStart + " on " + apt.getStart().toLocalDateTime().toLocalDate()).show();
                    meetingStartingSoon = true;
                }
            }
            if(!meetingStartingSoon){
                AlertHelper.CreateInformation("No meetings are upcoming.").show();
            }
        }catch(Exception e){
            AlertHelper.CreateError(e.getMessage()).show();
        }
    }

    /**
     * Open the customer creation/modification window and close the current window.
     * @param e ActionEvent
     */
    @FXML
    public void openCreateCustomer(ActionEvent e) {
        try{
            openWindow("modifyCustomer", null, this.Username, this.UserId);
            closeWindow(e);
        } catch(Exception ex) {
            AlertHelper.CreateError(ex.getMessage());
        }
    }

    @FXML public void viewAllAppointments(ActionEvent e) {
        openWindow("appointment", -1, this.Username, this.UserId);
    }

    /**
     * C195 Task 1: Requirement B: Write at least two different lambda expressions to improve your code.
     * "Using lambda expressions also improves the collection libraries", Oracle claims. This would make using a lambda expression the faster option for iterating, filtering, and extracting data from a collection.
     * Also, lambda expressions have concurrency features that improve performance in multi-core environments.
     */
    private final EventHandler<MouseEvent> clickHandler = mouseEvent -> {
        try{
            var customer = customerTable.getSelectionModel().getSelectedItem();
            openWindow("modifyCustomer", customer, this.Username, this.UserId);
            var stage = (Stage) customerTable.getScene().getWindow();
            stage.close();
        }catch(Exception e){
            AlertHelper.CreateError(e.getMessage());
        }
    };
}
