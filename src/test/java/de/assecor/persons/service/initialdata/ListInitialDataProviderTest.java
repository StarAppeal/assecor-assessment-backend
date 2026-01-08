package de.assecor.persons.service.initialdata;

import static org.junit.jupiter.api.Assertions.*;

import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.model.dto.PersonDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ListInitialDataProvider Tests")
class ListInitialDataProviderTest {

  @Test
  @DisplayName("Should load data from list")
  void shouldLoadDataFromList() throws DataLoadException {
    List<PersonDto> data =
        Arrays.asList(
            new PersonDto(0, "John", "Doe", "12345", "City1", 1),
            new PersonDto(0, "Jane", "Smith", "67890", "City2", 2));

    ListInitialDataProvider loader = new ListInitialDataProvider(data);
    List<PersonDto> loadedData = loader.loadData();

    assertEquals(2, loadedData.size());
    assertEquals("John", loadedData.get(0).firstName());
    assertEquals("Jane", loadedData.get(1).firstName());
  }

  @Test
  @DisplayName("Should return copy of the data")
  void shouldReturnCopyOfTheData() throws DataLoadException {
    List<PersonDto> data =
        new ArrayList<>(List.of(new PersonDto(0, "John", "Doe", "12345", "City1", 1)));

    ListInitialDataProvider loader = new ListInitialDataProvider(data);
    List<PersonDto> loadedData1 = loader.loadData();
    List<PersonDto> loadedData2 = loader.loadData();

    assertNotSame(loadedData1, loadedData2);
    assertEquals(loadedData1.size(), loadedData2.size());
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() throws DataLoadException {
    ListInitialDataProvider loader = new ListInitialDataProvider(new ArrayList<>());
    List<PersonDto> loadedData = loader.loadData();

    assertNotNull(loadedData);
    assertTrue(loadedData.isEmpty());
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() throws DataLoadException {
    ListInitialDataProvider loader = new ListInitialDataProvider(null);
    List<PersonDto> loadedData = loader.loadData();

    assertNotNull(loadedData);
    assertTrue(loadedData.isEmpty());
  }

  @Test
  @DisplayName("Should return false for isAvailable when list is empty")
  void shouldReturnFalseForIsAvailableWhenListIsEmpty() {
    ListInitialDataProvider loader = new ListInitialDataProvider(new ArrayList<>());
    assertFalse(loader.isAvailable());
  }

  @Test
  @DisplayName("Should return false for isAvailable when list is null")
  void shouldReturnFalseForIsAvailableWhenListIsNull() {
    ListInitialDataProvider loader = new ListInitialDataProvider(null);
    assertFalse(loader.isAvailable());
  }

  @Test
  @DisplayName("Should return true for isAvailable when list has data")
  void shouldReturnTrueForIsAvailableWhenListHasData() {
    List<PersonDto> data = List.of(new PersonDto(0, "John", "Doe", "12345", "City1", 1));

    ListInitialDataProvider loader = new ListInitialDataProvider(data);
    assertTrue(loader.isAvailable());
  }

  @Test
  @DisplayName("Should not modify original list")
  void shouldNotModifyOriginalList() throws DataLoadException {
    List<PersonDto> originalData =
        new ArrayList<>(
            Arrays.asList(
                new PersonDto(0, "John", "Doe", "12345", "City1", 1),
                new PersonDto(0, "Jane", "Smith", "67890", "City2", 2)));

    ListInitialDataProvider loader = new ListInitialDataProvider(originalData);

    originalData.add(new PersonDto(0, "Bob", "Johnson", "11111", "City3", 3));

    List<PersonDto> loadedData = loader.loadData();

    assertEquals(2, loadedData.size());
  }
}
