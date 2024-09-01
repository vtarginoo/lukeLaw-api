package br.lukelaw.mvp_luke_law.webscraping;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.ReCaptcha;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwoCaptchaService {

    private static final Logger log = LoggerFactory.getLogger(TwoCaptchaService.class);

    @Value("${2captcha.api.key}")
    private String apiKey;

    private TwoCaptcha solver;

    @PostConstruct
    public void init() {
        log.info("Inicializando o serviço TwoCaptcha...");
        this.solver = new TwoCaptcha(apiKey); // A apiKey já foi injetada neste ponto
        configureSolver();
        log.info("Serviço TwoCaptcha inicializado com sucesso.");
    }

    private void configureSolver() {
        log.info("Configurando o TwoCaptcha...");
        solver.setHost("2captcha.com");
        solver.setDefaultTimeout(120);
        solver.setRecaptchaTimeout(600);
        solver.setPollingInterval(10);
        log.info("Configuração do TwoCaptcha concluída.");
    }

    public String solveReCaptchaV2(String numProcesso) throws Exception {
        log.info("Iniciando a resolução do CAPTCHA...");



        String websiteUrl = "https://consultaunificadapje.tse.jus.br/#/public/resultado/" + numProcesso;
        String websiteKey = "6LfHZq0fAAAAAFNQsEPuE04EdG0ZeEFxhYXUo374";

        ReCaptcha captcha = new ReCaptcha();
        captcha.setSiteKey(websiteKey);
        captcha.setUrl(websiteUrl);
        captcha.setInvisible(false);
        captcha.setEnterprise(false);
        captcha.setAction("verify");


        log.info("Enviando CAPTCHA para o 2Captcha... URL: {}, SiteKey: {}", websiteUrl, websiteKey);

        try {
            log.info("Solving");
            solver.solve(captcha);
            log.info("Solved!");
            String token = captcha.getCode();
            log.info("CAPTCHA resolvido com sucesso. Token: {}", token);
            return token; // Retorna o token resolvido
        } catch (Exception e) {
            log.error("Erro ao resolver CAPTCHA: {}", e.getMessage(), e);
            throw new Exception("Erro ao resolver CAPTCHA: " + e.getMessage(), e);
        }
    }
}