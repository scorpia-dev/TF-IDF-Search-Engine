package searchEngine.exceptions;

import java.util.ArrayList;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	
	@ExceptionHandler(HttpMessageConversionException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<?> handleNotFoundException(HttpMessageConversionException e) {
		return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<?> handleNotFoundException(EntityNotFoundException e) {
		return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
		ArrayList<String> errors = new ArrayList<String>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
					+ violation.getMessage());
		}
		return new ResponseEntity<>("not valid due to validation error: " + errors, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
