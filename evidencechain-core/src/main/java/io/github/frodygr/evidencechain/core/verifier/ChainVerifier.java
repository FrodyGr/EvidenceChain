package io.github.frodygr.evidencechain.core.verifier;

import io.github.frodygr.evidencechain.core.merkle.MerkleAuditTree;
import io.github.frodygr.evidencechain.core.model.EvidenceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cryptographic verification engine inspecting event hashing, chain continuity, and Merkle root integrity.
 */
public class ChainVerifier {

    /**
     * Fully audits an ordered list of EvidenceEvent records.
     *
     * @param events sequence of events in chronological order
     * @return VerificationReport containing mathematical audit findings
     */
    public VerificationReport verifyChain(List<EvidenceEvent> events) {
        if (events == null || events.isEmpty()) {
            return VerificationReport.success(0, "EMPTY_CHAIN");
        }

        List<String> discrepancies = new ArrayList<>();
        String expectedPreviousHash = EvidenceEvent.GENESIS_HASH;

        for (int i = 0; i < events.size(); i++) {
            EvidenceEvent event = events.get(i);

            // 1. Verify internal payload integrity (tampering within the event data)
            if (!event.verifyIntegrity()) {
                discrepancies.add(String.format(
                    "Integrity violation at index %d [EventId: %s]: Event hash does not match computed payload hash.",
                    i, event.eventId()
                ));
            }

            // 2. Verify blockchain-style hash continuity link
            if (!Objects.equals(event.previousEventHash(), expectedPreviousHash)) {
                discrepancies.add(String.format(
                    "Continuity violation at index %d [EventId: %s]: Expected previousHash=%s, but found previousHash=%s.",
                    i, event.eventId(), expectedPreviousHash, event.previousEventHash()
                ));
            }

            expectedPreviousHash = event.eventHash();
        }

        // 3. Compute Merkle Root over the event hashes
        MerkleAuditTree tree = MerkleAuditTree.ofEvents(events);
        String merkleRoot = tree.getRootHash();

        if (discrepancies.isEmpty()) {
            return VerificationReport.success(events.size(), merkleRoot);
        }

        return VerificationReport.failure(events.size(), merkleRoot, discrepancies);
    }
}
