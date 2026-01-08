package de.assecor.persons.model.mapper;

import de.assecor.persons.model.Color;
import de.assecor.persons.model.document.PersonDocument;
import de.assecor.persons.model.dto.PersonDto;

public class PersonMapper {

  private PersonMapper() {
    // private constructor to prevent instantiation
  }

  public static PersonDto toDto(PersonDocument person) {
    if (person == null) {
      return null;
    }
    return new PersonDto(
        person.id(),
        person.firstName(),
        person.lastName(),
        person.zipCode(),
        person.city(),
        person.favoriteColor().getId());
  }

  public static PersonDocument toDocumentWithId(int id, PersonDto dto) {
    if (dto == null) return null;
    return new PersonDocument(
        id,
        dto.firstName(),
        dto.lastName(),
        dto.zipCode(),
        dto.city(),
        Color.fromId(dto.favoriteColor()));
  }
}
