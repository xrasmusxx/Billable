package ee.tak24.billable.common;

import ee.tak24.billable.client.Client;
import ee.tak24.billable.client.ClientRepository;
import ee.tak24.billable.project.BillingType;
import ee.tak24.billable.project.Project;
import ee.tak24.billable.project.ProjectRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Inserts fixed development data. Runs only with the "dev" profile, and only into an empty
 * database.
 */
@Component
@Profile("dev")
@Order(1)
class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private static final ZoneId TALLINN = ZoneId.of("Europe/Tallinn");

    private final ClientRepository clients;
    private final ProjectRepository projects;

    DevDataSeeder(ClientRepository clients, ProjectRepository projects) {
        this.clients = clients;
        this.projects = projects;
    }

    @Override
    public void run(String... args) {
        if (clients.count() > 0) {
            log.info("Development data already present, skipping seed");
            return;
        }

        Client acme = clients.save(new Client("Acme OÜ", "billing@acme.test", "EE101234567"));
        projects.save(hourly(acme, "Website redesign", 6500, 15));
        projects.save(capped(acme, "Support retainer", 6000, 200000, 6));
        Project intranet = hourly(acme, "Old intranet", 5000, 1);
        intranet.setArchivedAt(LocalDateTime.now(TALLINN).minusMonths(3));
        projects.save(intranet);

        Client birch = clients.save(new Client("Birch & Daughters", "accounts@birch.test", null));
        projects.save(fixed(birch, "Logo and brand book", 150000, 1));
        projects.save(hourly(birch, "Webshop maintenance", 5500, 15));

        Client kala = clients.save(new Client("Kuressaare Kala AS", "arved@kala.test", "EE100987654"));
        projects.save(fixed(kala, "Stock app MVP", 480000, 15));
        projects.save(capped(kala, "Data migration", 7000, 350000, 6));

        log.info("Seeded {} clients and {} projects", clients.count(), projects.count());
    }

    private static Project hourly(Client client, String name, long rateCents, int rounding) {
        Project project = new Project(client, name, BillingType.HOURLY);
        project.setHourlyRateCents(rateCents);
        project.setRoundingMinutes((short) rounding);
        return project;
    }

    private static Project fixed(Client client, String name, long priceCents, int rounding) {
        Project project = new Project(client, name, BillingType.FIXED);
        project.setFixedPriceCents(priceCents);
        project.setRoundingMinutes((short) rounding);
        return project;
    }

    private static Project capped(
            Client client, String name, long rateCents, long capCents, int rounding) {
        Project project = new Project(client, name, BillingType.CAPPED);
        project.setHourlyRateCents(rateCents);
        project.setBudgetCapCents(capCents);
        project.setRoundingMinutes((short) rounding);
        return project;
    }
}
