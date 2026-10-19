package ee.tak24.billable.common;

import ee.tak24.billable.client.Client;
import ee.tak24.billable.client.ClientRepository;
import ee.tak24.billable.project.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** TEMPORARY: try repository methods at startup. Delete before you finish lesson 03. */
@Component
@Profile("dev")
@Order(2)
class QueryPlayground implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QueryPlayground.class);

    private final ClientRepository clients;
    private final ProjectRepository projects;

    QueryPlayground(ClientRepository clients, ProjectRepository projects) {
        this.clients = clients;
        this.projects = projects;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        long clientCount = clients.count();
        long projectCount = projects.count();
        log.info("You have {} clients and {} projects.", clientCount, projectCount);

        var withA = clients.findByNameContainingIgnoreCaseOrderByName("a");
        log.info("Names containing 'a': {}", withA.stream().map(Client::getName).toList());

        Client acme = clients.findByEmail("billing@acme.test").orElseThrow();
        log.info("Acme has {} projects", projects.countByClientId(acme.getId()));

        var acmeProjects = projects.findByClientIdOrderByName(acme.getId());
        for (var p : acmeProjects) {
            log.info(
                    "  {} ({}), archived: {}",
                    p.getName(),
                    p.getBillingType().label(),
                    p.getArchivedAt() != null);
        }

        log.info("Acme projects via entity: {}", acme.getProjects().size());
    }
}
