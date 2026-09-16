package com.jonet.eventbooking.repository.specification;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.jonet.eventbooking.entity.EventEntity;

public class EventSpecifications {
    public static Specification<EventEntity> hasId(UUID id) {
        return (root, query, cb) -> id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<EventEntity> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<EventEntity> venueNameEquals(String venue) {
        return (root, query, cb) -> {
            if (venue == null) return null;
            return cb.equal(root.join("venue").get("name"), venue);
        };
    }

    public static Specification<EventEntity> startTimeAfter(LocalDateTime time) {
        return (root, query, cb) -> time == null ? null : cb.greaterThan(root.get("startTime"), time);
    }
}
