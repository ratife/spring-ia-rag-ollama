package mg.tife.springiarag;

import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private ChatClient chatClient;
    private VectorStore vectoreStore;
    
    @Value("classpath:/templates/prompt_doc.st")
    private Resource promptResourceDoc;
    
    @Value("classpath:/templates/prompt_sentiment.st")
    private Resource promptResourceSentiment;
    
    @Value("classpath:/templates/prompt_maths.st")
    private Resource promptResourceMaths;
    
    public ChatController(ChatClient.Builder builder,VectorStore vs){
        this.chatClient = builder.build();
        this.vectoreStore = vs;
    }

    @Autowired
    SystemMessage systemMessage;

    @GetMapping(value = "/sentiment",produces = MediaType.TEXT_PLAIN_VALUE)
    public String sentiment(String msg) {
    	PromptTemplate promptTemplate = new PromptTemplate(promptResourceSentiment);
    	Prompt prompt = promptTemplate.create();
        return chatClient.prompt()
                .system(prompt.getContents())
                .user(msg)
                .call()
                .content();
    }
    
    @GetMapping(value = "/maths",produces = MediaType.TEXT_PLAIN_VALUE)
    public String math(String msg) {
    	PromptTemplate promptTemplate = new PromptTemplate(promptResourceMaths);
    	Prompt prompt = promptTemplate.create();
        return chatClient.prompt()
                .system(prompt.getContents())
                .user(msg)
                .call()
                .content();
    }
    
    @GetMapping(value = "/doc",produces = MediaType.TEXT_PLAIN_VALUE)
    public String doc(String msg) {
    	PromptTemplate promptTemplate = new PromptTemplate(promptResourceDoc);
       
        List<Document> documents = vectoreStore.similaritySearch(
        		SearchRequest.query(msg).withTopK(4));
        
        List<String> context = documents.stream().map(d-> d.getContent()).toList();
        context.stream().forEach(System.out::println);
        
        Prompt prompt = promptTemplate.create(Map.of("context",context,"question",msg));
        return chatClient.prompt(prompt).call().content();
                
    }
    
}
