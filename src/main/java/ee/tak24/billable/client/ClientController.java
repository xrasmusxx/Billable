package ee.tak24.billable.client;

import ee.tak24.billable.common.NotFoundException;
import ee.tak24.billable.project.ClientProjectCount;
import ee.tak24.billable.project.ProjectRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientRepository clients;
    private final ProjectRepository projects;

    ClientController(ClientRepository clients, ProjectRepository projects) {
        this.clients = clients;
        this.projects = projects;
    }

    @GetMapping
    public String index(
            @PageableDefault(size = 10, sort = {"name", "id"}) Pageable pageable, Model model) {
        Page<Client> page = clients.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("projectCounts", projectCountsFor(page.getContent()));
        return "clients/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Client client =
                clients.findById(id).orElseThrow(() -> new NotFoundException("Client " + id + " not found"));
        model.addAttribute("client", client);
        model.addAttribute("projects", projects.findByClientIdOrderByName(id));
        return "clients/show";
    }

    /** Number of projects per client id, for one page of clients, in a single query. */
    private Map<Long, Long> projectCountsFor(List<Client> pageOfClients) {
        Map<Long, Long> counts = new HashMap<>();
        if (pageOfClients.isEmpty()) {
            return counts;
        }
        List<Long> ids = pageOfClients.stream().map(Client::getId).toList();
        ids.forEach(id -> counts.put(id, 0L));
        for (ClientProjectCount row : projects.countByClientIds(ids)) {
            counts.put(row.getClientId(), row.getProjectCount());
        }
        return counts;
    }
}