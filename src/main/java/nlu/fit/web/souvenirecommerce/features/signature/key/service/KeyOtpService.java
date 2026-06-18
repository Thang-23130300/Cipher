package nlu.fit.web.souvenirecommerce.features.signature.key.service;

import jakarta.mail.MessagingException;
import nlu.fit.web.souvenirecommerce.common.utils.EmailUtil;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.KeyChangeOtpDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.service.UserKeyService;
import nlu.fit.web.souvenirecommerce.model.entity.KeyChangeOtp;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

public class KeyOtpService {
    private final KeyChangeOtpDAO keyChangeOtpDAO = new KeyChangeOtpDAO();
    private final UserKeyService userKeyService = new UserKeyService();
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Tạo mã OTP 6 chữ số ngẫu nhiên, lưu hash SHA-256 vào DB cùng public key đang chờ và gửi email xác nhận.
     */
    public void generateAndSendOtp(Long userId, String email, String publicKey) throws MessagingException {
        if (publicKey == null || publicKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Public key không được để trống");
        }

        // Tạo mã OTP 6 chữ số
        int number = secureRandom.nextInt(900000) + 100000;
        String otpCode = String.valueOf(number);

        // Băm OTP để lưu trữ bảo mật
        String otpHash = sha256Hex(otpCode);

        // Lưu bản ghi OTP mới đang chờ
        KeyChangeOtp otp = KeyChangeOtp.builder()
                .userId(userId)
                .email(email)
                .otpHash(otpHash)
                .publicKeyPending(publicKey)
                .purpose("KEY_CHANGE")
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // Hết hạn sau 5 phút
                .build();
        keyChangeOtpDAO.save(otp);

        // Gửi email OTP tới người dùng
        sendOtpEmail(email, otpCode);
    }

    /**
     * Xác thực OTP được gửi lên. Nếu khớp thì đánh dấu đã xác thực.
     */
    public boolean verifyOtp(Long userId, String otpCode) {
        if (otpCode == null || otpCode.trim().length() != 6) {
            throw new IllegalArgumentException("Mã OTP không hợp lệ");
        }

        Optional<KeyChangeOtp> optOtp = keyChangeOtpDAO.findLatestPendingOtp(userId);
        if (optOtp.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy yêu cầu cập nhật key nào đang chờ");
        }

        KeyChangeOtp otp = optOtp.get();
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Mã OTP đã hết hạn");
        }

        String hash = sha256Hex(otpCode.trim());
        if (otp.getOtpHash().equalsIgnoreCase(hash)) {
            otp.setVerifiedAt(LocalDateTime.now());
            keyChangeOtpDAO.update(otp);
            return true;
        }

        return false;
    }

    /**
     * Xác nhận cập nhật: Thu hồi các key cũ, active key mới và đánh dấu OTP đã được tiêu thụ.
     */
    public void consumeOtpAndSaveKey(Long userId) {
        Optional<KeyChangeOtp> optOtp = keyChangeOtpDAO.findLatestPendingOtp(userId);
        if (optOtp.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy yêu cầu cập nhật key nào");
        }

        KeyChangeOtp otp = optOtp.get();
        if (otp.getVerifiedAt() == null) {
            throw new IllegalStateException("Mã OTP chưa được xác thực thành công trước đó");
        }
        if (otp.getConsumedAt() != null) {
            throw new IllegalStateException("Yêu cầu thay đổi key này đã được xử lý rồi");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Yêu cầu đã hết hạn");
        }

        // Lưu public key mới vào hệ thống (thu hồi các key cũ)
        userKeyService.saveNewPublicKey(userId, otp.getPublicKeyPending());

        // Đánh dấu OTP này đã được tiêu thụ thành công
        otp.setConsumedAt(LocalDateTime.now());
        keyChangeOtpDAO.update(otp);
    }

    private String sha256Hex(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo SHA-256 hash cho OTP", e);
        }
    }

    private void sendOtpEmail(String email, String otpCode) throws MessagingException {
        String subject = "Xác nhận cập nhật Public Key - INOLA";
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
                    <h2 style="color: #333; text-align: center;">Yêu cầu cập nhật Public Key mới</h2>
                    <p>Chào bạn,</p>
                    <p>Hệ thống nhận được yêu cầu cập nhật Public Key cho tài khoản của bạn. Để xác nhận hành động này, vui lòng sử dụng mã OTP dưới đây:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="font-size: 32px; font-weight: bold; color: #4F46E5; letter-spacing: 5px; background: #F3F4F6; padding: 10px 20px; border-radius: 6px;">
                            %s
                        </span>
                    </div>
                    <p style="color: #EF4444; font-weight: bold;">CẢNH BÁO BẢO MẬT QUAN TRỌNG:</p>
                    <ul>
                        <li><b>KHÔNG CHIA SẺ</b> mã OTP này với bất kỳ ai.</li>
                        <li><b>KHÔNG CHIA SẺ PRIVATE KEY</b> của bạn cho bất kỳ ai, kể cả nhân viên hệ thống. Private key phải được lưu trữ an toàn tuyệt mật trên thiết bị cá nhân của bạn.</li>
                    </ul>
                    <p>Mã OTP này có hiệu lực trong vòng <b>5 phút</b>.</p>
                    <p>Nếu bạn không thực hiện yêu cầu này, vui lòng đổi mật khẩu tài khoản ngay lập tức.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin-top: 30px;" />
                    <p style="font-size: 12px; color: #888; text-align: center;">Đây là email tự động, vui lòng không trả lời email này.</p>
                </div>
                """.formatted(otpCode);

        EmailUtil.send(email, subject, htmlContent, "text/html; charset=UTF-8");
    }
}
