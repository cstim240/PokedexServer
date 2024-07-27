package ca.cmpt213.exception;

public class TokimonCardNotFoundException extends RuntimeException{
    public TokimonCardNotFoundException(String message) {
        super(message); // call the constructor of the parent class
    }
}
