package com.cipher.signingtool;

import com.cipher.signingtool.localapi.LocalApiServer;
import com.cipher.signingtool.localapi.ConnectCallbackNotification;
import com.cipher.signingtool.localapi.ConnectCallbackResult;
import com.cipher.signingtool.localapi.PublicKeySavedNotification;
import com.cipher.signingtool.localapi.PublicKeySavedResult;
import com.cipher.signingtool.localapi.SignRequest;
import com.cipher.signingtool.localapi.SigningApiBridge;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.UUID;

public class SigningToolFrame extends JFrame implements SigningApiBridge {
    private final KeyPairService keyPairService = new KeyPairService();
    private final SignatureService signatureService = new SignatureService();
    private final KeyLoader keyLoader = new KeyLoader();
    private final KeyMatchService keyMatchService = new KeyMatchService();
    private final ActivePublicKeyClient activePublicKeyClient = new ActivePublicKeyClient();
    private final LocalConfigService localConfigService = new LocalConfigService();
    private final ToolKeyState keyState = new ToolKeyState();
    private final HttpClient routeCheckClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private final JTextArea publicKeyArea = createTextArea(8);
    private final JTextArea hashValueArea = createTextArea(5);
    private final JTextArea signatureArea = createTextArea(7);
    private final JLabel statusLabel = new JLabel("Sẵn sàng.", SwingConstants.LEFT);
    private final JLabel lastPrivateKeyPathLabel = new JLabel("Private Key gần nhất: chưa có", SwingConstants.LEFT);
    private final JLabel webPublicKeyLabel = new JLabel("Public Key web: chưa tải", SwingConstants.LEFT);
    private final LocalApiServer localApiServer = new LocalApiServer(this);
    private final JButton startApiButton = new JButton("Bật kết nối với website");
    private final JButton stopApiButton = new JButton("Tắt kết nối");

    private volatile WebPublicKeyData currentWebKeyData;

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
        JButton loadWebPublicButton = new JButton("Load Public Key From Web");

        generateButton.addActionListener(event -> generateKeyPair());
        copyPublicButton.addActionListener(event -> copyPublicKey());
        exportPublicButton.addActionListener(event -> exportPublicKey());
        exportPrivateButton.addActionListener(event -> exportPrivateKey());
        loadPrivateButton.addActionListener(event -> loadPrivateKey());
        loadRecentPrivateButton.addActionListener(event -> loadRecentPrivateKey());
        loadWebPublicButton.addActionListener(event -> loadPublicKeyFromWeb(loadWebPublicButton));

        startApiButton.addActionListener(event -> startApiServer());
        stopApiButton.addActionListener(event -> stopApiServer());
        stopApiButton.setEnabled(false);

        actions.add(generateButton);
        actions.add(copyPublicButton);
        actions.add(exportPublicButton);
        actions.add(exportPrivateButton);
        actions.add(loadPrivateButton);
        actions.add(loadRecentPrivateButton);
        actions.add(loadWebPublicButton);
        actions.add(startApiButton);
        actions.add(stopApiButton);

