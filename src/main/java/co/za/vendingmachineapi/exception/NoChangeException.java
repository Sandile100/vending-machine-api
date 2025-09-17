package co.za.vendingmachineapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoChangeException extends RuntimeException{

    public NoChangeException(String message) {
         super(message);
    }
}
