package de.assecor.persons.repository;

import de.assecor.persons.model.Color;
import de.assecor.persons.model.document.PersonDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonMongoRepository extends MongoRepository<PersonDocument, Integer> {

  Optional<PersonDocument> findTopByOrderByIdDesc();

  List<PersonDocument> findByFavoriteColor(Color color);
}
