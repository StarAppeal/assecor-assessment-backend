package de.assecor.persons.controller;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import de.assecor.persons.exception.InvalidColorException;
import de.assecor.persons.exception.PersonNotFoundException;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.service.DataService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest
@DisplayName("PersonsController Test")
class PersonsControllerTest {

  private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private DataService dataService;

  @Test
  @DisplayName("GET /persons - should return all persons")
  void getAll() throws Exception {
    when(dataService.getAllPersons())
        .thenReturn(
            List.of(
                person(1, "Max", "Mustermann", "10115", "Berlin", 1),
                person(2, "Anna", "Schmidt", "20095", "Hamburg", 2)));

    mockMvc
        .perform(get("/persons"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("Max"))
        .andExpect(jsonPath("$[1].lastname").value("Schmidt"));

    verify(dataService).getAllPersons();
  }

  @Test
  void getAllEmpty() throws Exception {
    when(dataService.getAllPersons()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/persons"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

    verify(dataService).getAllPersons();
  }

  @Test
  void getById() throws Exception {
    when(dataService.getPersonById(1))
        .thenReturn(person(1, "Max", "Mustermann", "10115", "Berlin", 1));

    mockMvc
        .perform(get("/persons/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.color").value(Color.BLUE.getName()));

    verify(dataService).getPersonById(1);
  }

  @Test
  void getByIdNotFound() throws Exception {
    when(dataService.getPersonById(999))
        .thenThrow(new PersonNotFoundException("Person with ID 999 not found."));

    mockMvc
        .perform(get("/persons/999"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Person Not Found"));

    verify(dataService).getPersonById(999);
  }

  @Test
  void getByColor() throws Exception {
    when(dataService.getPersonsByColor(Color.BLUE))
        .thenReturn(
            List.of(
                person(1, "Max", "Mustermann", "10115", "Berlin", 1),
                person(3, "Julia", "Meyer", "30159", "Hannover", 1)));

    mockMvc
        .perform(get("/persons/color/blau"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].color").value(Color.BLUE.getName()));

    verify(dataService).getPersonsByColor(Color.BLUE);
  }

  @Test
  void getByColorInvalid() throws Exception {
    when(dataService.getPersonsByColor(any()))
        .thenThrow(new InvalidColorException("Invalid Color name: pink"));

    mockMvc
        .perform(get("/persons/color/pink"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Invalid Color"));
  }

  @Test
  void addPerson() throws Exception {
    when(dataService.createPerson(any()))
        .thenReturn(person(1, "Max", "Mustermann", "10115", "Berlin", 1));

    mockMvc
        .perform(
            post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(person(0, "Max", "Mustermann", "10115", "Berlin", 1))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1));

    verify(dataService).createPerson(any());
  }

  @Test
  void updatePerson() throws Exception {
    when(dataService.updatePerson(eq(1), any()))
        .thenReturn(person(1, "Max", "Mustermann", "20095", "Hamburg", 2));

    mockMvc
        .perform(
            put("/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(person(1, "Max", "Mustermann", "20095", "Hamburg", 2))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.color").value(Color.GREEN.getName()));

    verify(dataService).updatePerson(eq(1), any());
  }

  @Test
  void deletePerson() throws Exception {
    doNothing().when(dataService).deletePerson(1);

    mockMvc.perform(delete("/persons/1")).andExpect(status().isNoContent());

    verify(dataService).deletePerson(1);
  }

  @Test
  void deletePersonNotFound() throws Exception {
    doThrow(new PersonNotFoundException("Person with id 999 not found"))
        .when(dataService)
        .deletePerson(999);

    mockMvc
        .perform(delete("/persons/999"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(PROBLEM_JSON));

    verify(dataService).deletePerson(999);
  }

  @Test
  void validationSingleError() throws Exception {
    assertValidationError(
        person(1, "", "Mustermann", "10115", "Berlin", 1), "name", "must not be blank");
  }

  @Test
  void validationMultipleErrors() throws Exception {
    mockMvc
        .perform(
            post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(person(0, "", "", "abc", "", 10))))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(PROBLEM_JSON))
        .andExpect(jsonPath("$.errors", aMapWithSize(5)));
  }

  private void assertValidationError(PersonDto person, String field, String message)
      throws Exception {
    mockMvc
        .perform(post("/persons").contentType(MediaType.APPLICATION_JSON).content(json(person)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(PROBLEM_JSON))
        .andExpect(jsonPath("$.detail").value("One or more fields are invalid"))
        .andExpect(jsonPath("$.errors." + field).value(message));

    verify(dataService, never()).createPerson(any());
  }

  private PersonDto person(int id, String first, String last, String zip, String city, int color) {
    return new PersonDto(id, first, last, zip, city, color);
  }

  private String json(Object o) throws Exception {
    return objectMapper.writeValueAsString(o);
  }
}
