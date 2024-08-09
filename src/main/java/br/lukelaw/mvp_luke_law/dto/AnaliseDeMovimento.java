package br.lukelaw.mvp_luke_law.dto;


import br.lukelaw.mvp_luke_law.entity.Movimento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;



@Getter
@AllArgsConstructor
@ToString
public class AnaliseDeMovimento {

    private String numeroProcesso;
    private String tribunal;
    private Movimento ultimoMovimento;
    private long horasDesdeUltimoMovimento;
    private boolean movimentoRecente;

}
