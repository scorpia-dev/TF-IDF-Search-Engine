package searchEngine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import searchEngine.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
