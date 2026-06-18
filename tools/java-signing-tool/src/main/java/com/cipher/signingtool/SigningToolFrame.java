package com.cipher.signingtool;

import com.cipher.signingtool.localapi.LocalApiServer;
import com.cipher.signingtool.localapi.SignRequest;
import com.cipher.signingtool.localapi.SigningApiBridge;

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
import javax.swing.SwingUtilities;
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
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.lang.reflect.InvocationTargetException;

public class SigningToolFrame extends JFrame implements SigningApiBridge {
    private final KeyPairService keyPairService = new KeyPairService();
    private final SignatureService signatureService = new SignatureService();
    private final KeyLoader keyLoader = new KeyLoader();
    private final LocalConfigService localConfigService = new LocalConfigService();

    private final JTextArea publicKeyArea = createTextArea(8);
    private final JTextArea hashValueArea = createTextArea(5);
    private final JTextArea signatureArea = createTextArea(7);
    private final JLabel statusLabel = new JLabel("Sẵn sàng.", SwingConstants.LEFT);
    private final JLabel lastPrivateKeyPathLabel = new JLabel("Private Key gần nhất: chưa có", SwingConstants.LEFT);
    private final LocalApiServer localApiServer = new LocalApiServer(this);
    private final JButton startApiButton = new JButton("Bật kết nối với website");
    private final JButton stopApiButton = new JButton("Tắt kết nối");

    private PrivateKey currentPrivateKey;
    private PublicKey currentPublicKey;

