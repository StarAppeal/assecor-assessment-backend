package de.assecor.persons.exception;

public class PersonNotFoundException extends RuntimeException {

  public PersonNotFoundException(String message) {
    super(message);
  }
}
