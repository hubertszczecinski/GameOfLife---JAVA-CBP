package progkom;

public class ValidationException extends BaseApplicationException {
    public ValidationException(String messageKey) {
        super(messageKey);
    }

    public ValidationException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }
}