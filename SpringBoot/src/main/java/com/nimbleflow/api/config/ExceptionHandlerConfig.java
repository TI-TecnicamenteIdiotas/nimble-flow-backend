package com.nimbleflow.api.config;

import com.nimbleflow.api.exception.BadRequestException;
import com.nimbleflow.api.exception.UnauthorizedException;
import com.nimbleflow.api.exception.response.BaseExceptionResponse;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfig {
    @ExceptionHandler({BadRequestException.class})
    @ResponseBody
    public ResponseEntity<BaseExceptionResponse> badRequestHandler(HttpServletRequest request, Exception exception) {
        BaseExceptionResponse responseBody = BaseExceptionResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(400)
                .error("Bad Request")
                .message(exception.getMessage())
                .path(request.getServletPath())
                .build();

        log.error(String.format("%s: %s (%s)", exception.getCause(), exception.getMessage(), Arrays.toString(exception.getStackTrace())));
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<BaseExceptionResponse> methodArgumentNotValidHandler(HttpServletRequest request, MethodArgumentNotValidException exception) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();

        List<String> errorList = new ArrayList<String>();

        for (FieldError error : errors) {
            errorList.add(String.format("%s %s", error.getField(), error.getDefaultMessage()));
        }

        BaseExceptionResponse responseBody = BaseExceptionResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(400)
                .error("Bad Request")
                .message(errorList.toString())
                .path(request.getServletPath())
                .build();

        log.error(String.format("%s: %s (%s)", exception.getCause(), exception.getMessage(), Arrays.toString(exception.getStackTrace())));
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthorizedException.class, SignatureException.class})
    @ResponseBody
    public ResponseEntity<BaseExceptionResponse> unauthorizedHandler(HttpServletRequest request, Exception exception) {
        BaseExceptionResponse responseBody = BaseExceptionResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message(UnauthorizedException.MESSAGE)
                .path(request.getServletPath())
                .build();

        log.error(String.format("%s: %s (%s)", exception.getCause(), exception.getMessage(), Arrays.toString(exception.getStackTrace())));
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<BaseExceptionResponse> defaultHandler(HttpServletRequest request, Exception exception) {
        BaseExceptionResponse responseBody = BaseExceptionResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .message(exception.getMessage())
                .path(request.getServletPath())
                .build();

        log.error(String.format("%s: %s (%s)", exception.getCause(), exception.getMessage(), Arrays.toString(exception.getStackTrace())));
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}