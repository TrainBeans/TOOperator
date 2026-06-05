package org.trainbeans.tooperator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DocumentRendererTest {

    @Test
    void rendersForm19LikeOutput() {
        TrainOrder order = new TrainOrder(
                19,
                "Extra 100 East",
                "Riverdale",
                "Proceed to next siding",
                "AB",
                LocalDateTime.now(),
                "Form: 19 | From: East Yard | Date: 2026-06-04 | To: Extra 100 East,Engine 100 | "
                        + "Opr: AB | Time: 13:45 | At: Riverdale | CTD: XY | Complete Time: 14:00 | "
                        + "Complete Opr: CD | Recopied By: EF | Recopy Opr: GH | Recopy Date: 2026-06-04");

        String html = DocumentRenderer.renderTrainOrder(order);

        assertTrue(html.contains("FORM 19"));
        assertTrue(html.contains("TRAIN ORDER No."));
        assertTrue(html.contains("East Yard"));
        assertTrue(html.contains("Extra 100 East"));
        assertTrue(html.contains("Engine 100"));
        assertTrue(html.contains("2026-06-04"));
        assertTrue(html.contains("13:45"));
        assertTrue(html.contains("XY"));
        assertTrue(html.contains("14:00"));
        assertTrue(html.contains("CD"));
        assertTrue(html.contains("EF"));
        assertTrue(html.contains("GH"));
    }

    @Test
    void rendersClearanceLikeOutput() {
        ClearanceCard card = new ClearanceCard(
                4,
                "Extra 1234 East",
                "Station: East Yard | Orders: 2 | Stop for: Extra 4321 West | Order Nos.: 11,12",
                "2026-06-04 13:15",
                "OP",
                LocalDateTime.now());

        String html = DocumentRenderer.renderClearanceCard(card);

        assertTrue(html.contains("CLEARANCE"));
        assertTrue(html.contains("Form 427-A"));
        assertTrue(html.contains("Conductor and Engineer No."));
        assertTrue(html.contains("East Yard"));
        assertTrue(html.contains("Extra 1234 East"));
        assertTrue(html.contains("I have"));
        assertTrue(html.contains("2"));
        assertTrue(html.contains("Extra 4321 West"));
        assertTrue(html.contains("11,12"));
        assertTrue(html.contains("2026-06-04"));
        assertTrue(html.contains("13:15"));
        assertFalse(html.contains("Authority Limits"));
    }
}
