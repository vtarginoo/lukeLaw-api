package br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse;

import br.lukelaw.mvp_luke_law.webscraping.config.WebDriverFactory;
import br.lukelaw.mvp_luke_law.webscraping.entity.Movimento;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.webscraping.exception.WebScrapingException;
import br.lukelaw.mvp_luke_law.webscraping.service.CriaMovimentoService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PjeTseWebScrapingService {

    @Autowired
    CriaMovimentoService movimentoService;

    @Autowired
    PjeTseWebScrapingUtil tseWebScrapingUtil;

    public Processo scrapePjeTseUltimoMov(String numProcesso) {


        WebDriver driver = WebDriverFactory.createChromeDriver(); /// Com Proxy




        // URL do PJE-TSE
        String pjeTseUrl = "https://consultaunificadapje.tse.jus.br/#/public/inicial/index";
        Processo processoCapturado = null;
        List<String> infoScrapedTreated = null;

        try {
            // Executa o scraping e processa o resultado
            infoScrapedTreated = realizarScraping(driver, pjeTseUrl, numProcesso);
        } catch (Exception e) {
            System.err.println("Aconteceu um Erro no WebScrapping: " + e.getMessage());
            e.printStackTrace();
            throw new WebScrapingException("Aconteceu um Erro no WebScrapping!");
        } finally {
            // Fechar o WebDriver e o Proxy logo após o scraping
            driver.quit();

        }

        // Criar o objeto Processo com as informações capturadas
        return criarProcesso(infoScrapedTreated, numProcesso);
    }


    private List<String> realizarScraping(WebDriver driver,
                                          String url, String numProcesso)
            throws Exception {
        System.out.println("Iniciando scraping do PJE-TSE para o processo: " + numProcesso);

        // Acessa a página e faz o scraping das informações
        String infoScraped = tseWebScrapingUtil.scrapingUltimaMov(driver,  url, numProcesso);

        // Analisa e devolve as informações corretas
        return tseWebScrapingUtil.analisarJson(infoScraped);
    }

    private Processo criarProcesso(List<String> infoScrapedTreated, String numProcesso) {
        String partesEnvolvidas = infoScrapedTreated.get(0);
        String ultimaMovimentacao = infoScrapedTreated.get(1);
        String daHoraUltimaMovimentacao = infoScrapedTreated.get(2);

        System.out.println("Partes capturadas: " + partesEnvolvidas);
        System.out.println("Movimentação capturada: " + ultimaMovimentacao);
        System.out.println("Movimentação DATA HORA: " + daHoraUltimaMovimentacao);

        // Verifica se a movimentação foi encontrada antes de prosseguir
        if (ultimaMovimentacao == null || ultimaMovimentacao.isEmpty()) {
            throw new WebScrapingException("Nenhuma movimentação encontrada para o processo: " + numProcesso);
        }

        Movimento ultimoMovimento = movimentoService.criarMovimentoTSE(ultimaMovimentacao, daHoraUltimaMovimentacao);
        List<Movimento> movimentos = new ArrayList<>();
        movimentos.add(ultimoMovimento);

        // Cria e retorna o objeto Processo
        return new Processo(partesEnvolvidas, numProcesso, "TSE", "Pje", "1ª Instância",
                movimentos, ultimoMovimento.dataHora());
    }

    }


