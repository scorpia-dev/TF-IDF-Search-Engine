package searchEngine.service;

import java.util.Map;

public interface CalculatorService {

    float calculateTfidf(Map<Long, Integer> documentIdAndWordOccurance, double idf, Long docId,
                                 int numOfWordsInDoc);

      float calculateTf(Map<Long, Integer> documentIdAndWordOccurance, Long docId, int numOfWordsInDoc);


      double calculateIdf(Map<Long, Integer> documentIdAndWordOccurance);

}
