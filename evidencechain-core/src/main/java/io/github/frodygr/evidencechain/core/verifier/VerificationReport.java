package io.github.frodygr.evidencechain.core.verifier;

import java.util.Collections;
import java.util.List;

public record VerificationReport(
    boolean valid,
    int totalEventsVerified,
    String computedMerkleRoot,
    List<String> discrepancies
) {
    public static VerificationReport success(int count, String merkleRoot) {
        return new VerificationReport(true, count, merkleRoot, Collections.emptyList());
    }

    public static VerificationReport failure(int count, String merkleRoot, List<String> discrepancies) {
        return new VerificationReport(false, count, merkleRoot, List.copyOf(discrepancies));
    }
}
