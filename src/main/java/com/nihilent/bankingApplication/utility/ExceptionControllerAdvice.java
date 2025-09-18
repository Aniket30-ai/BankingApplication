package com.nihilent.bankingApplication.utility;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nihilent.bankingApplication.exception.NihilentBankException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

	@Autowired
	private Environment environment;

//	@ExceptionHandler(NihilentBankException.class)
//	public ResponseEntity<ErrorMessage> handleException(NihilentBankException bankException) {
//
//		ErrorMessage errorMessage = new ErrorMessage();
//
//		errorMessage.setCode(HttpStatus.BAD_REQUEST.value());
//		errorMessage.setMessage(environment.getProperty(bankException.getMessage()));
//		errorMessage.setTimeStamp(LocalDateTime.now().toString());
//
//		return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.BAD_REQUEST);
//
//	}

	@ExceptionHandler(NihilentBankException.class)
	public ResponseEntity<ErrorMessage> handleUnauthorized(NihilentBankException ex) {
		ErrorMessage response = new ErrorMessage();

		response.setCode(HttpStatus.UNAUTHORIZED.value());
		response.setMessage(ex.getMessage());
		response.setTimeStamp(LocalDateTime.now().toString());
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException exception) {

		ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setCode(HttpStatus.BAD_REQUEST.value());

		errorMessage.setMessage(exception.getBindingResult().getAllErrors().stream().map(ex -> ex.getDefaultMessage())
				.collect(Collectors.joining(",")));

		errorMessage.setTimeStamp(LocalDateTime.now().toString());

		return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String,String>> handleConstraintViolation(ConstraintViolationException exception) {

		ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setCode(HttpStatus.BAD_REQUEST.value());

//		errorMessage.setMessage(exception.getBindingResult().getAllErrors().stream().map(ex -> ex.getDefaultMessage())
//				.collect(Collectors.joining(",")));
//		errorMessage.setMessage(exception.getConstraintViolations().forEach(voilation->voilation.getPropertyPath().toString().get));
		
		
		
		
		
		Map<String, String> errors = new HashMap<>();
		exception.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

//		errorMessage.setTimeStamp(LocalDateTime.now().toString());
//
//		return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> exception(Exception exception) {

		ErrorMessage errorMessage = new ErrorMessage();

		errorMessage.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		errorMessage.setMessage("Something went Wrong !!!!!! " + exception.getMessage());

		errorMessage.setTimeStamp(LocalDateTime.now().toString());

		return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
