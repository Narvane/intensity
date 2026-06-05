package br.com.narvane.config.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redireciona a raiz "/" para a documentação Swagger.
 */
@Controller
public class RootController {

    @GetMapping("/")
    public String root() {
        return "redirect:/swagger-ui.html";
    }
}
