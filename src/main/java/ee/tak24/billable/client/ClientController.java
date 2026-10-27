package ee.tak24.billable.client;

import ee.tak24.billable.common.NotFoundException;
import ee.tak24.billable.project.ClientProjectCount;
import ee.tak24.billable.project.ProjectRepository;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Client client = findClient(id);
        model.addAttribute("client", client);
        model.addAttribute("projects", projects.findByClientIdOrderByName(id));
        return "clients/show";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("form", new ClientForm());
        return "clients/create";
    }

    @PostMapping
    public String store(
            @Valid @ModelAttribute("form") ClientForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (form.getEmail() != null && clients.existsByEmailIgnoreCase(form.getEmail())) {
            bindingResult.rejectValue("email", "duplicate", "This e-mail is already used by another client.");
        }
        if (bindingResult.hasErrors()) {
            return "clients/create";
        }

        var client = clients.save(new Client(form.getName(), form.getEmail(), form.getVatNumber()));

        redirectAttributes.addFlashAttribute("status", "Client " + client.getName() + " created.");
        return "redirect:/clients/" + client.getId();
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        var client = findClient(id);
        model.addAttribute("client", client);
        model.addAttribute("form", ClientForm.from(client));
        return "clients/edit";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable long id,
            @Valid @ModelAttribute("form") ClientForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var client = findClient(id);

        if (form.getEmail() != null && clients.existsByEmailIgnoreCaseAndIdNot(form.getEmail(), id)) {
            bindingResult.rejectValue("email", "duplicate", "This e-mail is already used by another client.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("client", client);
            return "clients/edit";
        }

        client.update(form.getName(), form.getEmail(), form.getVatNumber());
        clients.save(client);

        redirectAttributes.addFlashAttribute("status", "Client updated.");
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable long id, RedirectAttributes redirectAttributes) {
        var client = findClient(id);
        long projectCount = projects.countByClientId(id);

        if (projectCount > 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    client.getName()
                            + " still has "
                            + projectCount
                            + " project(s). Delete or archive them first.");
            return "redirect:/clients/" + id;
        }

        clients.delete(client);

        redirectAttributes.addFlashAttribute("status", "Client " + client.getName() + " deleted.");
        return "redirect:/clients";
    }

    private Client findClient(long id) {
        return clients
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Client " + id + " not found"));
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