        // Nhãn cảnh báo Private Key màu đỏ nổi bật
        JLabel warningLabel = new JLabel("<html><b>CẢNH BÁO BẢO MẬT:</b> Private Key là khóa bí mật cá nhân của bạn. <b>TUYỆT ĐỐI KHÔNG</b> gửi file này cho bất kỳ ai hoặc tải lên bất kỳ trang web nào.</html>");
        warningLabel.setForeground(java.awt.Color.RED);
        warningLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Nhóm tất cả các điều khiển ở phía trên
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));

        actions.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        warningLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        lastPrivateKeyPathLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        webPublicKeyLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        topPanel.add(actions);
        topPanel.add(warningLabel);
        topPanel.add(lastPrivateKeyPathLabel);
        topPanel.add(webPublicKeyLabel);

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
            keyState.useGeneratedKeyPair(keyPair);
            resetWebKeyDisplay();
            publicKeyArea.setText(PemUtils.publicKeyToPem(keyPair.getPublic()));
            setStatus("Đã tạo cặp khóa RSA 2048. Hãy lưu Private Key và copy Public Key lên website.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void exportPublicKey() {
        PublicKey currentPublicKey = keyState.snapshot().currentPublicKey();
        if (currentPublicKey == null) {
            showError("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
            return;
        }

        chooseAndWritePem("public-key.pem", PemUtils.publicKeyToPem(currentPublicKey));
    }

    private void copyPublicKey() {
        PublicKey currentPublicKey = keyState.snapshot().currentPublicKey();
        if (currentPublicKey == null) {
            showError("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
            return;
        }

        copyText(PemUtils.publicKeyToPem(currentPublicKey), "Đã copy Public Key.");
    }

    private void exportPrivateKey() {
        PrivateKey currentPrivateKey = keyState.snapshot().currentPrivateKey();
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
        keyState.useLoadedPrivateKey(keyLoader.loadPrivateKey(privateKeyPath));
        resetWebKeyDisplay();
        publicKeyArea.setText("Đã tải Private Key local. Hãy dùng Load Public Key From Web để kiểm tra đúng cặp.");

        if (rememberPath) {
            saveLastPrivateKeyPath(privateKeyPath);
        }
        setStatus("Đã tải Private Key. Hãy load Public Key ACTIVE từ web để kiểm tra khớp.");
    }

    private void loadPublicKeyFromWeb(JButton sourceButton) {
        String browserOption = "Tự kết nối bằng trình duyệt";
        String manualOption = "Nhập JSESSIONID thủ công - Debug";
        String cancelOption = "Hủy";
        Object[] options = {browserOption, manualOption, cancelOption};

        int selectedOption = JOptionPane.showOptionDialog(
                this,
                "Chọn cách kết nối để tải Public Key từ web:",
                "Load Public Key From Web",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                browserOption
        );

        if (selectedOption == 0) {
            connectThroughBrowser();
            return;
        }
        if (selectedOption != 1) {
            return;
        }

        WebAccessInput input = promptWebAccess("Load Public Key From Web");
        if (input == null) {
            return;
        }

        sourceButton.setEnabled(false);
        setStatus("Đang tải Public Key ACTIVE từ web...");

        new SwingWorker<WebPublicKeyData, Void>() {
            @Override
            protected WebPublicKeyData doInBackground() {
                return activePublicKeyClient.load(input.webUrl(), input.sessionCookie());
            }

            @Override
            protected void done() {
                sourceButton.setEnabled(true);
                try {
                    WebPublicKeyData webKeyData = get();
                    PublicKey webPublicKey = keyLoader.loadPublicKeyPem(webKeyData.publicKeyPem());
                    currentWebKeyData = webKeyData;
                    keyState.useWebPublicKey(webPublicKey);
                    publicKeyArea.setText(webKeyData.publicKeyPem());
                    webPublicKeyLabel.setText("Public Key web: keyId=" + webKeyData.keyId()
                            + " | fingerprint=" + webKeyData.fingerprint());
                    webPublicKeyLabel.setForeground(new Color(30, 64, 175));
                    checkPrivateKeyAgainstWebKey();
                    if (keyState.snapshot().currentPrivateKey() == null) {
                        setStatus("Đã tải Public Key ACTIVE từ web. Hãy tải Private Key để kiểm tra khớp.");
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause.getMessage());
                }
            }
        }.execute();
    }

    private void connectThroughBrowser() {
        String webUrl = JOptionPane.showInputDialog(
                this,
                "Web URL (bao gồm context path nếu có).\n"
                        + "Ví dụ: http://localhost:8080/BACKEND_war_exploded",
                "http://localhost:8080/BACKEND_war_exploded"
        );
        if (webUrl == null) {
            return;
        }

        webUrl = webUrl.trim();
        if (webUrl.isBlank()) {
            showError("Web URL không được để trống.");
            return;
        }

        String normalizedWebUrl = webUrl.endsWith("/")
                ? webUrl.substring(0, webUrl.length() - 1)
                : webUrl;
        String nonce = UUID.randomUUID().toString();
        URI connectUri;
        try {
            connectUri = URI.create(normalizedWebUrl
                    + "/tool/connect?callback=http://127.0.0.1:9090/tool/connect/callback&nonce="
                    + nonce);
        } catch (IllegalArgumentException e) {
            showError("Web URL không hợp lệ.");
            return;
        }

        keyState.setPendingConnectNonce(nonce);
        try {
            ensureToolConnectRouteReachable(connectUri, normalizedWebUrl);
            if (!Desktop.isDesktopSupported()
                    || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                throw new IOException("Máy hiện tại không hỗ trợ mở trình duyệt tự động.");
            }
            Desktop.getDesktop().browse(connectUri);
            setStatus("Đã mở trình duyệt để kết nối với website.");
        } catch (IOException | SecurityException e) {
            keyState.clearPendingConnectNonce();
            showError(e.getMessage());
        }
    }

    private void ensureToolConnectRouteReachable(URI connectUri, String webBaseUrl) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(connectUri)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<Void> response = routeCheckClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == 404) {
                throw new IOException("Không tìm thấy route " + webBaseUrl + "/tool/connect.\n"
                        + "Hãy kiểm tra Tomcat đang chạy đúng project mới nhất và nhập đúng context path.\n"
                        + "Nếu app deploy dưới context /Cipher hoặc /BACKEND_war_exploded, hãy nhập đầy đủ ví dụ: "
                        + "http://localhost:8080/Cipher");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Kiểm tra route /tool/connect bị gián đoạn.", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Web URL không hợp lệ.", e);
        }
    }

    private void savePublicKeyToWeb(JButton sourceButton) {
        ToolKeyState.Snapshot generatedState = keyState.snapshot();
        if (!generatedState.generatedInCurrentSession()
                || generatedState.currentPublicKey() == null
                || generatedState.currentPrivateKey() == null) {
            showError("Hãy tạo cặp khóa mới trước khi lưu Public Key lên web.");
            return;
        }

        WebAccessInput input = promptWebAccess("Save Public Key To Web");
        if (input == null) {
            return;
        }

        String publicKeyPem = PemUtils.publicKeyToPem(generatedState.currentPublicKey());
        sourceButton.setEnabled(false);
        setStatus("Đang lưu Public Key mới lên web...");

        new SwingWorker<WebPublicKeyData, Void>() {
            @Override
            protected WebPublicKeyData doInBackground() {
                return activePublicKeyClient.save(input.webUrl(), input.sessionCookie(), publicKeyPem);
            }

            @Override
            protected void done() {
                sourceButton.setEnabled(true);
                try {
                    WebPublicKeyData savedKeyData = get();
                    PublicKey savedWebPublicKey = keyLoader.loadPublicKeyPem(savedKeyData.publicKeyPem());
                    if (!keyMatchService.matches(generatedState.currentPrivateKey(), savedWebPublicKey)) {
                        throw new IllegalStateException(
                                "Web đã phản hồi nhưng Public Key ACTIVE không khớp Private Key hiện tại."
                        );
                    }

                    ToolKeyState.Snapshot latestState = keyState.snapshot();
                    if (!sameKey(latestState.currentPrivateKey(), generatedState.currentPrivateKey())
                            || !sameKey(latestState.currentPublicKey(), generatedState.currentPublicKey())) {
                        throw new IllegalStateException(
                                "Key trong tool đã thay đổi trong lúc lưu. Trạng thái cũ không được áp dụng."
                        );
                    }

                    currentWebKeyData = savedKeyData;
                    keyState.markGeneratedPublicKeyUploaded(savedWebPublicKey);
                    webPublicKeyLabel.setText("Public Key web: keyId=" + savedKeyData.keyId()
                            + " | fingerprint=" + savedKeyData.fingerprint());
                    webPublicKeyLabel.setForeground(new Color(21, 128, 61));

                    String message = "Public key mới đã được lưu lên web. Private key hiện tại khớp với public key vừa lưu.";
                    setStatus(message);
                    JOptionPane.showMessageDialog(
                            SigningToolFrame.this,
                            message,
                            "Lưu Public Key thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    showError(cause.getMessage());
                }
            }
        }.execute();
    }

    private WebAccessInput promptWebAccess(String title) {
        JTextField webUrlField = new JTextField("http://localhost:8080/BACKEND_war_exploded", 32);
        JPasswordField sessionField = new JPasswordField(32);

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Web URL (bao gồm context path nếu có):"));
        form.add(webUrlField);
        form.add(new JLabel("JSESSIONID của phiên đang đăng nhập (không được lưu):"));
        form.add(sessionField);

        int option = JOptionPane.showConfirmDialog(
                this,
                form,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        String webUrl = webUrlField.getText().trim();
        String sessionCookie = new String(sessionField.getPassword()).trim();
        if (webUrl.isBlank() || sessionCookie.isBlank()) {
            showError("Web URL và JSESSIONID không được để trống.");
            return null;
        }
        return new WebAccessInput(webUrl, sessionCookie);
    }

    private record WebAccessInput(String webUrl, String sessionCookie) {
    }

    private void checkPrivateKeyAgainstWebKey() {
        ToolKeyState.Snapshot state = keyState.snapshot();
        if (state.currentPrivateKey() == null || state.currentWebPublicKey() == null) {
            return;
        }

        boolean matches = keyMatchService.matches(state.currentPrivateKey(), state.currentWebPublicKey());
        keyState.setKeyMatchResult(matches);
        if (matches) {
            webPublicKeyLabel.setForeground(new Color(21, 128, 61));
            setStatus("Private Key local KHỚP Public Key ACTIVE trên web.");
        } else {
            webPublicKeyLabel.setForeground(new Color(185, 28, 28));
            setStatus("CẢNH BÁO: Private Key local KHÔNG KHỚP Public Key ACTIVE trên web.");
        }
    }

    private void resetWebKeyDisplay() {
        currentWebKeyData = null;
        webPublicKeyLabel.setText("Public Key web: chưa tải");
        webPublicKeyLabel.setForeground(Color.DARK_GRAY);
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
            if (!confirmManualSigningKeyState()) {
                return;
            }
            String signature = signatureService.signHashValue(
                    hashValue,
                    keyState.snapshot().currentPrivateKey()
            );
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
        PublicKey currentPublicKey = keyState.snapshot().currentPublicKey();
        if (currentPublicKey == null) {
            throw new IllegalStateException("Chưa có Public Key. Vui lòng tạo cặp khóa trước.");
        }
        return PemUtils.publicKeyToPem(currentPublicKey);
    }

    @Override
    public boolean hasPrivateKey() {
        return keyState.snapshot().currentPrivateKey() != null;
    }

    @Override
    public ToolKeyState.Snapshot getKeyStateSnapshot() {
        return keyState.snapshot();
    }

    @Override
    public PublicKeySavedResult onPublicKeySaved(PublicKeySavedNotification notification) {
        PublicKey savedWebPublicKey = keyLoader.loadPublicKeyPem(notification.publicKey());
        ToolKeyState.SavedPublicKeyResult stateResult = keyState.applySavedWebPublicKey(savedWebPublicKey);
        currentWebKeyData = new WebPublicKeyData(
                notification.keyId(),
                notification.publicKey(),
                notification.fingerprint(),
                notification.createdAt()
        );

        String message = switch (stateResult) {
            case MATCHED -> "Public key đã được lưu trên web và khớp với private key hiện tại.";
            case MISMATCHED -> "Public key web vừa lưu không khớp với public key hiện tại trong tool.";
            case NO_CURRENT_PUBLIC_KEY ->
                    "Đã nhận public key từ web. Vui lòng Load Private Key và kiểm tra lại bằng Load Public Key From Web.";
        };

        SwingUtilities.invokeLater(() -> {
            webPublicKeyLabel.setText("Public Key web: keyId=" + notification.keyId()
                    + " | fingerprint=" + notification.fingerprint());
            webPublicKeyLabel.setForeground(switch (stateResult) {
                case MATCHED -> new Color(21, 128, 61);
                case MISMATCHED -> new Color(185, 28, 28);
                case NO_CURRENT_PUBLIC_KEY -> new Color(30, 64, 175);
            });
            if (stateResult == ToolKeyState.SavedPublicKeyResult.NO_CURRENT_PUBLIC_KEY) {
                publicKeyArea.setText(notification.publicKey());
            }
            setStatus(message);
        });

        return new PublicKeySavedResult(
                stateResult == ToolKeyState.SavedPublicKeyResult.MATCHED,
                message
        );
    }

    @Override
    public ConnectCallbackResult onConnectCallback(ConnectCallbackNotification notification) {
        synchronized (keyState) {
            String pendingNonce = keyState.getPendingConnectNonce();
            if (pendingNonce == null || !pendingNonce.equals(notification.nonce())) {
                throw new IllegalArgumentException("Nonce callback không hợp lệ hoặc đã hết hạn.");
            }
            keyState.clearPendingConnectNonce();
        }

        if (!notification.success()) {
            String message = notification.message() == null || notification.message().isBlank()
                    ? "Website không thể kết nối với Java Signing Tool."
                    : notification.message();
            SwingUtilities.invokeLater(() -> showError(message));
            return new ConnectCallbackResult(false, message);
        }
        if (notification.publicKey() == null || notification.publicKey().isBlank()) {
            throw new IllegalArgumentException("publicKey is required.");
        }

        PublicKey webPublicKey = keyLoader.loadPublicKeyPem(notification.publicKey());
        PrivateKey currentPrivateKey = keyState.snapshot().currentPrivateKey();
        boolean matches = currentPrivateKey != null
                && keyMatchService.matches(currentPrivateKey, webPublicKey);
        keyState.applyConnectedWebPublicKey(webPublicKey, matches);
        currentWebKeyData = new WebPublicKeyData(
                notification.keyId(),
                notification.publicKey(),
                notification.fingerprint(),
                notification.createdAt()
        );

        String message = matches
                ? "Public key ACTIVE khớp với private key hiện tại."
                : "Private key hiện tại không khớp public key ACTIVE trên web.";
        SwingUtilities.invokeLater(() -> {
            publicKeyArea.setText(notification.publicKey());
            webPublicKeyLabel.setText("Public Key web: keyId=" + notification.keyId()
                    + " | fingerprint=" + notification.fingerprint());
            webPublicKeyLabel.setForeground(matches
                    ? new Color(21, 128, 61)
                    : new Color(185, 28, 28));
            setStatus(message);
            JOptionPane.showMessageDialog(
                    SigningToolFrame.this,
                    message,
                    "Kết nối website",
                    matches ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE
            );
        });

        return new ConnectCallbackResult(matches, message);
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
        ToolKeyState.Snapshot state = keyState.snapshot();
        ensureAutomatedSigningKeyState(state);
        return signatureService.signHashValue(hashValue, state.currentPrivateKey());
    }

    private boolean confirmManualSigningKeyState() {
        SigningStatePolicy.Readiness readiness = signingReadiness();
        if (readiness == SigningStatePolicy.Readiness.READY) {
            return true;
        }
        if (readiness == SigningStatePolicy.Readiness.KEY_MISMATCH) {
            showError("Không thể ký: Private Key local không khớp Public Key ACTIVE trên web.");
            return false;
        }

        String message = readiness == SigningStatePolicy.Readiness.GENERATED_PUBLIC_KEY_NOT_UPLOADED
                ? "Public key vừa tạo chưa được lưu lên web. Bạn vẫn muốn ký thủ công?"
                : "Private key được tải từ file nhưng chưa được kiểm tra với Public Key ACTIVE. "
                        + "Hãy dùng Load Public Key From Web trước khi ký. Bạn vẫn muốn tiếp tục?";
        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Cảnh báo trạng thái khóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        return option == JOptionPane.YES_OPTION;
    }

    private void ensureAutomatedSigningKeyState(ToolKeyState.Snapshot state) {
        SigningStatePolicy.Readiness readiness = signingReadiness(state);
        switch (readiness) {
            case READY -> {
                return;
            }
            case GENERATED_PUBLIC_KEY_NOT_UPLOADED -> throw new IllegalStateException(
                    "Public key vừa tạo chưa được lưu lên web. Hãy copy key và lưu tại trang /key-management trước khi ký."
            );
            case FILE_PRIVATE_KEY_NOT_CHECKED -> throw new IllegalStateException(
                    "Private key được tải từ file chưa được kiểm tra. Hãy bấm Load Public Key From Web trước khi ký."
            );
            case KEY_MISMATCH -> throw new IllegalStateException(
                    "Private key local không khớp public key ACTIVE trên web. Đã chặn ký để tránh chữ ký không hợp lệ."
            );
        }
    }

    private SigningStatePolicy.Readiness signingReadiness() {
        return signingReadiness(keyState.snapshot());
    }

    private SigningStatePolicy.Readiness signingReadiness(ToolKeyState.Snapshot state) {
        return SigningStatePolicy.evaluate(
                state.generatedInCurrentSession(),
                state.publicKeyUploadedToWeb(),
                state.webPublicKeyLoaded(),
                state.keyMatchChecked(),
                state.keyPairMatched()
        );
    }

    private boolean sameKey(java.security.Key first, java.security.Key second) {
        return first != null
                && second != null
                && java.util.Arrays.equals(first.getEncoded(), second.getEncoded());
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
}
