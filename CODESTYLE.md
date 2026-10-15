<!-- CODESTYLE.md -->
# Code standard — Billable

**Agreed by the group on 2026-10-07** (lesson 02). Changes need a group decision and a new date
in the change log at the end of this file.

## 1. Tools

| What | Tool | Command |
|---|---|---|
| Format all Java files | Spotless + google-java-format (`pom.xml`) | `./mvnw spotless:apply` |
| Check formatting | Spotless, bound to `validate` — every build checks it | `./mvnw spotless:check` |
| Full check (CI does this) | Maven | `./mvnw verify` |
| Editor settings | `.editorconfig` | automatic (VS Code: install the EditorConfig extension) |

Run `./mvnw spotless:apply` before every commit. A pull request with a red CI check is not reviewed.

The formatter is the authority on layout. We do not discuss formatting in reviews.

## 2. Language

All identifiers, comments, commit messages and documentation are in **English**. We use the domain
words from PROJECT.md: client, project, time entry, tag, invoice, invoice line.

## 3. Naming

| Thing | Convention | Example |
|---|---|---|
| Class, interface, record, enum | UpperCamelCase noun | `InvoiceGenerator`, `BillingType` |
| Method | lowerCamelCase verb | `calculateVat()` |
| Boolean method | question | `isDraft()`, `hasBudgetCap()` |
| Variable, field | lowerCamelCase, with unit where relevant | `amountCents`, `roundedMinutes` |
| Constant | UPPER_SNAKE_CASE | `VAT_RATE_BP` |
| Enum constant | UPPER_SNAKE_CASE | `BillingType.HOURLY` |
| Package | lowercase, singular, by feature | `ee.ta25.billable.timeentry` |
| Table | plural snake_case | `time_entries` |
| Column | snake_case | `hourly_rate_cents` |
| Migration | `V<n>__<description>.sql` | `V2__create_projects.sql` |
| URL | lowercase, plural, hyphens | `/time-entries` |
| Template | folder per feature | `templates/clients/index.html` |
| Test method | **lowerCamelCase describing the behaviour** (vote) | `cappedBillingNeverExceedsCap()` |

## 4. Rules

- Controllers stay thin: no calculations, no business rules. Templates contain no logic beyond
  display conditions.
- Money is always integer cents (`long`). Never `float` or `double` for money.
- Constructor injection only. No `@Autowired` on fields.
- No `System.out.println`, `printStackTrace()` or commented-out code in committed code. Use a logger.
- `jakarta.*` imports only (never `javax.persistence` / `javax.validation`).
- Javadoc: **required on public methods of domain classes** (`billing`, `invoice`, `common` value
  objects, services); optional elsewhere (vote).

## 5. Commits

Conventional Commits: `type(scope): summary` in imperative mood, first line under 72 characters.
Types: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`, `ci`.
Scopes: `home`, `clients`, `projects`, `time-entries`, `billing`, `invoices`, `reports`.
Formatting-only changes go in their own `style:` commit.

## 6. Branches, pull requests and review

- No direct commits to `main`. Every change goes through a branch and a pull request.
- Branch names: `type/short-description`, e.g. `feature/client-list`, `fix/vat-rounding`.
- A pull request needs a **green CI check** and **one approving review from a classmate** before merge.
- Merge method: **Squash and merge** (vote). The squash commit message follows section 5.
- Reviews comment on correctness, names, structure, tests, security and docs — not on formatting.
- The author replies to every review comment.

## Change log

| Date | Change |
|---|---|
| 2026-10-07 | First version agreed by the group. |
