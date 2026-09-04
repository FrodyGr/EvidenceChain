# Welcome to the EvidenceChain Technical Documentation

**EvidenceChain** is a production-grade cryptographic audit ledger and legal-tech library designed for **Java 21** and **Spring Boot 3.3+**. It establishes mathematical chain-of-custody for high-assurance enterprise systems under the technical direction of a **Perito Informático Colegiado (Certified Judicial Computer Expert Nº 03624)**.

---

## Architectural Principles

1. **Mathematical Immutability**: Traditional relational audit tables are vulnerable to `UPDATE` or `DELETE` statements by privileged accounts. EvidenceChain links records with SHA-256 parent hashes and Merkle root calculation, guaranteeing that altering a single character makes mathematical verification fail.
2. **Selective Disclosure via $O(\log N)$ Merkle Proofs**: Verify that a specific transaction was committed without leaking other transactions or confidential records in the tree.
3. **Legal Evidentiary Value**: Built with explicit adherence to ISO/IEC 27037 (Guidelines for identification, collection, acquisition and preservation of digital evidence) and UNE 71506 standards.
4. **Zero-Latency In-Memory Operation**: Append-only lock-free concurrent execution that does not block core transactional throughput.

---

## Quick Navigation

* [Getting Started Guide](Getting-Started)
* [Complete API Reference](API-Reference)
* [Forensic Methodology & Standards](Forensic-Methodology-and-Standards)
* [Configuration Reference](Configuration-Reference)
