package br.lukelaw.mvp_luke_law.webscraping.config;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;


public class WebDriverFactory {

    public static WebDriver createChromeDriver() {
        // Verifica se estamos em um ambiente de contêiner
        boolean isRunningInDocker = Boolean.parseBoolean(System.getenv("DOCKER_ENV") != null ? System.getenv("DOCKER_ENV") : "false");

        // Configura as opções do Chrome
        ChromeOptions options = new ChromeOptions();

        // Executa o navegador em modo headless (sem exibir a UI)
        //options.addArguments("--headless");

        // Corrige possíveis erros na execução
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.addArguments("--disable-gpu"); // Pode não ser necessário, mas não custa tentar
        options.addArguments("--remote-debugging-port=9222"); // Útil para debugging

        // Evita a detecção do bot pelo site
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        // Define o tamanho da janela do navegador
        options.addArguments("window-size=1600,800");

        // Configura um user-agent para evitar ser identificado como bot
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        if (isRunningInDocker) {
            String chromeDriverPath = "/usr/bin/chromedriver";
            if (Files.exists(Paths.get(chromeDriverPath))) {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                System.out.println("Executando em um ambiente Docker.");
            } else {
                throw new RuntimeException("ChromeDriver não encontrado no caminho: " + chromeDriverPath);
            }
        } else {
            String chromeDriverPath = "C:\\Users\\vtarg\\Área de Trabalho\\devtools\\chromedriver-win64\\chromedriver.exe";
            if (Files.exists(Paths.get(chromeDriverPath))) {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                System.out.println("Executando localmente.");
            } else {
                throw new RuntimeException("ChromeDriver não encontrado no caminho: " + chromeDriverPath);
            }
        }

        WebDriver driver = new ChromeDriver(options);

        System.out.println("WebDriver iniciado com sucesso.");

        return driver;
    }


    public static WebDriver createChromeDriverWithProxy(BrowserMobProxy proxy) {
        // Configura as opções do Chrome
        ChromeOptions options = new ChromeOptions();

        // Configurações comuns
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("window-size=1600,800");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

        // Configura o proxy
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        options.setProxy(seleniumProxy);
        options.setAcceptInsecureCerts(true);

        // Verifica se estamos em um ambiente de contêiner
        boolean isRunningInDocker = Boolean.parseBoolean(System.getenv("DOCKER_ENV") != null ? System.getenv("DOCKER_ENV") : "false");

        if (isRunningInDocker) {
            String chromeDriverPath = "/usr/bin/chromedriver";
            if (Files.exists(Paths.get(chromeDriverPath))) {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                System.out.println("Executando em um ambiente Docker.");
            } else {
                throw new RuntimeException("ChromeDriver não encontrado no caminho: " + chromeDriverPath);
            }
        } else {
            String chromeDriverPath = "C:\\Users\\vtarg\\Área de Trabalho\\devtools\\chromedriver-win64\\chromedriver.exe";
            if (Files.exists(Paths.get(chromeDriverPath))) {
                System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                System.out.println("Executando localmente.");
            } else {
                throw new RuntimeException("ChromeDriver não encontrado no caminho: " + chromeDriverPath);
            }
        }

        // Cria o WebDriver com as opções configuradas
        WebDriver driver = new ChromeDriver(options);
        System.out.println("WebDriver com proxy iniciado com sucesso.");

        return driver;
    }


}