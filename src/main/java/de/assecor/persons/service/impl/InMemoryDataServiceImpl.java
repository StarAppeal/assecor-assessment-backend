package de.assecor.persons.service.impl;

import de.assecor.persons.exception.InvalidColorException;
import de.assecor.persons.exception.PersonNotFoundException;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.service.DataService;
import de.assecor.persons.exception.DataLoadException;
import de.assecor.persons.service.initialdata.InitialDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Profile("!mongo")
public class InMemoryDataServiceImpl implements DataService {

  private static final Logger logger = LoggerFactory.getLogger(InMemoryDataServiceImpl.class);

  private final AtomicInteger nextId = new AtomicInteger(1);

  // Thread-safe in-memory store for person data
  private final List<PersonDto> persons = new CopyOnWriteArrayList<>();

  private final InitialDataProvider initialDataProvider;

  @Autowired(required = false)
  public InMemoryDataServiceImpl(InitialDataProvider initialDataProvider) {
    this.initialDataProvider = initialDataProvider;
  }

  @PostConstruct
  public void init() {
    if (!persons.isEmpty()) {
      logger.warn("InMemoryDataService initialized with non-empty dataset, skipping data load");
      return;
    }

    if (initialDataProvider != null && initialDataProvider.isAvailable()) {
      try {
        List<PersonDto> loadedPersons = initialDataProvider.loadData();
        for (PersonDto person : loadedPersons) {
          createPerson(person);
        }
        logger.info("Initialized InMemoryDataService with {} persons", persons.size());
      } catch (DataLoadException e) {
        logger.error("Failed to load initial data from data source", e);
      }
    } else {
      logger.info("Initialized InMemoryDataService with empty dataset");
    }
  }

  @Override
  public List<PersonDto> getAllPersons() {
    return Collections.unmodifiableList(persons);
  }

  @Override
  public PersonDto getPersonById(int id) {
    return persons.stream()
        .filter(person -> person.id() == id)
        .findFirst()
        .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + id));
  }

  @Override
  public PersonDto createPerson(PersonDto person) {
    if (person == null) {
      throw new IllegalArgumentException("Person cannot be null");
    }

    PersonDto newPerson =
        new PersonDto(
            nextId.getAndIncrement(),
            person.firstName(),
            person.lastName(),
            person.zipCode(),
            person.city(),
            person.favoriteColor());

    persons.add(newPerson);
    logger.debug("Created person with id: {}", newPerson.id());
    return newPerson;
  }

  @Override
  public PersonDto updatePerson(int id, PersonDto person) {
    if (person == null) {
      throw new IllegalArgumentException("Person cannot be null");
    }

    int index = -1;
    for (int i = 0; i < persons.size(); i++) {
      if (persons.get(i).id() == id) {
        index = i;
        break;
      }
    }

    if (index == -1) {
      throw new PersonNotFoundException("Person not found with id: " + id);
    }

    PersonDto updatedPerson =
        new PersonDto(
            id,
            person.firstName(),
            person.lastName(),
            person.zipCode(),
            person.city(),
            person.favoriteColor());

    persons.set(index, updatedPerson);
    logger.debug("Updated person with id: {}", id);
    return updatedPerson;
  }

  @Override
  public void deletePerson(int id) {
    boolean removed = persons.removeIf(person -> person.id() == id);

    if (!removed) {
      throw new PersonNotFoundException("Person not found with id: " + id);
    }

    logger.debug("Deleted person with id: {}", id);
  }

  @Override
  public List<PersonDto> getPersonsByColor(Color color) {
    if (color == null) {
      throw new InvalidColorException("Color cannot be null");
    }

    return persons.stream().filter(person -> person.favoriteColor() == color.getId()).toList();
  }

  // @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
  int size() {
    return persons.size();
  }

  // @VisibleForTesting(otherwise = VisibleForTesting.AccessModifier.PRIVATE)
  void clear() {
    persons.clear();
    nextId.set(1);
    logger.debug("Cleared all persons from memory");
  }
}
