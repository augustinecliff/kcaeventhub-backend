package tiketihub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static tiketihub.controller.baseMappingConfig.base;

@Controller
@RequestMapping()
public class HomeController {

    @GetMapping
    public String pageData() {
        return "redirect:/home";
    }
}
