package bank.infrastructure.web;

import bank.application.exceptions.ClientNotFoundException;
import bank.application.exceptions.IllegalClientIdException;
import bank.domain.NegativeBalanceException;
import bank.application.exceptions.RepositoryError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalClientIdException.class)
    protected ResponseEntity<Object> handleIllegalClientIdException(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "INCORRECT ID";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NegativeBalanceException.class)
    protected ResponseEntity<Object> handleNegativeBalance(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "NOT ENOUGH MONEY";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    protected ResponseEntity<Object> handleClientNoFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "CLIENT NOT FOUND";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(RepositoryError.class)
    protected ResponseEntity<Object> handleBadServiceConnection(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "BAD SERVICE CONNECTION";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
