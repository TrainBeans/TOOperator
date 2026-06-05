package org.trainbeans.tooperator;

import java.time.format.DateTimeFormatter;

final class DocumentRenderer {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a");

    private DocumentRenderer() {
    }

    static String renderTrainOrder(TrainOrder order) {
        TrainOrderDetails details = parseTrainOrderDetails(order.formDetails());
        return """
                <html><body style='background:#f0f0f0;margin:0;padding:16px;font-family:Arial,sans-serif;'>
                <div style='max-width:760px;margin:auto;background:#fff;border:3px double #000;padding:18px 24px;font-family:"Courier New",monospace;'>
                  <div style='display:flex;justify-content:space-between;'>
                    <div style='font-size:15px;font-weight:bold;'>Free-moN Operations Railway</div>
                    <div style='text-align:right;'><div style='font-size:28px;font-weight:bold;letter-spacing:2px;'>FORM 19</div><div style='font-size:11px;'>NMRA/ORS 7/23</div></div>
                  </div>
                  <div style='text-align:center;margin-top:8px;font-size:16px;'>TRAIN ORDER No. <b>%d</b></div>
                  <hr style='border:none;border-top:2px solid #000;margin:10px 0'/>
                  <div style='margin:6px 0'>Form <span style='display:inline-block;border-bottom:1px solid #000;min-width:120px'>%s</span>
                    <span style='margin-left:16px'>From <span style='display:inline-block;border-bottom:1px solid #000;min-width:220px'>%s</span></span>
                  </div>
                  <div style='margin:6px 0'>To <span style='display:inline-block;border-bottom:1px solid #000;min-width:220px'>%s</span></div>
                  <div style='margin:6px 0'>At <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span>
                    <span style='margin-left:16px'>Date <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span></span>
                  </div>
                  <div style='margin:6px 0'>Operator <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span>
                    <span style='margin-left:16px'>Time <span style='display:inline-block;border-bottom:1px solid #000;min-width:90px'>%s</span></span>
                  </div>
                  <div style='border:1px solid #000;min-height:200px;white-space:pre-wrap;padding:8px;margin-top:10px;line-height:1.5'>%s</div>
                  <div style='margin-top:8px'>C.T.D. <span style='display:inline-block;border-bottom:1px solid #000;min-width:150px'>%s</span></div>
                  <div style='margin-top:6px'>complete time <span style='display:inline-block;border-bottom:1px solid #000;min-width:110px'>%s</span>
                    <span style='margin-left:10px'>opr. <span style='display:inline-block;border-bottom:1px solid #000;min-width:90px'>%s</span></span>
                  </div>
                  <div style='margin-top:6px'>recopied by <span style='display:inline-block;border-bottom:1px solid #000;min-width:140px'>%s</span>
                    <span style='margin-left:10px'>opr.: <span style='display:inline-block;border-bottom:1px solid #000;min-width:90px'>%s</span></span>
                    <span style='margin-left:10px'>date <span style='display:inline-block;border-bottom:1px solid #000;min-width:120px'>%s</span></span>
                  </div>
                  <div style='margin-top:10px;font-size:11px;text-align:center;font-style:italic;'>conductor and engineer must each have a copy of this order</div>
                </div></body></html>
                """.formatted(
                order.orderNumber(),
                escapeHtml(nonBlank(details.formType(), "19")),
                escapeHtml(details.fromLocation()),
                escapeHtml(nonBlank(details.toLines(), order.trainSymbol())),
                escapeHtml(nonBlank(details.atLocation(), order.station())),
                escapeHtml(nonBlank(details.orderDate(), DATE.format(order.recordedAt()))),
                escapeHtml(nonBlank(details.operatorInitials(), order.operator())),
                escapeHtml(nonBlank(details.timeIssued(), TIME.format(order.recordedAt()))),
                escapeHtml(order.orderText()),
                escapeHtml(details.dispatcherInitials()),
                escapeHtml(details.completeTime()),
                escapeHtml(details.completeOperator()),
                escapeHtml(details.recopiedBy()),
                escapeHtml(details.recopyOperator()),
                escapeHtml(details.recopyDate()));
    }

