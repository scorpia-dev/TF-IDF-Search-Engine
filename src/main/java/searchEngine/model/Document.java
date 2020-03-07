package searchEngine.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@NoArgsConstructor
@Entity
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long documentId;

	@NotEmpty(message = "document must contain text")
	private String text;

	public Document(String text) {
		this.setText(text);
	}

}
