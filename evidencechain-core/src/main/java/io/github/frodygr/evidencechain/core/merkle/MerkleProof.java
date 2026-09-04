package io.github.frodygr.evidencechain.core.merkle;

import java.util.List;
import java.util.Objects;

/**
 * Audit proof node in a Merkle tree allowing O(log N) verification of an event's inclusion.
 */
public record MerkleProof(
    String leafHash,
    int leafIndex,
    List<ProofNode> auditPath
) {
    public record ProofNode(String hash, boolean isRightSibling) {}

    public boolean verify(String expectedRootHash) {
        String currentHash = this.leafHash;

        for (ProofNode node : auditPath) {
            if (node.isRightSibling()) {
                currentHash = HashUtils.combine(currentHash, node.hash());
            } else {
                currentHash = HashUtils.combine(node.hash(), currentHash);
            }
        }

        return Objects.equals(currentHash, expectedRootHash);
    }
}
