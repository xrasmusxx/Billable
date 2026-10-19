package ee.tak24.billable.project;

import ee.tak24.billable.client.Client;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "projects",
    indexes = @Index(name = "projects_client_id_index", columnList = "client_id")
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false, length = 120)
    private String name;

    @Convert(converter = BillingTypeConverter.class)
    @Column(name = "billing_type", nullable = false, length = 20)
    private BillingType billingType;

    @Column(name = "hourly_rate_cents")
    private Long hourlyRateCents;

    @Column(name = "fixed_price_cents")
    private Long fixedPriceCents;

    @Column(name = "budget_cap_cents")
    private Long budgetCapCents;

    @Column(name = "rounding_minutes", nullable = false)
    private Short roundingMinutes = 1;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Project() {}

    public Project(Client client, String name, BillingType billingType) {
        this.client = client;
        this.name = name;
        this.billingType = billingType;
        this.roundingMinutes = 1;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BillingType getBillingType() {
        return billingType;
    }

    public void setBillingType(BillingType billingType) {
        this.billingType = billingType;
    }

    public Long getHourlyRateCents() {
        return hourlyRateCents;
    }

    public void setHourlyRateCents(Long hourlyRateCents) {
        this.hourlyRateCents = hourlyRateCents;
    }

    public Long getFixedPriceCents() {
        return fixedPriceCents;
    }

    public void setFixedPriceCents(Long fixedPriceCents) {
        this.fixedPriceCents = fixedPriceCents;
    }

    public Long getBudgetCapCents() {
        return budgetCapCents;
    }

    public void setBudgetCapCents(Long budgetCapCents) {
        this.budgetCapCents = budgetCapCents;
    }

    public Short getRoundingMinutes() {
        return roundingMinutes;
    }

    public void setRoundingMinutes(Short roundingMinutes) {
        this.roundingMinutes = roundingMinutes;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Converter(autoApply = false)
    public static class BillingTypeConverter implements AttributeConverter<BillingType, String> {

        @Override
        public String convertToDatabaseColumn(BillingType attribute) {
            return attribute == null ? null : attribute.dbValue();
        }

        @Override
        public BillingType convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }
            for (BillingType billingType : BillingType.values()) {
                if (billingType.dbValue().equals(dbData)) {
                    return billingType;
                }
            }
            throw new IllegalArgumentException("Unknown billing type in database: " + dbData);
        }
    }
}
