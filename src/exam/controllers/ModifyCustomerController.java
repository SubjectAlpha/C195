package exam.controllers;

import exam.entities.Customer;
import exam.utility.AlertHelper;
import exam.utility.JDBC;
import javafx.event.ActionEvent;
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
    @FXML ComboBox<String> countrySelect;
    @FXML ComboBox<String> firstDivisionSelect;
    @FXML Button deleteBtn;
    @FXML Button apptsBtn;

    /**
     * Set default values in the gui
     * @param customer Customer object used to populate page
     */
    @Override
    public void setDataObject(Object customer) {
        if(customer != null){
            Customer c = (Customer) customer;
            this.dataObject = c;

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
        }

        //Load countries
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Country FROM client_schedule.countries")) {
            var rs = stmt.executeQuery();
            while(rs.next()) {
                countrySelect.getItems().add(rs.getString("Country"));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }

        //Load divisions
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Division_ID, Division FROM client_schedule.first_level_divisions")) {
            var rs = stmt.executeQuery();
            while(rs.next()) {
                firstDivisionSelect.getItems().add(rs.getString("Division"));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
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
        int divisionId = -1;
        var getDivisionIdQuery = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division=?";

        if(this.dataObject != null) {
            Customer c = (Customer) this.dataObject;
            try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(getDivisionIdQuery)){
                stmt.setString(1, this.firstDivisionSelect.getSelectionModel().getSelectedItem());
                var res = stmt.executeQuery();
                while(res.next()){
                    divisionId = res.getInt("Division_ID");
                }

                var updateCustQuery = "UPDATE client_schedule.customers SET Customer_Name=?, Address=?, Postal_Code=?, Division_ID=?, Phone=?, Last_Update=?, Last_Updated_By=? WHERE Customer_ID=?";
                var updateCustStmt = JDBC.getConnection().prepareStatement(updateCustQuery);
                updateCustStmt.setString(1, this.customerName.getText());
                updateCustStmt.setString(2, this.customerAddress.getText());
                updateCustStmt.setString(3, this.customerPostal.getText());
                updateCustStmt.setInt(4, divisionId);
                updateCustStmt.setString(5, this.customerPhone.getText());
                updateCustStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                updateCustStmt.setString(7, this.Username);
                updateCustStmt.setInt(8, parseInt(this.customerId.getText()));
                var result = updateCustStmt.execute();
                if(!result) {
                    AlertHelper.CreateInformation("Customer updated").show();
                    openWindow("customer", null, this.Username, this.UserId);
                    closeWindow(e);
                }
            } catch (SQLException ex) {
                AlertHelper.CreateError(ex.getMessage()).show();
            }
        } else {
            try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(getDivisionIdQuery)) {
                stmt.setString(1, this.firstDivisionSelect.getSelectionModel().getSelectedItem());
                var res = stmt.executeQuery();
                while(res.next()){
                    divisionId = res.getInt("Division_ID");
                }

                var insertCustomerQuery = "INSERT INTO client_schedule.customers (Customer_Name, Address, Postal_Code, Division_ID, Phone, Last_Update, Last_Updated_By, Create_Date, Created_By) VALUES(?,?,?,?,?,?,?,?,?)";
                var insertCustStmt = JDBC.getConnection().prepareStatement(insertCustomerQuery);
                insertCustStmt.setString(1, this.customerName.getText());
                insertCustStmt.setString(2, this.customerAddress.getText());
                insertCustStmt.setString(3, this.customerPostal.getText());
                insertCustStmt.setInt(4, divisionId);
                insertCustStmt.setString(5, this.customerPhone.getText());
                insertCustStmt.setTimestamp(6, Timestamp.from(Instant.now()));
                insertCustStmt.setString(7, this.Username);
                insertCustStmt.setTimestamp(8, Timestamp.from(Instant.now()));
                insertCustStmt.setInt(9, this.UserId);

                var result = insertCustStmt.execute();
                if(!result) {
                    AlertHelper.CreateInformation("Customer created").show();
                    openWindow("customer", null, this.Username, this.UserId);
                    closeWindow(e);
                }

            } catch(SQLException ex) {
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
}
