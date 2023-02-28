package exam.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHelper {
    /**
     * @param content Content you want displayed in the body of the alert.
     * @return Alert object to be displayed
     */
    public static Alert CreateError(String content)
    {
        return CreateError(content, "An error occurred");
    }

    /**
     * @param content Content you want displayed in the body of the alert.
     * @param title Title of the alert you wish to display
     * @return Alert object to be displayed
     */
    public static Alert CreateError(String content, String title)
    {
        return CreateError(content, title, "An error occurred");
    }

    /**
     * @param content Content you want displayed in the body of the alert.
     * @param title Title of the alert you wish to display
     * @param header Header text for the alert
     * @return Alert object to be displayed
     */
    public static Alert CreateError(String content, String title, String header)
    {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        return a;
    }

    /**
     * @param content Content you want displayed in the body of the alert.
     * @param title Title of the alert you wish to display
     * @param header Header text for the alert
     * @return Alert object to be displayed
     */
    public static Alert CreateConfirmation(String content, String title, String header) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, title, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(header);
        a.setContentText(content);
        return a;
    }

    /**
     * @param content Content you want displayed in the body of the alert.
     * @return Alert object to be displayed
     */
    public static Alert CreateInformation(String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(content);
        return a;
    }
}
