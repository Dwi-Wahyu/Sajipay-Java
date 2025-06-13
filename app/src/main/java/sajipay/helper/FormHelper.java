package sajipay.helper;

import javafx.scene.control.TextField;

public class FormHelper {
    public static void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }
}
