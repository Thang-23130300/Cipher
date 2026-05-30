package nlu.fit.web.souvenirecommerce.profile.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
