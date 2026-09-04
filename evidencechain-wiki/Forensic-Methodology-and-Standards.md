# Forensic Methodology and Legal Standards

EvidenceChain is built with explicit alignment to international digital forensics standards under the technical oversight of a certified judicial computer expert (**Perito Informático Colegiado Nº 03624**).

---

## 1. Relevant Forensic Standards

* **ISO/IEC 27037**: Guidelines for identification, collection, acquisition, and preservation of digital evidence.
* **RFC 3161**: Internet X.509 Public Key Infrastructure Time-Stamp Protocol (TSP).
* **UNE 71506**: Spanish standard for digital forensic methodology and chain of custody preservation.

---

## 2. Chain of Custody Guarantees

In digital litigation, digital evidence must satisfy three criteria to be admissible:
1. **Authenticity**: Evidence must originate from the declared source.
2. **Integrity**: Evidence must remain unmolested from the instant of generation to the moment of examination.
3. **Continuity**: Every handoff and aggregation step must be verifiable.

EvidenceChain satisfies all three via SHA-256 parent chaining and Merkle trees, rendering database tampering mathematically impossible without detection.
