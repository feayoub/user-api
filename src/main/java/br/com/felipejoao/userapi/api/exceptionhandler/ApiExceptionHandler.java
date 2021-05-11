package br.com.felipejoao.userapi.api.exceptionhandler;

import br.com.felipejoao.userapi.domain.exception.BusinessException;
import br.com.felipejoao.userapi.domain.exception.WrongPasswordException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleNotMappedException(Exception ex, WebRequest web) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Problem problem = Problem.builder()
                .title(ex.getMessage())
                .status(status.value())
                .dateTime(OffsetDateTime.now())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, web);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest web) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        Problem problem = Problem.builder()
                .status(status.value())
                .title(ex.getMessage())
                .dateTime(OffsetDateTime.now())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, web);
    }

    @ExceptionHandler({BusinessException.class, WrongPasswordException.class})
    public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest web) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Problem problem = Problem.builder()
                .status(status.value())
                .title(ex.getMessage())
                .dateTime(OffsetDateTime.now())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, web);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers, HttpStatus status, WebRequest request) {
        Problem problem = Problem.builder()
                .status(status.value())
                .title(ex.getMessage())
                .dateTime(OffsetDateTime.now())
                .build();

        return super.handleExceptionInternal(ex, problem, headers, status, request);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        ArrayList<Problem.Field> fields = new ArrayList<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String nome = ((FieldError) error).getField();
            String msg = messageSource.getMessage(error, LocaleContextHolder.getLocale());
            fields.add(new Problem.Field(nome, msg));
        }

        Problem problem = Problem.builder()
                .status(status.value())
                .title("One or more fields are invalid")
                .dateTime(OffsetDateTime.now())
                .fields(fields)
                .build();

        return super.handleExceptionInternal(ex, problem, headers, status, request);
    }
}
