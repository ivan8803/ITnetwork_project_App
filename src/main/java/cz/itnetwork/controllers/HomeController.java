package cz.itnetwork.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Pohled s uvítací stránkou
     * @param model
     * @return
     */
    @GetMapping("/")
    public String viewHome(Model model) {
        model.addAttribute("activePage", "home");
        return "/pages/home/index";
    }

    /**
     * Pohled s informacemi o aplikaci
     * @param model
     * @return
     */
    @GetMapping("/about_app")
    public String viewAboutApp(Model model) {
        model.addAttribute("activePage", "about_app");
        return "/pages/home/about_app";
    }
}
