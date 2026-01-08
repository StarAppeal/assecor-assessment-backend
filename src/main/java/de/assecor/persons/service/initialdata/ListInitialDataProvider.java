package de.assecor.persons.service.initialdata;

import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.model.dto.PersonDto;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListInitialDataProvider implements InitialDataProvider {

  private static final Logger logger = LoggerFactory.getLogger(ListInitialDataProvider.class);

  private final List<PersonDto> data;

  public ListInitialDataProvider(List<PersonDto> data) {
    this.data = data != null ? new ArrayList<>(data) : new ArrayList<>();
  }

  @Override
  public List<PersonDto> loadData() throws DataLoadException {
    logger.info("Loading {} persons from list data source", data.size());
    return new ArrayList<>(data);
  }

  @Override
  public boolean isAvailable() {
    return !data.isEmpty();
  }
}