    static String renderClearanceCard(ClearanceCard card) {
        ClearanceDetails details = parseClearanceDetails(card.authorityLimits());
        String[] issuedDateTime = parseIssuedDateTime(card.dispatcher(), card.issuedAt());
        return """
                <html><body style='background:#e8e8e8;margin:0;padding:16px;font-family:Arial,sans-serif;'>
                <div style='max-width:520px;margin:auto;background:#fff;border:2px solid #000;padding:18px 22px;font-family:"Courier New",monospace;'>
                  <div style='display:flex;justify-content:space-between;font-size:11px'><span>Printed in U.S.A. NMRA/ORS 7/23</span><span>Form 427-A</span></div>
                  <div style='text-align:center;margin-top:6px;font-size:18px;font-weight:bold;'>Free-moN Operations Railway</div>
                  <div style='text-align:center;margin-top:4px;font-size:30px;font-weight:bold;letter-spacing:5px;'>CLEARANCE</div>
                  <hr style='border:none;border-top:1px solid #000;margin:12px 0'/>
                  <div style='margin:6px 0'>Station <span style='display:inline-block;border-bottom:1px solid #000;min-width:270px;text-align:center'>%s</span></div>
                  <div style='margin:6px 0'>Date <span style='display:inline-block;border-bottom:1px solid #000;min-width:190px;text-align:center'>%s</span>
                    <span style='margin-left:8px'>Time <span style='display:inline-block;border-bottom:1px solid #000;min-width:90px;text-align:center'>%s</span></span>
                  </div>
                  <div style='margin:6px 0'>Conductor and Engineer No. <span style='display:inline-block;border-bottom:1px solid #000;min-width:220px;text-align:center'>%s</span></div>
                  <div style='margin:6px 0'>I have <span style='display:inline-block;border-bottom:1px solid #000;min-width:48px;text-align:center'>%s</span> orders for your train.</div>
                  <div style='margin:6px 0'>Train order signal is at Stop for <span style='display:inline-block;border-bottom:1px solid #000;min-width:210px;text-align:center'>%s</span></div>
                  <div style='margin:6px 0'>Numbers of orders delivered with this clearance: <span style='display:inline-block;border-bottom:1px solid #000;min-width:210px;text-align:center'>%s</span></div>
                  <hr style='border:none;border-top:1px solid #000;margin:12px 0'/>
                  <div style='text-align:right;'>
                    <span style='display:inline-block;border-bottom:1px solid #000;min-width:240px;text-align:center'>%s</span> Operator.
                  </div>
                </div></body></html>
                """.formatted(
                escapeHtml(details.station()),
                escapeHtml(issuedDateTime[0]),
                escapeHtml(issuedDateTime[1]),
                escapeHtml(card.trainSymbol()),
                escapeHtml(details.orderCount()),
                escapeHtml(details.signalStopFor()),
                escapeHtml(details.orderNumbers()),
                escapeHtml(card.operator()));
    }

