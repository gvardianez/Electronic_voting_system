package errors;

public class TitleIsNotAvailableException extends RuntimeException{
    public TitleIsNotAvailableException(String message){
        super(message);
    }
}
