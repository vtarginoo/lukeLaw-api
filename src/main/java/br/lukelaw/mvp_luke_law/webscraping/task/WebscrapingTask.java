package br.lukelaw.mvp_luke_law.webscraping.task;

import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.webscraping.fontes.pje.PjeWebScrapingService;
import br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse.PjeTseWebScrapingService;
import br.lukelaw.mvp_luke_law.xSimulateBD.BDSimulate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
@EnableScheduling
public class WebscrapingTask {

    private static final Logger log = LoggerFactory.getLogger(WebscrapingTask.class);

    @Autowired
    private PjeWebScrapingService webScrapingService;

    @Autowired
    private PjeTseWebScrapingService tseWebScrapingService;

    @Autowired
    private KafkaTemplate<String, Processo> kafkaTemplate;

    @Autowired
    private BDSimulate bdSimulate;

    //@Scheduled(cron = "0 0 8-19 * * ?", zone = "America/Sao_Paulo")
    public void scrapingPJE() {
        executarScraping(webScrapingService::scrapePjeUltimoMov, "PJE");
    }

    @Scheduled(initialDelay = 30000)
    //@Scheduled(cron = "5 0 8-19 * * ?", zone = "America/Sao_Paulo")
    //@Scheduled(cron = "0 0/20 8-19 * * ?", zone = "America/Sao_Paulo")
    public void scrapingPJETse() {
        executarScraping(tseWebScrapingService::scrapePjeTseUltimoMov, "PJE-TSE");
    }

    private void executarScraping(Function<String, Processo> scrapingFunction, String source) {
        try {
            log.info("Iniciando scraping e envio ao Kafka para {}...", source);

            for (String processo : bdSimulate.processosAssociadosTSE.keySet()) {
                try {
                    Processo processoRaspado = scrapingFunction.apply(processo);

                    if (processoRaspado == null) {
                        log.warn("Falha no scraping para o processo {} no {}", processo, source);
                        continue;
                    }

                    kafkaTemplate.send("processos", processoRaspado);
                    log.info("Processo {} publicado no tópico Kafka pelo {}", processoRaspado.getNumeroProcesso(), source);

                } catch (Exception e) {
                    log.error("Erro ao realizar o scraping do processo {} no {}", processo, source, e);
                }
            }

            log.info("Finalizando scraping e envio ao Kafka para {}.", source);
        } catch (Exception e) {
            log.error("Erro ao realizar o scraping ou enviar ao Kafka para {}", source, e);
        }
    }
}







