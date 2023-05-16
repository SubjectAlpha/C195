package exam.entities;

import exam.utility.AlertHelper;
import exam.utility.JDBC;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Location {
    private int ID;
    private int ParentID;
    private String Name;

    public Location(int id, int parentID, String name) {
        ID = id;
        ParentID = parentID;
        Name = name;
    }

    @Override
    public String toString() {
        return this.Name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public static ArrayList<Location> GetCountries () {
        var countries = new ArrayList<Location>();
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Country_ID, Country FROM client_schedule.countries")) {
            var rs = stmt.executeQuery();
            while(rs.next()) {
                String name = rs.getString("Country");
                int id = rs.getInt("Country_ID");
                countries.add(new Location(id, -1, name));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return countries;
    }

    public static ArrayList<Location> GetDivisions() {
        var divisions = new ArrayList<Location>();
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Division_ID, Division, Country_ID FROM client_schedule.first_level_divisions")) {
            var rs = stmt.executeQuery();
            while(rs.next()) {
                divisions.add(new Location(rs.getInt(1), rs.getInt(3), rs.getString(2)));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return divisions;
    }

    public static ArrayList<Location> GetDivisions(int countryId) {
        var divisions = new ArrayList<Location>();
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Division_ID, Division, Country_ID FROM client_schedule.first_level_divisions WHERE Country_ID = ?")) {
            stmt.setInt(1, countryId);
            var rs = stmt.executeQuery();
            while(rs.next()) {
                divisions.add(new Location(rs.getInt(1), rs.getInt(3), rs.getString(2)));
            }
        } catch(SQLException e) {
            AlertHelper.CreateError(e.getMessage()).show();
        }
        return divisions;
    }
}
