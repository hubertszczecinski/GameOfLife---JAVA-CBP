package progkom;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class BaseApplicationException extends RuntimeException {
    public BaseApplicationException(String messageKey) {
        super(resolveMessage(messageKey, (Object[]) null));
    }

    public BaseApplicationException(String messageKey, Throwable cause) {
        super(resolveMessage(messageKey, (Object[]) null), cause);
    }

    private static String resolveMessage(String messageKey, Object... params) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");
            String message = resourceBundle.getString(messageKey);

            if (params != null && params.length > 0) {
                return MessageFormat.format(message, params);
            }

            return message;
        } catch (MissingResourceException e) {
            return "Brak wiadomości dla klucza: " + messageKey; // Domyślny komunikat
        }
    }
}