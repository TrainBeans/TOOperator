package org.trainbeans.tooperator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.print.PrinterException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public final class TOOperatorDesktopApp {
    private TOOperatorDesktopApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TOOperatorDesktopApp::createAndShowUi);
    }

    private static void createAndShowUi() {
        OperatorLogbook logbook = new OperatorLogbook();

        JFrame frame = new JFrame("TOOperator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 700));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Train Orders", buildTrainOrderPanel(logbook));
        tabs.addTab("Clearance Cards", buildClearanceCardPanel(logbook));
        tabs.addTab("OS Train", buildOsPanel(logbook));

        frame.add(tabs);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel buildTrainOrderPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JFormattedTextField orderNumber = new JFormattedTextField();
        JTextField trainSymbol = new JTextField();
        JTextField station = new JTextField();
        JTextField operator = new JTextField();
        JTextArea orderText = new JTextArea(5, 20);

        form.add(new JLabel("Order Number"));
        form.add(orderNumber);
        form.add(new JLabel("Train Symbol"));
        form.add(trainSymbol);
        form.add(new JLabel("Station"));
        form.add(station);
        form.add(new JLabel("Operator"));
        form.add(operator);
        form.add(new JLabel("Order Text"));
        form.add(new JScrollPane(orderText));

        DefaultListModel<TrainOrder> orderModel = new DefaultListModel<>();
        JList<TrainOrder> orderList = new JList<>(orderModel);

        JTextArea preview = createPreviewArea();

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
                preview.setText(logbook.printableTrainOrder(order));
            } catch (NumberFormatException ex) {
                showValidationError("Order number must be numeric.");
            }
        });

        JButton print = new JButton("Print Preview");
        print.addActionListener(e -> {
            TrainOrder selected = orderList.getSelectedValue();
            if (selected != null) {
                preview.setText(logbook.printableTrainOrder(selected));
                printText(preview);
            }
        });

        JPanel actions = new JPanel();
        actions.add(record);
        actions.add(print);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(orderList), BorderLayout.WEST);
        panel.add(new JScrollPane(preview), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildClearanceCardPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JFormattedTextField cardNumber = new JFormattedTextField();
        JTextField trainSymbol = new JTextField();
        JTextField limits = new JTextField();
        JTextField dispatcher = new JTextField();
        JTextField operator = new JTextField();

        form.add(new JLabel("Card Number"));
        form.add(cardNumber);
        form.add(new JLabel("Train Symbol"));
        form.add(trainSymbol);
        form.add(new JLabel("Authority Limits"));
        form.add(limits);
        form.add(new JLabel("Dispatcher"));
        form.add(dispatcher);
        form.add(new JLabel("Operator"));
        form.add(operator);

        DefaultListModel<ClearanceCard> cardModel = new DefaultListModel<>();
        JList<ClearanceCard> cardList = new JList<>(cardModel);

        JTextArea preview = createPreviewArea();

        JButton issue = new JButton("Issue Card");
        issue.addActionListener(e -> {
            try {
                ClearanceCard card = logbook.issueClearanceCard(
                        Long.parseLong(cardNumber.getText().trim()),
                        trainSymbol.getText().trim(),
                        limits.getText().trim(),
                        dispatcher.getText().trim(),
                        operator.getText().trim());
                cardModel.add(0, card);
                preview.setText(logbook.printableClearanceCard(card));
            } catch (NumberFormatException ex) {
                showValidationError("Card number must be numeric.");
            }
        });

        JButton print = new JButton("Print Preview");
        print.addActionListener(e -> {
            ClearanceCard selected = cardList.getSelectedValue();
            if (selected != null) {
                preview.setText(logbook.printableClearanceCard(selected));
                printText(preview);
            }
        });

        JPanel actions = new JPanel();
        actions.add(issue);
        actions.add(print);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(cardList), BorderLayout.WEST);
        panel.add(new JScrollPane(preview), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildOsPanel(OperatorLogbook logbook) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
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

        JTextArea preview = createPreviewArea();

        JButton record = new JButton("Record OS");
        record.addActionListener(e -> {
            TrainOsEvent event = logbook.osTrain(
                    trainSymbol.getText().trim(),
                    location.getText().trim(),
                    direction.getText().trim(),
                    operator.getText().trim());
            osModel.add(0, event);
            preview.setText(logbook.printableOsEntry(event));
        });

        JButton print = new JButton("Print Preview");
        print.addActionListener(e -> {
            TrainOsEvent selected = osList.getSelectedValue();
            if (selected != null) {
                preview.setText(logbook.printableOsEntry(selected));
                printText(preview);
            }
        });

        JPanel actions = new JPanel();
        actions.add(record);
        actions.add(print);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(osList), BorderLayout.WEST);
        panel.add(new JScrollPane(preview), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private static JTextArea createPreviewArea() {
        JTextArea preview = new JTextArea();
        preview.setEditable(false);
        preview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        return preview;
    }

    private static void printText(JTextArea area) {
        try {
            area.print();
        } catch (PrinterException ex) {
            showValidationError("Unable to print: " + ex.getMessage());
        }
    }

    private static void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "TOOperator", JOptionPane.WARNING_MESSAGE);
    }
}
