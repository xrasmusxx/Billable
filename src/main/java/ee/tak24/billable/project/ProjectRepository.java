package ee.tak24.billable.project;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    long countByClientId(Long clientId);

    List<Project> findByClientIdOrderByName(Long clientId);

    @Query(
            """
            select p.client.id as clientId, count(p) as projectCount
            from Project p
            where p.client.id in :clientIds
            group by p.client.id
            """)
    List<ClientProjectCount> countByClientIds(@Param("clientIds") Collection<Long> clientIds);

    // ...keep the methods you added in the lesson 03 independent work
}