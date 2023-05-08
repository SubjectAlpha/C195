package exam.entities;

import exam.utility.AlertHelper;
import exam.utility.JDBC;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Customer {

    private int Customer_ID;
    private int Division_ID;
    private String Customer_Name;
    private String Address;
    private String Postal_Code;
    private String Phone;
    private String Created_By;
    private String Last_Updated_By;
    private Timestamp Create_Date;
    private Timestamp Last_Update;

    public Customer(){}

    public Customer(int id){
        this.Customer_ID = id;
    }

    public Customer(int id, String name) {
        this.Customer_ID = id;
        this.Customer_Name = name;
    }

    public Customer(int id, String name, String address, String postal_Code,
            String phone, String createdBy, Timestamp createDate, String updatedBy,
            Timestamp lastUpdate, int divisionId)
    {
        this.Customer_ID = id;
        this.Customer_Name = name;
        this.Address = address;
        this.Postal_Code = postal_Code;
        this.Phone = phone;
        this.Created_By = createdBy;
        this.Create_Date = createDate;
        this.Last_Updated_By = updatedBy;
        this.Last_Update = lastUpdate;
        this.Division_ID = divisionId;
    }

    public int getCustomer_ID(){
        return this.Customer_ID;
    }

    public String getCustomer_Name(){
        return this.Customer_Name;
    }

    public String getAddress(){
        return this.Address;
    }

    public String getPostal_Code(){
        return this.Postal_Code;
    }

    public String getPhone(){
        return this.Phone;
    }

    public String getLast_Updated_By(){
        return this.Last_Updated_By;
    }

    public Timestamp getCreate_Date(){
        return this.Create_Date;
    }

    public String getCreated_By(){
        return this.Created_By;
    }

    public Timestamp getLast_Update(){
        return this.Last_Update;
    }

    public int getDivision_ID(){
        return this.Division_ID;
    }

    /**
     * @return String result of the country attached to this customer.
     */
    public String getCountry()
    {
        String query = "SELECT country FROM client_schedule.countries WHERE country_id=?";
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(query)) {
            stmt.setInt(1, this.getCountryId());
            var rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getString("country");
            }
        } catch(SQLException e) {}
        return "Not found";
    }

    public String getFirstDivision()
    {
        String query = "SELECT division FROM client_schedule.first_level_divisions WHERE division_id=?";
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(query)) {
            stmt.setInt(1, this.Division_ID);
            var rs = stmt.executeQuery();
            if(rs.next())
            {
                return rs.getString("division");
            }
        } catch (SQLException e) {}
        return null;
    }

    private int getCountryId()
    {
        String query = "SELECT country_id FROM client_schedule.first_level_divisions WHERE division_ID=?";
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(query)) {
            stmt.setInt(1, this.Division_ID);
            var rs = stmt.executeQuery();
            if(rs.next()) {
                return rs.getInt("country_id");
            }
        } catch (SQLException e) {
        }
        return -1;
    }

    public void setCustomer_Name(String name) {
        this.Customer_Name = name;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setPostal_Code(String postal_code) {
        this.Postal_Code = postal_code;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public void setLast_Updated_By(String name) {
        this.Last_Updated_By = name;
    }

    public void setLast_Update(long time){
        this.Last_Update = new Timestamp(time);
    }

    public void setDivision_ID(int divisionId) {
        this.Division_ID = divisionId;
    }

    public boolean delete() {
        return delete(this.Customer_ID);
    }

    /**
     * This function will delete a user in the database and all of their appointments.
     * @param customer_id ID of the customer you wish to delete.
     * @return result of the delete process
     */
    public static Customer get(int customer_id) {
        String customerQuery = "SELECT * FROM client_schedule.customers WHERE Customer_ID=?";

        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(customerQuery)){
            stmt.setInt(1, customer_id);
            var res = stmt.executeQuery();

            Customer customer = null;
            if(res.next()){
                var id = res.getInt("Customer_ID");
                var divisionId = res.getInt("Division_ID");
                var name = res.getString("Customer_Name");
                var address = res.getString("Address");
                var postal = res.getString("Postal_Code");
                var phone = res.getString("Phone");
                var createdBy = res.getString("Created_By");
                var createDate = res.getTimestamp("Create_Date");
                var updatedBy = res.getString("Last_Updated_By");
                var lastUpdate = res.getTimestamp("Last_Update");

                customer = new Customer(id, name, address, postal, phone, createdBy, createDate, updatedBy, lastUpdate, divisionId);
            }
            return customer;

        } catch (SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return null;
    }

    /**
     * @return List of all customers in the database
     */
    public static ArrayList<Customer> findAll() {
        String query = "SELECT * FROM client_schedule.customers";
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(query)){
            ArrayList<Customer> customers = new ArrayList<Customer>();
            var customersResult = stmt.executeQuery();
            while(customersResult.next()){
                var id = customersResult.getInt("Customer_ID");
                var divisionId = customersResult.getInt("Division_ID");
                var name = customersResult.getString("Customer_Name");
                var address = customersResult.getString("Address");
                var postal = customersResult.getString("Postal_Code");
                var phone = customersResult.getString("Phone");
                var createdBy = customersResult.getString("Created_By");
                var createDate = customersResult.getTimestamp("Create_Date");
                var updatedBy = customersResult.getString("Last_Updated_By");
                var lastUpdate = customersResult.getTimestamp("Last_Update");

                customers.add(new Customer(id, name, address, postal, phone, createdBy, createDate, updatedBy, lastUpdate, divisionId));
            }
            return customers;
        }catch(SQLException e){
            AlertHelper.CreateError(e.getMessage());
        }
        return null;
    }

    /**
     * This function will delete a user in the database and all of their appointments.
     * @param customer_id ID of the customer you wish to delete.
     * @return result of the delete process
     */
    public static boolean delete(int customer_id) {
        String appointmentsQuery = "DELETE FROM client_schedule.appointments WHERE Customer_ID=?";
        String customerQuery = "DELETE FROM client_schedule.customers WHERE Customer_ID=?";

        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(customerQuery)){
            PreparedStatement appStmt = JDBC.getConnection().prepareStatement(appointmentsQuery);
            appStmt.setInt(1, customer_id);
            stmt.setInt(1, customer_id);

            var appRes = appStmt.execute();
            if(!appRes) {
                var res = stmt.execute();
                return res;
            }
        } catch (SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return false;
    }

    @Override
    public String toString() {
        return this.Customer_Name;
    }
}
