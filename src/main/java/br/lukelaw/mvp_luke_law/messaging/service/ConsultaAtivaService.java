package br.lukelaw.mvp_luke_law.messaging.service;


import br.lukelaw.mvp_luke_law.messaging.templates.MessageTemplate;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.xSimulateBD.BDSimulate;
import br.lukelaw.mvp_luke_law.xSimulateBD.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaAtivaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaAtivaService.class);

    @Autowired
    MovimentoService movimentoService;

    @Autowired
    WhatsappService wppService;

    @Autowired
    MessageTemplate messageTemplate;

    @Autowired
    BDSimulate bdSimulate;

    //@KafkaListener(topics = "processos", groupId = "processo_group", containerFactory = "kafkaListenerContainerFactory")
    public void consumerConsultaAtiva(Processo requestProcesso) throws JsonProcessingException {

        log.info("Processo recebido: {}", requestProcesso.getNumeroProcesso());

        var analiseDeMovimento = movimentoService.analisarMovimentacao(requestProcesso);

        // Coloque a negação para testar a notificação mesmo quando o movimento não é recente
        if (analiseDeMovimento.isMovimentoRecente()) {
            List<Integer> advogadosAssociados = bdSimulate.processosAssociados.get(requestProcesso.getNumeroProcesso());
            if (advogadosAssociados != null) {
                for (int advId : advogadosAssociados) {
                    User advogado = bdSimulate.usuarios.stream()
                            .filter(user -> user.id() == advId)
                            .findFirst()
                            .orElse(null);

                    if (advogado != null) {
                        String messageBody = messageTemplate.mensagemBodyAtiva
                                (analiseDeMovimento, requestProcesso, advogado.nome());

                        wppService.envioWhatsapp(advogado.celular(), messageBody);
                    }
                }
            } else {
                log.warn("Nenhum advogado associado ao processo {}", requestProcesso.getNumeroProcesso());
            }
        }
    }


}

