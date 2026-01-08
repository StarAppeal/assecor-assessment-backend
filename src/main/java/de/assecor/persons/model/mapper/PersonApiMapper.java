package de.assecor.persons.model.mapper;

import de.assecor.persons.controller.request.CreatePersonRequest;
import de.assecor.persons.controller.request.UpdatePersonRequest;
import de.assecor.persons.controller.response.PersonResponse;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;

import java.util.List;

public class PersonApiMapper {

  private PersonApiMapper() {
    // private constructor to prevent instantiation
  }

  public static PersonDto toDto(CreatePersonRequest request) {
    return new PersonDto(
        0, request.name(), request.lastname(), request.zipcode(), request.city(), request.color());
  }

  public static PersonDto toDto(int id, UpdatePersonRequest request) {
    return new PersonDto(
        id, request.name(), request.lastname(), request.zipcode(), request.city(), request.color());
  }

  public static PersonResponse toResponse(PersonDto dto) {
    return new PersonResponse(
        dto.id(),
        dto.firstName(),
        dto.lastName(),
        dto.zipCode(),
        dto.city(),
        Color.fromId(dto.favoriteColor()));
  }

  public static List<PersonResponse> toResponseList(List<PersonDto> dtos) {
    return dtos.stream().map(PersonApiMapper::toResponse).toList();
  }
}
