package exam.entities;

import exam.utility.AlertHelper;
import exam.utility.JDBC;

import java.sql.SQLException;
import java.util.ArrayList;

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

    public static ArrayList<Contact> getAll(){
        var returnList = new ArrayList<Contact>();
        var report2Query = "SELECT Contact_ID, Contact_Name FROM client_schedule.contacts";

        try{
            var report2 = JDBC.getConnection().prepareStatement(report2Query);
            var report2Results = report2.executeQuery();
            while(report2Results.next()){
                returnList.add(new Contact(report2Results.getInt(1), report2Results.getString(2)));
            }
        } catch(Exception e) {
            AlertHelper.CreateError(e.getMessage());
        }

        return returnList;
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
