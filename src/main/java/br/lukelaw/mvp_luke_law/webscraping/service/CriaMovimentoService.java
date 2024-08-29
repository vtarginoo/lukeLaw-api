package br.lukelaw.mvp_luke_law.webscraping.service;

import br.lukelaw.mvp_luke_law.webscraping.entity.Movimento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class CriaMovimentoService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd/MM/yyyy HH:mm:ss");

    public Movimento criarMovimentoPje(String movimentoScrape) {
        try {
            // Separa a string em partes usando parênteses como delimitadores
            String[] partes = movimentoScrape.split("\\(");
            if (partes.length < 2) {
                throw new IllegalArgumentException(
                        "Formato inválido para a string de movimentação: " + movimentoScrape);
            }

            // A primeira parte é o nome da movimentação
            String nome = partes[0].trim();

            // A segunda parte contém a data e hora, remover o parêntese final e o código "00"
            String dataHoraString = partes[1].split("\\)")[0].trim();

            // Converte a string de data e hora para LocalDateTime
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraString, DATE_TIME_FORMATTER);

            // Define o código 999 para o último movimento
            Long ordem = 999L;

            // Retorna o novo objeto Movimento
            return new Movimento(ordem, nome, dataHora);

        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Erro ao parsear a string de movimentação: "
                    + movimentoScrape, e);
        }
    }

    public Movimento criarMovimentoTSE(String ultimaMovimentacao, String daHoraUltimaMovimentacao) {
        // Define a ordem como 999
        Long ordem = 999L;

        // Converte a string da data e hora para LocalDateTime
        LocalDateTime dataHora = LocalDateTime.parse(daHoraUltimaMovimentacao);

        // Cria e retorna o objeto Movimento
        return new Movimento(ordem, ultimaMovimentacao, dataHora);
    }

}
