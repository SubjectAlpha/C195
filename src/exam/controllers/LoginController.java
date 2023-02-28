package exam.controllers;

import exam.utility.AlertHelper;
import exam.utility.JDBC;
import exam.utility.LogHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController extends ControllerBase implements Initializable {

    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private Button signinBtn;
    @FXML private Label usernameLabel;
    @FXML Label passwordLabel;
    @FXML private Label localeLabel;

    /**
     * Initialize the GUI and detect if the language used is French, if it is French change the labels to their French translations.
     * @param url URL
     * @param resourceBundle ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        localeLabel.setText(Locale.getDefault().toLanguageTag());
        if(Locale.getDefault().getLanguage().equals("fr")){
            usernameLabel.setText("Nom d’utilisateur");
            passwordLabel.setText("Mot de passe");
            signinBtn.setText("Connexion");
        }
    }

    /**
     * Run query for login attempt using username and password TextField values. Log result at the end.
     * @param e ActionEvent
     */
    @FXML
    public void signin(ActionEvent e){
        String query = "SELECT User_ID FROM client_schedule.users WHERE User_Name=? AND Password=?;";
        var loginResult = false;
        try(PreparedStatement stmt = JDBC.getConnection().prepareStatement(query)){
            stmt.setString(1, usernameInput.getText());
            stmt.setString(2, passwordInput.getText());
            var result = stmt.executeQuery();
            if(result.next())
            {
                loginResult = true;
                var uid = result.getInt("User_ID");
                openWindow("customer", null, usernameInput.getText(), uid);
                closeWindow(e);
            }else{
                AlertHelper.CreateInformation("Your username or password was entered incorrectly.").show();
            }
        }catch(SQLException ex){
            AlertHelper.CreateError(ex.getMessage()).show();
        }
        finally {
            LogHelper.writeLoginAttempt(usernameInput.getText(), new Timestamp(System.currentTimeMillis()), loginResult,"login_activity.txt");
        }
    }
}
