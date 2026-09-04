# Configuration Reference

This guide details all configuration options for EvidenceChain in Spring Boot and standalone Java.

---

## Spring Boot Configuration (`application.yml`)

```yaml
evidencechain:
  # Master toggle for EvidenceChain auto-configuration
  enabled: true

  # Default case reference printed on forensic audit reports
  case-reference: "AUDIT-CASE-2026-Q3"

  # Whether to automatically generate and print an evidentiary certificate when Spring context closes
  auto-export-on-shutdown: false
```
