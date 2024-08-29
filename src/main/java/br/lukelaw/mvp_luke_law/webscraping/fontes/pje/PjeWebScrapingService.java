package br.lukelaw.mvp_luke_law.webscraping.fontes.pje;

import br.lukelaw.mvp_luke_law.messaging.service.MovimentoService;
import br.lukelaw.mvp_luke_law.webscraping.config.WebDriverFactory;
import br.lukelaw.mvp_luke_law.webscraping.entity.Movimento;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.webscraping.exception.WebScrapingException;
import br.lukelaw.mvp_luke_law.webscraping.service.CriaMovimentoService;
import org.openqa.selenium.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PjeWebScrapingService {

    @Autowired
    CriaMovimentoService movimentoService;

    public Processo scrapePjeUltimoMov(String numProcesso) {
        WebDriver driver = WebDriverFactory.createChromeDriver();
        String pjeUrl = "https://tjrj.pje.jus.br/1g/ConsultaPublica/listView.seam";
        Processo processoCapturado = null;
        Movimento ultimoMovimento = null;
        List<Movimento> movimentos = new ArrayList<>();

        System.out.println("Variáveis Configuradas");

        try {

            System.out.println("Iniciando scraping do PJE para o processo: " + numProcesso);

            //Informação Scraped -- Ultima Movimentação e Partes Envolvidas
            List<String> infoScraped = PjeWebScrapingUtil.ScrapingUltimaMov(driver, pjeUrl, numProcesso);
            String partesEnvolvidas = infoScraped.get(0);
            String ultimaMovimentacao = infoScraped.get(1);

            System.out.println("Partes capturadas: " + partesEnvolvidas);
            System.out.println("Movimentação capturada: " + ultimaMovimentacao);

            // Transforma a string capturada em um objeto Movimento
            ultimoMovimento = movimentoService.criarMovimentoPje(ultimaMovimentacao);

            System.out.println("Movimento criado: " + ultimoMovimento);
            // Insere o último movimento na lista
            movimentos.add(ultimoMovimento);

            // Transforma movimento em um processo
            processoCapturado = new Processo(partesEnvolvidas ,numProcesso, "TJRJ", "Pje", "1ªInstancia",
                    movimentos, ultimoMovimento.dataHora());


        } catch (Exception e) {
            throw new WebScrapingException("Aconteceu um Erro no WebScrapping!");


        } finally {
            driver.quit();
        }

        return processoCapturado;
    }
}


