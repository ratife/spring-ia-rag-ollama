package mg.tife.springiarag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

public class SentenceTextSplitter implements DocumentTransformer {

	private static final int MAX_SENTENCE_LENGTH = 10000;
	
	@Override
	public List<Document> apply(List<Document> documents) {
		 List<Document> sentences = new ArrayList<>();
	        for (Document doc : documents) {
	            String text = doc.getContent();
	            String[] splitSentences = text.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s");
	            for (String sentence : splitSentences) {
	                if (!sentence.isBlank() && sentence.length() <= MAX_SENTENCE_LENGTH) {
	                    sentences.add(new Document(sentence.trim()));
	                }
	                else {
	                	System.out.println("sentence.length()="+sentence.length());
	                }
	            }
	        }
			return sentences;
	}
}
