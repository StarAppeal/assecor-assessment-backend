package de.assecor.persons.model.document;

import de.assecor.persons.model.Color;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "persons")
public record PersonDocument(
    @Id int id,
    @Field(name = "firstName") String firstName,
    @Field(name = "lastName") String lastName,
    @Field(name = "zipCode") String zipCode,
    @Field(name = "city") String city,
    @Field(name = "favoriteColor") Color favoriteColor) {}
