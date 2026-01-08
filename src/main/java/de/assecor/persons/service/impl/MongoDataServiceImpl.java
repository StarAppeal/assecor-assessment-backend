package de.assecor.persons.service.impl;

import de.assecor.persons.exception.InvalidColorException;
import de.assecor.persons.exception.PersonNotFoundException;
import de.assecor.persons.model.Color;
import de.assecor.persons.model.document.PersonDocument;
import de.assecor.persons.model.dto.PersonDto;
import de.assecor.persons.model.mapper.PersonMapper;
import de.assecor.persons.repository.PersonMongoRepository;
import de.assecor.persons.service.SequenceGeneratorService;
import de.assecor.persons.service.DataService;
import de.assecor.persons.service.initialdata.InitialDataProvider;
import de.assecor.persons.model.document.SequenceDocument;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mongo")
public class MongoDataServiceImpl implements DataService {

  private static final Logger logger = LoggerFactory.getLogger(MongoDataServiceImpl.class);
  private final PersonMongoRepository repository;
  private final InitialDataProvider initialDataProvider;
  private final SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  public MongoDataServiceImpl(
      PersonMongoRepository repository,
      InitialDataProvider initialDataProvider,
      SequenceGeneratorService sequenceGeneratorService) {
    this.repository = repository;
    this.initialDataProvider = initialDataProvider;
    this.sequenceGeneratorService = sequenceGeneratorService;
  }

  @PostConstruct
  public void init() {
    if (repository.count() > 0) {
      logger.info("MongoDB already contains data.");
      initializeSequenceFromExistingData();
      return;
    }
    if (initialDataProvider != null && initialDataProvider.isAvailable()) {
      logger.info("Importing initial data into MongoDB...");
      try {
        List<PersonDto> initialData = initialDataProvider.loadData();
        initialData.forEach(this::createPerson);
        logger.info("Imported {} persons into MongoDB.", initialData.size());
      } catch (Exception e) {
        logger.error("Error importing initial data", e);
      }
    }
  }

  private void initializeSequenceFromExistingData() {
    repository
        .findTopByOrderByIdDesc()
        .map(PersonDocument::id)
        .ifPresent(
            maxId ->
                sequenceGeneratorService.initializeSequence(
                    SequenceDocument.PERSON_SEQUENCE, maxId));
  }

  @Override
  public List<PersonDto> getAllPersons() {
    return repository.findAll().stream().map(PersonMapper::toDto).toList();
  }

  @Override
  public PersonDto getPersonById(int id) {
    return repository
        .findById(id)
        .map(PersonMapper::toDto)
        .orElseThrow(() -> new PersonNotFoundException("Person with id " + id + " not found"));
  }

  @Override
  public PersonDto createPerson(PersonDto person) {
    int newId = sequenceGeneratorService.generateSequence(SequenceDocument.PERSON_SEQUENCE);

    PersonDocument doc = PersonMapper.toDocumentWithId(newId, person);

    PersonDocument savedDoc = repository.save(doc);
    logger.debug("Created Mongo Person with ID {}", savedDoc.id());

    return PersonMapper.toDto(savedDoc);
  }

  @Override
  public PersonDto updatePerson(int id, PersonDto person) {
    if (!repository.existsById(id)) {
      throw new PersonNotFoundException("Person with id " + id + " not found for update");
    }
    PersonDocument doc = PersonMapper.toDocumentWithId(id, person);
    PersonDocument updatedDoc = repository.save(doc);
    logger.debug("Updated Mongo Person with ID {}", updatedDoc.id());

    return PersonMapper.toDto(updatedDoc);
  }

  @Override
  public void deletePerson(int id) {
    if (!repository.existsById(id)) {
      throw new PersonNotFoundException("Person with id " + id + " not found for deletion");
    }
    repository.deleteById(id);
    logger.debug("Deleted Mongo Person with ID {}", id);
  }

  @Override
  public List<PersonDto> getPersonsByColor(Color color) {
    if (color == null) {
      throw new InvalidColorException("Color cannot be null");
    }

    return repository.findByFavoriteColor(color).stream().map(PersonMapper::toDto).toList();
  }
}
