package ee.tak24.billable.project;

/** One row of {@link ProjectRepository#countByClientIds}: a client id and its number of projects. */
public interface ClientProjectCount {

    Long getClientId();

    long getProjectCount();
}