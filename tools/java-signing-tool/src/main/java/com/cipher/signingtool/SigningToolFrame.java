package com.cipher.signingtool;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SigningToolFrame extends JFrame {
    private final KeyPairService keyPairService = new KeyPairService();
    private final SignatureService signatureService = new SignatureService();
    private final KeyLoader keyLoader = new KeyLoader();

    private final JTextArea publicKeyArea = createTextArea(8);
    private final JTextArea hashValueArea = createTextArea(5);
    private final JTextArea signatureArea = createTextArea(7);
    private final JLabel statusLabel = new JLabel("Ready", SwingConstants.LEFT);

    private PrivateKey currentPrivateKey;
    private PublicKey currentPublicKey;

    public SigningToolFrame() {
        super("Java Signing Tool - RSA SHA256withRSA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        pack();
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                createKeyPanel(),
                createSigningPanel()
        );
        splitPane.setResizeWeight(0.52);
        splitPane.setBorder(null);

        content.add(splitPane, BorderLayout.CENTER);
        return content;
    }

    private JPanel createKeyPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Key management"));

        publicKeyArea.setEditable(false);
        publicKeyArea.setText("Generate a key pair to show the public key PEM here.");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Generate Key Pair");
        JButton copyPublicButton = new JButton("Copy Public Key");
        JButton exportPublicButton = new JButton("Export Public Key");
        JButton exportPrivateButton = new JButton("Export Private Key");
        JButton loadPrivateButton = new JButton("Load Private Key");

        generateButton.addActionListener(event -> generateKeyPair());
        copyPublicButton.addActionListener(event -> copyPublicKey());
        exportPublicButton.addActionListener(event -> exportPublicKey());
        exportPrivateButton.addActionListener(event -> exportPrivateKey());
        loadPrivateButton.addActionListener(event -> loadPrivateKey());

        actions.add(generateButton);
        actions.add(copyPublicButton);
        actions.add(exportPublicButton);
        actions.add(exportPrivateButton);
        actions.add(loadPrivateButton);

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSigningPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Manual signing"));

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.add(new JLabel("hash_value"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(hashValueArea), BorderLayout.CENTER);

        JPanel signaturePanel = new JPanel(new BorderLayout(6, 6));
        signatureArea.setEditable(false);
        signaturePanel.add(new JLabel("Signature Base64"), BorderLayout.NORTH);
        signaturePanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton signButton = new JButton("Sign Hash");
        JButton copySignatureButton = new JButton("Copy Signature");

        signButton.addActionListener(event -> signHashValue());
        copySignatureButton.addActionListener(event -> copyText(signatureArea.getText(), "Signature copied."));

        actions.add(signButton);
        actions.add(copySignatureButton);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(inputPanel, BorderLayout.NORTH);
        center.add(signaturePanel, BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JTextArea createTextArea(int rows) {
        JTextArea textArea = new JTextArea(rows, 80);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return textArea;
    }

    private void generateKeyPair() {
        try {
            KeyPair keyPair = keyPairService.generateKeyPair();
            currentPrivateKey = keyPair.getPrivate();
            currentPublicKey = keyPair.getPublic();
            publicKeyArea.setText(PemUtils.publicKeyToPem(currentPublicKey));
            setStatus("Generated RSA 2048 key pair.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void exportPublicKey() {
        if (currentPublicKey == null) {
            showError("No public key available. Generate a key pair first.");
            return;
        }

        chooseAndWritePem("public-key.pem", PemUtils.publicKeyToPem(currentPublicKey));
    }

    private void copyPublicKey() {
        if (currentPublicKey == null) {
            showError("No public key available. Generate a key pair first.");
            return;
        }

        copyText(PemUtils.publicKeyToPem(currentPublicKey), "Public key copied.");
    }

    private void exportPrivateKey() {
        if (currentPrivateKey == null) {
            showError("No private key available. Generate or load a private key first.");
            return;
        }

        chooseAndWritePem("private-key.pem", PemUtils.privateKeyToPem(currentPrivateKey));
    }

    private void loadPrivateKey() {
        JFileChooser chooser = createPemChooser();
        chooser.setDialogTitle("Load Private Key");

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            currentPrivateKey = keyLoader.loadPrivateKey(chooser.getSelectedFile().toPath());
            currentPublicKey = null;
            publicKeyArea.setText("Private key loaded. Public key is not available from this file.");
            setStatus("Loaded private key from file.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void signHashValue() {
        String hashValue = hashValueArea.getText() == null ? "" : hashValueArea.getText().trim();

        if (hashValue.isEmpty()) {
            showError("hash_value must not be empty.");
            return;
        }

        if (!signatureService.isSha256Hex(hashValue)) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "hash_value is not a 64-character SHA-256 hex string. Sign it anyway?",
                    "Hash format warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            String signature = signatureService.signHashValue(hashValue, currentPrivateKey);
            signatureArea.setText(signature);
            setStatus("Signed hash_value with SHA256withRSA.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private JFileChooser createPemChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PEM files (*.pem)", "pem"));
        return chooser;
    }

    private void chooseAndWritePem(String defaultFileName, String pem) {
        JFileChooser chooser = createPemChooser();
        chooser.setDialogTitle("Export PEM");
        chooser.setSelectedFile(new java.io.File(defaultFileName));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            Files.writeString(chooser.getSelectedFile().toPath(), pem, StandardCharsets.UTF_8);
            setStatus("Exported " + chooser.getSelectedFile().getName());
        } catch (IOException e) {
            showError("Could not export PEM file: " + e.getMessage());
        }
    }

    private void copyText(String text, String successMessage) {
        if (text == null || text.isBlank()) {
            showError("Nothing to copy.");
            return;
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
        setStatus(successMessage);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        setStatus("Error: " + message);
    }
}
