package cz.itnetwork.controllers;

import cz.itnetwork.models.Insurance;
import cz.itnetwork.models.InsuredPerson;
import cz.itnetwork.services.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InsuranceController {

    @Autowired
    private InsuredPersonController insuredPersonController;

    @Autowired
    private InsuranceService insuranceService;

    /**
     * Odkazuje na první stránku se stránkováním založených pojištění
     * @param model
     * @return
     */
    @GetMapping("/insurances")
    public String viewInsurances(Model model) {
        return findPaginated(1, model);
    }

    /**
     * hlavní HTML se stránkováním seznamu pojištění
     * @param pageNumber
     * @param model
     * @return
     */
    @GetMapping("/insurances/{pageNumber}")
    public String findPaginated(@PathVariable int pageNumber, Model model) {
        int pageSize = 10;
        int totalPages = 0;
        List<String[]> page = new ArrayList<>();

        try {
            page = insuranceService.findPaginated(pageNumber, pageSize);
            totalPages = insuranceService.getTotalPages(pageSize);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("activePage", "insurance");
        model.addAttribute("title", "Pojištění");
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("insurances", page);
        return "/pages/insurances/index";
    }

    /**
     * Pohled s formulářem pro vytvoření nového pojištění
     * @param ipId - ID pojistníka
     * @param model
     * @return
     */
    @GetMapping("/insurances/add/{ipId}")
    public String viewAddInsurance(@PathVariable String ipId, Model model) {
        model.addAttribute("title", "Vytvoř pojištění");
        model.addAttribute("activePage", "insurance");
        model.addAttribute("insuranceForm", new Insurance());
        model.addAttribute("ip", insuredPersonController.getInsuredPersonById(ipId));
        return "/pages/insurances/add_insurance";
    }

    /**
     * Metoda, která přijímá vyplněný formulář s novým pojištěním
     * @param ipId
     * @param insurance
     * @param model
     * @return
     */
    @PostMapping("/insurances/add/{ipId}")
    public String addInsurance(@PathVariable String ipId, @ModelAttribute Insurance insurance, Model model) {
        boolean result = false;
        model.addAttribute("title", "Nové pojištění");
        model.addAttribute("activePage", "insurance");

        try {
            insuranceService.addInsurance(ipId, insurance, model);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return insuredPersonController.viewInsuredPersonDetail(ipId, model);
    }

    /**
     * Metoda, která přijímá požadavek na odstranění pojištění
     * @param source - odkazuje na stránku, ze které byl požadavek přijat, aby byl uživatel přesměrován zpět na tuto stránku
     * @param ipId - ID pojistníka
     * @param insuranceId - ID pojištění
     * @param model
     * @return
     */
    @GetMapping("/insurances/delete/{source}/{ipId}/{insuranceId}")
    public String deleteInsurance(@PathVariable String source, @PathVariable String ipId, @PathVariable String insuranceId, Model model) {
        try {
            insuranceService.deleteInsurance(insuranceId);
            model.addAttribute("dangerMessage", "Pojištění bylo odstraněno");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        switch (source) {
            case "detail": return insuranceDetail(insuranceId, model);
            case "ip": return insuredPersonController.viewInsuredPersonDetail(ipId, model);
        }

        return viewInsurances(model);
    }

    /**
     * Pohled na stránku s formulářem pro editaci pojištění
     * @param source - odkazuje na stránku, ze které byl požadavek přijat, aby byl uživatel přesměrován zpět na tuto stránku
     * @param ipId - ID pojistníka
     * @param insuranceId - ID pojištění
     * @param model
     * @return
     */
    @GetMapping("/insurances/{source}/{ipId}/{insuranceId}")
    public String viewEditInsurance(@PathVariable String source, @PathVariable String ipId, @PathVariable String insuranceId, Model model) {
        model.addAttribute("title", "Uprav pojištění");
        model.addAttribute("activePage", "insurance");
        model.addAttribute("source", source);

        Insurance insurance = new Insurance();
        InsuredPerson ip = new InsuredPerson();
        try {
            ip = insuredPersonController.getInsuredPersonById(ipId);
            insurance = insuranceService.getInsuranceByID(insuranceId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("insuranceForm", insurance);
        model.addAttribute("ip", ip);

        return "/pages/insurances/edit_insurance";
    }

    /**
     * Přijímá vyplněný formulář s úpravami pojištění
     * @param source - odkazuje na stránku, ze které byl požadavek přijat, aby byl uživatel přesměrován zpět na tuto stránku
     * @param ipId - ID pojistníka
     * @param insuranceId - ID pojištění
     * @param insurance
     * @param model
     * @return
     */
    @PostMapping("/insurances/edit/{source}/{ipId}/{insuranceId}")
    public String editInsurance(@PathVariable String source, @PathVariable String ipId, @PathVariable String insuranceId, @ModelAttribute Insurance insurance, Model model) {
        try {
            insuranceService.editInsurance(insuranceId, insurance, model);
            model.addAttribute("successMessage", "Pojištění bylo upraveno");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        switch (source) {
            case "detail": return insuranceDetail(insuranceId, model);
            case "index": return viewInsurances(model);
        }
        return insuredPersonController.viewInsuredPersonDetail(ipId, model);
    }

    /**
     * Vrací pohled s detailem o daném pojištění
     * @param insuranceId - ID pojištění
     * @param model
     * @return
     */
    @GetMapping("/insurances/detail/{insuranceId}")
    public String insuranceDetail(@PathVariable String insuranceId, Model model) {
        model.addAttribute("activePage", "insurance");
        Insurance insurance = new Insurance();
        InsuredPerson insuredPerson = new InsuredPerson();

        try {
            insurance = insuranceService.getInsuranceByID(insuranceId);
            insuredPerson = insuredPersonController.getInsuredPersonById(String.valueOf(insurance.getIpId()));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("insurance", insurance);
        model.addAttribute("ip", insuredPerson);

        return "/pages/insurances/insurance_detail";
    }
}
