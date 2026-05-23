package com.ecommerce.shared.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public abstract class AggregateRoot {
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
