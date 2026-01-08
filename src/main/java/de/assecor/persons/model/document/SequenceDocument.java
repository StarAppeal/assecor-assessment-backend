package de.assecor.persons.model.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequences")
public record SequenceDocument(@Id String id, int seq) {
  public static final String PERSON_SEQUENCE = "person_sequence";
}
