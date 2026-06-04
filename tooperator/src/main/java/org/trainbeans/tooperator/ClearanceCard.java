package org.trainbeans.tooperator;

import java.time.LocalDateTime;

public record ClearanceCard(
        long cardNumber,
        String trainSymbol,
        String authorityLimits,
        String dispatcher,
        String operator,
        LocalDateTime issuedAt
) {
}
