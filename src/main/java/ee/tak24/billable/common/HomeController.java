package ee.tak24.billable.common;

import ee.tak24.billable.client.ClientRepository;
import ee.tak24.billable.project.ProjectRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final ZoneId TALLINN = ZoneId.of("Europe/Tallinn");

    private final ClientRepository clients;
    private final ProjectRepository projects;

    HomeController(ClientRepository clients, ProjectRepository projects) {
        this.clients = clients;
        this.projects = projects;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("today", LocalDate.now(TALLINN));
        model.addAttribute("clientCount", clients.count());
        model.addAttribute("projectCount", projects.count());
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
