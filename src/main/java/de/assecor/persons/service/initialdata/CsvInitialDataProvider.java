package de.assecor.persons.service.initialdata;

import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.exception.DataLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvInitialDataProvider implements InitialDataProvider {

  private static final Logger logger = LoggerFactory.getLogger(CsvInitialDataProvider.class);

  private final Resource csvResource;

  public CsvInitialDataProvider(Resource csvResource) {
    this.csvResource = csvResource;
  }

  public CsvInitialDataProvider() {
    this.csvResource = null;
  }

  @Override
  public List<PersonDto> loadData() throws DataLoadException {
    if (csvResource == null || !csvResource.exists()) {
      logger.info("No CSV resource available, returning empty list");
      return new ArrayList<>();
    }

    List<PersonDto> persons = new ArrayList<>();
    int lineNumber = 0;

    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8))) {

      String line;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        try {
          PersonDto person = parseCsvLine(line, lineNumber);
          if (person != null) {
            persons.add(person);
          }
        } catch (Exception e) {
          logger.warn("Failed to parse line {}: {}. Error: {}", lineNumber, line, e.getMessage());
          // continue processing other lines
        }
      }

      logger.info("Successfully loaded {} persons from CSV", persons.size());
      return persons;

    } catch (IOException e) {
      throw new DataLoadException("Failed to read CSV file", e);
    }
  }

  @Override
  public boolean isAvailable() {
    return csvResource != null && csvResource.exists();
  }

  private PersonDto parseCsvLine(String line, int lineNumber) {
    if (line == null || line.trim().isEmpty()) {
      return null;
    }

    String[] parts = line.split(",");

    if (parts.length < 4) {
      logger.warn("Line {} has insufficient fields: {}", lineNumber, line);
      return null;
    }

    String lastName = parts[0].trim();
    String firstName = parts[1].trim();
    String zipAndCity = parts[2].trim();
    String colorIdStr = parts[3].trim();

    String[] zipCityParts = zipAndCity.split("\\s+", 2);
    if (zipCityParts.length < 2) {
      logger.warn("Line {} has invalid zip/city format: {}", lineNumber, zipAndCity);
      return null;
    }

    String zipCode = zipCityParts[0].trim();

    if (!zipCode.matches(PersonDto.ZIP_CODE_REGEX)) {
      logger.warn("Line {} has invalid zip code: {}", lineNumber, zipCityParts[0]);
      return null;
    }

    String city = zipCityParts[1].trim();

    int colorId;
    try {
      colorId = Integer.parseInt(colorIdStr);
      Color.fromId(colorId);
    } catch (IllegalArgumentException e) {
      logger.warn("Line {} has invalid color ID: {}", lineNumber, colorIdStr);
      return null;
    }

    return new PersonDto(0, firstName, lastName, zipCode, city, colorId);
  }
}
