package com.ipi.jva320.controller;

import com.ipi.jva320.service.SalarieAideADomicileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HomeController {

    @Autowired
    private SalarieAideADomicileService salarieService;

    @GetMapping("/")
    public String home(Model model) {
        Long nbSalaries = salarieService.countSalaries();
        model.addAttribute("nbSalaries", nbSalaries);
        return "home";
    }
}
