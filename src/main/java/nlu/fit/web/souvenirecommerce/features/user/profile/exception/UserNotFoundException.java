package nlu.fit.web.souvenirecommerce.features.user.profile.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
