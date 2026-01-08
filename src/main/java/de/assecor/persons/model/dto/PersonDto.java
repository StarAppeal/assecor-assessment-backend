package de.assecor.persons.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PersonDto(
    @JsonProperty("id") int id,
    @JsonProperty("name") String firstName,
    @JsonProperty("lastname") String lastName,
    @JsonProperty("zipcode") String zipCode,
    @JsonProperty("city") String city,
    @JsonProperty("color") int favoriteColor) {

  public static final String ZIP_CODE_REGEX = "\\d{5}";
}
