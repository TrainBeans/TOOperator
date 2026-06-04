package org.trainbeans.tooperator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
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

        JPanel form = createFormPanel("Form 19 — Order Entry");
        JFormattedTextField orderNumber = new JFormattedTextField();
        JTextField trainSymbol = new JTextField();
        JTextField station = new JTextField();
        JTextField operator = new JTextField();
        JTextArea orderText = new JTextArea(6, 20);
        orderText.setLineWrap(true);
        orderText.setWrapStyleWord(true);
        orderText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        form.add(new JLabel("TRAIN ORDER No."));
        form.add(orderNumber);
        form.add(new JLabel("To (Train Symbol)"));
        form.add(trainSymbol);
        form.add(new JLabel("At (Station)"));
        form.add(station);
        form.add(new JLabel("Operator"));
        form.add(operator);
        form.add(new JLabel("Instructions"));
        form.add(new JScrollPane(orderText));

        DefaultListModel<TrainOrder> orderModel = new DefaultListModel<>();
        JList<TrainOrder> orderList = new JList<>(orderModel);
        orderList.setFixedCellHeight(22);

        JTextPane preview = createPreviewPane();

        JButton record = new JButton("Record Order");
        record.addActionListener(e -> {
            try {
                TrainOrder order = logbook.recordTrainOrder(
                        Long.parseLong(orderNumber.getText().trim()),
                        trainSymbol.getText().trim(),
                        station.getText().trim(),
                        orderText.getText().trim(),
                        operator.getText().trim());
                orderModel.add(0, order);
                preview.setText(DocumentRenderer.renderTrainOrder(order));
                orderNumber.setValue(null);
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

        JPanel form = createFormPanel("Form A — Clearance Card Entry");
        JFormattedTextField cardNumber = new JFormattedTextField();
        JTextField trainSymbol = new JTextField();
        JTextField limits = new JTextField();
        JTextField dispatcher = new JTextField();
        JTextField operator = new JTextField();

        form.add(new JLabel("Card Number"));
        form.add(cardNumber);
        form.add(new JLabel("Conductor and Engineer No."));
        form.add(trainSymbol);
        form.add(new JLabel("Authority Limits"));
        form.add(limits);
        form.add(new JLabel("Dispatcher"));
        form.add(dispatcher);
        form.add(new JLabel("Operator"));
        form.add(operator);

        DefaultListModel<ClearanceCard> cardModel = new DefaultListModel<>();
        JList<ClearanceCard> cardList = new JList<>(cardModel);
        cardList.setFixedCellHeight(22);

        JTextPane preview = createPreviewPane();

        JButton issue = new JButton("Issue Clearance");
        issue.addActionListener(e -> {
            try {
                ClearanceCard card = logbook.issueClearanceCard(
                        Long.parseLong(cardNumber.getText().trim()),
                        trainSymbol.getText().trim(),
                        limits.getText().trim(),
                        dispatcher.getText().trim(),
                        operator.getText().trim());
                cardModel.add(0, card);
                preview.setText(DocumentRenderer.renderClearanceCard(card));
                cardNumber.setValue(null);
            } catch (NumberFormatException ex) {
                showValidationError("Card number must be numeric.");
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
