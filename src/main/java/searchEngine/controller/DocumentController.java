package searchEngine.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import searchEngine.model.Document;
import searchEngine.service.DocumentService;

@RequestMapping("/documents")
@RestController
public class DocumentController {

	@Autowired
	DocumentService documentService;

	@PostMapping()
	public List<Document> createDocument(@RequestBody List<Document> documents) {
		return documentService.createDocument(documents);
	}

	@GetMapping("/{word}")
	public Map<String, Float> getMatchingDocuments(@PathVariable String word) {
		return documentService.getMatchingDocuments(word);
	}

}
