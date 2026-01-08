package de.assecor.persons.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.assecor.persons.model.Color;

public record PersonResponse(
    @JsonProperty("id") int id,
    @JsonProperty("name") String firstName,
    @JsonProperty("lastname") String lastName,
    @JsonProperty("zipcode") String zipCode,
    @JsonProperty("city") String city,
    @JsonProperty("color") Color favoriteColor) {}
