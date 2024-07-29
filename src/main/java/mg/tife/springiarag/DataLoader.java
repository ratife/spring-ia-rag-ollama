package mg.tife.springiarag;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
public class DataLoader {

    @Value("classpath:/pdfs/test1.pdf")
    private Resource pdfResource;

    @Value("vector.json")
    private String vectoreFile;
    
    @Bean
    public  VectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        String path = Path.of("src","main","resources","vectoreStore").toFile().getAbsolutePath() + "/vector.json";
        File fileStore = new File(path);
        if(fileStore.exists()){
            System.out.println("Exist fileStore");
            simpleVectorStore.load(fileStore);
        }
        else{
            PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdfResource);
            List<Document> documents = documentReader.get();
            TextSplitter textSpliter = new TokenTextSplitter();
            List<Document> chuncks = textSpliter.split(documents);
            System.out.println("icciiiiiiiiiiii**********");
            simpleVectorStore.add(chuncks);
            System.out.println("icciiiiiiiiiiii========");
            simpleVectorStore.save(fileStore);
        }
        return simpleVectorStore;
    }
}