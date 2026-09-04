package io.github.frodygr.evidencechain.core.merkle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MerkleAuditTreeTest {

    @Test
    @DisplayName("Should generate deterministic Merkle Root for known leaf hashes")
    void shouldBuildDeterministicTree() {
        List<String> leaves = List.of(
            HashUtils.sha256("event-1"),
            HashUtils.sha256("event-2"),
            HashUtils.sha256("event-3"),
            HashUtils.sha256("event-4")
        );

        MerkleAuditTree tree1 = new MerkleAuditTree(leaves);
        MerkleAuditTree tree2 = new MerkleAuditTree(leaves);

        assertThat(tree1.getRootHash()).isEqualTo(tree2.getRootHash());
        assertThat(tree1.getRootHash()).isNotEmpty();
    }

    @Test
    @DisplayName("Should generate valid O(log N) Merkle proof and verify leaf membership")
    void shouldGenerateAndVerifyProof() {
        List<String> leaves = List.of(
            HashUtils.sha256("tx-100"),
            HashUtils.sha256("tx-101"),
            HashUtils.sha256("tx-102"),
            HashUtils.sha256("tx-103"),
            HashUtils.sha256("tx-104")
        );

        MerkleAuditTree tree = new MerkleAuditTree(leaves);
        String root = tree.getRootHash();

        for (int i = 0; i < leaves.size(); i++) {
            MerkleProof proof = tree.generateProof(i);
            assertThat(proof.verify(root)).isTrue();
        }
    }

    @Test
    @DisplayName("Should reject proof against forged root hash")
    void shouldRejectForgedRoot() {
        List<String> leaves = List.of(HashUtils.sha256("e1"), HashUtils.sha256("e2"));
        MerkleAuditTree tree = new MerkleAuditTree(leaves);

        MerkleProof proof = tree.generateProof(0);
        assertThat(proof.verify("0000000000000000000000000000000000000000000000000000000000000000")).isFalse();
    }
}
