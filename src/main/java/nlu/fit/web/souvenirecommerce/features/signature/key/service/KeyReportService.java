package nlu.fit.web.souvenirecommerce.features.signature.key.service;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.KeyCompromiseReportDAO;
import nlu.fit.web.souvenirecommerce.features.signature.key.dao.UserKeyDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class KeyReportService {
    private final UserKeyDAO userKeyDAO = new UserKeyDAO();
    private final KeyCompromiseReportDAO reportDAO = new KeyCompromiseReportDAO();

    public void reportKey(Long userId,
                          Long keyId,
                          String reportType,
                          LocalDateTime compromisedFrom,
                          String description) {
        validateUserId(userId);
        validateKeyId(keyId);

        String normalizedReportType = normalizeReportType(reportType);
        String normalizedDescription = normalizeDescription(description);

        if ("COMPROMISED".equals(normalizedReportType) && compromisedFrom == null) {
            throw new IllegalArgumentException("Vui lòng nhập thời điểm nghi ngờ lộ private key.");
        }

        if ("LOST".equals(normalizedReportType)) {
            compromisedFrom = null;
        }

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            int updatedRows = userKeyDAO.markKeyAsReported(
                    keyId,
                    userId,
                    normalizedReportType,
                    compromisedFrom,
                    normalizedDescription
            );

            if (updatedRows == 0) {
                throw new IllegalArgumentException("Chỉ có thể báo mất/lộ key đang ACTIVE.");
            }

            reportDAO.saveReport(
                    userId,
                    keyId,
                    normalizedReportType,
                    compromisedFrom,
                    normalizedDescription
            );

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            if (e instanceof IllegalArgumentException) {
                throw e;
            }

            throw new IllegalStateException("Không thể lưu báo cáo mất/lộ khóa.", e);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User chưa đăng nhập.");
        }
    }

    private void validateKeyId(Long keyId) {
        if (keyId == null) {
            throw new IllegalArgumentException("Key ID không hợp lệ.");
        }
    }

    private String normalizeReportType(String reportType) {
        if (reportType == null) {
            throw new IllegalArgumentException("Vui lòng chọn loại báo cáo.");
        }

        String normalized = reportType.trim().toUpperCase();

        if (!"LOST".equals(normalized) && !"COMPROMISED".equals(normalized)) {
            throw new IllegalArgumentException("Loại báo cáo không hợp lệ.");
        }

        return normalized;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalized = description.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}