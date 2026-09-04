package io.github.frodygr.evidencechain.core.export;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.core.model.EvidenceEvent;
import io.github.frodygr.evidencechain.core.verifier.VerificationReport;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporter generating human-readable and court-admissible forensic audit certificates.
 */
public class ForensicReportExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS 'UTC'")
            .withZone(ZoneId.of("UTC"));

    public String generateCertificate(EvidenceChainLedger ledger, String caseReference) {
        VerificationReport report = ledger.verify();
        List<EvidenceEvent> events = ledger.getEvents();

        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("                 DIGITAL FORENSIC AUDIT TRAIL CERTIFICATE                      \n");
        sb.append("                  EVIDENTIARY CHAIN OF CUSTODY REPORT                         \n");
        sb.append("================================================================================\n\n");

        sb.append("CASE REFERENCE:        ").append(caseReference != null ? caseReference : "GENERIC-AUDIT").append("\n");
        sb.append("ISSUANCE TIMESTAMP:    ").append(FORMATTER.format(Instant.now())).append("\n");
        sb.append("METHODOLOGY STANDARD:  RFC 3161 / ISO 27037 (Digital Evidence Handling)\n");
        sb.append("CRYPTOGRAPHIC SCHEME:  SHA-256 Binary Merkle Tree & Linked Ledger\n");
        sb.append("LEAD EXPERT / AUDITOR: Carlos Expósito - Perito Informático Colegiado Nº 03624\n\n");

        sb.append("--------------------------------------------------------------------------------\n");
        sb.append("1. CRYPTOGRAPHIC VERIFICATION SUMMARY\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append("Status:                ").append(report.valid() ? "VERIFIED (MATHEMATICALLY INTACT)" : "TAMPERED / FAILED").append("\n");
        sb.append("Total Events Verified: ").append(report.totalEventsVerified()).append("\n");
        sb.append("Final Merkle Root:     ").append(report.computedMerkleRoot()).append("\n");
        sb.append("Latest Block Hash:     ").append(ledger.getLatestHash()).append("\n\n");

        if (!report.valid()) {
            sb.append("DISCREPANCIES DETECTED:\n");
            for (String disc : report.discrepancies()) {
                sb.append("  [!] ").append(disc).append("\n");
            }
            sb.append("\n");
        }

        sb.append("--------------------------------------------------------------------------------\n");
        sb.append("2. CHRONOLOGICAL EVENT REGISTER (CHAIN OF CUSTODY)\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-24s | %-16s | %-20s | %-16s%n", "SEQ", "TIMESTAMP", "ACTOR", "ACTION", "HASH (PREFIX)"));
        sb.append("--------------------------------------------------------------------------------\n");

        for (int i = 0; i < events.size(); i++) {
            EvidenceEvent e = events.get(i);
            String timeStr = FORMATTER.format(e.timestamp());
            String hashPrefix = e.eventHash().substring(0, Math.min(16, e.eventHash().length())) + "...";
            sb.append(String.format("%-4d | %-24s | %-16s | %-20s | %-16s%n",
                i + 1, timeStr, truncate(e.actor(), 16), truncate(e.action(), 20), hashPrefix
            ));
        }

        sb.append("\n================================================================================\n");
        sb.append("                        FORENSIC EXPERT ATTESTATION                             \n");
        sb.append("================================================================================\n");
        sb.append("I hereby attest that the digital records above have been processed through an   \n");
        sb.append("immutable cryptographic ledger. Any mathematical deviation in hash continuity  \n");
        sb.append("or Merkle root calculations demonstrates physical alteration of the underlying  \n");
        sb.append("data stores.                                                                    \n");
        sb.append("                                                                                \n");
        sb.append("Certified by: Carlos Expósito (Perito Informático Colegiado Nº 03624)          \n");
        sb.append("================================================================================\n");

        return sb.toString();
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
