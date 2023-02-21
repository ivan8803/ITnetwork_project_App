package cz.itnetwork.controllers;

import cz.itnetwork.models.Insurance;
import cz.itnetwork.models.InsuredPerson;
import cz.itnetwork.services.InsuredPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.PSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ips")
public class InsuredPersonController {

    @Autowired
    private InsuredPersonService insuredPersonService;

    /**
     * Přesměrovává na hlavní stránku se seznamem uživatelů
     * @param model - s informací o aktivní stránce
     * @return = vrací na první list stránkování uživatelů
     */
    @GetMapping
    public String viewInsuredPersons(Model model) {
        return findPaginated(1, model);
    }

    /**
     * Hlavní HTML se stránkováním seznamu uživatelů
     * @param pageNumber
     * @param model
     * @return
     */
    @GetMapping("/{pageNumber}")
    public String findPaginated(@PathVariable int pageNumber, Model model) {
        int pageSize = 3;
        int totalPages = 0;
        List<InsuredPerson> page = new ArrayList<>();

        try {
            page = insuredPersonService.findPaginated(pageNumber, pageSize);
            totalPages = insuredPersonService.getTotalPages(pageSize);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("activePage", "ips");
        model.addAttribute("title", "Pojištěnci");
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("ips", page);
        return "/pages/ips/index";
    }

    /**
     * Přesměrovává na stránku s formulářem pro zadání nového pojistníka
     * @param model
     * @return
     */
    @GetMapping("/add_ip")
    public String viewAddInsuredPerson(Model model) {
        model.addAttribute("title", "Vlož pojištěnce");
        model.addAttribute("activePage", "ips");
        model.addAttribute("insuredPersonForm", new InsuredPerson());
        model.addAttribute("header", "Přidej pojištěnce");
        return "/pages/ips/add_ip";
    }

    /**
     * Přijímá vyplněný formulář s novým pojistníkem
     * @param insuredPerson
     * @param model
     * @return
     */
    @PostMapping("/add")
    public String addInsuredPerson(@ModelAttribute InsuredPerson insuredPerson, Model model) {
        try {
            insuredPersonService.addInsuredPerson(insuredPerson);
            model.addAttribute("successMessage", "Pojištěnec byl uložen");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return viewInsuredPersons(model);
    }

    /**
     * Vrací pohled s formulářem pro editaci uživatele
     * @param source - parametr odkazující na stránku, odkud byla editace provedena, aby byl uživatel vrácen zpět na tuto stránku
     * @param ipId - ID pojistníka
     * @param model
     * @return
     */
    @GetMapping("/{source}/{ipId}")
    public String viewEditInsuredPerson(@PathVariable String source, @PathVariable String ipId, Model model) {
        model.addAttribute("title", "Uprav pojištěnce");
        model.addAttribute("activePage", "ips");
        model.addAttribute("header", "Uprav Pojištěnce");
        model.addAttribute("source", source);

        InsuredPerson insuredPerson = new InsuredPerson();
        try {
            insuredPerson = insuredPersonService.getInsuredPersonByID(ipId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("insuredPersonForm", insuredPerson);

        return "/pages/ips/edit_ip";
    }

    /**
     * Přijímá vyplněný formulář s úpravami pojištěnce
     * @param source - parametr odkazující na stránku, odkud byla editace provedena, aby byl uživatel vrácen zpět na tuto stránku
     * @param ipId - ID pojistníka
     * @param insuredPerson
     * @param model
     * @return
     */
    @PostMapping("/edit/{source}/{ipId}")
    public String editInsuredPerson(@PathVariable String source, @PathVariable String ipId, @ModelAttribute InsuredPerson insuredPerson, Model model) {
        try {
            insuredPersonService.editInsuredPerson(ipId, insuredPerson);
            model.addAttribute("successMessage", "Pojištěnec byl upraven");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (source.equals("detail"))
            return viewInsuredPersonDetail(ipId, model);
        else
            return viewInsuredPersons(model);
    }

    /**
     * Metoda, která přijímá požadavek na odstranění uživatele. Odstranění všech pojištění,
     * která jsou svázána s uživatelem je řešen na úrovni databáze pomocí triggeru
     * @param ipId - ID pojistníka
     * @param model
     * @return
     */
    @GetMapping("/delete_ip/{ipId}")
    public String deleteInsuredPerson(@PathVariable String ipId, Model model) {
        try {
            insuredPersonService.deleteInsuredPerson(ipId);
            model.addAttribute("dangerMessage", "Pojištěnec byl odstraněn");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return viewInsuredPersons(model);
    }

    /**
     * Zobrazuje stránku s detailem pojištěnce
     * @param ipId - ID pojistníka
     * @param model
     * @return
     */
    @GetMapping("/ip/{ipId}")
    public String viewInsuredPersonDetail(@PathVariable String ipId, Model model) {
        InsuredPerson ip = new InsuredPerson();
        List<Insurance> insurances = new ArrayList<>();
        try {
            ip = insuredPersonService.getInsuredPersonByID(ipId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            insurances = insuredPersonService.getIPInsurances(ipId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        model.addAttribute("activePage", "ips");
        model.addAttribute("title", "Detail pojištěnce");
        model.addAttribute("ip", ip);
        model.addAttribute("insurances", insurances);
        return "/pages/ips/detail_ip";
    }

    /**
     * Vrací instanci pojistníka. Pomocná metoda pro třídu InsuranceController
     * @param ipId - ID pojistníka
     * @return
     */
    public InsuredPerson getInsuredPersonById(String ipId) {
        InsuredPerson insuredPerson = new InsuredPerson();
        try {
            insuredPerson = insuredPersonService.getInsuredPersonByID(ipId);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return insuredPerson;
    }
}
