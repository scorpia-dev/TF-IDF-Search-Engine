package searchEngine.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

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

		for (Document d : savedDocuments) {
			Long docId = d.getDocumentId();

			String[] words = splitDocumentIntoSingleWords(docId);
			for (String wordInDoc : words) {
				Optional<InvertedIndex> invertedIndex = invertedIndexRepository.findById(wordInDoc);
				if (invertedIndex.isPresent()) {
					InvertedIndex invertedIndexUpdate = invertedIndex.get();
					HashMap<Long, Integer> docIdAndTf = invertedIndexUpdate.getDocumentIdAndWordOccurance();

					if (docIdAndTf.containsKey(docId)) {
						Integer updatedOccurance = docIdAndTf.get(docId) + 1;
						docIdAndTf.put(docId, updatedOccurance);
						invertedIndexUpdate.setDocumentIdAndWordOccurance(docIdAndTf);
						invertedIndexRepository.save(invertedIndexUpdate);
					} else {
						docIdAndTf.put(docId, 1);
						invertedIndexUpdate.setDocumentIdAndWordOccurance(docIdAndTf);
						invertedIndexRepository.save(invertedIndexUpdate);
					}

				}
				else {
					HashMap<Long, Integer> docIdAndTf = new HashMap<Long, Integer>();
					docIdAndTf.put(docId, 1);
					
					InvertedIndex newInvertedIndex = new InvertedIndex(wordInDoc);
					newInvertedIndex.setDocumentIdAndWordOccurance(docIdAndTf);
					invertedIndexRepository.save(newInvertedIndex);
				}
			}
		}
		return documents;
	}

	public HashMap<String, Float> getMatchingDocuments(String word) {

		InvertedIndex invertedIndex = invertedIndexRepository.findById(word)
				.orElseThrow(
				() -> new EntityNotFoundException("No document contains the word: " + word));
		
		HashMap<Long, Integer> documentIdAndWordOccurance = invertedIndex.getDocumentIdAndWordOccurance();

		double idf = calculateIdf(documentIdAndWordOccurance);

		HashMap<Long, Float> tfidfHashMap = new HashMap<Long, Float>();
		Set<Long> documentsContainingSearchTerm = documentIdAndWordOccurance.keySet();

		for (Long docId : documentsContainingSearchTerm) {
			String[] words = splitDocumentIntoSingleWords(docId);
			int numOfWordsInDoc = words.length;
			float tfidf = calculateTfidf(documentIdAndWordOccurance, idf, docId, numOfWordsInDoc);

			tfidfHashMap.put(docId, tfidf);
		}
		HashMap<String, Float> tfidfSortedHashMap = sortResultsByTfidf(tfidfHashMap);
		
		return tfidfSortedHashMap;
	}

	private float calculateTfidf(HashMap<Long, Integer> documentIdAndWordOccurance, double idf, Long docId,
			int numOfWordsInDoc) {
		float tf = calculateTf(documentIdAndWordOccurance, docId, numOfWordsInDoc);
		float tfidf = (float) idf * tf;
		return tfidf;
	}

	private float calculateTf(HashMap<Long, Integer> documentIdAndWordOccurance, Long docId, int numOfWordsInDoc) {
		float tf = (float) documentIdAndWordOccurance.get(docId) / numOfWordsInDoc;
		return tf;
	}

	private double calculateIdf(HashMap<Long, Integer> documentIdAndWordOccurance) {
		List<Document> savedDocuments = documentRepository.findAll();
		double idf = Math.log((double) savedDocuments.size() / documentIdAndWordOccurance.size()+1);
		return idf;
	}

	public static HashMap<String, Float> sortResultsByTfidf(HashMap<Long, Float> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<Long, Float>> list = new LinkedList<Map.Entry<Long, Float>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<Long, Float>>() {
			public int compare(Map.Entry<Long, Float> o1, Map.Entry<Long, Float> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
		for (Entry<Long, Float> aa : list) {
			temp.put("document " + aa.getKey(), aa.getValue());
		}
		return temp;
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
