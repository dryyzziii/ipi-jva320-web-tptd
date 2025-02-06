package com.ipi.jva320.controller;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.service.SalarieAideADomicileService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/salaries")
public class SalarieController {

    @Autowired
    private SalarieAideADomicileService salarieService;

    @GetMapping("")
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(defaultValue = "nom") String sortProperty,
            @RequestParam(required = false) String nom,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection), sortProperty);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SalarieAideADomicile> salariePage;

        if (nom != null && !nom.trim().isEmpty()) {
            // Use service method that returns Page for filtered results
            salariePage = salarieService.getSalaries(nom, pageable);
            if (salariePage.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun salarié trouvé avec le nom: " + nom);
            }
        } else {
            salariePage = salarieService.getSalaries(pageable);
        }

        model.addAttribute("salaries", salariePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", salariePage.getTotalPages());
        model.addAttribute("totalItems", salariePage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("sortProperty", sortProperty);
        model.addAttribute("reverseSortDir", sortDirection.equals("ASC") ? "DESC" : "ASC");

        int startCount = page * size + 1;
        int endCount = Math.min((page * size + size), (int) salariePage.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        Long nbSalaries = salarieService.countSalaries();
        model.addAttribute("nbSalaries", nbSalaries);

        return "list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // Récupération du salarié depuis la base de données
        SalarieAideADomicile salarie = salarieService.getSalarie(id);

        // Si le salarié n'existe pas, on renvoie une 404
        if (salarie == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarié non trouvé");
        }

        model.addAttribute("salarie", salarie);

        Long nbSalaries = salarieService.countSalaries();
        model.addAttribute("nbSalaries", nbSalaries);
        return "detail_Salarie";
    }   

    @GetMapping("/aide/new")
    public String showCreateForm(Model model) {
        // On crée un salarié vide pour le formulaire
        SalarieAideADomicile newSalarie = new SalarieAideADomicile();
        model.addAttribute("salarie", newSalarie);
        return "detail_Salarie";
    }

    // Traite la soumission du formulaire
    @PostMapping("/save")
    public String saveSalarie(@Valid @ModelAttribute("salarie") SalarieAideADomicile salarie,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "detail_Salarie";
        }

        try {
            SalarieAideADomicile saved = salarieService.creerSalarieAideADomicile(salarie);
            return "redirect:/salaries/" + saved.getId();
        } catch (SalarieException e) {
            result.addError(new ObjectError("salarie", e.getMessage()));
            model.addAttribute("salarie", salarie);
            return "detail_Salarie";
        }
    }

    @PostMapping("/{id}")
    public String updateSalarie(@PathVariable Long id,
            @ModelAttribute @Valid SalarieAideADomicile salarie,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("salarie", salarie);
            return "detail_Salarie";
        }

        try {
            salarie.setId(id);
            SalarieAideADomicile updated = salarieService.updateSalarieAideADomicile(salarie);
            return "redirect:/salaries/" + updated.getId();
        } catch (SalarieException e) {
            result.addError(new ObjectError("salarie", e.getMessage()));
            model.addAttribute("salarie", salarie);
            return "detail_Salarie";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteSalarie(@PathVariable Long id) {
        try {
            salarieService.deleteSalarieAideADomicile(id);
            return "redirect:/salaries";
        } catch (SalarieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}