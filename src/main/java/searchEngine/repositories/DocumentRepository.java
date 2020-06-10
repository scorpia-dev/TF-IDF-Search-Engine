package searchEngine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import searchEngine.model.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

}
