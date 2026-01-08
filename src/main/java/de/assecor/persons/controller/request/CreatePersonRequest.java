package de.assecor.persons.controller.request;

import de.assecor.persons.model.dto.PersonDto;
import jakarta.validation.constraints.*;

public record CreatePersonRequest(
    @NotBlank String name,
    @NotBlank String lastname,
    @Pattern(regexp = PersonDto.ZIP_CODE_REGEX, message = "must be a valid german zipcode") @NotNull
        String zipcode,
    @NotBlank String city,
    @Min(value = 1) @Max(value = 7) int color) {}
