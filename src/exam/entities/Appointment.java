package exam.entities;

import exam.utility.AlertHelper;
import exam.utility.JDBC;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Appointment {
    private int Appointment_ID;
    private int Customer_ID;
    private int User_ID;
    private int Contact_ID;
    private String Title;
    private String Type;
    private String Description;
    private String Location;
    private String Created_By;
    private String Last_Updated_By;
    private Timestamp Start;
    private Timestamp End;
    private Timestamp Create_Date;
    private Timestamp Last_Update;

    public Appointment(){}

    public Appointment(int id, int customer_id, int user_id,
           int contact_id, String title, String type,
           String description, String location, String created_by, String last_updated_by,
                       Timestamp start, Timestamp end, Timestamp create, Timestamp last_update)
    {
        this.Appointment_ID = id;
        this.Customer_ID = customer_id;
        this.User_ID = user_id;
        this.Contact_ID = contact_id;
        this.Title = title;
        this.Type = type;
        this.Description = description;
        this.Location = location;
        this.Created_By = created_by;
        this.Last_Updated_By = last_updated_by;
        this.Start = start;
        this.End = end;
        this.Create_Date = create;
        this.Last_Update = last_update;
    }

    public int getAppointment_ID() {
        return Appointment_ID;
    }

    public void setAppointment_ID(int appointment_ID) {
        Appointment_ID = appointment_ID;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getContact_ID() {
        return Contact_ID;
    }

    public void setContact_ID(int contact_ID) {
        Contact_ID = contact_ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getCreated_By() {
        return Created_By;
    }

    public void setCreated_By(String created_By) {
        Created_By = created_By;
    }

    public String getLast_Updated_By() {
        return Last_Updated_By;
    }

    public void setLast_Updated_By(String last_Updated_By) {
        Last_Updated_By = last_Updated_By;
    }

    public Timestamp getStart() {
        return Start;
    }

    public void setStart(Timestamp start) {
        Start = start;
    }

    public Timestamp getEnd() {
        return End;
    }

    public void setEnd(Timestamp end) {
        End = end;
    }

    public Timestamp getCreate_Date() {
        return Create_Date;
    }

    public void setCreate_Date(Timestamp create_Date) {
        Create_Date = create_Date;
    }

    public Timestamp getLast_Update() {
        return Last_Update;
    }

    public void setLast_Update(Timestamp last_Update) {
        Last_Update = last_Update;
    }

    public boolean delete() {
        return delete(this.Appointment_ID);
    }

    /**
     * @param userId ID of the user whose appointments you are trying to see.
     * @return List of user appointments, or null if an error occurred.
     */
    public static ArrayList<Appointment> findByUser(int userId) {
        var getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE User_ID=? ORDER BY Create_Date";
        try(var stmt = JDBC.getConnection().prepareStatement(getAppointmentsQuery)) {
            stmt.setInt(1, userId);
            var results = stmt.executeQuery();
            var appointments = new ArrayList<Appointment>();
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

                appointments.add(new Appointment(id, custId, usrId, contId, title, type, description, location, createdBy, updatedBy, start, end, createDate, lastUpdate));
            }
            return appointments;
        } catch (SQLException ex) {
            AlertHelper.CreateError(ex.getMessage()).show();
        }
        return null;
    }

    /**
     * @param customerId The ID of the customer whose appointments you wish to find
     * @return That customer's appointments for the current week.
     */
    public static ArrayList<Appointment> findByCustomerWeek(int customerId) {
        var getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE Customer_ID=? AND YEARWEEK(Start, 1) = YEARWEEK(CURDATE(), 1) ORDER BY Start";
        if(customerId < 0) {
            getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE YEARWEEK(Start, 1) = YEARWEEK(CURDATE(), 1) ORDER BY Start";
        }
        try(var stmt = JDBC.getConnection().prepareStatement(getAppointmentsQuery)) {
            if(customerId > 0) { stmt.setInt(1, customerId); }
            var results = stmt.executeQuery();
            var appointments = new ArrayList<Appointment>();
            while(results.next()) {
                var id = results.getInt("Appointment_ID");
                var userId = results.getInt("User_ID");
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

                appointments.add(new Appointment(id, custId, userId, contId, title, type, description, location, createdBy, updatedBy, start, end, createDate, lastUpdate));
            }
            return appointments;
        } catch (SQLException ex) {
            AlertHelper.CreateError(ex.getMessage()).show();
        }
        return null;
    }

    /**
     * @param customerId The ID of the customer whose appointments you wish to find
     * @return That customer's appointments for the current month.
     */
    public static ArrayList<Appointment> findByCustomerMonth(int customerId) {
        var getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE Customer_ID=? AND MONTH(Start) = MONTH(CURDATE()) AND YEAR(Start) = YEAR(CURDATE()) ORDER BY Start";
        if(customerId < 0) {
            getAppointmentsQuery = "SELECT * FROM client_schedule.appointments WHERE MONTH(Start) = MONTH(CURDATE()) AND YEAR(Start) = YEAR(CURDATE()) ORDER BY Start";
        }
        try(var stmt = JDBC.getConnection().prepareStatement(getAppointmentsQuery)) {
            if(customerId > 0) {
                stmt.setInt(1, customerId);
            }
            var results = stmt.executeQuery();
            var appointments = new ArrayList<Appointment>();
            while(results.next()) {
                var id = results.getInt("Appointment_ID");
                var userId = results.getInt("User_ID");
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

                appointments.add(new Appointment(id, custId, userId, contId, title, type, description, location, createdBy, updatedBy, start, end, createDate, lastUpdate));
            }
            return appointments;
        } catch (SQLException ex) {
            AlertHelper.CreateError(ex.getMessage()).show();
        }
        return null;
    }

    /**
     * @param id the ID of the appointment you wish to delete.
     * @return Boolean value representing the success of the query
     */
    public static boolean delete(int id) {
        var appointmentsQuery = "DELETE FROM client_schedule.appointments WHERE Appointment_ID=?";
        var result = false;
        try(var stmt = JDBC.getConnection().prepareStatement(appointmentsQuery)) {
            stmt.setInt(1, id);
            result = !stmt.execute();
        }catch(SQLException e){
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return result;
    }
}
