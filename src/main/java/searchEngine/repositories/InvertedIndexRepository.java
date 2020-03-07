package searchEngine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import searchEngine.model.InvertedIndex;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, String> {

}
