package de.assecor.persons.service.initialdata;

import static org.junit.jupiter.api.Assertions.*;

import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.model.dto.PersonDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@DisplayName("CsvInitialDataProvider Tests")
class CsvInitialDataProviderTest {

  @Test
  @DisplayName("Should load valid CSV data")
  void shouldLoadValidCsvData() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/valid-data.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(3, persons.size());

    PersonDto firstPerson = persons.getFirst();
    assertEquals("Hans", firstPerson.firstName());
    assertEquals("Müller", firstPerson.lastName());
    assertEquals("67742", firstPerson.zipCode());
    assertEquals("Lauterecken", firstPerson.city());
    assertEquals(1, firstPerson.favoriteColor());

    PersonDto secondPerson = persons.get(1);
    assertEquals("Johnny", secondPerson.firstName());
    assertEquals("Johnson", secondPerson.lastName());
    assertEquals("88888", secondPerson.zipCode());
    assertEquals("Made-Up-City", secondPerson.city());
    assertEquals(3, secondPerson.favoriteColor());
  }

  @Test
  @DisplayName("Should handle empty CSV file")
  void shouldHandleEmptyCsvFile() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/empty.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertNotNull(persons);
    assertTrue(persons.isEmpty());
  }

  @Test
  @DisplayName("Should skip invalid lines and continue processing")
  void shouldSkipInvalidLinesAndContinueProcessing() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/mixed-data.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(3, persons.size());
    assertEquals("Hans", persons.getFirst().firstName());
    assertEquals("Peter", persons.get(1).firstName());
    assertEquals("John", persons.get(2).firstName());
  }

  @Test
  @DisplayName("Should handle city names with special characters")
  void shouldHandleCityNamesWithSpecialCharacters() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/special-chars.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(2, persons.size());
    assertEquals("Schweden-Örebro", persons.get(0).city());
    assertEquals("Paris-Élysées", persons.get(1).city());
  }

  @Test
  @DisplayName("Should skip lines with invalid color IDs")
  void shouldSkipLinesWithInvalidColorIds() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/invalid-colors.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(2, persons.size());
    assertEquals("Müller", persons.get(0).lastName());
    assertEquals("Petersen", persons.get(1).lastName());
  }

  @Test
  @DisplayName("Should return empty list when resource is null")
  void shouldReturnEmptyListWhenResourceIsNull() throws DataLoadException {
    CsvInitialDataProvider loader = new CsvInitialDataProvider(null);

    List<PersonDto> persons = loader.loadData();

    assertNotNull(persons);
    assertTrue(persons.isEmpty());
  }

  @Test
  @DisplayName("Should return false for isAvailable when resource is null")
  void shouldReturnFalseForIsAvailableWhenResourceIsNull() {
    CsvInitialDataProvider loader = new CsvInitialDataProvider(null);

    assertFalse(loader.isAvailable());
  }

  @Test
  @DisplayName("Should return true for isAvailable when resource exists")
  void shouldReturnTrueForIsAvailableWhenResourceExists() {
    Resource resource = new ClassPathResource("csv/valid-data.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    assertTrue(loader.isAvailable());
  }

  @Test
  @DisplayName("Should use default constructor")
  void shouldUseDefaultConstructor() throws DataLoadException {
    CsvInitialDataProvider loader = new CsvInitialDataProvider();

    assertFalse(loader.isAvailable());
    List<PersonDto> persons = loader.loadData();
    assertTrue(persons.isEmpty());
  }

  @Test
  @DisplayName("Should handle blank lines in CSV")
  void shouldHandleBlankLinesInCsv() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/blank-lines.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(2, persons.size());
  }

  @Test
  @DisplayName("Should validate all Color IDs")
  void shouldValidateAllColorIds() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/all-colors.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(7, persons.size());
    for (int i = 0; i < 7; i++) {
      assertEquals(i + 1, persons.get(i).favoriteColor());
    }
  }

  @Test
  @DisplayName("Should handle multi-word city names")
  void shouldHandleMultiWordCityNames() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/multi-word-cities.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertEquals(2, persons.size());
    assertEquals("New York City", persons.get(0).city());
    assertEquals("Los Angeles Downtown", persons.get(1).city());
  }

  @Test
  @DisplayName("Should return empty list when file does not exist")
  void shouldReturnEmptyListWhenFileDoesNotExist() throws DataLoadException {
    Resource resource = new ClassPathResource("csv/non-existent.csv");
    CsvInitialDataProvider loader = new CsvInitialDataProvider(resource);

    List<PersonDto> persons = loader.loadData();

    assertNotNull(persons);
    assertTrue(persons.isEmpty());
  }
}
