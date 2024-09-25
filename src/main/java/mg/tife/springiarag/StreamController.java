package mg.tife.springiarag;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

	private ExecutorService nonBlockingService = Executors
		      .newCachedThreadPool();
	
    private ChatClient chatClient;
    private VectorStore vectoreStore;
    
    @Value("classpath:/templates/prompt_doc.st")
    private Resource promptResourceDoc;
    
    @Value("classpath:/templates/prompt_sentiment.st")
    private Resource promptResourceSentiment;
    
    @Value("classpath:/templates/prompt_maths.st")
    private Resource promptResourceMaths;
    
    public StreamController(ChatClient.Builder builder,VectorStore vs){
        this.chatClient = builder.build();
        this.vectoreStore = vs;
    }

    @Autowired
    SystemMessage systemMessage;
    
    @PostMapping(value = "/doc",produces = MediaType.TEXT_PLAIN_VALUE)
    public SseEmitter handleSse(String msg) {
    	SseEmitter emitter = new SseEmitter();
        nonBlockingService.execute(() -> {
            try {
            	PromptTemplate promptTemplate = new PromptTemplate(promptResourceDoc);
                
                List<Document> documents = vectoreStore.similaritySearch(
                		SearchRequest.query(msg).withTopK(4));
                
                List<String> context = documents.stream().map(d-> d.getContent()).toList();
                
                Prompt prompt = promptTemplate.create(Map.of("context",context,"question",msg));
                
                Flux<String> output = chatClient.prompt(prompt).stream().content();
                
                output.collectList().block().stream().forEach(e->{System.out.println("HERRE =>" + e);});
               /*
                    try {
                        emitter.send(stream.chatResponse().blockFirst()); // Send each response to the client
                    } catch (IOException e) {
                        emitter.completeWithError(e); // Handle any IO exceptions
                    }
              //  });
                */
                //emitter.send(chatClient.prompt(prompt).call().content());
                // we could send more events
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }
    
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }
        try {
        	byte[] imageData = file.getBytes();
        	UserMessage userMessage = new UserMessage("Explan what do you see in this picture ?",
        	        List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageData)));
        	Prompt prompt = new Prompt(List.of(userMessage), OllamaOptions.create().withModel("llava"));
        	
        	String result =  chatClient.prompt(prompt)
                    .call()
                    .content();
        	
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
}
