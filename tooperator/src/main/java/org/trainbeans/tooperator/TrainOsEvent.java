package org.trainbeans.tooperator;

import java.time.LocalDateTime;

public record TrainOsEvent(
        String trainSymbol,
        String location,
        String direction,
        LocalDateTime observedAt,
        String operator
) {
}
