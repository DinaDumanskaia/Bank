package bank.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;
import java.util.Arrays;

@Controller
public class ThymeleafController {

    private ServletContext servletContext;

    public ThymeleafController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GetMapping
    String getClients(Model model) {
        model.addAttribute("headLine", "DBank Cashier");
        return "clients";
    }
}
