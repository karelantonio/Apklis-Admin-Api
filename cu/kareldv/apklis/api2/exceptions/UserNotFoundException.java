package cu.kareldv.apklis.api2.exceptions;

public class UserNotFoundException extends Error {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public UserNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UserNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
