package exam.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Helper {
    public static Alert CreateError(String content)
    {
        return CreateError(content, "An error occurred");
    }

    public static Alert CreateError(String content, String title)
    {
        return CreateError(content, title, "An error occurred");
    }

    public static Alert CreateError(String content, String title, String header)
    {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        return a;
    }

    public static Alert CreateConfirmation(String content, String title, String header) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, title, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(header);
        a.setContentText(content);
        return a;
    }

    public static Alert CreateInformation(String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(content);
        return a;
    }
}
