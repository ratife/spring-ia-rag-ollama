package mg.tife.springiarag;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Model {
	/*
    @Bean
    public ChatModel llma(){
        System.out.println("===sssasz=============");
        OllamaApi ollamaApi = new OllamaApi();
        System.out.println("================");
        OllamaChatModel ollamaChatModel = new OllamaChatModel(ollamaApi, OllamaOptions.create().
                                withTemperature(0F).withModel("llama2"));
        System.out.println("=====x===========");
        return ollamaChatModel;
        //System.out.println(chatResponse.getResult().getOutput().getContent());
    }*/
    
    @Bean
    public SystemMessage systemMessage(){
        String systeMsgTxt = """
                Vous êtres un assistant spécialiser dans le domaine de l'analyse des sentiment.
                Votre tâches est d'extraire à partir d'un ccomentaires le sentiment des differents aspects des ordinateurs
                achetés par des clients. Les aspects qui nous interressent sont : l'ecran, la souris et le clavier.
                Le sentiment peut être : positive, negative ou neutre
                Le résultat attendu sera au format JSON avec les champs suivants :
                - clavier : le sentiment relatif au clavier
                - souris : le sentiment relatif à la souris
                - ecran : le sentiment relatif à l'ecran
                """;
        SystemMessage systemMessage = new SystemMessage(systeMsgTxt);
        return systemMessage;
    }
}
