package br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse;


import br.lukelaw.mvp_luke_law.webscraping.TwoCaptchaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openqa.selenium.devtools.v128.network.Network;
import org.openqa.selenium.devtools.v128.network.model.Response;



import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


import org.openqa.selenium.WebDriver;





@Service
public class PjeTseWebScrapingUtil {

    @Autowired
    private TwoCaptchaService twoCaptchaService;

    private static final Logger log = LoggerFactory.getLogger(PjeTseWebScrapingUtil.class);


    public String scrapingUltimaMov(WebDriver driver, String url, String numProcesso) throws Exception {
        final StringBuilder interceptedResponseBody = new StringBuilder();

        // Configura o DevTools e captura a resposta
        setupDevTools(driver, interceptedResponseBody);

        // Carrega a página e realiza a interação inicial
        driver.get(url);
        firstInteractionScrape(driver, numProcesso);

        Thread.sleep(5000);

        // Aguarda a resposta após a resolução do CAPTCHA (incluindo handling de CAPTCHA)
        handleCaptcha(driver);

        // Aguarde após a interação para ser feita a Requisição
        Thread.sleep(10000); // Ajuste o tempo de espera conforme necessário

        // Processa a resposta capturada
        return processCapturedResponse(interceptedResponseBody);
    }




    private void setupDevTools(WebDriver driver, StringBuilder interceptedResponseBody) {
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Response response = responseReceived.getResponse();
            if (response.getUrl().contains("https://consultaunificadapje.tse.jus.br/consulta-publica-unificada/processo/10/0")) {
                try {
                    String body = devTools.send(Network.getResponseBody(responseReceived.getRequestId())).getBody();
                    interceptedResponseBody.append(body);
                    log.info("Captured Response Body: {}", body);
                } catch (Exception e) {
                    log.error("Error capturing response body", e);
                }
            }
        });
    }

    public void handleCaptcha(WebDriver driver) throws Exception {

        // Cria uma instância de WebDriverWait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Verifica se a URL é a esperada
        if (!driver.getCurrentUrl().equals("https://consultaunificadapje.tse.jus.br/#/public/inicial/index")) {
            log.info("URL diferente da esperada, não é necessário lidar com CAPTCHA.");
            return;
        }

        // Verifica se o iframe do CAPTCHA está presente
        List<WebElement> iframes = driver.findElements(By.cssSelector("iframe[src*='recaptcha']"));
        if (iframes.isEmpty()) {
            log.info("Nenhum iframe de CAPTCHA detectado, prosseguindo normalmente.");
            return;
        }

        // Se o iframe do CAPTCHA estiver presente, lidar com ele
        log.info("CAPTCHA detectado, lidando com ele.");

        // Passo 1: Clicar de forma bem humana em um canto direito da tela
        Actions actions = new Actions(driver);
        actions.moveByOffset(300, 300).pause(Duration.ofMillis(500)).click().perform(); // Ajuste as coordenadas conforme necessário
        log.info("Clica na Screen para fugir do Captcha");
        Thread.sleep(1200 + new Random().nextInt(800)); // Pausa para simular uma interação humana

        // Passo 2: Rolar a tela de forma bem humana para baixo
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,1000)"); // Ajuste o valor de scroll conforme necessário
        log.info("Scrolled down da Página bem humano");
        Thread.sleep(1200 + new Random().nextInt(800)); // Pausa após rolar a tela

        // Passo 3: Clicar de forma bem humana no botão de pesquisa novamente
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='push-top-sm mr-20 mat-raised-button mat-button-base mat-primary']")));
        searchButton.click();
        log.info("Clique no Pesquisar dnv");
        Thread.sleep(1200 + new Random().nextInt(800)); // Pausa após o clique
    }



    private String processCapturedResponse(StringBuilder interceptedResponseBody) {
        if (!interceptedResponseBody.isEmpty()) {
            log.info("Captured Response: {}", interceptedResponseBody.toString());
            return interceptedResponseBody.toString();  // Apenas retorna a string capturada
        } else {
            log.error("Nenhuma resposta foi capturada.");
            return "Nenhuma resposta foi capturada.";
        }
    }

    //// Tentativa Humanizada
    private void firstInteractionScrape(WebDriver driver, String numProcesso) throws InterruptedException {
        // Movimenta o mouse para simular um comportamento de usuário
        Actions actions = new Actions(driver);
        actions.moveByOffset(10, 10).perform();
        log.info("Mouse movido para um ponto específico na página.");
        Thread.sleep(500 + new Random().nextInt(500)); // Pausa aleatória

        // Localiza o campo de pesquisa e clica nele
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='numeroProcesso']")));
        actions.moveToElement(searchField).pause(Duration.ofMillis(300)).click().perform(); // Movendo para o campo e clicando
        log.info("Campo de pesquisa clicado.");
        Thread.sleep(600 + new Random().nextInt(400)); // Pausa aleatória

        // Insere o número do processo no campo de pesquisa, simulando a digitação humana
        for (char c : numProcesso.toCharArray()) {
            searchField.sendKeys(String.valueOf(c));
            Thread.sleep(150 + new Random().nextInt(100)); // Pausa entre teclas para simular digitação
        }
        log.info("Número do processo inserido: {}", numProcesso);

        // Rola a página para baixo para simular a navegação do usuário
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,300)");
        log.info("Página rolada para baixo.");
        Thread.sleep(1200 + new Random().nextInt(800)); // Pausa aleatória

        // Clica no botão de pesquisa de forma mais humana
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@class='push-top-sm mr-20 mat-raised-button mat-button-base mat-primary']")));
        actions.moveToElement(searchButton).pause(Duration.ofMillis(500)).click().perform(); // Movendo para o botão e clicando
        log.info("Interação Realizada!");
        Thread.sleep(1000 + new Random().nextInt(1000)); // Pausa final após o clique
    }


    public List<String> analisarJson(String jsonResponse) {
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


