package br.lukelaw.mvp_luke_law.webscraping.fontes.pje;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class PjeWebScrapingUtil {


    //Scraping Inicial para pegar a última movimentação
    public static List<String> ScrapingUltimaMov(WebDriver driver, String url, String numProcesso)
            throws InterruptedException {

        // Acessa a página do PJE Consulta Pública do TJ-RJ
        driver.get(url);

        // Adiciona o cookie rxvisitor manualmente
        Cookie rxVisitorCookie = new Cookie(
                "rxvisitor", "1722960632647NRQOMJVLQSK7UM5RBBNRF8S5QGFHD1R7");

        driver.manage().addCookie(rxVisitorCookie);

        // Adiciona o valor no local storage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "window.localStorage.setItem('rxvisitor', '1722960632647NRQOMJVLQSK7UM5RBBNRF8S5QGFHD1R7');");

        // Recarrega a página para que o cookie seja aplicado
        driver.navigate().refresh();

        // Identifica o campo de pesquisa e insere o número do processo
        WebElement searchField = driver.findElement(By.id(
                "fPP:numProcesso-inputNumeroProcessoDecoration:numProcesso-inputNumeroProcesso"));

        searchField.sendKeys(numProcesso);

        // Simula o pressionamento da tecla Enter para submeter a busca
        searchField.sendKeys(Keys.ENTER);

        // Espera um pouco para a página de login carregar, se for o caso
        Thread.sleep(2000);

        // Verifica se a página de login foi carregada
        if (driver.getCurrentUrl().contains("login")) {
            System.out.println("Redirecionado para a página de login, voltando...");

            // Volta para a página anterior
            driver.navigate().back();

            // Reenvia a pesquisa
            searchField = driver.findElement(By.id(
                    "fPP:numProcesso-inputNumeroProcessoDecoration:numProcesso-inputNumeroProcesso"));

            searchField.sendKeys(numProcesso);
            searchField.sendKeys(Keys.ENTER);

            // Aguarda um tempo para a página carregar novamente
            Thread.sleep(2000);
        }

        // =============== Scraping =======================

        // Espera explícita para garantir que o elemento esteja presente antes de acessá-lo
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement movimentacaoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='fPP:processosTable:tb']/tr[1]/td[3]")));

        // Captura a última movimentação
        String ultimaMovimentacao = movimentacaoElement.getText();
        System.out.println("Última movimentação capturada: " + ultimaMovimentacao);

        // Captura o conteúdo do <td> que contém as partes e o número do processo
        WebElement partesElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody[@id='fPP:processosTable:tb']/tr[1]/td[2]")));

        // Obtém o texto dentro da âncora <a> (que você deseja remover depois)
        String textoDentroDaAncora = partesElement.findElement(By.tagName("a")).getText();
        // Obtém o texto total do <td>
        String textoCompleto = partesElement.getText();
        // Remove o texto dentro da âncora para obter apenas as partes envolvidas
        String partesEnvolvidas = textoCompleto.replace(textoDentroDaAncora, "").trim();
        partesEnvolvidas = partesEnvolvidas.substring(textoCompleto.indexOf("\n") + 2).trim();

        System.out.println("Partes envolvidas capturadas: " + partesEnvolvidas);

        // Cria uma lista para armazenar as informações capturadas
        List<String> resultado = new ArrayList<>();
        resultado.add(partesEnvolvidas);
        resultado.add(ultimaMovimentacao);

        return resultado;
    }
}

