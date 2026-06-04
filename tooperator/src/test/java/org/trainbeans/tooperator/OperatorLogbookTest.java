package org.trainbeans.tooperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OperatorLogbookTest {

    @Test
    void recordsTrainOrderAndProducesPrintableOutput() {
        OperatorLogbook logbook = new OperatorLogbook();

        TrainOrder order = logbook.recordTrainOrder(12, "A1", "East Yard", "Hold main track at signal", "Taylor");

        assertEquals(1, logbook.getTrainOrders().size());
        String printable = logbook.printableTrainOrder(order);
        assertTrue(printable.contains("TRAIN ORDER #12"));
        assertTrue(printable.contains("Hold main track at signal"));
    }

    @Test
    void issuesClearanceCardAndProducesPrintableOutput() {
        OperatorLogbook logbook = new OperatorLogbook();

        ClearanceCard card = logbook.issueClearanceCard(44, "P7", "Yard to Jct", "Smith", "Jones");

        assertEquals(1, logbook.getClearanceCards().size());
        String printable = logbook.printableClearanceCard(card);
        assertTrue(printable.contains("CLEARANCE CARD #44"));
        assertTrue(printable.contains("Limits: Yard to Jct"));
    }

    @Test
    void recordsOsTrainEvents() {
        OperatorLogbook logbook = new OperatorLogbook();

        TrainOsEvent event = logbook.osTrain("B2", "Milepost 10", "Westbound", "Lane");

        assertEquals(1, logbook.getOsEvents().size());
        String printable = logbook.printableOsEntry(event);
        assertTrue(printable.contains("OS REPORT"));
        assertTrue(printable.contains("Westbound"));
    }
}
