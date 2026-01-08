package de.assecor.persons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import de.assecor.persons.model.document.SequenceDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@DisplayName("SequenceGeneratorService Tests")
@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceTest {

  @Mock private MongoOperations mongoOperations;

  private SequenceGeneratorService sequenceGeneratorService;

  @BeforeEach
  void setUp() {
    sequenceGeneratorService = new SequenceGeneratorService(mongoOperations);
  }

  @Nested
  @DisplayName("Generate Sequence Tests")
  class GenerateSequenceTests {

    @Test
    @DisplayName("Should return next sequence value")
    void shouldReturnNextSequenceValue() {
      SequenceDocument returnedDoc = new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 5);
      when(mongoOperations.findAndModify(
              any(Query.class),
              any(Update.class),
              any(FindAndModifyOptions.class),
              eq(SequenceDocument.class)))
          .thenReturn(returnedDoc);

      int result = sequenceGeneratorService.generateSequence(SequenceDocument.PERSON_SEQUENCE);

      assertEquals(5, result);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when counter is null")
    void shouldThrowExceptionWhenCounterIsNull() {
      when(mongoOperations.findAndModify(
              any(Query.class),
              any(Update.class),
              any(FindAndModifyOptions.class),
              eq(SequenceDocument.class)))
          .thenReturn(null);

      IllegalStateException exception =
          assertThrows(
              IllegalStateException.class,
              () -> sequenceGeneratorService.generateSequence(SequenceDocument.PERSON_SEQUENCE));

      assertEquals(
          "Failed to generate sequence for: " + SequenceDocument.PERSON_SEQUENCE,
          exception.getMessage());
    }

    @Test
    @DisplayName("Should use upsert option")
    void shouldUseUpsertOption() {
      SequenceDocument returnedDoc = new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 1);
      ArgumentCaptor<FindAndModifyOptions> optionsCaptor =
          ArgumentCaptor.forClass(FindAndModifyOptions.class);

      when(mongoOperations.findAndModify(
              any(Query.class),
              any(Update.class),
              optionsCaptor.capture(),
              eq(SequenceDocument.class)))
          .thenReturn(returnedDoc);

      sequenceGeneratorService.generateSequence(SequenceDocument.PERSON_SEQUENCE);

      FindAndModifyOptions capturedOptions = optionsCaptor.getValue();

      verify(mongoOperations)
          .findAndModify(
              any(Query.class),
              any(Update.class),
              any(FindAndModifyOptions.class),
              eq(SequenceDocument.class));
    }
  }

  @Nested
  @DisplayName("Initialize Sequence Tests")
  class InitializeSequenceTests {

    @Test
    @DisplayName("Should initialize sequence when not exists")
    void shouldInitializeSequenceWhenNotExists() {
      when(mongoOperations.findById(SequenceDocument.PERSON_SEQUENCE, SequenceDocument.class))
          .thenReturn(null);

      sequenceGeneratorService.initializeSequence(SequenceDocument.PERSON_SEQUENCE, 10);

      verify(mongoOperations).save(new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 10));
    }

    @Test
    @DisplayName("Should update sequence when new value is higher")
    void shouldUpdateSequenceWhenNewValueIsHigher() {
      SequenceDocument existingDoc = new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 5);
      when(mongoOperations.findById(SequenceDocument.PERSON_SEQUENCE, SequenceDocument.class))
          .thenReturn(existingDoc);

      sequenceGeneratorService.initializeSequence(SequenceDocument.PERSON_SEQUENCE, 10);

      verify(mongoOperations).save(new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 10));
    }

    @Test
    @DisplayName("Should not update sequence when existing value is higher")
    void shouldNotUpdateSequenceWhenExistingValueIsHigher() {
      SequenceDocument existingDoc = new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 15);
      when(mongoOperations.findById(SequenceDocument.PERSON_SEQUENCE, SequenceDocument.class))
          .thenReturn(existingDoc);

      sequenceGeneratorService.initializeSequence(SequenceDocument.PERSON_SEQUENCE, 10);

      verify(mongoOperations, never()).save(any(SequenceDocument.class));
    }

    @Test
    @DisplayName("Should update sequence when values are equal")
    void shouldNotUpdateSequenceWhenValuesAreEqual() {
      SequenceDocument existingDoc = new SequenceDocument(SequenceDocument.PERSON_SEQUENCE, 10);
      when(mongoOperations.findById(SequenceDocument.PERSON_SEQUENCE, SequenceDocument.class))
          .thenReturn(existingDoc);

      sequenceGeneratorService.initializeSequence(SequenceDocument.PERSON_SEQUENCE, 10);

      verify(mongoOperations, never()).save(any(SequenceDocument.class));
    }
  }
}
