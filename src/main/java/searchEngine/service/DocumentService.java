package searchEngine.service;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import searchEngine.model.Document;
import searchEngine.model.InvertedIndex;
import searchEngine.repositories.DocumentRepository;
import searchEngine.repositories.InvertedIndexRepository;

@Service
public class DocumentService {

	@Autowired
	DocumentRepository documentRepository;

	@Autowired
	InvertedIndexRepository invertedIndexRepository;

	public List<Document> createDocument(List<Document> documents) {


		List<Document> savedDocuments = documentRepository.saveAll(documents);

		savedDocuments.forEach(document -> {
			Long docId =document.getDocumentId();
			String[] words = splitDocumentIntoSingleWords(docId);

			Arrays.stream(words).forEach(word -> {
				Optional<InvertedIndex> invertedIndex = invertedIndexRepository.findById(word);

				if (invertedIndex.isPresent()) {
					InvertedIndex invertedIndexUpdate = invertedIndex.get();
					HashMap<Long, Integer> docIdAndTf = invertedIndexUpdate.getDocumentIdAndWordOccurance();
					docIdAndTf.computeIfPresent(docId, (key, val) -> val +1);
					docIdAndTf.putIfAbsent(docId, 1);
					invertedIndexUpdate.setDocumentIdAndWordOccurance(docIdAndTf);
					invertedIndexRepository.save(invertedIndexUpdate);

				}
				else {
					HashMap<Long, Integer> docIdAndTf = new HashMap<>();
					docIdAndTf.put(docId, 1);
					InvertedIndex newInvertedIndex = new InvertedIndex(word);
					newInvertedIndex.setDocumentIdAndWordOccurance(docIdAndTf);
					invertedIndexRepository.save(newInvertedIndex);
				}
			});
		});
		return documents;
	}

	public Map<String, Float> getMatchingDocuments(String word) {

		InvertedIndex invertedIndex = invertedIndexRepository.findById(word)
				.orElseThrow(
				() -> new EntityNotFoundException("No document contains the word: " + word));

		double idf = calculateIdf(invertedIndex.getDocumentIdAndWordOccurance());


		HashMap<Long, Integer> documentIdAndWordOccurance = invertedIndex.getDocumentIdAndWordOccurance();
		HashMap<Long, Float> tfidfHashMap = new HashMap<>();

		documentIdAndWordOccurance.keySet().forEach(docId -> {
					String[] words = splitDocumentIntoSingleWords(docId);
					int numOfWordsInDoc = words.length;
					float tfidf = calculateTfidf(documentIdAndWordOccurance, idf, docId, numOfWordsInDoc);
			tfidfHashMap.put(docId, tfidf);
		});

		return sortResultsByTfidf(tfidfHashMap);
	}

	private float calculateTfidf(Map<Long, Integer> documentIdAndWordOccurance, double idf, Long docId,
			int numOfWordsInDoc) {
		float tf = calculateTf(documentIdAndWordOccurance, docId, numOfWordsInDoc);
		return (float) idf * tf;
	}

	private float calculateTf(Map<Long, Integer> documentIdAndWordOccurance, Long docId, int numOfWordsInDoc) {
		return (float) documentIdAndWordOccurance.get(docId) / numOfWordsInDoc;
	}

	private double calculateIdf(HashMap<Long, Integer> documentIdAndWordOccurance) {
		List<Document> savedDocuments = documentRepository.findAll();
		return Math.log((double) savedDocuments.size() / documentIdAndWordOccurance.size()+1);
	}

	public static Map<String, Float> sortResultsByTfidf(Map<Long, Float> hm) {
		return hm.entrySet()
				.stream()
				.sorted(Entry.<Long,Float>comparingByValue().reversed())
				.collect(Collectors.toMap(entry -> "document " +entry.getKey(), Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	private String[] splitDocumentIntoSingleWords(Long docId) {
		Document doc = documentRepository.getOne(docId);
		String docText = doc.getText();
		String[] words = docText.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].replaceAll("[^\\w]", "");
		}
		return words;
	}	
}
