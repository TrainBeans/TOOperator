package org.trainbeans.tooperator;

import java.time.LocalDateTime;

public record TrainOrder(
        long orderNumber,
        String trainSymbol,
        String station,
        String orderText,
        String operator,
        LocalDateTime recordedAt
) {
    @Override
    public String toString() {
        return "#" + orderNumber + " — " + trainSymbol + " @ " + station;
    }
}
