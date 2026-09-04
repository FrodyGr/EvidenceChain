package io.github.frodygr.evidencechain.core.export;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ForensicReportExporterTest {

    @Test
    @DisplayName("Should generate court-admissible forensic certificate with Perito Informático attestation")
    void shouldGenerateForensicCertificate() {
        EvidenceChainLedger ledger = new EvidenceChainLedger();

        ledger.record("auditor-agent", "CONTRACT_DEPLOYED", "vault-01", Map.of("version", "1.0"));
        ledger.record("finance-officer", "TRANSACTION_EXECUTED", "treasury", Map.of("currency", "EUR", "val", "100000"));

        ForensicReportExporter exporter = new ForensicReportExporter();
        String cert = exporter.generateCertificate(ledger, "AUDIT-CASE-2026-004");

        assertThat(cert).contains("DIGITAL FORENSIC AUDIT TRAIL CERTIFICATE");
        assertThat(cert).contains("AUDIT-CASE-2026-004");
        assertThat(cert).contains("Carlos Expósito - Perito Informático Colegiado Nº 03624");
        assertThat(cert).contains("VERIFIED (MATHEMATICALLY INTACT)");
        assertThat(cert).contains("CONTRACT_DEPLOYED");
        assertThat(cert).contains("TRANSACTION_EXECUTED");
    }
}