    public SigningToolFrame() {
        super("INOLA Signing Tool - Ký đơn hàng bằng chữ ký số");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        refreshLastPrivateKeyPathLabel();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                localApiServer.stop();
            }
        });

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
        panel.setBorder(BorderFactory.createTitledBorder("Quản lý khóa chữ ký số"));

        publicKeyArea.setEditable(false);
        publicKeyArea.setText("Public Key sẽ hiển thị tại đây sau khi bạn bấm \"Tạo cặp khóa mới\".\n"
                + "Chỉ copy Public Key này lên website. Không gửi Private Key cho bất kỳ ai.");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateButton = new JButton("Tạo cặp khóa mới");
        JButton copyPublicButton = new JButton("Copy Public Key");
        JButton exportPublicButton = new JButton("Lưu Public Key");
        JButton exportPrivateButton = new JButton("Lưu Private Key");
        JButton loadPrivateButton = new JButton("Tải Private Key");
        JButton loadRecentPrivateButton = new JButton("Tải key gần nhất");

        generateButton.addActionListener(event -> generateKeyPair());
        copyPublicButton.addActionListener(event -> copyPublicKey());
        exportPublicButton.addActionListener(event -> exportPublicKey());
        exportPrivateButton.addActionListener(event -> exportPrivateKey());
        loadPrivateButton.addActionListener(event -> loadPrivateKey());
        loadRecentPrivateButton.addActionListener(event -> loadRecentPrivateKey());

        startApiButton.addActionListener(event -> startApiServer());
        stopApiButton.addActionListener(event -> stopApiServer());
        stopApiButton.setEnabled(false);

        actions.add(generateButton);
        actions.add(copyPublicButton);
        actions.add(exportPublicButton);
        actions.add(exportPrivateButton);
        actions.add(loadPrivateButton);
        actions.add(loadRecentPrivateButton);
        actions.add(startApiButton);
        actions.add(stopApiButton);

        // Khung tải khóa công khai từ Website
        JPanel webLoadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        webLoadPanel.add(new JLabel("Email tài khoản:"));
        javax.swing.JTextField emailField = new javax.swing.JTextField(20);
        JButton loadFromWebButton = new JButton("Tải Public Key từ Website");
        webLoadPanel.add(emailField);
        webLoadPanel.add(loadFromWebButton);

        loadFromWebButton.addActionListener(event -> {
            String email = emailField.getText();
            fetchPublicKeyFromWebsite(email);
        });

        // Nhãn cảnh báo Private Key màu đỏ nổi bật
        JLabel warningLabel = new JLabel("<html><b>CẢNH BÁO BẢO MẬT:</b> Private Key là khóa bí mật cá nhân của bạn. <b>TUYỆT ĐỐI KHÔNG</b> gửi file này cho bất kỳ ai hoặc tải lên bất kỳ trang web nào.</html>");
        warningLabel.setForeground(java.awt.Color.RED);
        warningLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Nhóm tất cả các điều khiển ở phía trên
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));

        actions.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        webLoadPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        warningLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        lastPrivateKeyPathLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        topPanel.add(actions);
        topPanel.add(webLoadPanel);
        topPanel.add(warningLabel);
        topPanel.add(lastPrivateKeyPathLabel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(publicKeyArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSigningPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Ký thủ công"));

        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.add(new JLabel("Mã băm đơn hàng cần ký"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(hashValueArea), BorderLayout.CENTER);

        JPanel signaturePanel = new JPanel(new BorderLayout(6, 6));
        signatureArea.setEditable(false);
        signaturePanel.add(new JLabel("Chữ ký Base64 sau khi ký"), BorderLayout.NORTH);
        signaturePanel.add(new JScrollPane(signatureArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton signButton = new JButton("Ký mã băm");
        JButton copySignatureButton = new JButton("Copy chữ ký");

        signButton.addActionListener(event -> signHashValue());
        copySignatureButton.addActionListener(event -> copyText(signatureArea.getText(), "Đã copy chữ ký."));

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
            setStatus("Đã tạo cặp khóa RSA 2048. Hãy lưu Private Key và copy Public Key lên website.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void exportPublicKey() {
        if (currentPublicKey == null) {
            showError("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
            return;
        }

        chooseAndWritePem("public-key.pem", PemUtils.publicKeyToPem(currentPublicKey));
    }

    private void copyPublicKey() {
        if (currentPublicKey == null) {
            showError("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
            return;
        }

        copyText(PemUtils.publicKeyToPem(currentPublicKey), "Đã copy Public Key.");
    }

    private void exportPrivateKey() {
        if (currentPrivateKey == null) {
            showError("Chưa có Private Key. Vui lòng tạo cặp khóa mới hoặc tải Private Key trước.");
            return;
        }

        Path exportedPath = chooseAndWritePem("private-key.pem", PemUtils.privateKeyToPem(currentPrivateKey));
        if (exportedPath != null) {
            saveLastPrivateKeyPath(exportedPath);
        }
    }

    private void loadPrivateKey() {
        JFileChooser chooser = createPemChooser();
        chooser.setDialogTitle("Tải Private Key");

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            loadPrivateKeyFromPath(chooser.getSelectedFile().toPath(), true);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadRecentPrivateKey() {
        Path recentPrivateKeyPath;
        try {
            recentPrivateKeyPath = localConfigService.getLastPrivateKeyPath().orElse(null);
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }

        if (recentPrivateKeyPath == null || !Files.exists(recentPrivateKeyPath)) {
            lastPrivateKeyPathLabel.setText("Private Key gần nhất: file không còn tồn tại");
            showError("Không tìm thấy Private Key gần nhất. Vui lòng chọn lại file Private Key.");
            return;
        }

        try {
            loadPrivateKeyFromPath(recentPrivateKeyPath, false);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadPrivateKeyFromPath(Path privateKeyPath, boolean rememberPath) {
        currentPrivateKey = keyLoader.loadPrivateKey(privateKeyPath);
        try {
            currentPublicKey = keyLoader.derivePublicKey(currentPrivateKey);
            publicKeyArea.setText(PemUtils.publicKeyToPem(currentPublicKey));
        } catch (Exception e) {
            currentPublicKey = null;
            publicKeyArea.setText("Đã tải Private Key, nhưng không thể suy ra Public Key từ khóa này.");
        }

        if (rememberPath) {
            saveLastPrivateKeyPath(privateKeyPath);
        }
        setStatus("Đã tải Private Key thành công.");
    }

    private void signHashValue() {
        String hashValue = hashValueArea.getText() == null ? "" : hashValueArea.getText().trim();

        if (hashValue.isEmpty()) {
            showError("Mã băm không được để trống.");
            return;
        }

        if (!signatureService.isSha256Hex(hashValue)) {
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Mã băm không đúng định dạng SHA-256 hex 64 ký tự. Bạn vẫn muốn ký tiếp?",
                    "Cảnh báo định dạng mã băm",
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
            setStatus("Đã ký mã băm bằng thuật toán SHA256withRSA.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void startApiServer() {
        try {
            localApiServer.start();
            startApiButton.setEnabled(false);
            stopApiButton.setEnabled(true);
            setStatus("Đã bật kết nối với website tại http://127.0.0.1:" + localApiServer.getPort());
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void stopApiServer() {
        localApiServer.stop();
        startApiButton.setEnabled(true);
        stopApiButton.setEnabled(false);
        setStatus("Đã tắt kết nối với website.");
    }

    @Override
    public String getPublicKeyPem() {
        if (currentPublicKey == null) {
            throw new IllegalStateException("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
        }
        return PemUtils.publicKeyToPem(currentPublicKey);
    }

    @Override
    public boolean hasPrivateKey() {
        return currentPrivateKey != null;
    }

    @Override
    public boolean isSha256Hex(String hashValue) {
        return signatureService.isSha256Hex(hashValue);
    }

    @Override
    public boolean confirmSigning(SignRequest request) {
        final int[] option = new int[] {JOptionPane.NO_OPTION};

        String hashValue = safeText(request.getHashValue());
        String shortHash = hashValue.length() > 80
                ? hashValue.substring(0, 40) + "\n" + hashValue.substring(40, 80) + "..."
                : hashValue;

        String message =
                "Website INOLA đang yêu cầu ký xác nhận đơn hàng.\n\n"
                        + "Mã đơn hàng: #" + safeText(request.getOrderId()) + "\n"
                        + "Website: " + safeText(request.getMerchantName()) + "\n"
                        + "Thuật toán băm: " + safeText(request.getHashAlgorithm()) + "\n"
                        + "Thuật toán ký: SHA256withRSA\n\n"
                        + "Mã băm đơn hàng:\n" + shortHash + "\n\n"
                        + "Bạn có đồng ý dùng Private Key hiện tại để ký đơn hàng này không?";

        Object[] options = {"Xác nhận ký", "Từ chối"};

        Runnable dialogTask = () -> option[0] = JOptionPane.showOptionDialog(
                this,
                message,
                "Xác nhận ký đơn hàng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]
        );

        try {
            if (SwingUtilities.isEventDispatchThread()) {
                dialogTask.run();
            } else {
                SwingUtilities.invokeAndWait(dialogTask);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (InvocationTargetException e) {
            return false;
        }

        return option[0] == JOptionPane.YES_OPTION;
    }

    @Override
    public String signHashValue(String hashValue) {
        return signatureService.signHashValue(hashValue, currentPrivateKey);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    private JFileChooser createPemChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp PEM (*.pem)", "pem"));
        return chooser;
    }

    private Path chooseAndWritePem(String defaultFileName, String pem) {
        JFileChooser chooser = createPemChooser();
        chooser.setDialogTitle("Lưu tệp PEM");
        chooser.setSelectedFile(new java.io.File(defaultFileName));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        try {
            Path selectedPath = chooser.getSelectedFile().toPath();
            Files.writeString(selectedPath, pem, StandardCharsets.UTF_8);
            setStatus("Đã lưu tệp " + chooser.getSelectedFile().getName());
            return selectedPath;
        } catch (IOException e) {
            showError("Không thể lưu tệp PEM: " + e.getMessage());
            return null;
        }
    }

    private void saveLastPrivateKeyPath(Path privateKeyPath) {
        try {
            localConfigService.saveLastPrivateKeyPath(privateKeyPath);
            refreshLastPrivateKeyPathLabel();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void refreshLastPrivateKeyPathLabel() {
        try {
            Path lastPath = localConfigService.getLastPrivateKeyPath().orElse(null);

            if (lastPath == null) {
                lastPrivateKeyPathLabel.setText("Private Key gần nhất: chưa có");
                return;
            }

            if (!Files.exists(lastPath)) {
                lastPrivateKeyPathLabel.setText("Private Key gần nhất: file không còn tồn tại");
                return;
            }

            lastPrivateKeyPathLabel.setText("Private Key gần nhất: " + lastPath);
        } catch (Exception e) {
            lastPrivateKeyPathLabel.setText("Private Key gần nhất: không thể đọc cấu hình");
        }
    }

    private void copyText(String text, String successMessage) {
        if (text == null || text.isBlank()) {
            showError("Không có nội dung để copy.");
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
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
        setStatus("Lỗi: " + message);
    }
    private void fetchPublicKeyFromWebsite(String email) {
        if (email == null || email.trim().isEmpty()) {
            showError("Vui lòng nhập Email tài khoản.");
            return;
        }

        setStatus("Đang tải Public Key cho " + email + "...");

        new Thread(() -> {
            try {
                String encodedEmail = java.net.URLEncoder.encode(email.trim(), StandardCharsets.UTF_8);
                java.net.URL url = new java.net.URL("http://localhost:8080/api/public-key?email=" + encodedEmail);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    java.io.InputStream in = conn.getInputStream();
                    String responseStr = new String(in.readAllBytes(), StandardCharsets.UTF_8);

                    boolean success = extractJsonBooleanField(responseStr, "success");
                    if (success) {
                        String publicKey = extractJsonStringField(responseStr, "publicKey");
                        if (publicKey != null) {
                            SwingUtilities.invokeLater(() -> {
                                publicKeyArea.setText(publicKey);
                                try {
                                    byte[] keyBytes = PemUtils.decodePem(publicKey, "PUBLIC KEY");
                                    java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(keyBytes);
                                    java.security.KeyFactory kf = java.security.KeyFactory.getInstance("RSA");
                                    currentPublicKey = kf.generatePublic(spec);
                                    setStatus("Đã tải và nạp Public Key từ website thành công.");
                                } catch (Exception ex) {
                                    setStatus("Đã tải Public Key từ website.");
                                }
                            });
                        } else {
                            showError("Không tìm thấy trường publicKey trong phản hồi từ Website.");
                        }
                    } else {
                        String message = extractJsonStringField(responseStr, "message");
                        showError(message != null ? message : "Lấy Public Key không thành công.");
                    }
                } else {
                    showError("Lỗi kết nối máy chủ: HTTP " + responseCode);
                }
            } catch (Exception e) {
                showError("Không thể kết nối đến website: " + e.getMessage());
            }
        }).start();
    }

    private String extractJsonStringField(String json, String field) {
        String key = "\"" + field + "\":\"";
        int index = json.indexOf(key);
        if (index == -1) return null;
        int start = index + key.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end).replace("\\n", "\n").replace("\\r", "\r");
    }

    private boolean extractJsonBooleanField(String json, String field) {
        String key = "\"" + field + "\":";
        int index = json.indexOf(key);
        if (index == -1) return false;
        int start = index + key.length();
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        if (start + 4 <= json.length() && json.substring(start, start + 4).equals("true")) {
            return true;
        }
        return false;
    }

}