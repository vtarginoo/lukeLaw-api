package br.lukelaw.mvp_luke_law.webscraping.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.util.Collections;


public class WebDriverFactory {

    public static WebDriver createChromeDriver() {
        // Verifica se estamos em um ambiente de contêiner
        boolean isRunningInDocker = Boolean.parseBoolean(System.getenv("DOCKER_ENV") != null ? System.getenv("DOCKER_ENV") : "false");

        // Configura as opções do Chrome
        ChromeOptions options = new ChromeOptions();

        // Executa o navegador em modo headless (sem exibir a UI)
        options.addArguments("--headless");

        // Corrige possíveis erros na execução
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Evita a detecção do bot pelo site
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // Define o tamanho da janela do navegador
        options.addArguments("window-size=1600,800");

        // Configura um user-agent para evitar ser identificado como bot
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        if (isRunningInDocker) {
            // Em um ambiente Docker, o ChromeDriver estará no PATH, então não é necessário definir o caminho
            options.setBinary("/usr/bin/chromium-browser");
            System.out.println("Executando em um ambiente Docker.");
        } else {
            // Ambiente local
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\vtarg\\Área de Trabalho\\devtools\\chromedriver-win64\\chromedriver.exe");
            System.out.println("Executando localmente.");
        }

        // Inicializa o WebDriver com as opções configuradas
        return new ChromeDriver(options);
    }
}
