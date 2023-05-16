package exam.controllers;

import exam.entities.Customer;
import exam.entities.Location;
import exam.utility.AlertHelper;
import exam.utility.JDBC;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import static java.lang.Integer.parseInt;

public class ModifyCustomerController extends ControllerBase {
    @FXML TextField customerId;
    @FXML TextField customerName;
    @FXML TextField customerPhone;
    @FXML TextField customerAddress;
    @FXML TextField customerPostal;
    @FXML ComboBox<Location> countrySelect;
    @FXML ComboBox<Location> firstDivisionSelect;
    @FXML Button deleteBtn;
    @FXML Button apptsBtn;

    /**
     * Set default values in the gui
     * @param customer Customer object used to populate page
     */
    @Override
    public void setDataObject(Object customer) {
        countrySelect.getItems().addAll(Location.GetCountries());
        if(customer != null){
            Customer c = (Customer) customer;
            this.dataObject = c;
            firstDivisionSelect.getItems().addAll(Location.GetDivisions(c.getCountryId()));

            customerId.setText(String.valueOf(c.getCustomer_ID()));
            customerName.setText(c.getCustomer_Name());
            customerPhone.setText(c.getPhone());
            customerAddress.setText(c.getAddress());
            customerPostal.setText(c.getPostal_Code());
            firstDivisionSelect.setValue(c.getFirstDivision());
            countrySelect.setValue(c.getCountry());
        }else{
            deleteBtn.setVisible(false);
            apptsBtn.setVisible(false);
            firstDivisionSelect.getItems().addAll(Location.GetDivisions());
        }
    }

    /**
     * Create delete alert and handle input
     * @param e
     */
    @FXML
    public void delete(ActionEvent e) {
        Customer c = (Customer)this.dataObject;
        var a = AlertHelper.CreateConfirmation("Confirm deletion of " + c.getCustomer_Name(), "Confirm deletion of " + c.getCustomer_Name(), "");
        a.showAndWait();
        if(a.getResult() == ButtonType.YES) {
            var deleteResult = c.delete();
            if(!deleteResult){
                AlertHelper.CreateInformation("User deleted").show();
                closeWindow(e);
                openWindow("customer", null, this.Username, this.UserId);
            }
        } else {
            a.close();
        }
    }

    /**
     * Detect whether the object is being created or if it is one being modified and then apply the form values to that object and save it in the database.
     * @param e
     */
    @FXML
    public void modify(ActionEvent e) {
        if(this.dataObject != null) {
            var updateCustQuery = "UPDATE client_schedule.customers SET Customer_Name=?, Address=?, Postal_Code=?, Division_ID=?, Phone=?, Last_Update=?, Last_Updated_By=? WHERE Customer_ID=?";
            try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(updateCustQuery)){
                stmt.setString(1, this.customerName.getText());
                stmt.setString(2, this.customerAddress.getText());
                stmt.setString(3, this.customerPostal.getText());
                stmt.setInt(4, this.firstDivisionSelect.getSelectionModel().getSelectedItem().getID());
                stmt.setString(5, this.customerPhone.getText());
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                stmt.setString(7, this.Username);
                stmt.setInt(8, parseInt(this.customerId.getText()));
                var result = stmt.execute();
                if(!result) {
                    AlertHelper.CreateInformation("Customer updated").show();
                    openWindow("customer", null, this.Username, this.UserId);
                    closeWindow(e);
                }
            } catch (SQLException ex) {
                AlertHelper.CreateError(ex.getMessage()).show();
            }
        } else {
            var insertCustomerQuery = "INSERT INTO client_schedule.customers (Customer_Name, Address, Postal_Code, Division_ID, Phone, Last_Update, Last_Updated_By, Create_Date, Created_By) VALUES(?,?,?,?,?,?,?,?,?)";
            try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(insertCustomerQuery)) {
                stmt.setString(1, this.customerName.getText());
                stmt.setString(2, this.customerAddress.getText());
                stmt.setString(3, this.customerPostal.getText());
                stmt.setInt(4, this.firstDivisionSelect.getSelectionModel().getSelectedItem().getID());
                stmt.setString(5, this.customerPhone.getText());
                stmt.setTimestamp(6, Timestamp.from(Instant.now()));
                stmt.setString(7, this.Username);
                stmt.setTimestamp(8, Timestamp.from(Instant.now()));
                stmt.setInt(9, this.UserId);

                var result = stmt.execute();
                if (!result) {
                    AlertHelper.CreateInformation("Customer created").show();
                    openWindow("customer", null, this.Username, this.UserId);
                    closeWindow(e);
                }
            } catch(Exception ex){
                AlertHelper.CreateError(ex.getMessage()).show();
            }
        }
    }

    /**
     * Open the appointment management window
     */
    @FXML
    public void openAppointments() {
        Customer c = (Customer)this.dataObject;
        openWindow("appointment", c.getCustomer_ID(), this.Username, this.UserId);
    }

    @FXML
    private void countrySelectHandler(ActionEvent e) {
        var targetObject = (ComboBox)e.getTarget();
        var country = (Location)targetObject.getSelectionModel().getSelectedItem();
        firstDivisionSelect.getItems().clear();
        var locations = Location.GetDivisions(country.getID());
        firstDivisionSelect.getItems().addAll(locations);
        firstDivisionSelect.setValue(locations.get(0));
    }
}
