package br.lukelaw.mvp_luke_law.webscraping.controller;


import br.lukelaw.mvp_luke_law.webscraping.dto.WSRequest;
import br.lukelaw.mvp_luke_law.webscraping.entity.Processo;
import br.lukelaw.mvp_luke_law.webscraping.exception.ProcessNotFoundException;
import br.lukelaw.mvp_luke_law.webscraping.fontes.pje.PjeWebScrapingService;
import br.lukelaw.mvp_luke_law.webscraping.fontes.pjetse.PjeTseWebScrapingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webscraping")
@Tag(name = "WebScrapingController", description = "Endpoints para realizar web scraping do PJE")
public class WebScrapingController {

    @Autowired
    private PjeWebScrapingService webScrapingService;

    @Autowired
    private PjeTseWebScrapingService tseWebScrapingService;

    @PostMapping("/pje")
    public ResponseEntity<Processo> scrapePje(@Valid @NotNull @RequestBody WSRequest request) {
        System.out.println("Received numProcesso: " + request.getNumProcesso());

        var requestProcesso = webScrapingService.scrapePjeUltimoMov(request.getNumProcesso());

        if (requestProcesso == null) {
            throw new ProcessNotFoundException("Processo não encontrado para o número: "
                    + request.getNumProcesso());
        }

        System.out.println("Web scraping concluído e processo encontrado.");
        return ResponseEntity.ok(requestProcesso);
    }


@PostMapping("/pjetse")
public ResponseEntity<Processo> scrapePjeTse(@Valid @NotNull @RequestBody WSRequest request) {
    System.out.println("Received numProcesso: " + request.getNumProcesso());

    Processo requestProcesso = tseWebScrapingService.scrapePjeUltimoMov(request.getNumProcesso());

    if (requestProcesso == null) {
        throw new ProcessNotFoundException("Processo não encontrado para o número: "
                + request.getNumProcesso());
    }

    System.out.println("Web scraping do TSE concluído e processo encontrado.");
    return ResponseEntity.ok(requestProcesso);
}
}




