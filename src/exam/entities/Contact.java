package exam.entities;

import exam.utility.AlertHelper;
import exam.utility.JDBC;

import java.sql.SQLException;

public class Contact {
    public Integer ID;
    public String Name;

    public Contact(Integer id, String name){
        this.ID = id;
        this.Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }

    public static Contact getById(int contactId) {
        try(var stmt = JDBC.getConnection().prepareStatement("SELECT Contact_ID, Contact_Name FROM client_schedule.contacts WHERE Contact_ID=?")){
            stmt.setInt(1, contactId);
            var result = stmt.executeQuery();
            if(result.next()){
                return new Contact(result.getInt("Contact_ID"), result.getString("Contact_Name"));
            }
        }catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return null;
    }
}
