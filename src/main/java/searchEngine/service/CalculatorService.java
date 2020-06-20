package searchEngine.service;

import searchEngine.model.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CalculatorService {

      private float calculateTfidf(Map<Long, Integer> documentIdAndWordOccurance, double idf, Long docId,
                                 int numOfWordsInDoc);

      float calculateTf(Map<Long, Integer> documentIdAndWordOccurance, Long docId, int numOfWordsInDoc);


      double calculateIdf(HashMap<Long, Integer> documentIdAndWordOccurance);

}
