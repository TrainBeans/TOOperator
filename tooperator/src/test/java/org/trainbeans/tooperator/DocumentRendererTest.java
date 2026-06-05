package org.trainbeans.tooperator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DocumentRendererTest {

    @Test
    void rendersForm19LikeOutput() {
        TrainOrder order = new TrainOrder(19, "Extra 100 East", "Riverdale", "Proceed to next siding", "AB", LocalDateTime.now());

        String html = DocumentRenderer.renderTrainOrder(order);

        assertTrue(html.contains("FORM 19"));
        assertTrue(html.contains("TRAIN ORDER No."));
        assertTrue(html.contains("Extra 100 East"));
    }

    @Test
    void rendersClearanceLikeOutput() {
        ClearanceCard card = new ClearanceCard(4, "Extra 1234 East", "Yard to Jct", "CTD", "OP", LocalDateTime.now());

        String html = DocumentRenderer.renderClearanceCard(card);

        assertTrue(html.contains("CLEARANCE"));
        assertTrue(html.contains("Form 427-A"));
        assertTrue(html.contains("Conductor and Engineer No."));
        assertTrue(html.contains("Extra 1234 East"));
        assertFalse(html.contains("Authority Limits"));
    }
}
