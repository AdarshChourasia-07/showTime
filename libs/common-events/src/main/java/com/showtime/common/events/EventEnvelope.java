package com.showtime.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Wrapper for every event published to Kafka.
 *
 * @param eventId     unique id, used by consumers to ignore duplicates (idempotency)
 * @param eventType   e.g. "show.created", "booking.confirmed"
 * @param aggregateId id of the entity the event is about; also the Kafka message key (keeps ordering per entity)
 * @param occurredAt  when the change happened
 * @param payload     the event body
 */
public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        T payload) {

    public static <T> EventEnvelope<T> of(String eventType, String aggregateId, T payload) {
        return new EventEnvelope<>(UUID.randomUUID(), eventType, aggregateId, Instant.now(), payload);
    }
}
