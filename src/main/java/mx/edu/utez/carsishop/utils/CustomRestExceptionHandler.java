package mx.edu.utez.carsishop.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class CustomRestExceptionHandler {
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        // Personaliza el mensaje de error aquí según tus necesidades
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, "Error en la validación de la entrada", errors);
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }


    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex  ) {
        String error = ex.getParameterName() + " parameter is missing";

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error =
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();

        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));

        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED,
                ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

        ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ex.getLocalizedMessage(), builder.substring(0, builder.length() - 2));
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleBindException(BindException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpStatus status ) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, HttpStatus status) {
        ApiError apiError = new ApiError(
                status, ex.getCause().getMessage(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({SQLException.class})
    protected ResponseEntity<Object> sqlException(SQLException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getSQLState(), ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {
        String errorMessage = (ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage();
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, ex.getMessage());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

}
