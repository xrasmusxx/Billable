package ee.tak24.billable.project;

import jakarta.persistence.EnumeratedValue;

/** How a project is billed. The database stores {@link #dbValue()}. */
public enum BillingType {
    HOURLY("hourly", "Hourly"),
    FIXED("fixed", "Fixed price"),
    CAPPED("capped", "Capped hourly");

    @EnumeratedValue private final String dbValue;
    private final String label;

    BillingType(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    /** The value stored in {@code projects.billing_type}. */
    public String dbValue() {
        return dbValue;
    }

    /** Human-readable name for pages and forms. */
    public String label() {
        return label;
    }
}
