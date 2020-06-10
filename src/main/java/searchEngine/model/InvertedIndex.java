package searchEngine.model;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class InvertedIndex {

	@Id
	private String word;

	@Lob
	private HashMap<Long, Integer> documentIdAndWordOccurance;

	public InvertedIndex(String word) {
		this.setWord(word);
	}

}
