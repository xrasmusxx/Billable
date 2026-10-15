package ee.tak24.billable.common;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final ZoneId TALLINN = ZoneId.of("Europe/Tallinn");

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now(TALLINN));
        return "home";
    }
}