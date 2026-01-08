package de.assecor.persons.service.impl;

import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.exception.InvalidColorException;
import de.assecor.persons.exception.PersonNotFoundException;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.document.PersonDocument;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.repository.PersonMongoRepository;
import de.assecor.persons.service.SequenceGeneratorService;
import de.assecor.persons.service.initialdata.InitialDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("MongoDataService Tests")
@ExtendWith(MockitoExtension.class)
class MongoDataServiceImplTest {

  @Mock private PersonMongoRepository repository;

  @Mock private InitialDataProvider initialDataProvider;

  @Mock private SequenceGeneratorService sequenceGeneratorService;

  private MongoDataServiceImpl dataService;

  @BeforeEach
  void setUp() {
    dataService =
        new MongoDataServiceImpl(repository, initialDataProvider, sequenceGeneratorService);
  }

  @Nested
  @DisplayName("Init Tests")
  class InitTests {

    @Test
    @DisplayName("Should skip import when database already contains data")
    void shouldSkipImportWhenDatabaseContainsData() throws DataLoadException {
      when(repository.count()).thenReturn(5L);

      dataService.init();

      verify(repository, never()).save(any());
      verify(initialDataProvider, never()).loadData();
    }

    @Test
    @DisplayName("Should import initial data when database is empty")
    void shouldImportInitialDataWhenDatabaseIsEmpty() throws DataLoadException {
      when(repository.count()).thenReturn(0L);
      when(initialDataProvider.isAvailable()).thenReturn(true);
      when(initialDataProvider.loadData())
          .thenReturn(List.of(new PersonDto(0, "John", "Doe", "12345", "City", 1)));
      when(sequenceGeneratorService.generateSequence(any())).thenReturn(1);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      dataService.init();

      verify(repository).save(any(PersonDocument.class));
    }

    @Test
    @DisplayName("Should handle null initial data provider")
    void shouldHandleNullInitialDataProvider() {
      MongoDataServiceImpl serviceWithNullProvider =
          new MongoDataServiceImpl(repository, null, sequenceGeneratorService);
      when(repository.count()).thenReturn(0L);

      assertDoesNotThrow(serviceWithNullProvider::init);
    }

    @Test
    @DisplayName("Should skip import when initial data provider not available")
    void shouldSkipImportWhenInitialDataProviderNotAvailable() throws DataLoadException {
      when(repository.count()).thenReturn(0L);
      when(initialDataProvider.isAvailable()).thenReturn(false);

      dataService.init();

      verify(initialDataProvider, never()).loadData();
    }

    @Test
    @DisplayName("Should handle exception during data import")
    void shouldHandleExceptionDuringDataImport() throws DataLoadException {
      when(repository.count()).thenReturn(0L);
      when(initialDataProvider.isAvailable()).thenReturn(true);
      when(initialDataProvider.loadData()).thenThrow(new RuntimeException("Import error"));

      assertDoesNotThrow(() -> dataService.init());
    }
  }

  @Nested
  @DisplayName("Get All Persons Tests")
  class GetAllPersonsTests {

    @Test
    @DisplayName("Should return all persons")
    void shouldReturnAllPersons() {
      List<PersonDocument> documents =
          List.of(
              new PersonDocument(1, "John", "Doe", "12345", "City1", Color.BLUE),
              new PersonDocument(2, "Jane", "Smith", "67890", "City2", Color.GREEN));
      when(repository.findAll()).thenReturn(documents);

      List<PersonDto> result = dataService.getAllPersons();

      assertEquals(2, result.size());
      assertEquals("John", result.get(0).firstName());
      assertEquals("Jane", result.get(1).firstName());
    }

    @Test
    @DisplayName("Should return empty list when no persons exist")
    void shouldReturnEmptyListWhenNoPersonsExist() {
      when(repository.findAll()).thenReturn(List.of());

      List<PersonDto> result = dataService.getAllPersons();

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }
  }

  @Nested
  @DisplayName("Get Person By Id Tests")
  class GetPersonByIdTests {

    @Test
    @DisplayName("Should return person when found")
    void shouldReturnPersonWhenFound() {
      PersonDocument document = new PersonDocument(1, "John", "Doe", "12345", "City", Color.BLUE);
      when(repository.findById(1)).thenReturn(Optional.of(document));

      PersonDto result = dataService.getPersonById(1);

      assertNotNull(result);
      assertEquals(1, result.id());
      assertEquals("John", result.firstName());
      assertEquals("Doe", result.lastName());
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when person not found")
    void shouldThrowExceptionWhenPersonNotFound() {
      when(repository.findById(999)).thenReturn(Optional.empty());

      assertThrows(PersonNotFoundException.class, () -> dataService.getPersonById(999));
    }
  }

  @Nested
  @DisplayName("Create Person Tests")
  class CreatePersonTests {

