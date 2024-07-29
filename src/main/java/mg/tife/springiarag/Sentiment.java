package mg.tife.springiarag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Data
public class Sentiment {
    private String clavier;
    private String souris;
    private String ecran;
}