    static String renderOsEntry(TrainOsEvent event) {
        return """
                <html><body style='background:#f3f5f8;margin:0;padding:16px;font-family:Arial,sans-serif;'>
                <div style='max-width:640px;margin:auto;background:#fff;border:1px solid #aab3c1;padding:18px 24px;'>
                  <div style='background:#1a2a4a;color:#fff;padding:8px 12px;font-weight:bold;'>OS TRAIN REPORT</div>
                  <div style='padding:12px;font-family:"Courier New",monospace;'>
                    <div style='margin:8px 0'><b>Train:</b> %s</div>
                    <div style='margin:8px 0'><b>Location:</b> %s</div>
                    <div style='margin:8px 0'><b>Direction:</b> %s</div>
                    <div style='margin:8px 0'><b>Observed:</b> %s %s</div>
                    <div style='margin:8px 0'><b>Operator:</b> %s</div>
                  </div>
                </div></body></html>
                """.formatted(
                escapeHtml(event.trainSymbol()),
                escapeHtml(event.location()),
                escapeHtml(event.direction()),
                DATE.format(event.observedAt()),
                TIME.format(event.observedAt()),
                escapeHtml(event.operator()));
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static ClearanceDetails parseClearanceDetails(String authorityLimits) {
        String station = "";
        String orderCount = "";
        String signalStopFor = "";
        String orderNumbers = "";

        if (authorityLimits != null) {
            for (String part : authorityLimits.split("\\s*\\|\\s*")) {
                if (part.startsWith("Station:")) {
                    station = part.substring("Station:".length()).trim();
                } else if (part.startsWith("Orders:")) {
                    orderCount = part.substring("Orders:".length()).trim();
                } else if (part.startsWith("Stop for:")) {
                    signalStopFor = part.substring("Stop for:".length()).trim();
                } else if (part.startsWith("Order Nos.:")) {
                    orderNumbers = part.substring("Order Nos.:".length()).trim();
                }
            }
        }
        return new ClearanceDetails(station, orderCount, signalStopFor, orderNumbers);
    }

    private static String[] parseIssuedDateTime(String dispatcher, java.time.LocalDateTime issuedAt) {
        if (dispatcher != null && !dispatcher.isBlank()) {
            String[] parts = dispatcher.trim().split("\\s+", 2);
            String date = parts[0];
            String time = parts.length > 1 ? parts[1] : "";
            return new String[]{date, time};
        }
        return new String[]{DATE.format(issuedAt), TIME.format(issuedAt)};
    }

    private static TrainOrderDetails parseTrainOrderDetails(String formDetails) {
        String formType = "";
        String fromLocation = "";
        String orderDate = "";
        String toLines = "";
        String operatorInitials = "";
        String timeIssued = "";
        String atLocation = "";
        String dispatcherInitials = "";
        String completeTime = "";
        String completeOperator = "";
        String recopiedBy = "";
        String recopyOperator = "";
        String recopyDate = "";

        if (formDetails != null) {
            for (String part : formDetails.split("\\s*\\|\\s*")) {
                if (part.startsWith("Form:")) {
                    formType = part.substring("Form:".length()).trim();
                } else if (part.startsWith("From:")) {
                    fromLocation = part.substring("From:".length()).trim();
                } else if (part.startsWith("Date:")) {
                    orderDate = part.substring("Date:".length()).trim();
                } else if (part.startsWith("To:")) {
                    toLines = part.substring("To:".length()).trim();
                } else if (part.startsWith("Opr:")) {
                    operatorInitials = part.substring("Opr:".length()).trim();
                } else if (part.startsWith("Time:")) {
                    timeIssued = part.substring("Time:".length()).trim();
                } else if (part.startsWith("At:")) {
                    atLocation = part.substring("At:".length()).trim();
                } else if (part.startsWith("CTD:")) {
                    dispatcherInitials = part.substring("CTD:".length()).trim();
                } else if (part.startsWith("Complete Time:")) {
                    completeTime = part.substring("Complete Time:".length()).trim();
                } else if (part.startsWith("Complete Opr:")) {
                    completeOperator = part.substring("Complete Opr:".length()).trim();
                } else if (part.startsWith("Recopied By:")) {
                    recopiedBy = part.substring("Recopied By:".length()).trim();
                } else if (part.startsWith("Recopy Opr:")) {
                    recopyOperator = part.substring("Recopy Opr:".length()).trim();
                } else if (part.startsWith("Recopy Date:")) {
                    recopyDate = part.substring("Recopy Date:".length()).trim();
                }
            }
        }

        return new TrainOrderDetails(
                formType,
                fromLocation,
                orderDate,
                toLines,
                operatorInitials,
                timeIssued,
                atLocation,
                dispatcherInitials,
                completeTime,
                completeOperator,
                recopiedBy,
                recopyOperator,
                recopyDate);
    }

    private static String nonBlank(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private record TrainOrderDetails(
            String formType,
            String fromLocation,
            String orderDate,
            String toLines,
            String operatorInitials,
            String timeIssued,
            String atLocation,
            String dispatcherInitials,
            String completeTime,
            String completeOperator,
            String recopiedBy,
            String recopyOperator,
            String recopyDate
    ) {
    }

    private record ClearanceDetails(String station, String orderCount, String signalStopFor, String orderNumbers) {
    }
}
