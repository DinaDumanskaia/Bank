package bank.infrastructure.web;

import bank.application.ClientNotFoundException;
import bank.domain.NegativeBalanceException;
import bank.infrastructure.database.BadServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NegativeBalanceException.class)
    protected ResponseEntity<Object> handleNegativeBalance(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Negative balance";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    protected ResponseEntity<Object> handleClientNoFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Client not found";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BadServiceConnection.class)
    protected ResponseEntity<Object> handleBadServiceConnection(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Bad service connection";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
