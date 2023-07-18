package tiketihub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tiketihub.data.OrganizedEventRepository;
import tiketihub.entity.OrganizedEvent;

import static tiketihub.controller.baseMappingConfig.base;

@Controller
@RequestMapping()
public class HomeController {
    private final OrganizedEventRepository organizedEventRepo;

    public HomeController(OrganizedEventRepository organizedEventRepo) {
        this.organizedEventRepo = organizedEventRepo;
    }

    @GetMapping
    public String none() {
        return "redirect:"+base+"/home";
    }

    @GetMapping(base+"/home")
    public String pageData() {
        return "home";
    }

}
