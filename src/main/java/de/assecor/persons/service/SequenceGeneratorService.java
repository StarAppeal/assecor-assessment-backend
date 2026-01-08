package de.assecor.persons.service;

import de.assecor.persons.model.document.SequenceDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@Profile("mongo")
public class SequenceGeneratorService {

  private final MongoOperations mongoOperations;

  @Autowired
  public SequenceGeneratorService(MongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public int generateSequence(String sequenceName) {
    SequenceDocument counter =
        mongoOperations.findAndModify(
            Query.query(Criteria.where("_id").is(sequenceName)),
            new Update().inc("seq", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            SequenceDocument.class);

    if (counter == null) {
      throw new IllegalStateException("Failed to generate sequence for: " + sequenceName);
    }

    return counter.seq();
  }

  public void initializeSequence(String sequenceName, int value) {
    SequenceDocument existing = mongoOperations.findById(sequenceName, SequenceDocument.class);

    if (existing == null || existing.seq() < value) {
      mongoOperations.save(new SequenceDocument(sequenceName, value));
    }
  }
}