    @Test
    @DisplayName("Should create person with new ID when repository is empty")
    void shouldCreatePersonWithNewIdWhenRepositoryIsEmpty() {
      PersonDto personToCreate = new PersonDto(0, "John", "Doe", "12345", "City", 1);
      when(sequenceGeneratorService.generateSequence(any())).thenReturn(1);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      PersonDto result = dataService.createPerson(personToCreate);

      assertNotNull(result);
      assertEquals(1, result.id());
      assertEquals("John", result.firstName());
      verify(repository).save(any(PersonDocument.class));
    }

    @Test
    @DisplayName("Should create person with incremented ID")
    void shouldCreatePersonWithIncrementedId() {
      PersonDto personToCreate = new PersonDto(0, "Jane", "Smith", "67890", "City", 2);
      when(sequenceGeneratorService.generateSequence(any())).thenReturn(6);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      PersonDto result = dataService.createPerson(personToCreate);

      assertNotNull(result);
      assertEquals(6, result.id());
      assertEquals("Jane", result.firstName());
    }

    @Test
    @DisplayName("Should save person to repository")
    void shouldSavePersonToRepository() {
      PersonDto personToCreate = new PersonDto(0, "John", "Doe", "12345", "City", 1);
      when(sequenceGeneratorService.generateSequence(any())).thenReturn(1);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      dataService.createPerson(personToCreate);

      verify(repository)
          .save(
              argThat(
                  doc ->
                      doc.firstName().equals("John")
                          && doc.lastName().equals("Doe")
                          && doc.zipCode().equals("12345")
                          && doc.city().equals("City")
                          && doc.favoriteColor() == Color.BLUE));
    }
  }

  @Nested
  @DisplayName("Update Person Tests")
  class UpdatePersonTests {

    @Test
    @DisplayName("Should update existing person")
    void shouldUpdateExistingPerson() {
      PersonDto updateData = new PersonDto(0, "Jane", "Smith", "67890", "NewCity", 2);
      when(repository.existsById(1)).thenReturn(true);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      PersonDto result = dataService.updatePerson(1, updateData);

      assertNotNull(result);
      assertEquals(1, result.id());
      assertEquals("Jane", result.firstName());
      assertEquals("Smith", result.lastName());
      assertEquals("67890", result.zipCode());
      assertEquals("NewCity", result.city());
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when updating non-existent person")
    void shouldThrowExceptionWhenUpdatingNonExistentPerson() {
      PersonDto updateData = new PersonDto(0, "Jane", "Smith", "67890", "City", 2);
      when(repository.existsById(999)).thenReturn(false);

      assertThrows(PersonNotFoundException.class, () -> dataService.updatePerson(999, updateData));
      verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should preserve ID during update")
    void shouldPreserveIdDuringUpdate() {
      PersonDto updateData = new PersonDto(99, "Jane", "Smith", "67890", "City", 2);
      when(repository.existsById(5)).thenReturn(true);
      when(repository.save(any(PersonDocument.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      PersonDto result = dataService.updatePerson(5, updateData);

      assertEquals(5, result.id());
      verify(repository).save(argThat(doc -> doc.id() == 5));
    }
  }

  @Nested
  @DisplayName("Delete Person Tests")
  class DeletePersonTests {

    @Test
    @DisplayName("Should delete existing person")
    void shouldDeleteExistingPerson() {
      when(repository.existsById(1)).thenReturn(true);

      dataService.deletePerson(1);

      verify(repository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw PersonNotFoundException when deleting non-existent person")
    void shouldThrowExceptionWhenDeletingNonExistentPerson() {
      when(repository.existsById(999)).thenReturn(false);

      assertThrows(PersonNotFoundException.class, () -> dataService.deletePerson(999));
      verify(repository, never()).deleteById(any());
    }
  }

  @Nested
  @DisplayName("Get Persons By Color Tests")
  class GetPersonsByColorTests {

    @Test
    @DisplayName("Should return persons with specified color")
    void shouldReturnPersonsWithSpecifiedColor() {
      List<PersonDocument> documents =
          List.of(
              new PersonDocument(1, "John", "Doe", "12345", "City1", Color.BLUE),
              new PersonDocument(3, "Alice", "Brown", "11111", "City3", Color.BLUE));
      when(repository.findByFavoriteColor(Color.BLUE)).thenReturn(documents);

      List<PersonDto> result = dataService.getPersonsByColor(Color.BLUE);

      assertEquals(2, result.size());
      assertTrue(result.stream().allMatch(p -> p.favoriteColor() == 1));
    }

    @Test
    @DisplayName("Should return empty list when no persons with color exist")
    void shouldReturnEmptyListWhenNoPersonsWithColorExist() {
      when(repository.findByFavoriteColor(Color.VIOLET)).thenReturn(List.of());

      List<PersonDto> result = dataService.getPersonsByColor(Color.VIOLET);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw InvalidColorException when color is null")
    void shouldThrowExceptionWhenColorIsNull() {
      assertThrows(InvalidColorException.class, () -> dataService.getPersonsByColor(null));
      verify(repository, never()).findByFavoriteColor(any());
    }
  }
}
