package org.trainbeans.tooperator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PrinterException;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public final class TOOperatorDesktopApp {
    private static final Color NAVY = new Color(26, 42, 74);
    private static final Color LIGHT_BACKGROUND = new Color(240, 240, 240);

    private TOOperatorDesktopApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TOOperatorDesktopApp::createAndShowUi);
    }

    private static void createAndShowUi() {
        setSystemLookAndFeel();
        OperatorLogbook logbook = new OperatorLogbook();

        JFrame frame = new JFrame("TOOperator — Unified Dispatch Desk");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1180, 760));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_BACKGROUND);
        root.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Form 19 Train Orders", buildTrainOrderPanel(logbook));
        tabs.addTab("Form A Clearance Cards", buildClearanceCardPanel(logbook));
        tabs.addTab("OS Train Report", buildOsPanel(logbook));

        root.add(tabs, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel buildTrainOrderPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JPanel form = createEntryPanel("Form 19 — Train Order Entry");
        GridBagConstraints gbc = formGbc();
        int row = 0;

        JTextField formType = new JTextField();
        JFormattedTextField orderNumber = new JFormattedTextField();
        JTextField fromLocation = new JTextField();
        JTextField orderDate = new JTextField();
        JTextArea toLines = new JTextArea(2, 20);
        JTextField operatorInitials = new JTextField();
        JTextField timeIssued = new JTextField();
        JTextField atLocation = new JTextField();
        JTextArea orderText = new JTextArea(5, 20);
        JTextField dispatcherInitials = new JTextField();
        JTextField completeTime = new JTextField();
        JTextField completeOperator = new JTextField();
        JTextField recopiedBy = new JTextField();
        JTextField recopyOperator = new JTextField();
        JTextField recopyDate = new JTextField();

        toLines.setLineWrap(true);
        toLines.setWrapStyleWord(true);
        orderText.setLineWrap(true);
        orderText.setWrapStyleWord(true);
        orderText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        addFormRow(form, gbc, row++, "FORM 19", formType);
        addFormRow(form, gbc, row++, "TRAIN ORDER No.", orderNumber);
        addTwoFieldRow(form, gbc, row++, "From", fromLocation, "Date", orderDate);
        addFormRow(form, gbc, row++, "To", new JScrollPane(toLines));
        addThreeFieldRow(form, gbc, row++, "Opr.;", operatorInitials, "M.", timeIssued, "At", atLocation);
        addFormRow(form, gbc, row++, "Instructions", new JScrollPane(orderText));
        addFormRow(form, gbc, row++, "C.T.D.", dispatcherInitials);
        addTwoFieldRow(form, gbc, row++, "complete time", completeTime, "opr.", completeOperator);
        addThreeFieldRow(form, gbc, row++, "recopied by", recopiedBy, "opr.:", recopyOperator, "date", recopyDate);

        DefaultListModel<TrainOrder> orderModel = new DefaultListModel<>();
        JList<TrainOrder> orderList = new JList<>(orderModel);
        orderList.setFixedCellHeight(22);

        JTextPane preview = createPreviewPane();

        JButton record = new JButton("Record Order");
        record.addActionListener(e -> {
            try {
                TrainOrder order = logbook.recordTrainOrder(
                        Long.parseLong(orderNumber.getText().trim()),
                        firstNonBlankLine(toLines.getText()),
                        atLocation.getText().trim(),
                        orderText.getText().trim(),
                        operatorInitials.getText().trim(),
                        buildTrainOrderDetails(
                                formType.getText().trim(),
                                fromLocation.getText().trim(),
                                orderDate.getText().trim(),
                                toLines.getText().trim(),
                                operatorInitials.getText().trim(),
                                timeIssued.getText().trim(),
                                atLocation.getText().trim(),
                                dispatcherInitials.getText().trim(),
                                completeTime.getText().trim(),
                                completeOperator.getText().trim(),
                                recopiedBy.getText().trim(),
                                recopyOperator.getText().trim(),
                                recopyDate.getText().trim()));
                orderModel.add(0, order);
                preview.setText(DocumentRenderer.renderTrainOrder(order));
                orderNumber.setValue(null);
                toLines.setText("");
                orderText.setText("");
            } catch (NumberFormatException ex) {
                showValidationError("Order number must be numeric.");
            }
        });

        JButton previewSelected = new JButton("Preview Selected");
        previewSelected.addActionListener(e -> {
            TrainOrder selected = orderList.getSelectedValue();
            if (selected != null) {
                preview.setText(DocumentRenderer.renderTrainOrder(selected));
            }
        });

        JButton print = new JButton("Print Form 19");
        print.addActionListener(e -> printText(preview));

        panel.add(form, BorderLayout.NORTH);
        panel.add(createWorkspace(orderList, preview), BorderLayout.CENTER);
        panel.add(createActionBar(record, previewSelected, print), BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildClearanceCardPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JPanel form = createEntryPanel("Form A — Clearance Card Entry");
        GridBagConstraints gbc = formGbc();
        int row = 0;

        JTextField station = new JTextField();
        JTextField trainSymbol = new JTextField();
        JTextField issuedDate = new JTextField(LocalDate.now().toString());
        JTextField issuedTime = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString());
        JTextField orderCount = new JTextField("0");
        JTextField signalStopFor = new JTextField();
        JTextField operator = new JTextField();
        JTextArea orderNumbers = new JTextArea(2, 20);
        orderNumbers.setLineWrap(true);
        orderNumbers.setWrapStyleWord(true);

        addFormRow(form, gbc, row++, "Station", station);
        addFormRow(form, gbc, row++, "Conductor and Engineer No.", trainSymbol);
        addTwoFieldRow(form, gbc, row++, "Date", issuedDate, "Time", issuedTime);
        addFormRow(form, gbc, row++, "I have ___ orders for your train", orderCount);
        addFormRow(form, gbc, row++, "Train order signal is at stop for", signalStopFor);
        addFormRow(form, gbc, row++, "Operator", operator);
        addFormRow(form, gbc, row++, "Numbers of orders delivered with this clearance", new JScrollPane(orderNumbers));

        DefaultListModel<ClearanceCard> cardModel = new DefaultListModel<>();
        JList<ClearanceCard> cardList = new JList<>(cardModel);
        cardList.setFixedCellHeight(22);

        JTextPane preview = createPreviewPane();

        JButton issue = new JButton("Issue Clearance");
        issue.addActionListener(e -> {
            try {
                long nextCardNumber = logbook.getClearanceCards().size() + 1L;
                ClearanceCard card = logbook.issueClearanceCard(
                        nextCardNumber,
                        trainSymbol.getText().trim(),
                        buildAuthorityLimits(station.getText().trim(), orderCount.getText().trim(),
                                signalStopFor.getText().trim(), orderNumbers.getText().trim()),
                        (issuedDate.getText().trim() + " " + issuedTime.getText().trim()).trim(),
                        operator.getText().trim());
                cardModel.add(0, card);
                preview.setText(DocumentRenderer.renderClearanceCard(card));
            } catch (RuntimeException ex) {
                showValidationError("Unable to issue clearance card: " + ex.getMessage());
            }
        });

        JButton previewSelected = new JButton("Preview Selected");
        previewSelected.addActionListener(e -> {
            ClearanceCard selected = cardList.getSelectedValue();
            if (selected != null) {
                preview.setText(DocumentRenderer.renderClearanceCard(selected));
            }
        });

        JButton print = new JButton("Print Clearance Card");
        print.addActionListener(e -> printText(preview));

        panel.add(form, BorderLayout.NORTH);
        panel.add(createWorkspace(cardList, preview), BorderLayout.CENTER);
        panel.add(createActionBar(issue, previewSelected, print), BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildOsPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JPanel form = createFormPanel("OS Train Report Entry");
        JTextField trainSymbol = new JTextField();
        JTextField location = new JTextField();
        JTextField direction = new JTextField();
        JTextField operator = new JTextField();

        form.add(new JLabel("Train Symbol"));
        form.add(trainSymbol);
        form.add(new JLabel("Location"));
        form.add(location);
        form.add(new JLabel("Direction"));
        form.add(direction);
        form.add(new JLabel("Operator"));
        form.add(operator);

        DefaultListModel<TrainOsEvent> osModel = new DefaultListModel<>();
        JList<TrainOsEvent> osList = new JList<>(osModel);
        osList.setFixedCellHeight(22);

        JTextPane preview = createPreviewPane();

        JButton record = new JButton("Record OS");
        record.addActionListener(e -> {
            TrainOsEvent event = logbook.osTrain(
                    trainSymbol.getText().trim(),
                    location.getText().trim(),
                    direction.getText().trim(),
                    operator.getText().trim());
            osModel.add(0, event);
            preview.setText(DocumentRenderer.renderOsEntry(event));
        });

        JButton previewSelected = new JButton("Preview Selected");
        previewSelected.addActionListener(e -> {
            TrainOsEvent selected = osList.getSelectedValue();
            if (selected != null) {
                preview.setText(DocumentRenderer.renderOsEntry(selected));
            }
        });

        JButton print = new JButton("Print OS Report");
        print.addActionListener(e -> printText(preview));

        panel.add(form, BorderLayout.NORTH);
        panel.add(createWorkspace(osList, preview), BorderLayout.CENTER);
        panel.add(createActionBar(record, previewSelected, print), BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel title = new JLabel("TrainBeans TOOperator Dispatch Desk");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        JLabel subtitle = new JLabel("Integrated Form 19 / Form A / OS workflow");
        subtitle.setForeground(new Color(200, 215, 240));
        subtitle.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        return header;
    }

    private static JPanel createFormPanel(String title) {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        Border outside = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180, 188, 200)), title);
        form.setBorder(BorderFactory.createCompoundBorder(outside, BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        form.setBackground(Color.WHITE);
        return form;
    }

    private static JPanel createEntryPanel(String title) {
        JPanel form = new JPanel(new GridBagLayout());
        Border outside = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180, 188, 200)), title);
        form.setBorder(BorderFactory.createCompoundBorder(outside, BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        form.setBackground(Color.WHITE);
        return form;
    }

    private static GridBagConstraints formGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        return gbc;
    }

    private static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private static void addTwoFieldRow(JPanel panel, GridBagConstraints gbc, int row,
                                       String leftLabel, java.awt.Component leftField,
                                       String rightLabel, java.awt.Component rightField) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        rowPanel.setOpaque(false);
        rowPanel.add(new JLabel(leftLabel));
        rowPanel.add(leftField);
        rowPanel.add(new JLabel(rightLabel));
        rowPanel.add(rightField);
        addFormRow(panel, gbc, row, "", rowPanel);
    }

    private static void addThreeFieldRow(JPanel panel, GridBagConstraints gbc, int row,
                                         String label1, java.awt.Component field1,
                                         String label2, java.awt.Component field2,
                                         String label3, java.awt.Component field3) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 6, 8, 0));
        rowPanel.setOpaque(false);
        rowPanel.add(new JLabel(label1));
        rowPanel.add(field1);
        rowPanel.add(new JLabel(label2));
        rowPanel.add(field2);
        rowPanel.add(new JLabel(label3));
        rowPanel.add(field3);
        addFormRow(panel, gbc, row, "", rowPanel);
    }

    private static String firstNonBlankLine(String text) {
        for (String line : text.split("\\R")) {
            if (!line.isBlank()) {
                return line.trim();
            }
        }
        return "";
    }

    private static String buildAuthorityLimits(String station, String orderCount, String signalStopFor, String orderNumbers) {
        return "Station: " + station + " | Orders: " + orderCount + " | Stop for: " + signalStopFor
                + " | Order Nos.: " + orderNumbers.replace('\n', ',');
    }

    private static String buildTrainOrderDetails(String formType, String fromLocation, String orderDate, String toLines,
                                                 String operatorInitials, String timeIssued, String atLocation,
                                                 String dispatcherInitials, String completeTime, String completeOperator,
                                                 String recopiedBy, String recopyOperator, String recopyDate) {
        return "Form: " + formType
                + " | From: " + fromLocation
                + " | Date: " + orderDate
                + " | To: " + toLines.replace('\n', ',')
                + " | Opr: " + operatorInitials
                + " | Time: " + timeIssued
                + " | At: " + atLocation
                + " | CTD: " + dispatcherInitials
                + " | Complete Time: " + completeTime
                + " | Complete Opr: " + completeOperator
                + " | Recopied By: " + recopiedBy
                + " | Recopy Opr: " + recopyOperator
                + " | Recopy Date: " + recopyDate;
    }

    private static JPanel createWorkspace(JList<?> list, JTextPane preview) {
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Recent Entries"));
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Document Preview"));
        right.add(new JScrollPane(preview), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setResizeWeight(0.28);

        JPanel container = new JPanel(new BorderLayout());
        container.add(splitPane, BorderLayout.CENTER);
        return container;
    }

    private static JPanel createActionBar(JButton primary, JButton preview, JButton print) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(primary);
        actions.add(preview);
        actions.add(print);
        return actions;
    }

    private static JTextPane createPreviewPane() {
        JTextPane preview = new JTextPane();
        preview.setContentType("text/html");
        preview.setEditable(false);
        preview.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        preview.setBackground(Color.WHITE);
        preview.setText("<html><body style='font-family:Arial,sans-serif;padding:18px;color:#333'>Select an entry to preview.</body></html>");
        return preview;
    }

    private static void printText(JTextComponent textComponent) {
        try {
            textComponent.print();
        } catch (PrinterException ex) {
            showValidationError("Unable to print: " + ex.getMessage());
        }
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep Swing default look and feel if system LAF is unavailable.
        }
    }

    private static void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "TOOperator", JOptionPane.WARNING_MESSAGE);
    }
}
