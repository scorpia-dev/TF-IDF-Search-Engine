package searchEngine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import searchEngine.model.Document;
import searchEngine.repositories.DocumentRepository;
import searchEngine.service.DocumentService;

@AutoConfigureTestDatabase(replace = Replace.ANY)
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class DocumentControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	DocumentService documentService;

	@Autowired
	DocumentRepository documentrepository;

	@Autowired
	ObjectMapper objectMapper;

	@Transactional
	@Test
	public void createSingleDocumentTest() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		String text = "The brown fox jumped over the brown dog";

		Document document = new Document(text);
		documents.add(document);

		String json = objectMapper.writeValueAsString(documents);

		mvc.perform(post("/documents").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isOk()).andDo(print())

				.andExpect(MockMvcResultMatchers.jsonPath("$[0].documentId").value("1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("The brown fox jumped over the brown dog"))
				.andReturn();

		assertTrue(documentrepository.findAll().size() == 1);

	}

	@Transactional
	@Test
	public void createSingleDocumentEmptyTextTest() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		String text = "";

		Document document = new Document(text);
		documents.add(document);

		String json = objectMapper.writeValueAsString(documents);

		mvc.perform(post("/documents").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isInternalServerError()).andDo(print())
				.andExpect(content().string(containsString(
						"not valid due to validation error: [searchEngine.model.Document text: document must contain text]")));
	}

	@Transactional
	@Test
	public void createMultiDocumentTest() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		String text1 = "The brown fox jumped over the brown dog";
		String text2 = "The lazy brown dog, sat in the other corner";
		String text3 = "The Red Fox bit the lazy dog!";

		Document document1 = new Document(text1);
		Document document2 = new Document(text2);
		Document document3 = new Document(text3);

		documents.add(document1);
		documents.add(document2);
		documents.add(document3);

		String json = objectMapper.writeValueAsString(documents);

		mvc.perform(post("/documents").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(json)).andExpect(status().isOk()).andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].documentId").value("1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("The brown fox jumped over the brown dog"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].documentId").value("2"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].text")
						.value("The lazy brown dog, sat in the other corner"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].documentId").value("3"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[2].text").value("The Red Fox bit the lazy dog!"))
				.andReturn();

		assertTrue(documentrepository.findAll().size() == 3);
	}

	@Transactional
	@Test
	public void listMatchingDocumentsTest() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		String text1 = "The brown fox jumped over the brown dog";
		String text2 = "The lazy brown dog, sat in the other corner";
		String text3 = "The Red Fox bit the lazy dog!";

		Document document1 = new Document(text1);
		Document document2 = new Document(text2);
		Document document3 = new Document(text3);

		documents.add(document1);
		documents.add(document2);
		documents.add(document3);
		documentService.createDocument(documents);

		String searchTerm1 = "dog";
		mvc.perform(get("/documents/{word}", searchTerm1).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string("{\"document 1\":0.0,\"document 2\":0.0,\"document 3\":0.0}"));
		assertTrue(documentService.getMatchingDocuments(searchTerm1).size() == 3);

		String searchTerm2 = "fox";
		mvc.perform(get("/documents/{word}", searchTerm2).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string("{\"document 3\":0.05792359,\"document 1\":0.050683137}"));
		assertTrue(documentService.getMatchingDocuments(searchTerm2).size() == 2);

		String searchTerm3 = "brown";
		mvc.perform(get("/documents/{word}", searchTerm3).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(content().string("{\"document 1\":0.101366274,\"document 2\":0.04505168}"));
		assertTrue(documentService.getMatchingDocuments(searchTerm3).size() == 2);
	}

	@Transactional
	@Test
	public void listMatchingDocumentsNegativeTest() throws Exception {
		List<Document> documents = new ArrayList<Document>();
		String text1 = "The brown fox jumped over the brown dog";
		String text2 = "The lazy brown dog, sat in the other corner";
		String text3 = "The Red Fox bit the lazy dog!";

		Document document1 = new Document(text1);
		Document document2 = new Document(text2);
		Document document3 = new Document(text3);

		documents.add(document1);
		documents.add(document2);
		documents.add(document3);

		documentService.createDocument(documents);

		String searchTerm = "hello";

		mvc.perform(get("/documents/{word}", searchTerm).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andDo(print()).andExpect(content().string(containsString(
						"not valid due to validation error: No document contains the word: " + searchTerm)));

		assertTrue(documentrepository.findAll().size() == 3);
	}
}
