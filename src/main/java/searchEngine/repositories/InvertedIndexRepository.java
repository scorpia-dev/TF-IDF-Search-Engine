package searchEngine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import searchEngine.model.InvertedIndex;

@Repository
public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, String> {

}
