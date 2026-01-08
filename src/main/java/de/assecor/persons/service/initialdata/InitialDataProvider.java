package de.assecor.persons.service.initialdata;

import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.model.dto.PersonDto;
import java.util.List;

public interface InitialDataProvider {

  List<PersonDto> loadData() throws DataLoadException;

  boolean isAvailable();
}
