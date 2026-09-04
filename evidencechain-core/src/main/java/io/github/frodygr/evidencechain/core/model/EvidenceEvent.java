package io.github.frodygr.evidencechain.core.model;

import io.github.frodygr.evidencechain.core.merkle.HashUtils;

import java.time.Instant;
import java.util.*;

/**
 * Immutable cryptographic audit event record chained to prior events.
 */
public record EvidenceEvent(
    String eventId,
    String actor,
    String action,
    String resource,
    Map<String, String> metadata,
    Instant timestamp,
    String previousEventHash,
    String eventHash
) {

    public static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    public static EvidenceEvent create(
        String eventId,
        String actor,
        String action,
        String resource,
        Map<String, String> metadata,
        Instant timestamp,
        String previousEventHash
    ) {
        Objects.requireNonNull(eventId, "eventId must not be null");
        Objects.requireNonNull(actor, "actor must not be null");
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(resource, "resource must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        String prev = previousEventHash != null ? previousEventHash : GENESIS_HASH;

        Map<String, String> sortedMeta = metadata != null ? new TreeMap<>(metadata) : Collections.emptyMap();
        String computedHash = calculateHash(eventId, actor, action, resource, sortedMeta, timestamp, prev);

        return new EvidenceEvent(
            eventId,
            actor,
            action,
            resource,
            Collections.unmodifiableMap(sortedMeta),
            timestamp,
            prev,
            computedHash
        );
    }

    public static String calculateHash(
        String eventId,
        String actor,
        String action,
        String resource,
        Map<String, String> sortedMetadata,
        Instant timestamp,
        String previousHash
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(eventId).append("|")
          .append(actor).append("|")
          .append(action).append("|")
          .append(resource).append("|")
          .append(timestamp.toEpochMilli()).append("|")
          .append(previousHash).append("|");

        if (sortedMetadata != null) {
            sortedMetadata.forEach((k, v) -> sb.append(k).append("=").append(v).append(";"));
        }

        return HashUtils.sha256(sb.toString());
    }

    public boolean verifyIntegrity() {
        String recomputed = calculateHash(eventId, actor, action, resource, metadata, timestamp, previousEventHash);
        return Objects.equals(this.eventHash, recomputed);
    }
}
