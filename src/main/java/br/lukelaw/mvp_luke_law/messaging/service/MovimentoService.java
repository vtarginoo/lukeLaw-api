package br.lukelaw.mvp_luke_law.messaging.service;


import br.lukelaw.mvp_luke_law.messaging.dto.AnaliseDeMovimento;
import br.lukelaw.mvp_luke_law.webscraping.entity.Movimento;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Service
public class MovimentoService {

    private static final Logger log = LoggerFactory.getLogger(MovimentoService.class);


    public AnaliseDeMovimento analisarMovimentacao(Processo processo) {
        // Obter o último movimento
        Movimento ultimoMovimento = processo.getMovimentos().get(processo.getMovimentos().size() - 1);
        log.info("Último movimento obtido: {}", ultimoMovimento);

        // Converter a data do último movimento para LocalDateTime
        LocalDateTime dataUltimoMovimento = ultimoMovimento.dataHora();
        log.info("Data e hora do último movimento: {}", dataUltimoMovimento);

        // Obter a data e hora atual
        LocalDateTime agora = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        log.info("Data e hora atual (SP): {}", agora);

        // Calcular a diferença em horas
        long horasDesdeUltimoMovimento = ChronoUnit.HOURS.between(dataUltimoMovimento, agora);
        log.info("Horas desde o último movimento: {}", horasDesdeUltimoMovimento);

        // Verificar se a movimentação ocorreu fora do horário de expediente (entre 19h e 7h)
        boolean foraDoHorarioExpediente = dataUltimoMovimento.getHour() >= 19 || dataUltimoMovimento.getHour() < 7;
        log.info("Movimentação fora do horário de expediente: {}", foraDoHorarioExpediente);

        // Verificar se estamos na primeira execução do dia (às 8h)
        boolean primeiraVerificacaoDoDia = agora.getHour() == 8;
        log.info("Primeira verificação do dia: {}", primeiraVerificacaoDoDia);

        // Lógica para movimentação fora do horário de expediente
        if (foraDoHorarioExpediente && horasDesdeUltimoMovimento < 12 && primeiraVerificacaoDoDia) {
            log.info("Movimento recente detectado fora do horário de expediente.");
            return new AnaliseDeMovimento(processo, horasDesdeUltimoMovimento, true);
        }

        // Lógica padrão para movimentação dentro do horário de expediente
        boolean movimentoRecente = horasDesdeUltimoMovimento < 2;
        log.info("Movimento recente dentro do horário de expediente: {}", movimentoRecente);

        return new AnaliseDeMovimento(processo, horasDesdeUltimoMovimento, movimentoRecente);
    }

}