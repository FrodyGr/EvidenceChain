# Getting Started with EvidenceChain

This guide demonstrates setting up EvidenceChain in a Java 21 application and Spring Boot 3 microservice.

---

## 1. Maven Dependency

```xml
<dependency>
    <groupId>io.github.frodygr</groupId>
    <artifactId>evidencechain-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

For Spring Boot applications:

```xml
<dependency>
    <groupId>io.github.frodygr</groupId>
    <artifactId>evidencechain-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

## 2. Core Java Example

```java
import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.core.export.ForensicReportExporter;
import io.github.frodygr.evidencechain.core.verifier.VerificationReport;

import java.util.Map;

public class AuditDemo {

    public static void main(String[] args) {
        EvidenceChainLedger ledger = new EvidenceChainLedger();

        // 1. Record sensitive events
        ledger.record("dr_smith", "ACCESS_PATIENT_CHART", "Patient/9912", Map.of("diagnosis", "CONSULTATION"));
        ledger.record("dr_smith", "PRESCRIBE_MEDICATION", "Patient/9912", Map.of("rx", "AMOXICILLIN_500MG"));

        // 2. Perform automated cryptographic verification
        VerificationReport report = ledger.verify();
        System.out.println("Valid: " + report.valid()); // true
        System.out.println("Root:  " + report.computedMerkleRoot());

        // 3. Export evidentiary certificate
        ForensicReportExporter exporter = new ForensicReportExporter();
        String certificate = exporter.generateCertificate(ledger, "HIPAA-AUDIT-2026-Q3");
        System.out.println(certificate);
    }
}
```

---

## 3. Spring Boot 3 Example

```java
@Service
public class PatientRecordService {

    @AuditedEvidence(action = "UPDATE_MEDICAL_RECORD", resource = "EHR_DATABASE")
    public void updateDiagnosis(String patientId, String diagnosisNotes) {
        // Business logic runs normally
        // Arguments and timestamp are cryptographically hashed and linked to the ledger!
    }
}
```
