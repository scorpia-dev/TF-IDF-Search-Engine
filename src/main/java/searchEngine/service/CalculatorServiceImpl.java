package searchEngine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchEngine.model.Document;
import searchEngine.repositories.DocumentRepository;

import java.util.List;
import java.util.Map;

@Service
public class CalculatorServiceImpl implements CalculatorService{

    @Autowired
    DocumentRepository documentRepository;

    public float calculateTfidf(Map<Long, Integer> documentIdAndWordOccurance, double idf, Long docId,
                                 int numOfWordsInDoc) {
        float tf = calculateTf(documentIdAndWordOccurance, docId, numOfWordsInDoc);
        return (float) idf * tf;
    }

    public float calculateTf(Map<Long, Integer> documentIdAndWordOccurance, Long docId, int numOfWordsInDoc) {
        return (float) documentIdAndWordOccurance.get(docId) / numOfWordsInDoc;
    }

    public double calculateIdf(Map<Long, Integer> documentIdAndWordOccurance) {
        List<Document> savedDocuments = documentRepository.findAll();
        return Math.log((double) savedDocuments.size() / documentIdAndWordOccurance.size()+1);
    }
}
