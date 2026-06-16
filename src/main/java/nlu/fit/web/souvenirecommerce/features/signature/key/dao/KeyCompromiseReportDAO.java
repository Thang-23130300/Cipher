package nlu.fit.web.souvenirecommerce.features.signature.key.dao;

import nlu.fit.web.souvenirecommerce.common.utils.HibernateUtil;
import nlu.fit.web.souvenirecommerce.model.entity.KeyCompromiseReport;
import org.hibernate.Session;

public class KeyCompromiseReportDAO {

    public void save(KeyCompromiseReport report) {
        if (report == null) {
            throw new IllegalArgumentException("KeyCompromiseReport không được null");
        }
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(report);
    }
}