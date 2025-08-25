package progkom;

public class DatabaseOperationException extends BaseApplicationException {
    public DatabaseOperationException(String messageKey) {
        super(messageKey);
    }

    public DatabaseOperationException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }
}
