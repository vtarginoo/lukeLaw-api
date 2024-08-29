package br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse;

import br.lukelaw.mvp_luke_law.webscraping.config.WebDriverFactory;
import br.lukelaw.mvp_luke_law.webscraping.entity.Movimento;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.webscraping.exception.WebScrapingException;
import br.lukelaw.mvp_luke_law.webscraping.service.CriaMovimentoService;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
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

    public Processo scrapePjeUltimoMov(String numProcesso) {

        // Iniciar o BrowserMob Proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        WebDriver driver = WebDriverFactory.createChromeDriverWithProxy(proxy);

        // Iniciar a captura de tráfego HTTP
        proxy.newHar("pje");

        String pjeTseUrl = "https://consultaunificadapje.tse.jus.br/#/public/inicial/index";
        Processo processoCapturado = null;
        Movimento ultimoMovimento = null;
        List<Movimento> movimentos = new ArrayList<>();

        System.out.println("Variáveis Configuradas");

        try {
            System.out.println("Iniciando scraping do PJE-TSE para o processo: " + numProcesso);

            // Acessa a página e faz o scraping das informações
            List<String> infoScraped = tseWebScrapingUtil.ScrapingUltimaMov(driver, proxy, pjeTseUrl, numProcesso);
            String partesEnvolvidas = infoScraped.get(0);
            String ultimaMovimentacao = infoScraped.get(1);
            String daHoraUltimaMovimentacao = infoScraped.get(2);

            System.out.println("Partes capturadas: " + partesEnvolvidas);
            System.out.println("Movimentação capturada: " + ultimaMovimentacao);
            System.out.println("Movimentação DATA HORA: " + daHoraUltimaMovimentacao);

            // Verifica se a movimentação foi encontrada antes de prosseguir
            if (ultimaMovimentacao == null || ultimaMovimentacao.isEmpty()) {
                throw new WebScrapingException("Nenhuma movimentação encontrada para o processo: " + numProcesso);
            }

            // Transforma a string capturada em um objeto Movimento
            ultimoMovimento = movimentoService.criarMovimentoTSE(ultimaMovimentacao,daHoraUltimaMovimentacao);
            movimentos.add(ultimoMovimento);

            // Cria o objeto Processo com as informações capturadas
            processoCapturado = new Processo(partesEnvolvidas, numProcesso, "TSE", "Pje", "1ª Instância",
                    movimentos, ultimoMovimento.dataHora());

            System.out.println("Processo capturado: " + processoCapturado);

        } catch (Exception e) {
            System.err.println("Aconteceu um Erro no WebScrapping: " + e.getMessage());
            e.printStackTrace();
            throw new WebScrapingException("Aconteceu um Erro no WebScrapping!");

        } finally {
            driver.quit(); // Fechar o WebDriver
            proxy.stop(); // Fechar o Proxy
        }

        return processoCapturado;
    }
}