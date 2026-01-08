package de.assecor.persons.controller;

import de.assecor.persons.controller.request.CreatePersonRequest;
import de.assecor.persons.controller.request.UpdatePersonRequest;
import de.assecor.persons.controller.response.PersonResponse;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.model.mapper.PersonApiMapper;
import de.assecor.persons.service.DataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/persons")
public class PersonsController {

  private static final Logger logger = LoggerFactory.getLogger(PersonsController.class);

  private final DataService service;

  @Autowired
  public PersonsController(DataService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<PersonResponse>> getAll() {
    logger.info("GET /persons called");
    List<PersonDto> result = service.getAllPersons();
    return ResponseEntity.ok(PersonApiMapper.toResponseList(result));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonResponse> getById(@PathVariable("id") int id) {
    logger.info("GET /persons/{} called", id);
    PersonDto person = service.getPersonById(id);
    return ResponseEntity.ok(PersonApiMapper.toResponse(person));
  }

  @GetMapping("/color/{color}")
  public ResponseEntity<List<PersonResponse>> getByColor(@PathVariable("color") String color) {
    logger.info("GET /persons/color/{} called", color);
    List<PersonDto> persons = service.getPersonsByColor(Color.fromName(color));
    return ResponseEntity.ok(PersonApiMapper.toResponseList(persons));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PersonResponse> addPerson(
      @RequestBody @Valid CreatePersonRequest personRequest) {
    logger.info("POST /persons/ called with personRequest: {}", personRequest);

    PersonDto input = PersonApiMapper.toDto(personRequest);

    PersonDto savedPerson = service.createPerson(input);

    return ResponseEntity.status(HttpStatus.CREATED).body(PersonApiMapper.toResponse(savedPerson));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PersonResponse> updatePerson(
      @PathVariable("id") int id, @RequestBody @Valid UpdatePersonRequest request) {
    logger.info("PUT /persons/{} called with person: {}", id, request);

    PersonDto input = PersonApiMapper.toDto(id, request);

    PersonDto updatedPerson = service.updatePerson(id, input);

    return ResponseEntity.ok(PersonApiMapper.toResponse(updatedPerson));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePerson(@PathVariable("id") int id) {
    logger.info("DELETE /persons/{} called", id);
    service.deletePerson(id);
    return ResponseEntity.noContent().build();
  }
}
