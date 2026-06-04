package org.trainbeans.tooperator;

import java.time.format.DateTimeFormatter;

final class DocumentRenderer {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a");

    private DocumentRenderer() {
    }

    static String renderTrainOrder(TrainOrder order) {
        return """
                <html><body style='background:#f0f0f0;margin:0;padding:16px;font-family:Arial,sans-serif;'>
                <div style='max-width:760px;margin:auto;background:#fff;border:3px double #000;padding:18px 24px;font-family:"Courier New",monospace;'>
                  <div style='display:flex;justify-content:space-between;'>
                    <div style='font-size:15px;font-weight:bold;'>Free-moN Operations Railway</div>
                    <div style='text-align:right;'><div style='font-size:28px;font-weight:bold;letter-spacing:2px;'>FORM 19</div><div style='font-size:11px;'>NMRA/ORS 7/23</div></div>
                  </div>
                  <div style='text-align:center;margin-top:8px;font-size:16px;'>TRAIN ORDER No. <b>%d</b></div>
                  <hr style='border:none;border-top:2px solid #000;margin:10px 0'/>
                  <div style='margin:6px 0'>To <span style='display:inline-block;border-bottom:1px solid #000;min-width:220px'>%s</span></div>
                  <div style='margin:6px 0'>At <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span>
                    <span style='margin-left:16px'>Date <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span></span>
                  </div>
                  <div style='margin:6px 0'>Operator <span style='display:inline-block;border-bottom:1px solid #000;min-width:180px'>%s</span>
                    <span style='margin-left:16px'>Time <span style='display:inline-block;border-bottom:1px solid #000;min-width:90px'>%s</span></span>
                  </div>
                  <div style='border:1px solid #000;min-height:200px;white-space:pre-wrap;padding:8px;margin-top:10px;line-height:1.5'>%s</div>
                  <div style='margin-top:10px;font-size:11px;text-align:center;font-style:italic;'>conductor and engineer must each have a copy of this order</div>
                </div></body></html>
                """.formatted(
                order.orderNumber(),
                escapeHtml(order.trainSymbol()),
                escapeHtml(order.station()),
                DATE.format(order.recordedAt()),
                escapeHtml(order.operator()),
                TIME.format(order.recordedAt()),
                escapeHtml(order.orderText()));
    }

    static String renderClearanceCard(ClearanceCard card) {
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
                  <div style='margin:6px 0'>Authority Limits <span style='display:inline-block;border-bottom:1px solid #000;min-width:250px;text-align:center'>%s</span></div>
                  <div style='margin:6px 0'>Dispatcher <span style='display:inline-block;border-bottom:1px solid #000;min-width:240px;text-align:center'>%s</span></div>
                  <hr style='border:none;border-top:1px solid #000;margin:12px 0'/>
                  <div style='text-align:right;'>
                    <span style='display:inline-block;border-bottom:1px solid #000;min-width:240px;text-align:center'>%s</span> Operator.
                  </div>
                </div></body></html>
                """.formatted(
                escapeHtml(card.trainSymbol()),
                DATE.format(card.issuedAt()),
                TIME.format(card.issuedAt()),
                escapeHtml(card.trainSymbol()),
                escapeHtml(card.authorityLimits()),
                escapeHtml(card.dispatcher()),
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
}
