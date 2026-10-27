package ee.tak24.billable.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

/** What the client form sends. Only these fields can be set by the user. */
public class ClientForm {

    @NotBlank(message = "Name is required.")
    @Size(max = 120, message = "Name can be at most 120 characters.")
    private String name;

    @NotBlank(message = "E-mail is required.")
    @Email(message = "Enter a valid e-mail address.")
    @Size(max = 255, message = "E-mail can be at most 255 characters.")
    private String email;

    @Pattern(
            regexp = "^EE\\d{9}$",
            message = "The VAT number must be EE followed by 9 digits, for example EE101234567.")
    private String vatNumber;

    /** Creates a form filled with an existing client's values, for the edit page. */
    public static ClientForm from(Client client) {
        var form = new ClientForm();
        form.setName(client.getName());
        form.setEmail(client.getEmail());
        form.setVatNumber(client.getVatNumber());
        return form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    /** Stores e-mails in lower case, so "Info@Saar.ee" and "info@saar.ee" are the same. */
    public void setEmail(String email) {
        this.email = email == null ? null : email.toLowerCase(Locale.ROOT);
    }

    public String getVatNumber() {
        return vatNumber;
    }

    /** Accepts "ee 101 234 567" and stores "EE101234567". */
    public void setVatNumber(String vatNumber) {
        this.vatNumber =
                vatNumber == null ? null : vatNumber.replace(" ", "").toUpperCase(Locale.ROOT);
    }
}
