package de.assecor.persons.service;

import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;
import java.util.List;

public interface DataService {
  List<PersonDto> getAllPersons();

  PersonDto getPersonById(int id);

  PersonDto createPerson(PersonDto person);

  PersonDto updatePerson(int id, PersonDto person);

  void deletePerson(int id);

  List<PersonDto> getPersonsByColor(Color color);
}
