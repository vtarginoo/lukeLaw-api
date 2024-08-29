package br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse;

import br.lukelaw.mvp_luke_law.webscraping.AntiCaptchaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


import org.openqa.selenium.WebDriver;



@Service
public class PjeTseWebScrapingUtil {

    @Autowired
    private AntiCaptchaService antiCaptchaService;

    private static final Logger log = LoggerFactory.getLogger(PjeTseWebScrapingUtil.class);


    public List<String> ScrapingUltimaMov(WebDriver driver, BrowserMobProxy proxy, String url, String numProcesso) throws InterruptedException {
        final StringBuilder interceptedResponseBody = new StringBuilder();

        // Configura o proxy para capturar o conteúdo da resposta
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        // Carrega a página
        driver.get(url);

        // Interage com a página
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='numeroProcesso']")));

        searchField.sendKeys(numProcesso);
        log.info("Número do processo inserido: {}", numProcesso);


        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@class='push-top-sm mr-20 mat-raised-button mat-button-base mat-primary']")));
        searchButton.click();
        log.info("Botão de pesquisa clicado.");

        // Espera para garantir que a requisição seja processada e o proxy capture a resposta
        Thread.sleep(90000);

        // Captura e processa as requisições interceptadas
        proxy.getHar().getLog().getEntries().forEach(entry -> {
            if (entry.getRequest().getUrl().contains("/consulta-publica-unificada/processo/10/0")) {
                interceptedResponseBody.append(entry.getResponse().getContent().getText());
                log.info("Response Body: {}", interceptedResponseBody.toString());
            }
        });

        String capturedResponse = interceptedResponseBody.toString();
        log.info("Captured Response: {}", capturedResponse);

        // Chama o método privado para analisar o JSON e extrair as informações desejadas
        return analisarJson(capturedResponse);
    }






    private List<String> analisarJson(String jsonResponse) {
        List<String> resultado = new ArrayList<>();

        try {
            // Parseia o JSON capturado
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Acessa o segundo elemento da lista principal (onde estão os detalhes do processo)
            JsonNode processoNode = rootNode.get(1).get(0);

            // Extrai o Assunto Principal
            String assuntoPrincipal = processoNode.path("assuntoPrincipal").asText();
            log.info("Assunto Principal: {}", assuntoPrincipal);
            resultado.add(assuntoPrincipal);

            // Extrai o Último Movimento
            String ultimoMovimento = processoNode.path("ultimoMovimento").asText();
            log.info("Último Movimento: {}", ultimoMovimento);
            resultado.add(ultimoMovimento);

            // Extrai a Data do Último Movimento
            String dataUltimoMovimento = processoNode.path("dataUltimoMovimento").asText();
            log.info("Data do Último Movimento: {}", dataUltimoMovimento);
            resultado.add(dataUltimoMovimento);

        } catch (Exception e) {
            log.error("Erro ao processar o JSON: {}", e.getMessage(), e);
            e.printStackTrace();
            resultado.add("Erro ao processar o JSON: " + e.getMessage());
        }

        return resultado;
    }
}