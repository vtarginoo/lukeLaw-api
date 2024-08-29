package br.lukelaw.mvp_luke_law.webscraping;


import com.anti_captcha.Api.RecaptchaV2Proxyless;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.MalformedURLException;
import java.net.URL;

@Service
public class AntiCaptchaService {

    @Value("${anticaptcha.api.key}")
    private String antiCaptchaApiKey;

    public String solveRecaptchaV2(String websiteUrl, String websiteKey) {
        try {
            RecaptchaV2Proxyless api = new RecaptchaV2Proxyless();
            api.setClientKey(antiCaptchaApiKey);
            api.setWebsiteUrl(new URL(websiteUrl));
            api.setWebsiteKey(websiteKey);

            if (!api.createTask()) {
                System.err.println("Falha ao enviar a tarefa para a API: " + api.getErrorMessage());
                return null;
            }

            if (!api.waitForResult()) {
                System.err.println("Não foi possível resolver o Captcha.");
                return null;
            }

            return api.getTaskSolution().getGRecaptchaResponse();
        } catch (MalformedURLException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}