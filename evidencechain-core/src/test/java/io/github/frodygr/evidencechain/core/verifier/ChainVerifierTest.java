package io.github.frodygr.evidencechain.core.verifier;

import io.github.frodygr.evidencechain.core.EvidenceChainLedger;
import io.github.frodygr.evidencechain.core.model.EvidenceEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ChainVerifierTest {

    @Test
    @DisplayName("Should successfully verify an untampered evidence chain")
    void shouldVerifyIntactChain() {
        EvidenceChainLedger ledger = new EvidenceChainLedger();

        ledger.record("user-1", "LOGIN", "auth-service", Map.of("ip", "192.168.1.10"));
        ledger.record("user-1", "TRANSFER", "account-4591", Map.of("amount", "250.00"));
        ledger.record("admin", "PASSWORD_RESET", "user-1", Map.of("reason", "support-ticket"));

        VerificationReport report = ledger.verify();

        assertThat(report.valid()).isTrue();
        assertThat(report.totalEventsVerified()).isEqualTo(3);
        assertThat(report.discrepancies()).isEmpty();
        assertThat(report.computedMerkleRoot()).isNotEmpty();
    }

    @Test
    @DisplayName("Should detect tampering when an attacker modifies a record attribute or payload in database")
    void shouldCatchPayloadTampering() {
        EvidenceChainLedger ledger = new EvidenceChainLedger();

        ledger.record("user-1", "DEPOSIT", "account-100", Map.of("amount", "100.00"));
        EvidenceEvent legitTransfer = ledger.record("user-1", "TRANSFER", "account-200", Map.of("amount", "500.00"));
        ledger.record("user-1", "LOGOUT", "auth-service", Map.of());

        // Attacker accesses database and alters amount from 500.00 to 50000.00 while leaving eventHash intact
        EvidenceEvent tamperedTransfer = new EvidenceEvent(
            legitTransfer.eventId(),
            legitTransfer.actor(),
            legitTransfer.action(),
            legitTransfer.resource(),
            Map.of("amount", "50000.00"), // TAMPERED VALUE!
            legitTransfer.timestamp(),
            legitTransfer.previousEventHash(),
            legitTransfer.eventHash() // Original hash
        );

        List<EvidenceEvent> tamperedList = new ArrayList<>(ledger.getEvents());
        tamperedList.set(1, tamperedTransfer);

        ChainVerifier verifier = new ChainVerifier();
        VerificationReport report = verifier.verifyChain(tamperedList);

        assertThat(report.valid()).isFalse();
        assertThat(report.discrepancies()).isNotEmpty();
        assertThat(report.discrepancies().get(0)).contains("Integrity violation at index 1");
    }

    @Test
    @DisplayName("Should detect continuity deletion when a malicious actor drops an intermediate audit event")
    void shouldCatchDroppedEvent() {
        EvidenceChainLedger ledger = new EvidenceChainLedger();

        ledger.record("system", "BOOT", "node-1", Map.of());
        ledger.record("attacker", "UNAUTHORIZED_ACCESS", "confidential-db", Map.of());
        ledger.record("system", "SHUTDOWN", "node-1", Map.of());

        List<EvidenceEvent> tamperedList = new ArrayList<>(ledger.getEvents());
        tamperedList.remove(1); // Malicious admin deletes the unauthorized access record!

        ChainVerifier verifier = new ChainVerifier();
        VerificationReport report = verifier.verifyChain(tamperedList);

        assertThat(report.valid()).isFalse();
        assertThat(report.discrepancies()).isNotEmpty();
        assertThat(report.discrepancies().get(0)).contains("Continuity violation at index 1");
    }
}
