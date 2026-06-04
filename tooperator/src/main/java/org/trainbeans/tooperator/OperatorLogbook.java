package org.trainbeans.tooperator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OperatorLogbook {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final List<TrainOrder> trainOrders = new ArrayList<>();
    private final List<ClearanceCard> clearanceCards = new ArrayList<>();
    private final List<TrainOsEvent> osEvents = new ArrayList<>();

    public TrainOrder recordTrainOrder(long orderNumber, String trainSymbol, String station, String orderText, String operator) {
        TrainOrder order = new TrainOrder(orderNumber, trainSymbol, station, orderText, operator, LocalDateTime.now());
        trainOrders.add(order);
        return order;
    }

    public ClearanceCard issueClearanceCard(long cardNumber, String trainSymbol, String authorityLimits, String dispatcher, String operator) {
        ClearanceCard card = new ClearanceCard(cardNumber, trainSymbol, authorityLimits, dispatcher, operator, LocalDateTime.now());
        clearanceCards.add(card);
        return card;
    }

    public TrainOsEvent osTrain(String trainSymbol, String location, String direction, String operator) {
        TrainOsEvent event = new TrainOsEvent(trainSymbol, location, direction, LocalDateTime.now(), operator);
        osEvents.add(event);
        return event;
    }

    public List<TrainOrder> getTrainOrders() {
        return List.copyOf(trainOrders);
    }

    public List<ClearanceCard> getClearanceCards() {
        return List.copyOf(clearanceCards);
    }

    public List<TrainOsEvent> getOsEvents() {
        return List.copyOf(osEvents);
    }

    public String printableTrainOrder(TrainOrder order) {
        return String.format(
                "TRAIN ORDER #%d%nTrain: %s%nStation: %s%nOperator: %s%nRecorded: %s%n%nOrder:%n%s%n",
                order.orderNumber(),
                order.trainSymbol(),
                order.station(),
                order.operator(),
                DATE_TIME_FORMATTER.format(order.recordedAt()),
                order.orderText());
    }

    public String printableClearanceCard(ClearanceCard card) {
        return String.format(
                "CLEARANCE CARD #%d%nTrain: %s%nLimits: %s%nDispatcher: %s%nOperator: %s%nIssued: %s%n",
                card.cardNumber(),
                card.trainSymbol(),
                card.authorityLimits(),
                card.dispatcher(),
                card.operator(),
                DATE_TIME_FORMATTER.format(card.issuedAt()));
    }

    public String printableOsEntry(TrainOsEvent event) {
        return String.format(
                "OS REPORT%nTrain: %s%nLocation: %s%nDirection: %s%nObserved: %s%nOperator: %s%n",
                event.trainSymbol(),
                event.location(),
                event.direction(),
                DATE_TIME_FORMATTER.format(event.observedAt()),
                event.operator());
    }
}
