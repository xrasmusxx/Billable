package ee.tak24.billable.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Display helpers for templates, e.g. {@code ${@formatters.euros(6500)}}. Temporary: lesson 10
 * replaces the money formatting with {@code Money}.
 */
@Component
public class Formatters {

    private static final Locale ESTONIAN = Locale.of("et", "EE");

    /** Formats integer cents as euros: 150000 → "1 500,00 €", -5 → "−0,05 €". */
    public String euros(long cents) {
        return NumberFormat.getCurrencyInstance(ESTONIAN).format(BigDecimal.valueOf(cents, 2));
    }

    /** Like {@link #euros(long)}, but shows "—" when there is no amount (a nullable column). */
    public String eurosOrDash(Long cents) {
        return cents == null ? "—" : euros(cents);
    }
}