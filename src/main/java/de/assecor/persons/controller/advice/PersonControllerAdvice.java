package de.assecor.persons.controller.advice;

import static de.assecor.persons.controller.advice.ControllerAdviceUtils.problem;

import de.assecor.persons.controller.PersonsController;
import de.assecor.persons.exception.InvalidColorException;
import de.assecor.persons.exception.PersonNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PersonsController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PersonControllerAdvice {

  private static final Logger logger = LoggerFactory.getLogger(PersonControllerAdvice.class);

  @ExceptionHandler(PersonNotFoundException.class)
  public ResponseEntity<ProblemDetail> handlePersonNotFound(PersonNotFoundException ex) {
    logger.warn("Person not found: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem(HttpStatus.NOT_FOUND, "Person Not Found", ex.getMessage()));
  }

  @ExceptionHandler(InvalidColorException.class)
  public ResponseEntity<ProblemDetail> handleInvalidColor(InvalidColorException ex) {
    logger.warn("Invalid color: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem(HttpStatus.BAD_REQUEST, "Invalid Color", ex.getMessage()));
  }
}
