package io.github.frodygr.evidencechain.core;

import io.github.frodygr.evidencechain.core.merkle.MerkleAuditTree;
import io.github.frodygr.evidencechain.core.merkle.MerkleProof;
import io.github.frodygr.evidencechain.core.model.EvidenceEvent;
import io.github.frodygr.evidencechain.core.verifier.ChainVerifier;
import io.github.frodygr.evidencechain.core.verifier.VerificationReport;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe append-only cryptographic ledger for evidence events.
 */
public class EvidenceChainLedger {

    private final List<EvidenceEvent> events = new CopyOnWriteArrayList<>();
    private final ReentrantLock appendLock = new ReentrantLock();
    private final ChainVerifier verifier = new ChainVerifier();
    private volatile String latestHash = EvidenceEvent.GENESIS_HASH;

    /**
     * Appends a new auditable event to the chain, linking it cryptographically to the prior event.
     *
     * @param actor user or service performing the action
     * @param action business action name
     * @param resource target entity identifier
     * @param metadata context attributes
     * @return newly recorded immutable EvidenceEvent
     */
    public EvidenceEvent record(String actor, String action, String resource, Map<String, String> metadata) {
        appendLock.lock();
        try {
            String eventId = UUID.randomUUID().toString();
            Instant now = Instant.now();
            EvidenceEvent event = EvidenceEvent.create(eventId, actor, action, resource, metadata, now, latestHash);
            events.add(event);
            latestHash = event.eventHash();
            return event;
        } finally {
            appendLock.unlock();
        }
    }

    public List<EvidenceEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public int size() {
        return events.size();
    }

    public String getLatestHash() {
        return latestHash;
    }

    public VerificationReport verify() {
        return verifier.verifyChain(events);
    }

    public MerkleAuditTree buildMerkleTree() {
        return MerkleAuditTree.ofEvents(events);
    }

    public MerkleProof generateProof(int index) {
        return buildMerkleTree().generateProof(index);
    }
}
