package io.github.frodygr.evidencechain.core.merkle;

import io.github.frodygr.evidencechain.core.model.EvidenceEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Binary SHA-256 Merkle Tree implementation for immutable audit ledgers.
 */
public class MerkleAuditTree {

    private final List<String> leafHashes;
    private final List<List<String>> levels;
    private final String rootHash;

    public MerkleAuditTree(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            this.leafHashes = Collections.emptyList();
            this.levels = Collections.emptyList();
            this.rootHash = HashUtils.sha256("");
            return;
        }

        this.leafHashes = new ArrayList<>(hashes);
        this.levels = new ArrayList<>();
        this.rootHash = buildTree();
    }

    public static MerkleAuditTree ofEvents(List<EvidenceEvent> events) {
        List<String> hashes = new ArrayList<>();
        if (events != null) {
            for (EvidenceEvent event : events) {
                hashes.add(event.eventHash());
            }
        }
        return new MerkleAuditTree(hashes);
    }

    private String buildTree() {
        List<String> currentLevel = new ArrayList<>(leafHashes);
        levels.add(currentLevel);

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                String left = currentLevel.get(i);
                String right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left; // duplicate last if odd
                nextLevel.add(HashUtils.combine(left, right));
            }
            levels.add(nextLevel);
            currentLevel = nextLevel;
        }

        return currentLevel.get(0);
    }

    public String getRootHash() {
        return rootHash;
    }

    public int getLeafCount() {
        return leafHashes.size();
    }

    public MerkleProof generateProof(int leafIndex) {
        if (leafIndex < 0 || leafIndex >= leafHashes.size()) {
            throw new IndexOutOfBoundsException("Leaf index " + leafIndex + " is out of bounds for leaf count " + leafHashes.size());
        }

        List<MerkleProof.ProofNode> path = new ArrayList<>();
        int index = leafIndex;

        for (int l = 0; l < levels.size() - 1; l++) {
            List<String> level = levels.get(l);
            boolean isRightSibling = (index % 2 == 0);
            int siblingIndex = isRightSibling ? index + 1 : index - 1;

            if (siblingIndex < level.size()) {
                path.add(new MerkleProof.ProofNode(level.get(siblingIndex), isRightSibling));
            } else {
                // Odd node paired with itself
                path.add(new MerkleProof.ProofNode(level.get(index), isRightSibling));
            }

            index = index / 2;
        }

        return new MerkleProof(leafHashes.get(leafIndex), leafIndex, path);
    }
}
