package ee.tak24.billable;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ee.tak24.billable.client.Client;
import ee.tak24.billable.client.ClientRepository;
import ee.tak24.billable.project.BillingType;
import ee.tak24.billable.project.Project;
import ee.tak24.billable.project.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.security.user.name=test-user",
		"spring.security.user.password=test-password"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@WithMockUser
class BillableApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ClientRepository clients;

	@Autowired
	private ProjectRepository projects;

	@Test
	void contextLoads() {}

	@Test
	void rendersNewClientForm() throws Exception {
		mockMvc.perform(get("/clients/new"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("New client")))
				.andExpect(content().string(containsString("Create client")));
	}

	@Test
	void rendersClientList() throws Exception {
		mockMvc.perform(get("/clients"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Clients")));
	}

	@Test
	void rendersClientWithProjects() throws Exception {
		var client = clients.save(new Client("Acme OÜ", "acme@example.test", null));
		projects.save(new Project(client, "Website redesign", BillingType.HOURLY));

		mockMvc.perform(get("/clients/{id}", client.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Acme OÜ")))
				.andExpect(content().string(containsString("Website redesign")))
				.andExpect(content().string(containsString("Active")));

		mockMvc.perform(get("/clients/{id}/edit", client.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Edit Acme OÜ")))
				.andExpect(content().string(containsString("Save changes")));
	}

	@Test
	@WithAnonymousUser
	void redirectsAnonymousUsersToLogin() throws Exception {
		mockMvc.perform(get("/clients"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithAnonymousUser
	void logsInWithConfiguredCredentials() throws Exception {
		mockMvc.perform(formLogin().user("test-user").password("test-password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}
}
