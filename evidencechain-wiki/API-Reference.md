# Complete API Reference & Developer Cheat-Sheet

This document provides a technical specification of every class, interface, method, verification report, and configuration property in **EvidenceChain**, accompanied by concrete code examples.

---

## Table of Contents

1. [Ledger Manager: `EvidenceChainLedger`](#1-ledger-manager-evidencechainledger)
2. [Event Model: `EvidenceEvent`](#2-event-model-evidenceevent)
3. [Merkle Tree: `MerkleAuditTree`](#3-merkle-tree-merkleaudittree)
4. [Inclusion Proof: `MerkleProof`](#4-inclusion-proof-merkleproof)
5. [Verification Engine: `ChainVerifier`](#5-verification-engine-chainverifier)
6. [Forensic Certificate Exporter: `ForensicReportExporter`](#6-forensic-certificate-exporter-forensicreportexporter)
7. [Spring Boot `@AuditedEvidence` Annotation](#7-spring-boot-auditedevidence-annotation)
8. [Spring Boot Configuration Properties](#8-spring-boot-configuration-properties)

---

## 1. Ledger Manager: `EvidenceChainLedger`

Package: `io.github.frodygr.evidencechain.core.EvidenceChainLedger`

Thread-safe append-only ledger orchestrating record creation, hash chaining, and mathematical verification.

### `.record(String actor, String action, String resource, Map<String, String> metadata)`

* **Signature**: `public EvidenceEvent record(String actor, String action, String resource, Map<String, String> metadata)`
* **Description**: Appends a new immutable audit record to the ledger. Cryptographically computes its SHA-256 hash using sorted metadata and links it to the preceding event's hash.
* **Parameters**:
  * `actor`: Identifier of the user, worker, or service executing the action.
  * `action`: Action identifier (e.g., `"RECORD_EXPORTED"`).
  * `resource`: Identifier of the affected resource.
  * `metadata`: Key-value context attributes.
* **Returns**: Newly created, sealed `EvidenceEvent`.

```java
EvidenceChainLedger ledger = new EvidenceChainLedger();

EvidenceEvent event = ledger.record(
    "admin_usr_10",
    "ROLE_ELEVATION",
    "User/4819",
    Map.of("grantedRole", "SUPER_ADMIN", "ip", "10.0.1.5")
);
```

---

### `.verify()`

* **Signature**: `public VerificationReport verify()`
* **Description**: Runs a full mathematical audit across all chained events in the ledger, validating internal payload integrity, parent hash continuity, and Merkle root calculation.
* **Returns**: A `VerificationReport` with boolean status and detailed discrepancy messages if tampered.

```java
VerificationReport report = ledger.verify();
if (!report.valid()) {
    System.err.println("Audit failed! Discrepancies: " + report.discrepancies());
}
```

---

### `.generateProof(int index)`

* **Signature**: `public MerkleProof generateProof(int index)`
* **Description**: Computes an $O(\log N)$ Merkle audit proof path for the event at `index`. Allows third parties to verify that an event was included in the ledger without reading other records.

```java
MerkleProof proof = ledger.generateProof(0);
boolean isVerified = proof.verify(ledger.verify().computedMerkleRoot());
```

---

## 2. Event Model: `EvidenceEvent`

Package: `io.github.frodygr.evidencechain.core.model.EvidenceEvent`

Immutable record holding complete event coordinates and cryptographic digests:

| Component | Return Type | Description |
| :--- | :--- | :--- |
| `eventId()` | `String` | Unique UUID assigned at creation. |
| `actor()` | `String` | Actor or service that triggered the event. |
| `action()` | `String` | The action performed. |
| `resource()` | `String` | Target resource identifier. |
| `metadata()` | `Map<String, String>` | Sorted, unmodifiable context attributes. |
| `timestamp()` | `Instant` | UTC creation timestamp. |
| `previousEventHash()` | `String` | SHA-256 digest of the immediately preceding event (`"GENESIS"` for first). |
| `eventHash()` | `String` | SHA-256 digest of the full event payload. |
| `verifyIntegrity()` | `boolean` | Recomputes hash locally and returns true if matching `eventHash()`. |

---

## 3. Merkle Tree: `MerkleAuditTree`

Package: `io.github.frodygr.evidencechain.core.merkle.MerkleAuditTree`

Binary Merkle tree implementation computing tree levels and root hash:

```java
MerkleAuditTree tree = MerkleAuditTree.ofEvents(ledger.getEvents());
String root = tree.getRootHash();
```

---

## 4. Inclusion Proof: `MerkleProof`

Package: `io.github.frodygr.evidencechain.core.merkle.MerkleProof`

```java
MerkleProof proof = tree.generateProof(leafIndex);

// Verify membership against known root hash
boolean valid = proof.verify(expectedRootHash);
```

---

## 5. Verification Engine: `ChainVerifier`

Package: `io.github.frodygr.evidencechain.core.verifier.ChainVerifier`

```java
ChainVerifier verifier = new ChainVerifier();
VerificationReport report = verifier.verifyChain(eventsList);
```

---

## 6. Forensic Certificate Exporter: `ForensicReportExporter`

Package: `io.github.frodygr.evidencechain.core.export.ForensicReportExporter`

Generates human-readable digital forensic evidence certificates ready for legal proceedings:

```java
ForensicReportExporter exporter = new ForensicReportExporter();
String cert = exporter.generateCertificate(ledger, "CASE-2026-GDPR-INQUIRY");
```

---

## 7. Spring Boot `@AuditedEvidence` Annotation

Package: `io.github.frodygr.evidencechain.spring.annotation.AuditedEvidence`

```java
@Service
public class PayrollService {

    @AuditedEvidence(action = "DISBURSE_PAYROLL", resource = "Treasury")
    public void disburse(String employeeId, BigDecimal salary) {
        // Automatically recorded to EvidenceChainLedger!
    }
}
```

---

## 8. Spring Boot Configuration Properties

Configure in `application.yml`:

```yaml
evidencechain:
  # Master toggle for automatic evidence auditing
  enabled: true

  # Default case or compliance audit identifier
  case-reference: "CORP-AUDIT-2026"

  # Automatically print forensic certificate to console on application shutdown
  auto-export-on-shutdown: false
```
