package nlu.fit.web.souvenirecommerce.features.signature.key.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.model.entity.KeyCompromiseReport;
import org.hibernate.Session;
import java.time.LocalDateTime;

public class KeyCompromiseReportDAO {

    public void save(KeyCompromiseReport report) {
        if (report == null) {
            throw new IllegalArgumentException("KeyCompromiseReport không được null");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(report);
    }

    public void saveReport(Long userId,
                           Long keyId,
                           String reportType,
                           LocalDateTime compromisedFrom,
                           String description) {
        String sql = """
                INSERT INTO key_compromise_reports
                    (user_id, key_id, report_type, compromised_from, description, created_at)
                VALUES
                    (:userId, :keyId, :reportType, :compromisedFrom, :description, NOW())
                """;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.createNativeMutationQuery(sql)
                .setParameter("userId", userId)
                .setParameter("keyId", keyId)
                .setParameter("reportType", reportType)
                .setParameter("compromisedFrom", compromisedFrom)
                .setParameter("description", description)
                .executeUpdate();
    }
}